package it.unitn.disi.smatch.matchers.structure.tree;

import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * An interface for tree matchers.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface ITreeMatcher {

    /**
     * Matches two trees.
     *
     * @param sourceContext source context
     * @param targetContext target context
     * @param acolMapping   mapping between context ACoLs
     * @return a mapping between context nodes
     * @throws TreeMatcherException TreeMatcherException
     */
    IContextMapping<INode> treeMatch(IContext sourceContext, IContext targetContext, IContextMapping<IAtomicConceptOfLabel> acolMapping) throws TreeMatcherException;
}
