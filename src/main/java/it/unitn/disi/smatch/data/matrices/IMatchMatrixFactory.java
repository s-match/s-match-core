package it.unitn.disi.smatch.data.matrices;

/**
 * Produces matching matrices.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMatchMatrixFactory {

    /**
     * Returns a matrix x per y.
     *
     * @param x rows count
     * @param y column count
     */
    IMatchMatrix getInstance(int x, int y);
}
