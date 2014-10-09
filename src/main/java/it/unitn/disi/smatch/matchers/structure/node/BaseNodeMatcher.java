package it.unitn.disi.smatch.matchers.structure.node;

import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.deciders.ISATSolver;
import it.unitn.disi.smatch.deciders.SATSolverException;

import java.util.*;

/**
 * Contains routines used by other matchers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseNodeMatcher {

    protected final ISATSolver satSolver;

    protected BaseNodeMatcher(ISATSolver satSolver) {
        this.satSolver = satSolver;
    }

    /**
     * Makes axioms for a CNF formula out of relations between atomic concepts.
     *
     * @param hashConceptNumber HashMap for atomic concept of labels with its id
     * @param sourceACoLs       acol id -> acol object
     * @param targetACoLs       acol id -> acol object
     * @param acolMapping       mapping between atomic concepts
     * @param sourceNode        source node
     * @param targetNode        target node
     * @return axiom string and axiom count
     */
    protected static Object[] mkAxioms(Map<IAtomicConceptOfLabel, String> hashConceptNumber,
                                       Map<String, IAtomicConceptOfLabel> sourceACoLs,
                                       Map<String, IAtomicConceptOfLabel> targetACoLs,
                                       IContextMapping<IAtomicConceptOfLabel> acolMapping,
                                       INode sourceNode, INode targetNode) {
        StringBuilder axioms = new StringBuilder();
        Integer numberOfClauses = 0;
        // create DIMACS variables for all concepts in the matching task
        createVariables(hashConceptNumber, sourceACoLs, sourceNode);
        createVariables(hashConceptNumber, targetACoLs, targetNode);

        for (Iterator<IAtomicConceptOfLabel> i = sourceNode.nodeData().pathToRootConceptIterator(); i.hasNext(); ) {
            IAtomicConceptOfLabel sourceACoL = i.next();
            for (Iterator<IAtomicConceptOfLabel> j = targetNode.nodeData().pathToRootConceptIterator(); j.hasNext(); ) {
                IAtomicConceptOfLabel targetACoL = j.next();
                char relation = acolMapping.getRelation(sourceACoL, targetACoL);
                if (IMappingElement.IDK != relation) {
                    //get the numbers of DIMACS variables corresponding to ACoLs
                    String sourceVarNumber = hashConceptNumber.get(sourceACoL);
                    String targetVarNumber = hashConceptNumber.get(targetACoL);
                    if (IMappingElement.LESS_GENERAL == relation) {
                        String tmp = "-" + sourceVarNumber + " " + targetVarNumber + " 0\n";
                        //if not already present add to axioms
                        if (-1 == axioms.indexOf(tmp)) {
                            axioms.append(tmp);
                            numberOfClauses++;
                        }
                    } else if (IMappingElement.MORE_GENERAL == relation) {
                        String tmp = sourceVarNumber + " -" + targetVarNumber + " 0\n";
                        if (-1 == axioms.indexOf(tmp)) {
                            axioms.append(tmp);
                            numberOfClauses++;
                        }
                    } else if (IMappingElement.EQUIVALENCE == relation) {
                        if (!sourceVarNumber.equals(targetVarNumber)) {
                            //add clauses for less and more generality
                            String tmp = "-" + sourceVarNumber + " " + targetVarNumber + " 0\n";
                            if (-1 == axioms.indexOf(tmp)) {
                                axioms.append(tmp);
                                numberOfClauses++;
                            }
                            tmp = sourceVarNumber + " -" + targetVarNumber + " 0\n";
                            if (-1 == axioms.indexOf(tmp)) {
                                axioms.append(tmp);
                                numberOfClauses++;
                            }
                        }
                    } else if (IMappingElement.DISJOINT == relation) {
                        String tmp = "-" + sourceVarNumber + " -" + targetVarNumber + " 0\n";
                        if (-1 == axioms.indexOf(tmp)) {
                            axioms.append(tmp);
                            numberOfClauses++;
                        }
                    }
                }
            }
        }
        return new Object[]{axioms.toString(), numberOfClauses};
    }

    /**
     * Creates DIMACS variables for all concepts in the node matching task.
     *
     * @param hashConceptNumber acol -> variable number
     * @param acolsMap          acol id -> acol
     * @param node              node
     */
    private static void createVariables(Map<IAtomicConceptOfLabel, String> hashConceptNumber,
                                        Map<String, IAtomicConceptOfLabel> acolsMap, INode node) {
        cacheACoLs(acolsMap, node);
        for (Iterator<IAtomicConceptOfLabel> i = node.nodeData().pathToRootConceptIterator(); i.hasNext(); ) {
            IAtomicConceptOfLabel acol = i.next();
            // create corresponding to id variable number
            // and put it as a value of hash table with key equal to ACoL
            if (!hashConceptNumber.containsKey(acol)) {
                hashConceptNumber.put(acol, Integer.toString(hashConceptNumber.size() + 1));
            }
        }
    }

    private static void cacheACoLs(Map<String, IAtomicConceptOfLabel> acolsMap, INode node) {
        // without concepts can't check the map, so by default check path to root
        boolean cached = isNodeCached(acolsMap, node);
        if (!cached) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (node) {
                cached = isNodeCached(acolsMap, node);
                if (!cached) {
                    // ensure the nodes above are cached
                    if (null != node.getParent()) {
                        cacheACoLs(acolsMap, node.getParent());
                    }

                    // cache acol ids for node
                    for (IAtomicConceptOfLabel acol : node.nodeData().getConcepts()) {
                        acolsMap.put(node.nodeData().getId() + "_" + Integer.toString(acol.getId()), acol);
                    }
                }
            }
        }
    }

    private static boolean isNodeCached(Map<String, IAtomicConceptOfLabel> acolsMap, INode node) {
        boolean cached;
        if (node.nodeData().getConcepts().isEmpty()) {
            cached = false;
        } else {
            // if last acol cached - all preceding are cached too
            String key = node.nodeData().getId() +
                    "_" + Integer.toString(node.nodeData().getConcepts().get(node.nodeData().getConcepts().size() - 1).getId());
            cached = acolsMap.containsKey(key);
        }
        return cached;
    }

    /**
     * Parses a c@node formula replacing references to concepts with references to the DIMACS variables. Uses and depends
     * on CNF representation which is "conjunction of disjunctions",  that is the first level list represents
     * conjunction of second-level lists representing disjunction clauses.
     *
     * @param hashConceptNumber HashMap acol -> variable number
     * @param acolsMap          map with acol id -> acol mapping
     * @param node              node
     * @return formula with DIMACS variables
     */
    protected ArrayList<ArrayList<String>> parseFormula(Map<IAtomicConceptOfLabel, String> hashConceptNumber,
                                                        Map<String, IAtomicConceptOfLabel> acolsMap, INode node) {
        ArrayList<ArrayList<String>> representation = new ArrayList<>();
        boolean saved_negation = false;
        for (StringTokenizer clauseTokenizer = new StringTokenizer(node.nodeData().getNodeFormula(), "&"); clauseTokenizer.hasMoreTokens(); ) {
            String clause = clauseTokenizer.nextToken();
            ArrayList<String> clause_vec = new ArrayList<>();
            for (StringTokenizer varTokenizer = new StringTokenizer(clause, "|() "); varTokenizer.hasMoreTokens(); ) {
                String var = varTokenizer.nextToken();
                boolean negation = false;
                if (var.startsWith("~")) {
                    negation = true;
                    var = var.substring(1);
                }
                if (var.length() < 2) {
                    saved_negation = true;
                    continue;
                }
                String var_num = hashConceptNumber.get(acolsMap.get(var));
                if (negation || saved_negation) {
                    saved_negation = false;
                    var_num = "-" + var_num;
                }
                clause_vec.add(var_num);
            }
            representation.add(clause_vec);
        }
        return representation;
    }

    /**
     * Converts parsed formula into DIMACS format.
     *
     * @param formula parsed formula
     * @return formula in DIMACS format
     */
    protected static String DIMACSfromList(ArrayList<ArrayList<String>> formula) {
        StringBuilder dimacs = new StringBuilder("");
        for (List<String> conjClause : formula) {
            for (String disjClause : conjClause) {
                dimacs.append(disjClause).append(" ");
            }
            dimacs.append(" 0\n");
        }
        return dimacs.toString();
    }

    protected static int negateFormulaInList(Map<IAtomicConceptOfLabel, String> hashConceptNumber, ArrayList<ArrayList<String>> pivot, ArrayList<ArrayList<String>> result) {
        result.clear();
        ArrayList<String> firstClause = new ArrayList<>();
        int numberOfVariables = hashConceptNumber.size();
        for (ArrayList<String> v : pivot) {
            if (v.size() == 1) {
                firstClause.add(changeSign(v.get(0)));
            }
            if (v.size() > 1) {
                numberOfVariables++;
                final String lsn = Integer.toString(numberOfVariables);
                final String negatedLSN = "-" + lsn;
                firstClause.add(negatedLSN);
                ArrayList<String> longClause = new ArrayList<>();
                longClause.add(negatedLSN);
                for (String var : v) {
                    longClause.add(var);
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(lsn);
                    tmp.add(changeSign(var));
                    result.add(tmp);
                }
                result.add(longClause);
            }
        }
        if (firstClause.size() > 0) {
            result.add(firstClause);
        }
        return numberOfVariables;
    }

    protected boolean isUnsatisfiable(String satProblem) throws NodeMatcherException {
        try {
            return !satSolver.isSatisfiable(satProblem);
        } catch (SATSolverException e) {
            throw new NodeMatcherException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    protected static char getRelationString(boolean isContains, boolean isContained, boolean isOpposite) {
        //return the tests results
        if (isOpposite) {
            //The concepts have opposite meaning
            return IMappingElement.DISJOINT;
        }
        if (isContains && isContained) {
            //The concepts are equivalent
            return IMappingElement.EQUIVALENCE;
        }
        if (isContained) {
            //The source concept is LG the target concept
            return IMappingElement.LESS_GENERAL;
        }
        if (isContains) {
            //The target concept is LG the source concept
            return IMappingElement.MORE_GENERAL;
        }
        return IMappingElement.IDK;
    }

    protected static String changeSign(String strClause) {
        if ('-' == strClause.charAt(0)) {
            strClause = strClause.substring(1);
        } else {
            strClause = "-" + strClause;
        }
        return strClause;
    }
}
