package it.unitn.disi.smatch.data.matrices;

/**
 * An interface to a matrix with matching results.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMatchMatrix {

    /**
     * Returns an element.
     *
     * @param x row
     * @param y column
     * @return an element value
     */
    char get(int x, int y);

    /**
     * Sets an element.
     *
     * @param x     row
     * @param y     column
     * @param value a new element value
     * @return true if matrix was modified 
     */
    boolean set(int x, int y, char value);

    /**
     * Returns row count.
     *
     * @return row count
     */
    int getX();

    /**
     * Returns column count.
     *
     * @return column count
     */
    int getY();
}