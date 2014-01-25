package it.unitn.disi.smatch.matchers.structure.node;

import it.unitn.disi.common.components.Configurable;
import it.unitn.disi.common.components.ConfigurableException;
import it.unitn.disi.common.components.ConfigurationKeyMissingException;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.deciders.ISATSolver;
import it.unitn.disi.smatch.deciders.SATSolverException;

import java.util.*;

/**
 * Contains routines used by other matchers. Needs SATSolver configuration parameter pointing to a class implementing
 * {@link it.unitn.disi.smatch.deciders.ISATSolver} to solve SAT problems.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class BaseNodeMatcher extends Configurable {

    private static final String SAT_SOLVER_KEY = "SATSolver";
    protected ISATSolver satSolver = null;

    @Override
    public boolean setProperties(Properties newProperties) throws ConfigurableException {
        Properties oldProperties = new Properties();
        oldProperties.putAll(properties);

        boolean result = super.setProperties(newProperties);
        if (result) {
            if (newProperties.containsKey(SAT_SOLVER_KEY)) {
                satSolver = (ISATSolver) configureComponent(satSolver, oldProperties, newProperties, "SAT solver", SAT_SOLVER_KEY, ISATSolver.class);
            } else {
                throw new ConfigurationKeyMissingException(SAT_SOLVER_KEY);
            }
        }
        return result;
    }

    /**
     * Makes axioms for a CNF formula out of relations between atomic concepts.
     *
     * @param hashConceptNumber HashMap for atomic concept of labels with its id
     * @param nmtAcols          node -> list of node matching task acols
     * @param sourceACoLs       acol id -> acol object
     * @param targetACoLs       acol id -> acol object
     * @param acolMapping       mapping between atomic concepts
     * @param sourceNode        source node
     * @param targetNode        target node
     * @return axiom string and axiom count
     */
    protected static Object[] mkAxioms(HashMap<IAtomicConceptOfLabel, String> hashConceptNumber,
                                       Map<INode, ArrayList<IAtomicConceptOfLabel>> nmtAcols,
                                       Map<String, IAtomicConceptOfLabel> sourceACoLs,
                                       Map<String, IAtomicConceptOfLabel> targetACoLs,
                                       IContextMapping<IAtomicConceptOfLabel> acolMapping,
                                       INode sourceNode, INode targetNode) {
        StringBuilder axioms = new StringBuilder();
        Integer numberOfClauses = 0;
        // create DIMACS variables for all acols in the matching task
        createVariables(hashConceptNumber, nmtAcols, sourceACoLs, sourceNode);
        createVariables(hashConceptNumber, nmtAcols, targetACoLs, targetNode);

        ArrayList<IAtomicConceptOfLabel> sourceACols = nmtAcols.get(sourceNode);
        ArrayList<IAtomicConceptOfLabel> targetACols = nmtAcols.get(targetNode);
        if (null != sourceACols && null != targetACols) {
            for (IAtomicConceptOfLabel sourceACoL : sourceACols) {
                for (IAtomicConceptOfLabel targetACoL : targetACols) {
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
        }
        return new Object[]{axioms.toString(), numberOfClauses};
    }

    private static void createVariables(HashMap<IAtomicConceptOfLabel, String> hashConceptNumber,
                                        Map<INode, ArrayList<IAtomicConceptOfLabel>> nmtAcols,
                                        Map<String, IAtomicConceptOfLabel> acolsMap, INode node) {
        // creates DIMACS variables for all concepts in the node matching task
        ArrayList<IAtomicConceptOfLabel> acols = nmtAcols.get(node);
        if (null == acols) {
            // create acols list and cache it

            // count acols to allocate properly sized list
            int acolCount = 0;
            INode curNode = node;
            while (null != curNode) {
                acolCount = acolCount + curNode.getNodeData().getACoLCount();
                curNode = curNode.getParent();
            }

            // collect acols
            acols = new ArrayList<IAtomicConceptOfLabel>(acolCount);
            curNode = node;
            while (null != curNode) {
                acols.addAll(curNode.getNodeData().getACoLsList());
                curNode = curNode.getParent();
            }
            nmtAcols.put(node, acols);

            // cache also acol ids for node - for the nodes above it they should be cached already
            if (node != null) {
                for (IAtomicConceptOfLabel acol : node.getNodeData().getACoLsList()) {
                    acolsMap.put(node.getNodeData().getId() + "." + Integer.toString(acol.getId()), acol);
                }
            }
        }
        for (IAtomicConceptOfLabel sourceACoL : acols) {
            // create corresponding to id variable number
            // and put it as a value of hash table with key equal to ACoL
            if (!hashConceptNumber.containsKey(sourceACoL)) {
                hashConceptNumber.put(sourceACoL, Integer.toString(hashConceptNumber.size() + 1));
            }
        }
    }

    /**
     * Parses a c@node formula replacing references to acols with references to the DIMACS variables. Uses and depends
     * on CNF representation which is "conjunction of disjunctions",  that is the first level list represents
     * conjunction of second-level lists representing disjunction clauses.
     *
     * @param hashConceptNumber HashMap acol -> variable number
     * @param acolsMap          map with acol id -> acol mapping
     * @param node              node
     * @return formula with DIMACS variables
     */
    protected ArrayList<ArrayList<String>> parseFormula(HashMap<IAtomicConceptOfLabel, String> hashConceptNumber,
                                                        Map<String, IAtomicConceptOfLabel> acolsMap, INode node) {
        ArrayList<ArrayList<String>> representation = new ArrayList<ArrayList<String>>();
        boolean saved_negation = false;
        for (StringTokenizer clauseTokenizer = new StringTokenizer(node.getNodeData().getcNodeFormula(), "&"); clauseTokenizer.hasMoreTokens(); ) {
            String clause = clauseTokenizer.nextToken();
            ArrayList<String> clause_vec = new ArrayList<String>();
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

    protected static int negateFormulaInList(HashMap<IAtomicConceptOfLabel, String> hashConceptNumber, ArrayList<ArrayList<String>> pivot, ArrayList<ArrayList<String>> result) {
        result.clear();
        ArrayList<String> firstClause = new ArrayList<String>();
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
                ArrayList<String> longClause = new ArrayList<String>();
                longClause.add(negatedLSN);
                for (String var : v) {
                    longClause.add(var);
                    ArrayList<String> tmp = new ArrayList<String>();
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
