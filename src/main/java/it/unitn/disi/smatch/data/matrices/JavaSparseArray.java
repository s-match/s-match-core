package it.unitn.disi.smatch.data.matrices;

import it.unitn.disi.smatch.data.mappings.IMappingElement;

import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Implements a Java Sparse Array (see 10.1.1.13.7544.pdf).
 * Does not have max density limit. No boundary checks.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class JavaSparseArray implements IMatchMatrix, IMatchMatrixFactory {

    private final int rows;
    private final int cols;

    private final char[][] value;
    //indexes of values. keep indexes sorted.
    private final int[][] index;

    private final ReadWriteLock indexLock;
    private final ReadWriteLock valueLock;

    /**
     * Factory constructor.
     */
    public JavaSparseArray() {
        this.rows = 0;
        this.cols = 0;
        this.value = null;
        this.index = null;
        this.indexLock = null;
        this.valueLock = null;
    }

    /**
     * Matrix instance constructor.
     *
     * @param x row count
     * @param y column count
     */
    public JavaSparseArray(final int x, final int y) {
        this.rows = x;
        this.cols = y;
        this.index = new int[x][];
        this.value = new char[x][];
        this.indexLock = new ReentrantReadWriteLock();
        this.valueLock = new ReentrantReadWriteLock();
    }

    @Override
    public char get(final int x, final int y) {
        char result = IMappingElement.IDK;
        indexLock.readLock().lock();
        if (null != index[x]) {
            int idx = Arrays.binarySearch(index[x], y);
            if (-1 < idx) {
                valueLock.readLock().lock();
                result = value[x][idx];
                valueLock.readLock().unlock();
            }
        }
        indexLock.readLock().unlock();
        return result;
    }

    @Override
    public boolean set(final int x, final int y, final char aValue) {
        boolean result;
        indexLock.readLock().lock();
        if (IMappingElement.IDK != aValue) {
            if (null != index[x]) {//row exists
                int idx = Arrays.binarySearch(index[x], y);
                indexLock.readLock().unlock();

                if (-1 < idx) {//element exists
                    valueLock.writeLock().lock();
                    result = aValue == value[x][idx];
                    value[x][idx] = aValue;
                    valueLock.writeLock().unlock();
                } else { //element does not exist
                    indexLock.writeLock().lock();
                    valueLock.writeLock().lock();
                    int[] newIndexRow = new int[index[x].length + 1];
                    char[] newValueRow = new char[index[x].length + 1];

                    //keep index sorted
                    int insertAt = -(idx + 1);
                    System.arraycopy(index[x], 0, newIndexRow, 0, insertAt);
                    newIndexRow[insertAt] = y;
                    System.arraycopy(value[x], 0, newValueRow, 0, insertAt);
                    newValueRow[insertAt] = aValue;
                    if (index[x].length > insertAt) {
                        System.arraycopy(index[x], insertAt, newIndexRow, insertAt + 1, index[x].length - insertAt);
                        System.arraycopy(value[x], insertAt, newValueRow, insertAt + 1, index[x].length - insertAt);
                    }

                    index[x] = newIndexRow;
                    value[x] = newValueRow;

                    valueLock.writeLock().unlock();
                    indexLock.writeLock().unlock();
                    result = true;
                }
            } else {
                indexLock.readLock().unlock();

                indexLock.writeLock().lock();
                valueLock.writeLock().lock();

                index[x] = new int[1];
                index[x][0] = y;
                value[x] = new char[1];
                value[x][0] = aValue;

                valueLock.writeLock().unlock();
                indexLock.writeLock().unlock();
                result = true;
            }
        } else {
            //check and remove it
            if (null != index[x]) {//row exists
                int idx = Arrays.binarySearch(index[x], y);
                indexLock.readLock().unlock();

                if (-1 < idx) {//element exists
                    indexLock.writeLock().lock();
                    valueLock.writeLock().lock();

                    if (1 < index[x].length) {
                        //remove element
                        int[] newIndexRow = new int[index[x].length - 1];
                        char[] newValueRow = new char[index[x].length - 1];
                        //before element
                        if (0 < idx) {
                            System.arraycopy(index[x], 0, newIndexRow, 0, idx);
                            System.arraycopy(value[x], 0, newValueRow, 0, idx);
                        }
                        //after element
                        if (idx < (index[x].length - 1)) {
                            System.arraycopy(index[x], idx + 1, newIndexRow, idx, index[x].length - 1 - idx);
                            System.arraycopy(value[x], idx + 1, newValueRow, idx, index[x].length - 1 - idx);
                        }
                        index[x] = newIndexRow;
                        value[x] = newValueRow;
                    } else {
                        if (1 == index[x].length) {
                            //remove entire row
                            index[x] = null;
                            value[x] = null;
                        }
                    }

                    valueLock.writeLock().unlock();
                    indexLock.writeLock().unlock();
                    result = true;
                } else {
                    result = false;
                }
            } else {
                indexLock.readLock().unlock();
                result = false;
            }
        }
        return result;
    }

    @Override
    public int getX() {
        return rows;
    }

    @Override
    public int getY() {
        return cols;
    }

    @Override
    public IMatchMatrix getInstance(final int x, final int y) {
        return new JavaSparseArray(x, y);
    }
}