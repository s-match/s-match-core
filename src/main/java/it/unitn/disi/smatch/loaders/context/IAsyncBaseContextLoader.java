package it.unitn.disi.smatch.loaders.context;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.trees.IBaseContext;
import it.unitn.disi.smatch.data.trees.IBaseNode;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IAsyncBaseContextLoader<E extends IBaseContext<T>, T extends IBaseNode> extends IBaseContextLoader<E, T> {

    /**
     * Loads the context from a file or database asynchronously.
     *
     * @param location context location
     * @return loading task
     */
    AsyncTask<E, T> asyncLoad(String location);
}
