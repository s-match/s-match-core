package it.unitn.disi.smatch.data;

import java.io.Serializable;

/**
 * An object with an index.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class IndexedObject implements IIndexedObject, Serializable {

    protected int index;

    public IndexedObject() {
        this.index = -1;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int newIndex) {
        index = newIndex;
    }
}
