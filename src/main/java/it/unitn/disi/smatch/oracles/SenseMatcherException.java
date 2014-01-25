package it.unitn.disi.smatch.oracles;

import it.unitn.disi.smatch.SMatchException;

/**
 * Exception for Linguistic Oracles.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class SenseMatcherException extends SMatchException {

    public SenseMatcherException(String errorDescription) {
        super(errorDescription);
    }

    public SenseMatcherException(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }
}
