package it.unitn.disi.smatch.matchers.element;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.IContext;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IAsyncElementMatcher extends IElementMatcher {

    /**
     * Performs Step 3 of semantic matching algorithm.
     *
     * @param sourceContext interface of source context
     * @param targetContext interface of target context
     * @return mapping between atomic concepts in both contexts
     */
    AsyncTask<IContextMapping<IAtomicConceptOfLabel>, IMappingElement<IAtomicConceptOfLabel>>
    asyncElementLevelMatching(IContext sourceContext, IContext targetContext);
}
