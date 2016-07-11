package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IAsyncMappingRenderer extends IMappingRenderer {

    /**
     * Renders the mapping using a current mapping renderer.
     *
     * @param mapping  a mapping
     * @param location a render destination passed to the mapping renderer
     * @return async task instance
     */
    AsyncTask<Void, IMappingElement<INode>> asyncRender(IContextMapping<INode> mapping, String location);
}