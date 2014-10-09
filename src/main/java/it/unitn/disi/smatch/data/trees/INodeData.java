package it.unitn.disi.smatch.data.trees;

import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;

import java.util.Iterator;
import java.util.List;

/**
 * An interface to the data part of the node.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface INodeData extends IBaseNodeData {

    /**
     * Returns the concept of a label formula. Concept of a label is a logical representation of the
     * natural language label associated with a node.
     *
     * @return the concept of a label formula
     */
    String getLabelFormula();

    /**
     * Sets the concept of a label formula.
     *
     * @param cLabFormula the concept of a label formula
     */
    void setLabelFormula(String cLabFormula);

    /**
     * Returns the concept at node formula. Concept at node formula is a logical representation of the concept
     * of a node located in a certain part of the tree.
     *
     * @return the concept at node formula
     */
    String getNodeFormula();

    /**
     * Sets the concept at node formula.
     *
     * @param cNodeFormula the concept at node formula
     */
    void setNodeFormula(String cNodeFormula);

    /**
     * Indicates whether this node belongs to the source context.
     * This is needed for new algorithms which sometimes swap order of the nodes during tree traversal.
     *
     * @return true if the node belongs to the source context
     */
    boolean getSource();

    /**
     * Sets the source flag.
     *
     * @param source the source flag
     */
    void setSource(boolean source);

    /**
     * Returns whether the node has been preprocessed.
     *
     * @return whether the node has been preprocessed
     */
    boolean getIsPreprocessed();

    /**
     * Sets whether the node has been preprocessed.
     *
     * @param isPreprocessed whether the node has been preprocessed
     */
    void setIsPreprocessed(boolean isPreprocessed);

    /**
     * Returns whether the subtree rooted at this node has been preprocessed.
     *
     * @return whether the subtree rooted at this node has been preprocessed
     */
    boolean isSubtreePreprocessed();

    /**
     * Returns provenance information.
     *
     * @return provenance information
     */
    String getProvenance();

    /**
     * Sets provenance information.
     *
     * @param provenance provenance information
     */
    void setProvenance(String provenance);

    /**
     * Creates an instance of an ACoL.
     *
     * @return an instance of an ACoL
     */
    IAtomicConceptOfLabel createConcept();

    /**
     * Returns a list of the concepts of the receiver.
     *
     * @return list of the concepts of the receiver
     */
    List<IAtomicConceptOfLabel> getConcepts();

    /**
     * Sets a list of the concepts of the receiver.
     */
    void setConcepts(List<IAtomicConceptOfLabel> concepts);

    /**
     * All concepts along path to root.
     *
     * @return all concepts along path to root
     */
    Iterator<IAtomicConceptOfLabel> pathToRootConceptIterator();
}