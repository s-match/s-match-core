package it.unitn.disi.smatch.deciders;

/**
 * Each SAT solver needs to implement only one method,
 * which takes as an input DIMACS string and returns true if it is satisfiable.
 * <p/>
 * DIMACS format is described in the note:
 * DIMACS Challenge - Satisfiability - Suggested Format, which can be found for example here:
 * <a href="http://www.domagoj-babic.com/uploads/ResearchProjects/Spear/dimacs-cnf.pdf">
 * http://www.domagoj-babic.com/uploads/ResearchProjects/Spear/dimacs-cnf.pdf</a>
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface ISATSolver {

    /**
     * Checks whether input string in DIMACS format is satisfiable or not.
     *
     * @param input problem in DIMACS format
     * @return whether problem is satisfiable or not
     * @throws SATSolverException SATSolverException
     */
    boolean isSatisfiable(String input) throws SATSolverException;
}
