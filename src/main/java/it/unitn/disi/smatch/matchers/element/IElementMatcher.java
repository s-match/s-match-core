package it.unitn.disi.smatch.matchers.element;

import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.IContext;

/**
 * Interface for collections of matchers, which perform element-level matching.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IElementMatcher {

    /**
     * Performs Step 3 of semantic matching algorithm.
     *
     * @param sourceContext interface of source context
     * @param targetContext interface of target context
     * @return mapping between atomic concepts in both contexts
     * @throws ElementMatcherException ElementMatcherException
     */
    IContextMapping<IAtomicConceptOfLabel> elementLevelMatching(IContext sourceContext, IContext targetContext) throws ElementMatcherException;
}
