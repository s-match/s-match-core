package it.unitn.disi.smatch.matchers.element.gloss;

import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.matchers.element.ISenseGlossBasedElementLevelSemanticMatcher;
import it.unitn.disi.smatch.matchers.element.MatcherLibraryException;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Implements WNGloss matcher. See Element Level Semantic matchers paper for more details.
 * <p/>
 * Accepts the following parameters:
 * <p/>
 * threshold - integer parameter, which by default equals 1.
 * <p/>
 * meaninglessWords - string parameter which indicates words to ignore. Check the source file for default value.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class WNGloss implements ISenseGlossBasedElementLevelSemanticMatcher {

    // the words which are cut off from the area of discourse
    public final static String DEFAULT_MEANINGLESS_WORDS = "of on to their than from for by in at is are have has the a as with your etc our into its his her which him among those against ";

    protected final int threshold;
    protected final String meaninglessWords;

    public WNGloss() {
        this.threshold = 1;
        this.meaninglessWords = DEFAULT_MEANINGLESS_WORDS;
    }

    public WNGloss(int threshold) {
        this.threshold = threshold;
        this.meaninglessWords = DEFAULT_MEANINGLESS_WORDS;
    }

    public WNGloss(int threshold, String meaninglessWords) {
        this.threshold = threshold;
        this.meaninglessWords = meaninglessWords;
    }


    /**
     * Computes the relations with WordNet gloss matcher.
     *
     * @param source gloss of source
     * @param target gloss of target
     * @return more general, less general or IDK relation
     */
    public char match(ISense source, ISense target) throws MatcherLibraryException {
        String sSynset = source.getGloss();
        String tSynset = target.getGloss();
        StringTokenizer stSource = new StringTokenizer(sSynset, " ,.\"'();");
        StringTokenizer stTarget = new StringTokenizer(tSynset, " ,.\"'();");
        String lemma;
        int counter = 0;
        while (stSource.hasMoreTokens()) {
            lemma = stSource.nextToken();
            if (!meaninglessWords.contains(lemma)) {
                List<String> lemmas = target.getLemmas();
                for (String lemmaToCompare : lemmas) {
                    if (lemma.equals(lemmaToCompare)) {
                        counter++;
                    }
                }
            }
        }
        if (counter >= threshold) {
            return IMappingElement.LESS_GENERAL;
        }

        while (stTarget.hasMoreTokens()) {
            lemma = stTarget.nextToken();
            if (!meaninglessWords.contains(lemma)) {
                List<String> lemmas = source.getLemmas();
                for (String lemmaToCompare : lemmas) {
                    if (lemma.equals(lemmaToCompare)) {
                        counter++;
                    }
                }
            }
        }
        if (counter >= threshold) {
            return IMappingElement.MORE_GENERAL;
        }
        return IMappingElement.IDK;
    }
}
