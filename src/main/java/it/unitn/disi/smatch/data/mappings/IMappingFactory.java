package it.unitn.disi.smatch.data.mappings;

import it.unitn.disi.common.components.IConfigurable;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * Produces mappings.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMappingFactory extends IConfigurable {

    IContextMapping<INode> getContextMappingInstance(IContext source, IContext target);

    IContextMapping<IAtomicConceptOfLabel> getACoLMappingInstance(IContext source, IContext target);
}
