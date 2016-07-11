package it.unitn.disi.common;

/**
 * Exception for common components.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class DISIException extends Exception {

    /**
     * Constructor.
     * Creates a new Exception by using super(msg) method.
     *
     * @param errorDescription the description of the error
     */
    public DISIException(String errorDescription) {
        super(errorDescription);
    }

    /**
     * Constructor.
     * Creates a new Exception by using super(msg, cause) method.
     *
     * @param errorDescription the description of the error
     * @param cause            the cause
     */
    public DISIException(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }
}
