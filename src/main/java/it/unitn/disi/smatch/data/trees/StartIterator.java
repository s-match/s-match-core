package it.unitn.disi.smatch.data.trees;

import java.util.Iterator;

/**
 * Iterator with one object prepended.
 *
 * @param <E>
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class StartIterator<E> implements Iterator<E> {
    private E start;
    private Iterator<E> i;

    public StartIterator(E start, Iterator<E> i) {
        if (null == start) {
            throw new IllegalArgumentException("argument is null");
        }
        this.start = start;
        this.i = i;
    }

    public boolean hasNext() {
        return (null != start || i.hasNext());
    }

    public E next() {
        E result = start;
        if (null != start) {
            start = null;
        } else {
            result = i.next();
        }
        return result;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
