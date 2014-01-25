package it.unitn.disi.smatch.matchers.element.string;

import it.unitn.disi.common.components.Configurable;
import it.unitn.disi.common.components.ConfigurableException;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.matchers.element.IStringBasedElementLevelSemanticMatcher;

import java.util.Properties;

/**
 * Implements Edit Distance matcher. See Element Level Semantic matchers paper for more details.
 * <p/>
 * Accepts the following parameters:
 * <p/>
 * threshold - float parameter, which by default equals 0.9.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class EditDistance extends Configurable implements IStringBasedElementLevelSemanticMatcher {

    private static int MATCH = 0;
    private static int MISMATCH = 1;
    private static int GAP = 1; // treating gap = mismatch

    private static final String THRESHOLD_KEY = "threshold";
    private double threshold = 0.9;

    @Override
    public boolean setProperties(Properties newProperties) throws ConfigurableException {
        boolean result = super.setProperties(newProperties);
        if (result) {
            if (newProperties.containsKey(THRESHOLD_KEY)) {
                threshold = Double.parseDouble(newProperties.getProperty(THRESHOLD_KEY));
            }
        }
        return result;
    }


    /**
     * Computes the relation with edit distance matcher.
     *
     * @param str1 one input string
     * @param str2 another input string
     * @return synonym or IDK relation
     */
    public char match(String str1, String str2) {
        if (str1 == null || str2 == null || str1.length() == 0 || str2.length() == 0) {
            return IMappingElement.IDK;
        }
        float sim = 1 - (float) levenshteinDistance(str1, str2) / java.lang.Math.max(str1.length(), str2.length());
        if (threshold <= sim) {
            return IMappingElement.EQUIVALENCE;
        } else {
            return IMappingElement.IDK;
        }
    }

    /**
     * Calculates edit distance.
     *
     * @param str1 source string
     * @param str2 target string
     * @return edit distance
     */
    private static int levenshteinDistance(String str1, String str2) {
        //Add a dummy character to the beginning of both strings
        str1 = " " + str1;
        str2 = " " + str2;
        int n = str1.length(), m = str2.length();
        int[][] D = new int[n][m];
        D[0][0] = 0;
        int i, j;
        for (i = 1; i < n; i++) D[i][0] = D[i - 1][0] + GAP;//distance(null, null);
        for (j = 1; j < m; j++) D[0][j] = D[0][j - 1] + GAP;//distance(null, null);
        for (i = 1; i < n; i++) {
            for (j = 1; j < m; j++) {
                int m1 = D[i - 1][j] + GAP;//distance(str1.charAt(i), null);
                int m2 = D[i - 1][j - 1] + distance(str1.charAt(i), str2.charAt(j));
                int m3 = D[i][j - 1] + GAP;//distance(null, str2.charAt(j));
                D[i][j] = Math.min(Math.min(m1, m2), m3);
            }
        }
        return D[n - 1][m - 1];
    }

    /**
     * Treats with online mismatch and gap.
     *
     * @param a a
     * @param b b
     * @return match or mismatch
     */
    private static int distance(Character a, Character b) {
        if (null == a || null == b) return GAP;
        if (!a.equals(b)) return MISMATCH;
        return MATCH;
    }
}
