package it.unitn.disi.smatch.preprocessors;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * Base class for context preprocessors.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseContextPreprocessor extends AsyncTask<Void, INode> implements IContextPreprocessor {

    // for task parameters
    protected final IContext context;

    public BaseContextPreprocessor() {
        this.context = null;
    }

    public BaseContextPreprocessor(IContext context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground() throws Exception {
        final String threadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(Thread.currentThread().getName()
                    + " [" + this.getClass().getSimpleName() + ": context.size=" + context.nodesCount() + "]");

            preprocess(context);
            return null;
        } finally {
            Thread.currentThread().setName(threadName);
        }
    }
}