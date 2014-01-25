package it.unitn.disi.smatch.matchers.element.gloss;

import it.unitn.disi.common.components.ConfigurableException;
import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.matchers.element.ISenseGlossBasedElementLevelSemanticMatcher;
import it.unitn.disi.smatch.matchers.element.MatcherLibraryException;

import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Implements WNSemanticGlossComparison matcher. See Element Level Semantic matchers paper for more details.
 * <p/>
 * Accepts the following parameters:
 * <p/>
 * meaninglessWords - string parameter which indicates words to ignore. Check the source file for default value.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class WNSemanticGlossComparison extends BasicGlossMatcher implements ISenseGlossBasedElementLevelSemanticMatcher {

    // the words which are cut off from the area of discourse
    private static final String MEANINGLESS_WORDS_KEY = "meaninglessWords";
    private String meaninglessWords = "of on to their than from for by in at is are have has the a as with your etc our into its his her which him among those against ";

    @Override
    public boolean setProperties(Properties newProperties) throws ConfigurableException {
        boolean result = super.setProperties(newProperties);
        if (result) {
            if (newProperties.containsKey(MEANINGLESS_WORDS_KEY)) {
                meaninglessWords = newProperties.getProperty(MEANINGLESS_WORDS_KEY) + " ";
            }
        }
        return result;
    }

    /**
     * Computes the relations with WordNet semantic gloss matcher.
     *
     * @param source the gloss of source
     * @param target the gloss of target
     * @return less general, more general, equal, opposite or IDK relation
     */
    public char match(ISense source, ISense target) throws MatcherLibraryException {
        int Equals = 0;
        int moreGeneral = 0;
        int lessGeneral = 0;
        int Opposite = 0;
        String sSynset = source.getGloss();
        String tSynset = target.getGloss();
        StringTokenizer stSource = new StringTokenizer(sSynset, " ,.\"'()");
        String lemmaS, lemmaT;
        while (stSource.hasMoreTokens()) {
            StringTokenizer stTarget = new StringTokenizer(tSynset, " ,.\"'()");
            lemmaS = stSource.nextToken();
            if (!meaninglessWords.contains(lemmaS))
                while (stTarget.hasMoreTokens()) {
                    lemmaT = stTarget.nextToken();
                    if (!meaninglessWords.contains(lemmaT)) {
                        if (isWordLessGeneral(lemmaS, lemmaT))
                            lessGeneral++;
                        else if (isWordMoreGeneral(lemmaS, lemmaT))
                            moreGeneral++;
                        else if (isWordSynonym(lemmaS, lemmaT))
                            Equals++;
                        else if (isWordOpposite(lemmaS, lemmaT))
                            Opposite++;
                    }
                }
        }
        return getRelationFromInts(lessGeneral, moreGeneral, Equals, Opposite);
    }

    /**
     * Decides which relation to return.
     *
     * @param lg  number of less general words between two extended gloss
     * @param mg  number of more general words between two extended gloss
     * @param syn number of synonym words between two extended gloss
     * @param opp number of opposite words between two extended gloss
     * @return the more frequent relation between two extended glosses.
     */
    private char getRelationFromInts(int lg, int mg, int syn, int opp) {
        if ((lg >= mg) && (lg >= syn) && (lg >= opp) && (lg > 0))
            return IMappingElement.LESS_GENERAL;
        if ((mg >= lg) && (mg >= syn) && (mg >= opp) && (mg > 0))
            return IMappingElement.MORE_GENERAL;
        if ((syn >= mg) && (syn >= lg) && (syn >= opp) && (syn > 0))
            return IMappingElement.LESS_GENERAL;
        if ((opp >= mg) && (opp >= syn) && (opp >= lg) && (opp > 0))
            return IMappingElement.LESS_GENERAL;
        return IMappingElement.IDK;
    }
}
