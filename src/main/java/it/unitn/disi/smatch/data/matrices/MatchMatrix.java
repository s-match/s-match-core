package it.unitn.disi.smatch.data.matrices;

import it.unitn.disi.smatch.data.mappings.IMappingElement;

import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Default matrix for matching results.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MatchMatrix implements IMatchMatrix, IMatchMatrixFactory {

    private final int x;
    private final int y;
    private final char[][] matrix;
    private final ReadWriteLock matrixLock = new ReentrantReadWriteLock();

    /**
     * Factory constructor.
     */
    public MatchMatrix() {
        this.x = 0;
        this.y = 0;
        this.matrix = null;
    }

    /**
     * Matrix instance constructor.
     *
     * @param x row count
     * @param y column count
     */
    public MatchMatrix(final int x, final int y) {
        this.x = x;
        this.y = y;
        this.matrix = new char[x][y];
        for (char[] row : matrix) {
            Arrays.fill(row, IMappingElement.IDK);
        }
    }

    @Override
    public char get(final int x, final int y) {
        matrixLock.readLock().lock();
        char c = matrix[x][y];
        matrixLock.readLock().unlock();
        return c;
    }

    @Override
    public boolean set(final int x, final int y, final char value) {
        matrixLock.readLock().lock();
        boolean result = value == matrix[x][y];
        matrixLock.readLock().unlock();
        if (!result) {
            matrixLock.writeLock().lock();
            matrix[x][y] = value;
            matrixLock.writeLock().unlock();
        }
        return result;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public IMatchMatrix getInstance(final int x, final int y) {
        return new MatchMatrix(x, y);
    }
}