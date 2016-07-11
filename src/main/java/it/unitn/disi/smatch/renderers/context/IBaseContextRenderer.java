package it.unitn.disi.smatch.renderers.context;

import it.unitn.disi.smatch.data.trees.IBaseContext;
import it.unitn.disi.smatch.data.trees.IBaseNode;
import it.unitn.disi.smatch.loaders.ILoader;

/**
 * Base interface for context renderers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IBaseContextRenderer<E extends IBaseContext<T>, T extends IBaseNode> {

    /**
     * Renders context into file or database.
     *
     * @param context  context to save
     * @param location context location
     * @throws ContextRendererException ContextRendererException
     */
    void render(E context, String location) throws ContextRendererException;

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