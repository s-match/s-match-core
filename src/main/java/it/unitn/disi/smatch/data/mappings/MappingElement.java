package it.unitn.disi.smatch.data.mappings;

/**
 * Mapping element implementation.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MappingElement<T> implements IMappingElement<T> {

    protected T source;
    protected T target;
    protected char relation;

    public MappingElement(T source, T target, char relation) {
        this.source = source;
        this.target = target;
        this.relation = relation;
    }

    public T getSource() {
        return source;
    }

    public T getTarget() {
        return target;
    }

    public char getRelation() {
        return relation;
    }

    public int hashCode() {
        int result;
        result = (source != null ? source.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (int) relation;
        return result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        @SuppressWarnings("unchecked")
        MappingElement<T> that = (MappingElement<T>) o;

        if (relation != that.relation) {
            return false;
        }
        if (source != null ? !source.equals(that.source) : that.source != null) {
            return false;
        }
        if (target != null ? !target.equals(that.target) : that.target != null) {
            return false;
        }

        return true;
    }
}