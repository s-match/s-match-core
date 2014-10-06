package it.unitn.disi.smatch.matchers.structure.node;

import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.deciders.ISATSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Default node matcher used by a {@link it.unitn.disi.smatch.matchers.structure.tree.def.DefaultTreeMatcher}.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class DefaultNodeMatcher extends BaseNodeMatcher implements INodeMatcher {

    public DefaultNodeMatcher(ISATSolver satSolver) {
        super(satSolver);
    }

    public char nodeMatch(IContextMapping<IAtomicConceptOfLabel> acolMapping,
                          Map<String, IAtomicConceptOfLabel> sourceACoLs,
                          Map<String, IAtomicConceptOfLabel> targetACoLs,
                          INode sourceNode, INode targetNode) throws NodeMatcherException {
        char result = IMappingElement.IDK;
        String sourceCNodeFormula = sourceNode.getNodeData().getcNodeFormula();
        String targetCNodeFormula = targetNode.getNodeData().getcNodeFormula();
        String sourceCLabFormula = sourceNode.getNodeData().getcLabFormula();
        String targetCLabFormula = targetNode.getNodeData().getcLabFormula();

        if (null != sourceCNodeFormula && null != targetCNodeFormula && !sourceCNodeFormula.isEmpty() && !targetCNodeFormula.isEmpty() &&
                null != sourceCLabFormula && null != targetCLabFormula && !sourceCLabFormula.isEmpty() && !targetCLabFormula.isEmpty()
                ) {
            //whether particular relation holds
            boolean isContains;
            boolean isContained;
            boolean isOpposite;

            // ACoLs -> its DIMACS variable number
            Map<IAtomicConceptOfLabel, String> hashConceptNumber = new HashMap<>();
            // number of variables in SAT problem
            Integer numberOfVariables;
            // number of clauses in SAT problem
            Integer numberOfClauses;

            Object[] obj = mkAxioms(hashConceptNumber, sourceACoLs, targetACoLs, acolMapping, sourceNode, targetNode);
            String axioms = (String) obj[0];
            int num_of_axiom_clauses = (Integer) obj[1];
            // parse formulas with acols into formulas with DIMACS variables
            ArrayList<ArrayList<String>> contextA = parseFormula(hashConceptNumber, sourceACoLs, sourceNode);
            ArrayList<ArrayList<String>> contextB = parseFormula(hashConceptNumber, targetACoLs, targetNode);
            // create contexts in DIMACS format
            String contextAInDIMACSFormat = DIMACSfromList(contextA);
            String contextBInDIMACSFormat = DIMACSfromList(contextB);

            // negated formula
            ArrayList<ArrayList<String>> negatedContext = new ArrayList<>();
            // sat problem in DIMACS format
            String satProblemInDIMACS;
            // sat problem with DIMACS header
            String DIMACSproblem;

            // LG test
            // negate the context
            numberOfVariables = negateFormulaInList(hashConceptNumber, contextB, negatedContext);
            // get the sat problem in DIMACS format
            satProblemInDIMACS = axioms + contextAInDIMACSFormat + DIMACSfromList(negatedContext);
            // get number of clauses for SAT problem
            numberOfClauses = num_of_axiom_clauses + contextA.size() + negatedContext.size();
            // add DIMACS header
            DIMACSproblem = "p cnf " + numberOfVariables + " " + numberOfClauses + "\n" + satProblemInDIMACS;
            // do LG test
            isContained = isUnsatisfiable(DIMACSproblem);

            // MG test
            // negate the context
            numberOfVariables = negateFormulaInList(hashConceptNumber, contextA, negatedContext);
            // get the sat problem in DIMACS format
            satProblemInDIMACS = axioms + contextBInDIMACSFormat + DIMACSfromList(negatedContext);
            // get number of clauses for SAT problem
            numberOfClauses = num_of_axiom_clauses + contextB.size() + negatedContext.size();
            // add DIMACS header
            DIMACSproblem = "p cnf " + numberOfVariables + " " + numberOfClauses + "\n" + satProblemInDIMACS;
            // do MG test
            isContains = isUnsatisfiable(DIMACSproblem);

            // DJ test
            // get the sat problem in DIMACS format
            satProblemInDIMACS = axioms + contextBInDIMACSFormat + contextAInDIMACSFormat;
            // get number of clauses for SAT problem
            numberOfClauses = contextA.size() + contextB.size() + num_of_axiom_clauses;
            numberOfVariables = hashConceptNumber.size();
            // add DIMACS header
            DIMACSproblem = "p cnf " + numberOfVariables + " " + numberOfClauses + "\n" + satProblemInDIMACS;
            // do disjointness test
            isOpposite = isUnsatisfiable(DIMACSproblem);

            result = getRelationString(isContains, isContained, isOpposite);
        }
        return result;
    }
}
