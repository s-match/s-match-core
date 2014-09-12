package it.unitn.disi.smatch.matchers.element;

import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.oracles.LinguisticOracleException;

import java.util.List;

/**
 * Implements WNHierarchy matcher. See Element Level Semantic matchers paper for more details. Accepts depth integer
 * parameter, which by default equals 2.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class WNHierarchy implements ISenseGlossBasedElementLevelSemanticMatcher {

    private final int depth;

    public WNHierarchy() {
        depth = 2;
    }

    public WNHierarchy(int depth) {
        this.depth = depth;
    }

    /**
     * Matches two senses with WNHierarchy matcher.
     *
     * @param source gloss of source label
     * @param target gloss of target label
     * @return synonym or IDK relation
     */
    public char match(ISense source, ISense target) throws MatcherLibraryException {
        List<ISense> sourceList = getAncestors(source, depth);
        List<ISense> targetList = getAncestors(target, depth);
        targetList.retainAll(sourceList);
        if (targetList.size() > 0)
            return IMappingElement.EQUIVALENCE;
        else
            return IMappingElement.IDK;
    }

    private List<ISense> getAncestors(ISense node, int depth) throws MatcherLibraryException {
        try {
            return node.getParents(depth);
        } catch (LinguisticOracleException e) {
            throw new MatcherLibraryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }
}