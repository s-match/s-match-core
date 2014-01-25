package it.unitn.disi.smatch.preprocessors;

import it.unitn.disi.smatch.SMatchException;

/**
 * Exception for Context Preprocessors.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ContextPreprocessorException extends SMatchException {

    public ContextPreprocessorException(String errorDescription) {
        super(errorDescription);
    }

    public ContextPreprocessorException(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }
}
