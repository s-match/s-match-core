package it.unitn.disi.smatch.deciders;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SAT solver which caches answers. Observed cache hit rates vary from 70% on small (dozens of nodes) matching tasks
 * to 99% on large (hundreds of nodes) tasks.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class CachingSolver implements ISATSolver {

    protected final ISATSolver satSolver;

    private final Map<String, Boolean> solutionsCache = new ConcurrentHashMap<>();

    public CachingSolver(ISATSolver satSolver) {
        this.satSolver = satSolver;
    }

    /**
     * Calls the solver and caches the answer.
     *
     * @param input The String that contains sat problem in DIMACS format
     * @return boolean True if the formula is satisfiable, false otherwise
     * @throws SATSolverException SATSolverException
     */
    public boolean isSatisfiable(String input) throws SATSolverException {
        Boolean result = solutionsCache.get(input);
        if (null == result) {
            result = satSolver.isSatisfiable(input);
            solutionsCache.put(input, result);
        }
        return result;
    }
}