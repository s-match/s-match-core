package it.unitn.disi.smatch.loaders.context;

import it.unitn.disi.smatch.SMatchException;

/**
 * Exception for Context Loaders.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ContextLoaderException extends SMatchException {

    public ContextLoaderException(String errorDescription) {
        super(errorDescription);
    }

    public ContextLoaderException(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }
}
