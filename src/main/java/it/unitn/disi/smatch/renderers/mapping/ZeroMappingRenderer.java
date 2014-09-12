package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.ILoader;

/**
 * Renders nothing.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ZeroMappingRenderer implements IMappingRenderer {

    public void render(IContextMapping<INode> mapping, String outputFile) throws MappingRendererException {
        //does nothing
    }

    public String getDescription() {
        return ILoader.TXT_FILES;
    }

    public ILoader.LoaderType getType() {
        return ILoader.LoaderType.FILE;
    }
}
