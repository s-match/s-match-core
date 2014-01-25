package it.unitn.disi.smatch;

import it.unitn.disi.common.components.ConfigurableException;

/**
 * Main S-Match exception.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class SMatchException extends ConfigurableException {

    /**
     * Constructor.
     * Creates a new Exception by using super(msg) method.
     *
     * @param errorDescription the description of the error
     */
    public SMatchException(String errorDescription) {
        super(errorDescription);
    }

    /**
     * Constructor.
     * Creates a new Exception by using super(msg, cause) method.
     *
     * @param errorDescription the description of the error
     * @param cause            the cause
     */
    public SMatchException(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }
}
