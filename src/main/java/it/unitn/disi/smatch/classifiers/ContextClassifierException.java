package it.unitn.disi.smatch.classifiers;

import it.unitn.disi.smatch.SMatchException;

/**
 * Exception for Context Classifiers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ContextClassifierException extends SMatchException {

    public ContextClassifierException(String errorDescription) {
        super(errorDescription);
    }

    public ContextClassifierException(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }
}
