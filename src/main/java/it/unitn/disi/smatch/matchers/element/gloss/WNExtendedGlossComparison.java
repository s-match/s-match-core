package it.unitn.disi.smatch.matchers.element.gloss;

import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.matchers.element.ElementMatcherException;
import it.unitn.disi.smatch.matchers.element.ISenseGlossBasedElementLevelSemanticMatcher;
import it.unitn.disi.smatch.oracles.ILinguisticOracle;
import it.unitn.disi.smatch.oracles.ISenseMatcher;
import it.unitn.disi.smatch.oracles.LinguisticOracleException;

import java.util.StringTokenizer;

/**
 * Implements WNExtendedGlossComparison matcher. See Element Level Semantic matchers paper for more details.
 * <p/>
 * Accepts the following parameters:
 * <p/>
 * threshold - integer parameter, which by default equals 5.
 * <p/>
 * meaninglessWords - string parameter which indicates words to ignore. Check the source file for default value.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class WNExtendedGlossComparison extends WNExtendedGloss implements ISenseGlossBasedElementLevelSemanticMatcher {

    public WNExtendedGlossComparison(ILinguisticOracle linguisticOracle, ISenseMatcher senseMatcher) {
        super(linguisticOracle, senseMatcher);
    }

    public WNExtendedGlossComparison(ILinguisticOracle linguisticOracle, ISenseMatcher senseMatcher, int threshold) {
        super(linguisticOracle, senseMatcher, threshold);
    }

    public WNExtendedGlossComparison(ILinguisticOracle linguisticOracle, ISenseMatcher senseMatcher, int threshold, String meaninglessWords) {
        super(linguisticOracle, senseMatcher, threshold, meaninglessWords);
    }

    /**
     * Computes the relation for extended gloss matcher.
     *
     * @param source1 the gloss of source
     * @param target1 the gloss of target
     * @return synonym or IDK relation
     */
    public char match(ISense source1, ISense target1) throws ElementMatcherException {
        char result = IMappingElement.IDK;
        try {
            String tExtendedGloss = getExtendedGloss(target1, 1, IMappingElement.LESS_GENERAL);
            String sExtendedGloss = getExtendedGloss(source1, 1, IMappingElement.LESS_GENERAL);
            //variations of this matcher
            StringTokenizer stSource = new StringTokenizer(tExtendedGloss, " ,.\"'();");
            String lemmaS, lemmaT;
            int counter = 0;
            while (stSource.hasMoreTokens()) {
                StringTokenizer stTarget = new StringTokenizer(sExtendedGloss, " ,.\"'();");
                lemmaS = stSource.nextToken();
                if (!meaninglessWords.contains(lemmaS))
                    while (stTarget.hasMoreTokens()) {
                        lemmaT = stTarget.nextToken();
                        if (!meaninglessWords.contains(lemmaT))
                            if (lemmaS.equalsIgnoreCase(lemmaT))
                                counter++;
                    }
            }
            if (counter > threshold) {
                result = IMappingElement.EQUIVALENCE;
            }
        } catch (LinguisticOracleException e) {
            throw new ElementMatcherException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
        return result;
    }
}
