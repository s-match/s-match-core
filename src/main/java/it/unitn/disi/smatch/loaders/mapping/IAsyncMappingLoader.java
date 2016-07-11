package it.unitn.disi.smatch.loaders.mapping;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IAsyncMappingLoader extends IMappingLoader {

    /**
     * Loads the mapping from a file or database asynchronously.
     *
     * @param source   interface of data structure of source
     * @param target   interface of data structure of target
     * @param fileName file with a mapping
     * @return loading task
     */
    AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncLoad(IContext source, IContext target, String fileName);
}
