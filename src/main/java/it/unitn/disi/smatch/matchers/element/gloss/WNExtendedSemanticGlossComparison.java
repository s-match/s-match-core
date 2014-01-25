package it.unitn.disi.smatch.matchers.element.gloss;

import it.unitn.disi.common.components.ConfigurableException;
import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.matchers.element.ISenseGlossBasedElementLevelSemanticMatcher;
import it.unitn.disi.smatch.matchers.element.MatcherLibraryException;
import it.unitn.disi.smatch.oracles.LinguisticOracleException;

import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Implements WNExtendedSemanticGlossComparison matcher. See Element Level Semantic matchers paper for more details.
 * <p/>
 * Accepts the following parameters:
 * <p/>
 * meaninglessWords - string parameter which indicates words to ignore. Check the source file for default value.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class WNExtendedSemanticGlossComparison extends BasicGlossMatcher implements ISenseGlossBasedElementLevelSemanticMatcher {

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
     * Computes the relation for extended semantic gloss matcher.
     *
     * @param source the gloss of source
     * @param target the gloss of target
     * @return more general, less general or IDK relation
     */
    public char match(ISense source, ISense target) throws MatcherLibraryException {
        char result = IMappingElement.IDK;
        try {
            String sSynset = source.getGloss();

            // get gloss of Immediate ancestor of target node
            String tLGExtendedGloss = getExtendedGloss(target, 1, IMappingElement.LESS_GENERAL);
            // get relation frequently occur between gloss of source and extended gloss of target
            char LGRel = getDominantRelation(sSynset, tLGExtendedGloss);
            // get final relation
            char LGFinal = getRelationFromRels(IMappingElement.LESS_GENERAL, LGRel);
            // get gloss of Immediate descendant of target node
            String tMGExtendedGloss = getExtendedGloss(target, 1, IMappingElement.MORE_GENERAL);
            char MGRel = getDominantRelation(sSynset, tMGExtendedGloss);
            char MGFinal = getRelationFromRels(IMappingElement.MORE_GENERAL, MGRel);
            // Compute final relation
            if (MGFinal == LGFinal) {
                result = MGFinal;
            }
            if (MGFinal == IMappingElement.IDK) {
                result = LGFinal;
            }
            if (LGFinal == IMappingElement.IDK) {
                result = MGFinal;
            }
        } catch (LinguisticOracleException e) {
            throw new MatcherLibraryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
        return result;
    }

    /**
     * Gets Semantic relation occurring more frequently between words in two extended glosses.
     *
     * @param sExtendedGloss extended gloss of source
     * @param tExtendedGloss extended gloss of target
     * @return more general, less general or IDK relation
     * @throws MatcherLibraryException MatcherLibraryException
     */
    private char getDominantRelation(String sExtendedGloss, String tExtendedGloss) throws MatcherLibraryException {
        int Equals = 0;
        int moreGeneral = 0;
        int lessGeneral = 0;
        int Opposite = 0;
        StringTokenizer stSource = new StringTokenizer(sExtendedGloss, " ,.\"'()");
        String lemmaS, lemmaT;
        while (stSource.hasMoreTokens()) {
            StringTokenizer stTarget = new StringTokenizer(tExtendedGloss, " ,.\"'()");
            lemmaS = stSource.nextToken();
            if (!meaninglessWords.contains(lemmaS)) {
                while (stTarget.hasMoreTokens()) {
                    lemmaT = stTarget.nextToken();
                    if (!meaninglessWords.contains(lemmaT)) {
                        if (isWordLessGeneral(lemmaS, lemmaT)) {
                            lessGeneral++;
                        } else if (isWordMoreGeneral(lemmaS, lemmaT)) {
                            moreGeneral++;
                        } else if (isWordSynonym(lemmaS, lemmaT)) {
                            Equals++;
                        } else if (isWordOpposite(lemmaS, lemmaT)) {
                            Opposite++;
                        }
                    }
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
        if ((lg >= mg) && (lg >= syn) && (lg >= opp) && (lg > 0)) {
            return IMappingElement.LESS_GENERAL;
        }
        if ((mg >= lg) && (mg >= syn) && (mg >= opp) && (mg > 0)) {
            return IMappingElement.MORE_GENERAL;
        }
        if ((syn >= mg) && (syn >= lg) && (syn >= opp) && (syn > 0)) {
            return IMappingElement.LESS_GENERAL;
        }
        if ((opp >= mg) && (opp >= syn) && (opp >= lg) && (opp > 0)) {
            return IMappingElement.LESS_GENERAL;
        }
        return IMappingElement.IDK;
    }

    /**
     * Decides which relation to return as a function of relation for which extended gloss was built.
     *
     * @param builtForRel relation for which the gloss was built
     * @param glossRel    relation
     * @return less general, more general or IDK relation
     */
    private char getRelationFromRels(char builtForRel, char glossRel) {
        if (builtForRel == IMappingElement.EQUIVALENCE) {
            return glossRel;
        }
        if (builtForRel == IMappingElement.LESS_GENERAL) {
            if ((glossRel == IMappingElement.LESS_GENERAL) || (glossRel == IMappingElement.EQUIVALENCE)) {
                return IMappingElement.LESS_GENERAL;
            }
        }
        if (builtForRel == IMappingElement.MORE_GENERAL) {
            if ((glossRel == IMappingElement.MORE_GENERAL) || (glossRel == IMappingElement.EQUIVALENCE)) {
                return IMappingElement.MORE_GENERAL;
            }
        }
        return IMappingElement.IDK;
    }
}
