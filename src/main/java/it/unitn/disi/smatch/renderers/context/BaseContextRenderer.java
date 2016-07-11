package it.unitn.disi.smatch.renderers.context;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.trees.IBaseContext;
import it.unitn.disi.smatch.data.trees.IBaseNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for context renderers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseContextRenderer<E extends IBaseContext<T>, T extends IBaseNode> extends AsyncTask<Void, T> implements IBaseContextRenderer<E, T> {

    private static final Logger log = LoggerFactory.getLogger(BaseContextRenderer.class);

    protected final boolean sort;

    // for task parameters
    protected final String location;
    protected final E context;

    protected BaseContextRenderer() {
        this.location = null;
        this.context = null;
        this.sort = false;
    }

    protected BaseContextRenderer(boolean sort) {
        this.location = null;
        this.context = null;
        this.sort = sort;
    }

    protected BaseContextRenderer(String location, E context) {
        this.location = location;
        if (null == context) {
            throw new IllegalArgumentException("context is required!");
        }
        this.context = context;
        this.sort = false;
        setTotal(context.nodesCount());
    }

    protected BaseContextRenderer(String location, E context, boolean sort) {
        this.location = location;
        if (null == context) {
            throw new IllegalArgumentException("context is required!");
        }
        this.context = context;
        this.sort = sort;
        setTotal(context.nodesCount());
    }

    public void render(E context, String location) throws ContextRendererException {
        process(context, location);

        if (log.isInfoEnabled()) {
            log.info("Rendered nodes: " + getProgress());
        }
    }

    protected abstract void process(E context, String fileName) throws ContextRendererException;

    @Override
    protected Void doInBackground() throws Exception {
        final String threadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(Thread.currentThread().getName()
                    + " [" + this.getClass().getSimpleName()
                    + ": context.size=" + context.nodesCount()
                    + ", location=" + location + "]");

            render(context, location);
            return null;
        } finally {
            Thread.currentThread().setName(threadName);
        }
    }
}