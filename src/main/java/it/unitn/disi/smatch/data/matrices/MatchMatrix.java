package it.unitn.disi.smatch.data.matrices;

import it.unitn.disi.smatch.data.mappings.IMappingElement;

import java.util.Arrays;

/**
 * Default matrix for matching results.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MatchMatrix implements IMatchMatrix, IMatchMatrixFactory {

    private int x = 0;
    private int y = 0;
    private char[][] matrix = null;

    public void init(int x, int y) {
        matrix = new char[x][y];
        this.x = x;
        this.y = y;

        for (char[] row : matrix) {
            Arrays.fill(row, IMappingElement.IDK);
        }
    }

    public char get(int x, int y) {
        return matrix[x][y];
    }

    public boolean set(int x, int y, final char value) {
        boolean result = value == matrix[x][y];
        matrix[x][y] = value;
        return result;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public IMatchMatrix getInstance() {
        return new MatchMatrix();
    }
}