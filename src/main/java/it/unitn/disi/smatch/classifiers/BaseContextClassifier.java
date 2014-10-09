package it.unitn.disi.smatch.classifiers;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * Base class for context classifiers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseContextClassifier extends AsyncTask<Void, INode> implements IContextClassifier {

    // for task parameters
    protected final IContext context;

    protected BaseContextClassifier() {
        this.context = null;
    }

    protected BaseContextClassifier(IContext context) {
        this.context = context;
        setTotal(context.nodesCount());
    }

    public void buildCNodeFormulas(IContext context) throws ContextClassifierException {
        process(context);
    }

    protected abstract void process(IContext context) throws ContextClassifierException;

    @Override
    protected Void doInBackground() throws Exception {
        final String threadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(Thread.currentThread().getName()
                    + " [" + this.getClass().getSimpleName() + ": context.size=" + context.nodesCount() + "]");
            buildCNodeFormulas(context);
            return null;
        } finally {
            Thread.currentThread().setName(threadName);
        }
    }
}