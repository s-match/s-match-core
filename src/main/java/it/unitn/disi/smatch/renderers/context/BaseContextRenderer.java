package it.unitn.disi.smatch.renderers.context;

import it.unitn.disi.smatch.data.trees.IBaseContext;
import it.unitn.disi.smatch.data.util.ProgressContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for context renderers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseContextRenderer<E extends IBaseContext> implements IBaseContextRenderer<E> {

    private static final Logger log = LoggerFactory.getLogger(BaseContextRenderer.class);

    protected final boolean sort;

    protected BaseContextRenderer() {
        this.sort = false;
    }

    protected BaseContextRenderer(boolean sort) {
        this.sort = sort;
    }

    public void render(E context, String location) throws ContextRendererException {
        ProgressContainer progressContainer = new ProgressContainer(context.getNodesList().size(), log);

        process(context, location, progressContainer);

        reportStats(context);
    }

    protected abstract void process(E context, String fileName, ProgressContainer progressContainer) throws ContextRendererException;

    protected void reportStats(E context) {
        if (log.isInfoEnabled()) {
            log.info("Rendered nodes: " + context.getNodesList().size());
        }
    }
}