package it.unitn.disi.smatch.loaders.context;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.trees.IBaseContext;
import it.unitn.disi.smatch.data.trees.IBaseNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Base class for loaders.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseContextLoader<E extends IBaseContext<T>, T extends IBaseNode> extends AsyncTask<E, T> implements IBaseContextLoader<E, T> {

    private static final Logger log = LoggerFactory.getLogger(BaseContextLoader.class);

    // for task parameters
    protected final String location;

    protected BaseContextLoader() {
        this.location = null;
    }

    protected BaseContextLoader(String location) {
        this.location = location;
    }

    protected void createIds(E result) {
        if (null != result) {
            log.debug("Creating ids for context...");
            int nodesParsed = 0;
            for (Iterator<? extends IBaseNode> i = result.nodeIterator(); i.hasNext(); ) {
                i.next().nodeData().setId("n" + Integer.toString(nodesParsed));
                nodesParsed++;
            }
        }
    }

    @Override
    protected E doInBackground() throws Exception {
        final String threadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(Thread.currentThread().getName()
                    + " [" + this.getClass().getSimpleName() + ": location=" + location + "]");

            return loadContext(location);
        } finally {
            Thread.currentThread().setName(threadName);
        }

    }
}