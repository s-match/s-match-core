package it.unitn.disi.smatch.classifiers;

import it.unitn.disi.smatch.data.trees.IContext;

/**
 * Interface for classifiers.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IContextClassifier {

    /**
     * Constructs concept@node formulas for all the nodes in the context.
     *
     * @param context the context with concept at label formulas
     * @throws ContextClassifierException ContextClassifierException
     */
    void buildCNodeFormulas(IContext context) throws ContextClassifierException;
}
