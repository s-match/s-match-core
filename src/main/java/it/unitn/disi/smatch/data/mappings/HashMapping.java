package it.unitn.disi.smatch.data.mappings;

import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

import java.util.*;

/**
 * Default mapping implementation. Permits only one relation between source and target. Maps source and target pairs to
 * the index of the relation character stored in a string.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class HashMapping<T> extends BaseMapping<T> implements IContextMapping<T>, IMappingFactory {

    protected Properties properties;

    // source+target pairs mapped to index of relations
    private final Map<NodePair<T, T>, Integer> entries;
    // relations for the above pairs
    private StringBuilder relations;

    private static class NodePair<K, V> {
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

    public HashMapping(IContext sourceContext, IContext targetContext) {
        this();
        this.sourceContext = sourceContext;
        this.targetContext = targetContext;
        properties = new Properties();
    }

    public HashMapping(Properties properties) {
        this();
        this.properties = properties;
    }

    public HashMapping() {
        entries = new HashMap<>();
        relations = new StringBuilder();
    }

    public Properties getProperties() {
        return properties;
    }

    public IContextMapping<INode> getContextMappingInstance(IContext source, IContext target) {
        return new HashMapping<>(source, target);
    }

    public IContextMapping<IAtomicConceptOfLabel> getACoLMappingInstance(IContext source, IContext target) {
        return new HashMapping<>(source, target);
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        @SuppressWarnings("unchecked")
        IMappingElement<T> e = (IMappingElement<T>) o;
        if (IMappingElement.IDK == e.getRelation()) {
            return false;
        }
        Integer idx = entries.get(new NodePair<>(e.getSource(), e.getTarget()));
        return null != idx && 0 <= idx && idx < relations.length() && (e.getRelation() == relations.charAt(idx));
    }

    private class Itr implements Iterator<IMappingElement<T>> {
        private final Iterator<NodePair<T, T>> i;
        private NodePair<T, T> lastPair;

        private Itr(Iterator<NodePair<T, T>> i) {
            this.i = i;
        }

        public boolean hasNext() {
            return i.hasNext();
        }

        public IMappingElement<T> next() {
            NodePair<T, T> np = i.next();
            lastPair = np;
            return new MappingElement<>(np.getKey(), np.getValue(), relations.charAt(entries.get(np)));
        }

        public void remove() {
            int idx = entries.get(lastPair);
            relations.delete(idx, idx + 1);
            i.remove();
        }
    }

    public Iterator<IMappingElement<T>> iterator() {
        return new Itr(entries.keySet().iterator());
    }

    public boolean add(IMappingElement<T> e) {
        return setRelation(e.getSource(), e.getTarget(), e.getRelation());
    }

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
            if (e.getRelation() == relations.charAt(idx)) {
                relations.delete(idx, idx + 1);
                entries.remove(np);
                return true;
            } else {
                return false;
            }
        }
    }

    public void clear() {
        entries.clear();
        relations = new StringBuilder();
    }

    public char getRelation(T source, T target) {
        NodePair<T, T> np = new NodePair<>(source, target);
        Integer idx = entries.get(np);
        if (null == idx) {
            return IMappingElement.IDK;
        } else {
            return relations.charAt(idx);
        }
    }

    public boolean setRelation(T source, T target, char relation) {
        NodePair<T, T> np = new NodePair<>(source, target);
        Integer idx = entries.get(np);
        if (null == idx) {
            if (IMappingElement.IDK != relation) {
                entries.put(np, relations.length());
                relations.append(relation);
                return true;
            }
            return false;
        } else {
            if (IMappingElement.IDK != relation) {
                if (relation != relations.charAt(idx)) {
                    relations.setCharAt(idx, relation);
                    return true;
                }
                return false;
            } else {
                relations.delete(idx, idx + 1);
                entries.remove(np);
                return true;
            }
        }
    }

    public List<IMappingElement<T>> getSources(final T source) {
        List<IMappingElement<T>> result = new ArrayList<>();
        for (IMappingElement<T> me : this) {
            if (source == me.getSource()) {
                result.add(me);
            }
        }
        //TODO impose the order to keep results consistent, because hashset iterator does not guarantee the order
        return result;
    }

    public List<IMappingElement<T>> getTargets(T target) {
        List<IMappingElement<T>> result = new ArrayList<>();
        for (IMappingElement<T> me : this) {
            if (target == me.getTarget()) {
                result.add(me);
            }
        }
        //TODO impose the order to keep results consistent, because hashset iterator does not guarantee the order
        return result;
    }
}