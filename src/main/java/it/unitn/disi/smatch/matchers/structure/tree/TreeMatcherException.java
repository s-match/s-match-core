package it.unitn.disi.smatch.matchers.structure.tree;

import it.unitn.disi.smatch.SMatchException;

/**
 * Exception for Tree Matchers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class TreeMatcherException extends SMatchException {

    public TreeMatcherException(String errorDescription) {
        super(errorDescription);
    }

    public TreeMatcherException(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }
}
