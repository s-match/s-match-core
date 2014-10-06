package it.unitn.disi.smatch.loaders.context;

import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * Asynchronous version of the {@link it.unitn.disi.smatch.IMatchManager IMatchManager}.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IAsyncContextLoader extends IAsyncBaseContextLoader<IContext, INode> {
}
