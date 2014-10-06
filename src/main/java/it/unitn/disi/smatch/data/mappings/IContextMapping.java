package it.unitn.disi.smatch.data.mappings;

import it.unitn.disi.smatch.data.trees.IContext;

/**
 * Interface for context mappings.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IContextMapping<T> extends IMapping<T> {

    IContext getSourceContext();

    IContext getTargetContext();
}
