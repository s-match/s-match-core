package it.unitn.disi.smatch.matchers.element;

import it.unitn.disi.smatch.SMatchException;

/**
 * Exception for Matcher Libraries.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MatcherLibraryException extends SMatchException {

    public MatcherLibraryException(String errorDescription) {
        super(errorDescription);
    }

    public MatcherLibraryException(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }
}
