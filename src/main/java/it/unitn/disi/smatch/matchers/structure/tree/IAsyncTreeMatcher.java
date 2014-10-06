package it.unitn.disi.smatch.matchers.structure.tree;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IAsyncTreeMatcher extends ITreeMatcher {

    /**
     * Matches two trees.
     *
     * @param sourceContext source context
     * @param targetContext target context
     * @param acolMapping   mapping between context ACoLs
     * @return a mapping between context nodes
     */
    AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncTreeMatch(IContext sourceContext,
                                                                             IContext targetContext,
                                                                             IContextMapping<IAtomicConceptOfLabel> acolMapping);
}
