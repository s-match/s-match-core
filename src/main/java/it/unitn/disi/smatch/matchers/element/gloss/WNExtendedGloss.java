package it.unitn.disi.smatch.matchers.element.gloss;

import it.unitn.disi.common.components.ConfigurableException;
import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.matchers.element.ISenseGlossBasedElementLevelSemanticMatcher;
import it.unitn.disi.smatch.matchers.element.MatcherLibraryException;
import it.unitn.disi.smatch.oracles.LinguisticOracleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Implements WNExtendedGlossComparison matcher. See Element Level Semantic matchers paper for more details.
 * Accepts the following parameters:
 * <p/>
 * threshold - integer parameter, which by default equals 5.
 * <p/>
 * meaninglessWords - string parameter which indicates words to ignore. Check the source file for default value.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class WNExtendedGloss extends BasicGlossMatcher implements ISenseGlossBasedElementLevelSemanticMatcher {

    private static final String THRESHOLD_KEY = "threshold";
    private int threshold = 5;

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
     * Computes the relation for extended gloss matcher.
     *
     * @param source the gloss of source
     * @param target the gloss of target
     * @return synonym or IDK relation
     */
    public char match(ISense source, ISense target) throws MatcherLibraryException {
        char result = IMappingElement.IDK;
        try {
            String tExtendedGloss = getExtendedGloss(target, 1, IMappingElement.LESS_GENERAL);
            List<String> sourceLemmas = source.getLemmas();
            //variations of this matcher
            //StringTokenizer stSource = new StringTokenizer(tExtendedGloss, " ,.\"'();");
            String lemmaT;
            int counter = 0;
            for (String sourceLemma : sourceLemmas) {
                StringTokenizer stTarget = new StringTokenizer(tExtendedGloss, " ,.\"'();");
                if (!meaninglessWords.contains(sourceLemma))
                    while (stTarget.hasMoreTokens()) {
                        lemmaT = stTarget.nextToken();
                        if (!meaninglessWords.contains(lemmaT)) {
                            if (sourceLemma.equalsIgnoreCase(lemmaT)) {
                                counter++;
                            }
                        }
                    }
            }
            if (counter > threshold) {
                result = IMappingElement.EQUIVALENCE;
            }
        } catch (LinguisticOracleException e) {
            throw new MatcherLibraryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
        return result;
    }
}