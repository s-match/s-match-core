package it.unitn.disi.smatch.matchers.element.gloss;

import it.unitn.disi.common.components.Configurable;
import it.unitn.disi.common.components.ConfigurableException;
import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.matchers.element.ISenseGlossBasedElementLevelSemanticMatcher;

import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Implements WNGlossComparison matcher. See Element Level Semantic matchers paper for more details.
 * <p/>
 * Accepts the following parameters:
 * <p/>
 * threshold - integer parameter, which by default equals 2.
 * <p/>
 * meaninglessWords - string parameter which indicates words to ignore. Check the source file for default value.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class WNGlossComparison extends Configurable implements ISenseGlossBasedElementLevelSemanticMatcher {

    private static final String THRESHOLD_KEY = "threshold";
    private int threshold = 2;

    // the words which are cut off from the area of discourse
    private static final String MEANINGLESS_WORDS_KEY = "meaninglessWords";
    private String meaninglessWords = "of on to their than from for by in at is are have has the a as with your etc our into its his her which him among those against ";

    @Override
    public boolean setProperties(Properties newProperties) throws ConfigurableException {
        boolean result = super.setProperties(newProperties);
        if (result) {
            if (newProperties.containsKey(THRESHOLD_KEY)) {
                threshold = Integer.parseInt(newProperties.getProperty(THRESHOLD_KEY));
            }

            if (newProperties.containsKey(MEANINGLESS_WORDS_KEY)) {
                meaninglessWords = newProperties.getProperty(MEANINGLESS_WORDS_KEY) + " ";
            }
        }
        return result;
    }

    /**
     * Computes the relations with WordNet gloss comparison matcher.
     *
     * @param source gloss of source
     * @param target gloss of target
     * @return synonym or IDK relation
     */
    public char match(ISense source, ISense target) {
        String sSynset = source.getGloss();
        String tSynset = target.getGloss();
        StringTokenizer stSource = new StringTokenizer(sSynset, " ,.\"'();");
        String lemmaS, lemmaT;
        int counter = 0;
        while (stSource.hasMoreTokens()) {
            StringTokenizer stTarget = new StringTokenizer(tSynset, " ,.\"'();");
            lemmaS = stSource.nextToken();
            if (!meaninglessWords.contains(lemmaS))
                while (stTarget.hasMoreTokens()) {
                    lemmaT = stTarget.nextToken();
                    if (!meaninglessWords.contains(lemmaT))
                        if (lemmaS.equals(lemmaT))
                            counter++;
                }
        }
        if (counter >= threshold)
            return IMappingElement.EQUIVALENCE;
        else
            return IMappingElement.IDK;
    }
}
