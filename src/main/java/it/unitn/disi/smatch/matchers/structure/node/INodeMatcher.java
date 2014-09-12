package it.unitn.disi.smatch.matchers.structure.node;

import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.INode;

import java.util.ArrayList;
import java.util.Map;

/**
 * An interface for node matchers.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface INodeMatcher {

    /**
     * Matches two nodes and returns a relation between them.
     *
     * @param acolMapping a mapping between atomic concepts of labels
     * @param nmtAcols    node -> list of node matching task acols
     * @param sourceACoLs mapping acol id -> acol object
     * @param targetACoLs mapping acol id -> acol object
     * @param sourceNode  source node
     * @param targetNode  target node
     * @return relation between source and target nodes
     * @throws NodeMatcherException NodeMatcherException
     */
    public char nodeMatch(IContextMapping<IAtomicConceptOfLabel> acolMapping,
                          Map<INode, ArrayList<IAtomicConceptOfLabel>> nmtAcols,
                          Map<String, IAtomicConceptOfLabel> sourceACoLs,
                          Map<String, IAtomicConceptOfLabel> targetACoLs,
                          INode sourceNode, INode targetNode) throws NodeMatcherException;
}
