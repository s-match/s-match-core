package it.unitn.disi.smatch.data.mappings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.unitn.disi.smatch.data.IIndexedObject;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.matrices.IMatchMatrix;
import it.unitn.disi.smatch.data.matrices.IMatchMatrixFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages a mapping using a matrix. Needs a configuration key matchMatrixFactory with a class implementing
 * {@link it.unitn.disi.smatch.data.matrices.IMatchMatrixFactory} to produce matrix instances.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MatrixMapping<T extends IIndexedObject> extends BaseMapping<T> implements IContextMapping<T>, IMappingFactory {

    protected final IMatchMatrixFactory factory;

    protected final IMatchMatrix matrix;

    private final AtomicInteger elementCount;

    private final T[] sources;
    private final T[] targets;

    // for iterator
    private volatile transient int modCount;

    private final class MatrixMappingIterator implements Iterator<IMappingElement<T>> {

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

    /**
     * Constructor for mapping factory.
     *
     * @param factory matrix factory
     */
    public MatrixMapping(IMatchMatrixFactory factory) {
        this.factory = factory;
        if (null == factory) {
            throw new IllegalArgumentException("factory is required!");
        }
        this.matrix = null;
        this.elementCount = null;
        this.sources = null;
        this.targets = null;
    }

    /**
     * Constructor for mapping instance.
     *
     * @param factory       matrix factory
     * @param sourceContext source context
     * @param targetContext target context
     */
    @SuppressWarnings("unchecked")
    public MatrixMapping(IMatchMatrixFactory factory, IContext sourceContext, IContext targetContext) {
        super(sourceContext, targetContext);

        // counts and indexes them
        int rows = indexSource(sourceContext);
        int cols = indexTarget(targetContext);

        this.matrix = factory.getInstance(rows, cols);
        this.factory = factory;

        this.sources = (T[]) new IIndexedObject[rows];
        this.targets = (T[]) new IIndexedObject[cols];

        initRows(sourceContext, sources);
        initCols(targetContext, targets);

        this.elementCount = new AtomicInteger();
        this.modCount = 0;
    }

    protected void initCols(IContext targetContext, IIndexedObject[] targets) {
        // void
    }

    protected void initRows(IContext sourceContext, IIndexedObject[] sources) {
        // void
    }

    @Override
    public char getRelation(IIndexedObject source, IIndexedObject target) {
        return matrix.get(source.getIndex(), target.getIndex());
    }

    @Override
    public boolean setRelation(final IIndexedObject source, final IIndexedObject target, final char relation) {
        final boolean result =
                source == sources[source.getIndex()] &&
                        target == targets[target.getIndex()] &&
                        relation == matrix.get(source.getIndex(), target.getIndex());

        if (!result) {
            if (source == sources[source.getIndex()] && target == targets[target.getIndex()]) {
                modCount++;
                matrix.set(source.getIndex(), target.getIndex(), relation);
                if (IMappingElement.IDK == relation) {
                    elementCount.decrementAndGet();
                } else {
                    elementCount.incrementAndGet();
                }
            } else {
                throw new IllegalStateException("mapping is not initialized correctly!");
            }
        }

        return !result;
    }

    @Override
    public Set<IMappingElement<T>> getSources(final T source) {
        final int sIdx = source.getIndex();
        Set<IMappingElement<T>> result = Collections.emptySet();
        if (0 <= sIdx && sIdx < sources.length && (source == sources[sIdx])) {
            result = new HashSet<>();
            for (int j = 0; j < targets.length; j++) {
                char rel = matrix.get(sIdx, j);
                if (IMappingElement.IDK != rel) {
                    result.add(new MappingElement<>(sources[sIdx], targets[j], rel));
                }
            }

        }
        return result;
    }

    @Override
    public Set<IMappingElement<T>> getTargets(T target) {
        final int tIdx = target.getIndex();
        Set<IMappingElement<T>> result = Collections.emptySet();
        if (0 <= tIdx && tIdx < targets.length && (target == targets[tIdx])) {
            result = new HashSet<>();
            for (int i = 0; i < sources.length; i++) {
                char rel = matrix.get(i, tIdx);
                if (IMappingElement.IDK != rel) {
                    result.add(new MappingElement<>(sources[i], targets[tIdx], rel));
                }
            }
        }
        return result;
    }

    @Override
    public int size() {
        return elementCount.get();
    }

    @Override
    @JsonIgnore
    public boolean isEmpty() {
        return 0 == elementCount.get();
    }

    @Override
    public boolean contains(Object o) {
        boolean result = false;
        if (o instanceof IMappingElement) {
            final IMappingElement e = (IMappingElement) o;
            if (e.getSource() instanceof IIndexedObject) {
                final IIndexedObject s = (IIndexedObject) e.getSource();
                if (e.getTarget() instanceof IIndexedObject) {
                    final IIndexedObject t = (IIndexedObject) e.getTarget();
                    result = IMappingElement.IDK != getRelation(s, t) && s == sources[s.getIndex()] && t == targets[t.getIndex()];
                }
            }
        }
        return result;
    }

    @Override
    public Iterator<IMappingElement<T>> iterator() {
        return new MatrixMappingIterator();
    }

    @Override
    public boolean add(IMappingElement<T> e) {
        return setRelation(e.getSource(), e.getTarget(), e.getRelation());
    }

    @Override
    public boolean remove(Object o) {
        boolean result = false;
        if (o instanceof IMappingElement) {
            IMappingElement e = (IMappingElement) o;
            if (e.getSource() instanceof IIndexedObject) {
                final IIndexedObject s = (IIndexedObject) e.getSource();
                if (e.getTarget() instanceof IIndexedObject) {
                    final IIndexedObject t = (IIndexedObject) e.getTarget();
                    result = setRelation(s, t, IMappingElement.IDK);
                }
            }
        }

        return result;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IContextMapping<INode> getContextMappingInstance(IContext source, IContext target) {
        return new NodesMatrixMapping(factory, source, target);
    }

    @Override
    public IContextMapping<IAtomicConceptOfLabel> getConceptMappingInstance(IContext source, IContext target) {
        return new ConceptMatrixMapping(factory, source, target);
    }

    protected int indexTarget(IContext c) {
        return -1;
    }

    protected int indexSource(IContext c) {
        return -1;
    }
}