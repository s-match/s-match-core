package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.ILoader;

/**
 * Renders nothing.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ZeroMappingRenderer extends AsyncTask<Void, IMappingElement<INode>> implements IMappingRenderer, IAsyncMappingRenderer {

    public void render(IContextMapping<INode> mapping, String location) throws MappingRendererException {
        //does nothing
    }

    public String getDescription() {
        return ILoader.TXT_FILES;
    }

    public ILoader.LoaderType getType() {
        return ILoader.LoaderType.FILE;
    }

    @Override
    public AsyncTask<Void, IMappingElement<INode>> asyncRender(IContextMapping<INode> mapping, String location) {
        return new ZeroMappingRenderer();
    }

    @Override
    protected Void doInBackground() throws Exception {
        return null;
    }
}