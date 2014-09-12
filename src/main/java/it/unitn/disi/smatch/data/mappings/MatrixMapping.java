package it.unitn.disi.smatch.data.mappings;

import it.unitn.disi.smatch.data.IIndexedObject;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.matrices.IMatchMatrix;
import it.unitn.disi.smatch.data.matrices.IMatchMatrixFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

import java.util.*;

/**
 * Manages a mapping using a matrix. Needs a configuration key matchMatrixFactory with a class implementing
 * {@link it.unitn.disi.smatch.data.matrices.IMatchMatrixFactory} to produce matrix instances.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MatrixMapping<T extends IIndexedObject> extends BaseMapping<T> implements IContextMapping<T>, IMappingFactory {

    protected final IMatchMatrixFactory factory;

    protected IMatchMatrix matrix;

    // for set size();
    private int elementCount;

    private T[] sources;
    private T[] targets;

    private volatile transient int modCount;

    private class MatrixMappingIterator implements Iterator<IMappingElement<T>> {

        private int expectedModCount;
        private int curRow;
        private int curCol;
        private IMappingElement<T> next;
        private IMappingElement<T> current;

        private MatrixMappingIterator() {
            this.expectedModCount = modCount;
            if (0 == size()) {
                next = null;
            } else {
                curRow = -1;
                curCol = matrix.getY() - 1;
                next = findNext();
            }
        }

        public boolean hasNext() {
            return null != next;
        }

        public IMappingElement<T> next() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (null == next) {
                throw new NoSuchElementException();
            }

            current = next;
            next = findNext();
            return current;
        }

        public void remove() {
            if (null == current) {
                throw new IllegalStateException();
            }
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            setRelation(current.getSource(), current.getTarget(), IMappingElement.IDK);
            expectedModCount = modCount;
            current = null;
        }

        private IMappingElement<T> findNext() {
            IMappingElement<T> result = null;
            char relation = IMappingElement.IDK;
            do {
                curCol++;
                if (matrix.getY() == curCol) {
                    curRow++;
                    curCol = 0;
                }
            }
            while (curRow < matrix.getX() && curCol < matrix.getY() && IMappingElement.IDK == (relation = matrix.get(curRow, curCol)));

            if (IMappingElement.IDK != relation) {
                result = new MappingElement<>(sources[curRow], targets[curCol], relation);
            }
            return result;
        }
    }

    public MatrixMapping(IMatchMatrixFactory factory) {
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    public MatrixMapping(IMatchMatrixFactory factory, IContext sourceContext, IContext targetContext) {
        this.sourceContext = sourceContext;
        this.targetContext = targetContext;
        this.factory = factory;
        matrix = factory.getInstance();
        // counts and indexes them
        int rows = getRowCount(sourceContext);
        int cols = getColCount(targetContext);
        matrix.init(rows, cols);

        sources = (T[]) new IIndexedObject[rows];
        targets = (T[]) new IIndexedObject[cols];

        initRows(sourceContext, sources);
        initCols(targetContext, targets);

        elementCount = 0;
        modCount = 0;
    }

    protected void initCols(IContext targetContext, IIndexedObject[] targets) {
        // void
    }

    protected void initRows(IContext sourceContext, IIndexedObject[] sources) {
        // void
    }

    public char getRelation(T source, T target) {
        return matrix.get(source.getIndex(), target.getIndex());
    }

    public boolean setRelation(final T source, final T target, final char relation) {
        final boolean result =
                source == sources[source.getIndex()] &&
                        target == targets[target.getIndex()] &&
                        relation == matrix.get(source.getIndex(), target.getIndex());

        if (!result) {
            if (source == sources[source.getIndex()] && target == targets[target.getIndex()]) {
                modCount++;
                matrix.set(source.getIndex(), target.getIndex(), relation);
                if (IMappingElement.IDK == relation) {
                    elementCount--;
                } else {
                    elementCount++;
                }
            }
        }

        return !result;
    }

    public List<IMappingElement<T>> getSources(final T source) {
        final int sIdx = source.getIndex();
        if (0 <= sIdx && sIdx < sources.length && (source == sources[sIdx])) {
            ArrayList<IMappingElement<T>> result = new ArrayList<>();
            for (int j = 0; j < targets.length; j++) {
                char rel = matrix.get(sIdx, j);
                if (IMappingElement.IDK != rel) {
                    result.add(new MappingElement<>(sources[sIdx], targets[j], rel));
                }
            }
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    public List<IMappingElement<T>> getTargets(T target) {
        final int tIdx = target.getIndex();
        if (0 <= tIdx && tIdx < targets.length && (target == targets[tIdx])) {
            ArrayList<IMappingElement<T>> result = new ArrayList<>();
            for (int i = 0; i < sources.length; i++) {
                char rel = matrix.get(i, tIdx);
                if (IMappingElement.IDK != rel) {
                    result.add(new MappingElement<>(sources[i], targets[tIdx], rel));
                }
            }
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    public int size() {
        return elementCount;
    }

    public boolean isEmpty() {
        return 0 == elementCount;
    }

    public boolean contains(Object o) {
        boolean result = false;
        if (o instanceof IMappingElement) {
            final IMappingElement e = (IMappingElement) o;
            if (e.getSource() instanceof IIndexedObject) {
                @SuppressWarnings("unchecked")
                final T s = (T) e.getSource();
                if (e.getTarget() instanceof IIndexedObject) {
                    @SuppressWarnings("unchecked")
                    final T t = (T) e.getTarget();
                    result = IMappingElement.IDK != getRelation(s, t) && s == sources[s.getIndex()] && t == targets[t.getIndex()];
                }
            }
        }
        return result;
    }

    public Iterator<IMappingElement<T>> iterator() {
        return new MatrixMappingIterator();
    }

    public boolean add(IMappingElement<T> e) {
        return setRelation(e.getSource(), e.getTarget(), e.getRelation());
    }

    public boolean remove(Object o) {
        boolean result = false;
        if (o instanceof IMappingElement) {
            IMappingElement e = (IMappingElement) o;
            if (e.getSource() instanceof IIndexedObject) {
                @SuppressWarnings("unchecked")
                T s = (T) e.getSource();
                if (e.getTarget() instanceof IIndexedObject) {
                    @SuppressWarnings("unchecked")
                    T t = (T) e.getTarget();
                    result = setRelation(s, t, IMappingElement.IDK);
                }
            }
        }

        return result;
    }

    public void clear() {
        final int rows = matrix.getX();
        final int cols = matrix.getY();
        matrix.init(rows, cols);

        elementCount = 0;
    }

    public IContextMapping<INode> getContextMappingInstance(IContext source, IContext target) {
        return new NodesMatrixMapping(factory, source, target);
    }

    public IContextMapping<IAtomicConceptOfLabel> getACoLMappingInstance(IContext source, IContext target) {
        return new ACoLMatrixMapping(factory, source, target);
    }

    protected int getColCount(IContext c) {
        return -1;
    }

    protected int getRowCount(IContext c) {
        return -1;
    }
}