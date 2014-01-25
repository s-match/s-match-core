package it.unitn.disi.smatch.loaders.mapping;

import it.unitn.disi.smatch.SMatchException;

/**
 * Exception for Mapping Loaders.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MappingLoaderException extends SMatchException {

    public MappingLoaderException(String errorDescription) {
        super(errorDescription);
    }

    public MappingLoaderException(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }
}
