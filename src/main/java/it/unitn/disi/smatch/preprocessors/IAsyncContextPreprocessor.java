package it.unitn.disi.smatch.preprocessors;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IAsyncContextPreprocessor extends IContextPreprocessor {

    /**
     * This method translates natural language labels of a context into logical formulas asynchronously.
     *
     * @param context context to be preprocessed
     */
    AsyncTask<Void, INode> asyncPreprocess(IContext context);
}
