package it.unitn.disi.smatch.classifiers;

import it.unitn.disi.smatch.data.trees.IContext;

/**
 * A ContextClassifier that builds no concept@node formula.
 *
 * @since 2.0.0
 * @author <a rel="author" href="http://davidleoni.it/">David Leoni</a>
 */
public class ZeroContextClassifier extends BaseContextClassifier {

    @Override
    protected void process(IContext context) throws ContextClassifierException {          
    }                  
}