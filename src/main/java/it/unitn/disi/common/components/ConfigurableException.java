package it.unitn.disi.common.components;

import it.unitn.disi.common.DISIException;

/**
 * Exception for Configurables.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ConfigurableException extends DISIException {

    public ConfigurableException(String errorDescription) {
        super(errorDescription);
    }

    public ConfigurableException(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }
}
