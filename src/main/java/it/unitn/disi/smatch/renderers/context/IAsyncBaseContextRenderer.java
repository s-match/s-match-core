package it.unitn.disi.smatch.renderers.context;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.trees.IBaseContext;
import it.unitn.disi.smatch.data.trees.IBaseNode;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IAsyncBaseContextRenderer<E extends IBaseContext<T>, T extends IBaseNode> extends IBaseContextRenderer<E, T> {

    /**
     * Renders context into file or database asynchronously.
     *
     * @param context  context to save
     * @param location context location
     */
    AsyncTask<Void, T> asyncRender(E context, String location);
}