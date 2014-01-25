package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.common.components.IConfigurable;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.ILoader;

/**
 * An interface for mapping renderers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMappingRenderer extends IConfigurable {

    /**
     * Saves the mapping into a file.
     *
     * @param mapping    a mapping to render
     * @param outputFile an output file or DB connection
     * @throws MappingRendererException MappingRendererException
     */
    void render(IContextMapping<INode> mapping, String outputFile) throws MappingRendererException;

    /**
     * Returns the description of the format.
     *
     * @return the description of the format
     */
    String getDescription();

    /**
     * Returns the type of the renderer.
     *
     * @return the type of the renderer
     */
    ILoader.LoaderType getType();
}
