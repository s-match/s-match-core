package it.unitn.disi.smatch.data.mappings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default mapping implementation. Permits only one relation between source and target.
 * Maps source and target pairs to the index of the relation character stored in a string.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class HashMapping<T> extends BaseMapping<T> implements IContextMapping<T>, IMappingFactory, Serializable {

    // source+target pairs mapped to index relation or null for IDK
    private final Map<NodePair<T, T>, Integer> entries;

    // relations for the above pairs
    private static final char[] relations = {IMappingElement.EQUIVALENCE,
            IMappingElement.LESS_GENERAL,
            IMappingElement.MORE_GENERAL,
            IMappingElement.DISJOINT,
            IMappingElement.ENTAILED_LESS_GENERAL,
            IMappingElement.ENTAILED_MORE_GENERAL,
            IMappingElement.ENTAILED_DISJOINT
    };

    private static Integer relationToIndex(final char relation) {
        switch (relation) {
            case IMappingElement.EQUIVALENCE:
                return 0;
            case IMappingElement.LESS_GENERAL:
                return 1;
            case IMappingElement.MORE_GENERAL:
                return 2;
            case IMappingElement.DISJOINT:
                return 3;
            case IMappingElement.ENTAILED_LESS_GENERAL:
                return 4;
            case IMappingElement.ENTAILED_MORE_GENERAL:
                return 5;
            case IMappingElement.ENTAILED_DISJOINT:
                return 6;
            default:
                throw new IllegalStateException();
        }
    }

    private static final class NodePair<K, V> implements Serializable {
        final K key;
        final V value;

        NodePair(K k, V v) {
            value = v;
            key = k;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final boolean equals(Object o) {
            if (!(o instanceof NodePair)) {
                return false;
            }
            NodePair e = (NodePair) o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2))) {
                    return true;
                }
            }
            return false;
        }

        public final int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^
                    (value == null ? 0 : value.hashCode());
        }

        public final String toString() {
            return getKey() + "=" + getValue();
        }
    }

    /**
     * Factory constructor.
     */
    public HashMapping() {
        this.entries = null;
    }

    /**
     * Mapping instance constructor.
     *
     * @param sourceContext source context
     * @param targetContext target context
     */
    public HashMapping(final IContext sourceContext, final IContext targetContext) {
        super(sourceContext, targetContext);
        this.entries = new ConcurrentHashMap<>();
    }

    @Override
    public IContextMapping<INode> getContextMappingInstance(final IContext source, final IContext target) {
        return new HashMapping<>(source, target);
    }

    @Override
    public IContextMapping<IAtomicConceptOfLabel> getConceptMappingInstance(final IContext source, final IContext target) {
        return new HashMapping<>(source, target);
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    @JsonIgnore
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        if (!(o instanceof IMappingElement)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        IMappingElement<T> e = (IMappingElement<T>) o;
        if (IMappingElement.IDK == e.getRelation()) {
            return false;
        }
        Integer idx = entries.get(new NodePair<>(e.getSource(), e.getTarget()));
        return (null != idx && e.getRelation() == relations[idx])
                || (null == idx && e.getRelation() == IMappingElement.IDK);
    }

    private class Itr implements Iterator<IMappingElement<T>> {
        private final Iterator<NodePair<T, T>> i;

        private Itr(Iterator<NodePair<T, T>> i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return i.hasNext();
        }

        @Override
        public IMappingElement<T> next() {
            final NodePair<T, T> np = i.next();
            return new MappingElement<>(np.getKey(), np.getValue(), relations[entries.get(np)]);
        }

        @Override
        public void remove() {
            i.remove();
        }
    }

    @Override
    public Iterator<IMappingElement<T>> iterator() {
        return new Itr(entries.keySet().iterator());
    }

    @Override
    public boolean add(final IMappingElement<T> e) {
        return setRelation(e.getSource(), e.getTarget(), e.getRelation());
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        @SuppressWarnings("unchecked")
        IMappingElement<T> e = (IMappingElement<T>) o;
        NodePair<T, T> np = new NodePair<>(e.getSource(), e.getTarget());
        Integer idx = entries.get(np);
        if (null == idx) {
            return false;
        } else {
            if (e.getRelation() == relations[idx]) {
                entries.remove(np);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void clear() {
        entries.clear();
    }

    @Override
    public char getRelation(final T source, final T target) {
        NodePair<T, T> np = new NodePair<>(source, target);
        Integer idx = entries.get(np);
        if (null == idx) {
            return IMappingElement.IDK;
        } else {
            return relations[idx];
        }
    }

    @Override
    public boolean setRelation(final T source, final T target, final char relation) {
        NodePair<T, T> np = new NodePair<>(source, target);
        Integer idx = entries.get(np);
        if (null == idx) {
            if (IMappingElement.IDK != relation) {
                entries.put(np, relationToIndex(relation));
                return true;
            }
            return false;
        } else {
            if (IMappingElement.IDK != relation) {
                if (relation != relations[idx]) {
                    entries.put(np, relationToIndex(relation));
                    return true;
                }
                return false;
            } else {
                entries.remove(np);
                return true;
            }
        }
    }

    @Override
    public Set<IMappingElement<T>> getSources(final T source) {
        Set<IMappingElement<T>> result = new HashSet<>();
        for (IMappingElement<T> me : this) {
            if (source == me.getSource()) {
                result.add(me);
            }
        }
        return result;
    }

    @Override
    public Set<IMappingElement<T>> getTargets(T target) {
        Set<IMappingElement<T>> result = new HashSet<>();
        for (IMappingElement<T> me : this) {
            if (target == me.getTarget()) {
                result.add(me);
            }
        }
        return result;
    }
}