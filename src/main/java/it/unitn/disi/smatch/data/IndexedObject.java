package it.unitn.disi.smatch.data;

/**
 * An object with an index.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class IndexedObject implements IIndexedObject {

    protected int index;

    public IndexedObject() {
        this.index = -1;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int newIndex) {
        index = newIndex;
    }
}
