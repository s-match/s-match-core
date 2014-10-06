package it.unitn.disi.smatch.classifiers;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IAsyncContextClassifier extends IContextClassifier {

    /**
     * Constructs concept@node formulas for all the nodes in the context.
     *
     * @param context the context with concept at label formulas
     */
    AsyncTask<Void, INode> asyncClassify(IContext context);
}
