package it.unitn.disi.smatch.matchers.structure.node;

import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.deciders.ISATSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Node matcher for {@link it.unitn.disi.smatch.matchers.structure.tree.mini.OptimizedStageTreeMatcher} for minimal links
 * matching. For comments on the code see {@link it.unitn.disi.smatch.matchers.structure.node.DefaultNodeMatcher}
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class OptimizedStageNodeMatcher extends BaseNodeMatcher implements INodeMatcher {

    public OptimizedStageNodeMatcher(ISATSolver satSolver) {
        super(satSolver);
    }

    // support INodeMatcher to allow casts
    @Override
    public char nodeMatch(IContextMapping<IAtomicConceptOfLabel> acolMapping, Map<String, IAtomicConceptOfLabel> sourceACoLs, Map<String, IAtomicConceptOfLabel> targetACoLs, INode sourceNode, INode targetNode) throws NodeMatcherException {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks whether source node and target node are disjoint.
     *
     * @param acolMapping mapping between concepts
     * @param sourceACoLs mapping acol id -> acol object
     * @param targetACoLs mapping acol id -> acol object
     * @param sourceNode  interface of source node
     * @param targetNode  interface of target node
     * @return true if the nodes are in disjoint relation
     * @throws NodeMatcherException NodeMatcherException
     */
    public boolean nodeDisjoint(IContextMapping<IAtomicConceptOfLabel> acolMapping,
                                Map<String, IAtomicConceptOfLabel> sourceACoLs,
                                Map<String, IAtomicConceptOfLabel> targetACoLs,
                                INode sourceNode, INode targetNode) throws NodeMatcherException {
        boolean result = false;
        String sourceCNodeFormula = sourceNode.nodeData().getNodeFormula();
        String targetCNodeFormula = targetNode.nodeData().getNodeFormula();
        String sourceCLabFormula = sourceNode.nodeData().getLabelFormula();
        String targetCLabFormula = targetNode.nodeData().getLabelFormula();

        if (null != sourceCNodeFormula && null != targetCNodeFormula && !sourceCNodeFormula.isEmpty() && !targetCNodeFormula.isEmpty() &&
                null != sourceCLabFormula && null != targetCLabFormula && !sourceCLabFormula.isEmpty() && !targetCLabFormula.isEmpty()
                ) {
            HashMap<IAtomicConceptOfLabel, String> hashConceptNumber = new HashMap<>();
            Object[] obj = mkAxioms(hashConceptNumber, sourceACoLs, targetACoLs, acolMapping, sourceNode, targetNode);
            String axioms = (String) obj[0];
            int num_of_axiom_clauses = (Integer) obj[1];

            ArrayList<ArrayList<String>> contextA = parseFormula(hashConceptNumber, sourceACoLs, sourceNode);
            ArrayList<ArrayList<String>> contextB = parseFormula(hashConceptNumber, targetACoLs, targetNode);
            String contextAInDIMACSFormat = DIMACSfromList(contextA);
            String contextBInDIMACSFormat = DIMACSfromList(contextB);

            String satProblemInDIMACS = axioms + contextBInDIMACSFormat + contextAInDIMACSFormat;
            int numberOfClauses = contextA.size() + contextB.size() + num_of_axiom_clauses;
            int numberOfVariables = hashConceptNumber.size();
            String DIMACSproblem = "p cnf " + numberOfVariables + " " + numberOfClauses + "\n" + satProblemInDIMACS;

            result = isUnsatisfiable(DIMACSproblem);
        }
        return result;
    }

    /**
     * Checks whether the source node is subsumed by the target node.
     *
     * @param sourceNode  interface of source node
     * @param targetNode  interface of target node
     * @param acolMapping mapping between concepts
     * @param sourceACoLs mapping acol id -> acol object
     * @param targetACoLs mapping acol id -> acol object
     * @return true if the nodes are in subsumption relation
     * @throws NodeMatcherException NodeMatcherException
     */
    public boolean nodeSubsumedBy(INode sourceNode, INode targetNode,
                                  IContextMapping<IAtomicConceptOfLabel> acolMapping,
                                  Map<String, IAtomicConceptOfLabel> sourceACoLs,
                                  Map<String, IAtomicConceptOfLabel> targetACoLs) throws NodeMatcherException {
        boolean result = false;
        String sourceCNodeFormula = sourceNode.nodeData().getNodeFormula();
        String targetCNodeFormula = targetNode.nodeData().getNodeFormula();
        String sourceCLabFormula = sourceNode.nodeData().getLabelFormula();
        String targetCLabFormula = targetNode.nodeData().getLabelFormula();

        if (null != sourceCNodeFormula && null != targetCNodeFormula && !sourceCNodeFormula.isEmpty() && !targetCNodeFormula.isEmpty() &&
                null != sourceCLabFormula && null != targetCLabFormula && !sourceCLabFormula.isEmpty() && !targetCLabFormula.isEmpty()
                ) {
            if (sourceNode.nodeData().getSource()) {
                HashMap<IAtomicConceptOfLabel, String> hashConceptNumber = new HashMap<>();
                Object[] obj = mkAxioms(hashConceptNumber, sourceACoLs, targetACoLs, acolMapping, sourceNode, targetNode);
                String axioms = (String) obj[0];
                int num_of_axiom_clauses = (Integer) obj[1];

                ArrayList<ArrayList<String>> contextA = parseFormula(hashConceptNumber, sourceACoLs, sourceNode);
                ArrayList<ArrayList<String>> contextB = parseFormula(hashConceptNumber, targetACoLs, targetNode);
                String contextAInDIMACSFormat = DIMACSfromList(contextA);

                ArrayList<ArrayList<String>> negatedContext = new ArrayList<>();
                //LG test
                Integer numberOfVariables = negateFormulaInList(hashConceptNumber, contextB, negatedContext);
                String satProblemInDIMACS = axioms + contextAInDIMACSFormat + DIMACSfromList(negatedContext);
                Integer numberOfClauses = num_of_axiom_clauses + contextA.size() + negatedContext.size();
                String DIMACSproblem = "p cnf " + numberOfVariables + " " + numberOfClauses + "\n" + satProblemInDIMACS;

                result = isUnsatisfiable(DIMACSproblem);
            } else {
                //swap source, target and relation
                HashMap<IAtomicConceptOfLabel, String> hashConceptNumber = new HashMap<>();
                Object[] obj = mkAxioms(hashConceptNumber, sourceACoLs, targetACoLs, acolMapping, targetNode, sourceNode);
                String axioms = (String) obj[0];
                int num_of_axiom_clauses = (Integer) obj[1];

                ArrayList<ArrayList<String>> contextA = parseFormula(hashConceptNumber, sourceACoLs, targetNode);
                ArrayList<ArrayList<String>> contextB = parseFormula(hashConceptNumber, targetACoLs, sourceNode);
                String contextBInDIMACSFormat = DIMACSfromList(contextB);

                ArrayList<ArrayList<String>> negatedContext = new ArrayList<>();
                //MG test
                Integer numberOfVariables = negateFormulaInList(hashConceptNumber, contextA, negatedContext);
                String satProblemInDIMACS = axioms + contextBInDIMACSFormat + DIMACSfromList(negatedContext);
                Integer numberOfClauses = num_of_axiom_clauses + contextB.size() + negatedContext.size();
                String DIMACSproblem = "p cnf " + numberOfVariables + " " + numberOfClauses + "\n" + satProblemInDIMACS;

                result = isUnsatisfiable(DIMACSproblem);
            }
        }
        return result;
    }
}