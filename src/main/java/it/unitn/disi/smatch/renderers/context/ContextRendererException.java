package it.unitn.disi.smatch.renderers.context;

import it.unitn.disi.smatch.SMatchException;

/**
 * Exception for Context Renderers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ContextRendererException extends SMatchException {

    public ContextRendererException(String errorDescription) {
        super(errorDescription);
    }

    public ContextRendererException(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }
}
