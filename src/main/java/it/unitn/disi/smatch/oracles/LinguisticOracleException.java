package it.unitn.disi.smatch.oracles;

import it.unitn.disi.smatch.SMatchException;

/**
 * Exception for Linguistic Oracles.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class LinguisticOracleException extends SMatchException {

    public LinguisticOracleException(String errorDescription) {
        super(errorDescription);
    }

    public LinguisticOracleException(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }
}
