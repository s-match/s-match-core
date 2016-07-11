package it.unitn.disi.smatch.deciders;

import it.unitn.disi.smatch.SMatchException;

/**
 * Exception for SAT Solvers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class SATSolverException extends SMatchException {

    public SATSolverException(String errorDescription) {
        super(errorDescription);
    }

    public SATSolverException(String errorDescription, Throwable cause) {
        super(errorDescription, cause);
    }
}
