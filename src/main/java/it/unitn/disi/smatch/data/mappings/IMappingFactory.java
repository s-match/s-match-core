package it.unitn.disi.smatch.data.mappings;

import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * Produces mappings.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMappingFactory {

    IContextMapping<INode> getContextMappingInstance(IContext source, IContext target);

    IContextMapping<IAtomicConceptOfLabel> getACoLMappingInstance(IContext source, IContext target);
}
