package it.unitn.disi.smatch.renderers.context;

import it.unitn.disi.smatch.data.trees.IBaseContext;
import it.unitn.disi.smatch.data.trees.IBaseNode;
import it.unitn.disi.smatch.loaders.ILoader;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Base class for file context renderers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseFileContextRenderer<E extends IBaseContext<T>, T extends IBaseNode> extends BaseContextRenderer<E, T> {

    protected BaseFileContextRenderer() {
        super();
    }

    protected BaseFileContextRenderer(boolean sort) {
        super(sort);
    }

    protected BaseFileContextRenderer(String location, E context) {
        super(location, context);
    }

    protected BaseFileContextRenderer(String location, E context, boolean sort) {
        super(location, context, sort);
    }

    @Override
    protected void process(E context, String fileName) throws ContextRendererException {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"))) {
            process(context, out);
        } catch (IOException e) {
            throw new ContextRendererException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    protected abstract void process(E context, BufferedWriter out) throws IOException, ContextRendererException;

    public ILoader.LoaderType getType() {
        return ILoader.LoaderType.FILE;
    }
}