package it.unitn.disi.smatch.data.trees;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.unitn.disi.smatch.data.ling.AtomicConceptOfLabel;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;

import java.util.*;

/**
 * This class represents a node in the hierarchy. It contains logical (cNode and cLab formulas),
 * linguistic (WN senses, tokens) and structural information (parent and children of a node).
 * <p/>
 * Many things are modeled after DefaultMutableTreeNode.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class Node extends BaseNode<INode, INodeData> implements INode, INodeData {

    protected boolean isPreprocessed;

    protected String labelFormula;
    protected String nodeFormula;
    // might be better implemented for a whole context via BitSet
    protected boolean source;
    protected String provenance;

    protected List<IAtomicConceptOfLabel> concepts;

    public Node() {
        super();
        labelFormula = "";
        nodeFormula = "";
        concepts = new ArrayList<>();
    }

    /**
     * Constructor class which sets the node name.
     *
     * @param name the name of the node
     */
    public Node(String name) {
        super(name);
        labelFormula = "";
        nodeFormula = "";
        concepts = new ArrayList<>();
    }

    @Override
    public String getLabelFormula() {
        return labelFormula;
    }

    @Override
    public void setLabelFormula(String cLabFormula) {
        this.labelFormula = cLabFormula;
    }

    @Override
    public String getNodeFormula() {
        return nodeFormula;
    }

    @Override
    public void setNodeFormula(String cNodeFormula) {
        this.nodeFormula = cNodeFormula;
    }

    @Override
    public boolean getSource() {
        return source;
    }

    @Override
    public void setSource(boolean source) {
        this.source = source;
    }

    @Override
    public boolean getIsPreprocessed() {
        return isPreprocessed;
    }

    @Override
    public void setIsPreprocessed(boolean isPreprocessed) {
        this.isPreprocessed = isPreprocessed;
    }

    @JsonIgnore
    @Override
    public boolean isSubtreePreprocessed() {
        boolean result = isPreprocessed;
        if (result && null != children) {
            for (INode child : children) {
                result = child.nodeData().isSubtreePreprocessed();
                if (!result) {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public String getProvenance() {
        return provenance;
    }

    @Override
    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    @Override
    public INode createChild() {
        INode child = new Node();
        addChild(child);
        return child;
    }

    @Override
    public INode createChild(String name) {
        INode child = new Node(name);
        addChild(child);
        return child;
    }

    @Override
    public Iterator<IAtomicConceptOfLabel> pathToRootConceptIterator() {
        return new PathToRootConceptIterator(this);
    }

    @Override
    public IAtomicConceptOfLabel createConcept() {
        return new AtomicConceptOfLabel();
    }

    @Override
    public List<IAtomicConceptOfLabel> getConcepts() {
        return concepts;
    }

    @Override
    public void setConcepts(List<IAtomicConceptOfLabel> concepts) {
        this.concepts = concepts;
    }

    private final static class PathToRootConceptIterator implements Iterator<IAtomicConceptOfLabel> {
        private INode curNode;
        private int curIndex;
        private IAtomicConceptOfLabel next;

        public PathToRootConceptIterator(final INode node) {
            curNode = node;
            curIndex = 0;

            // find next
            while (null != curNode && curNode.nodeData().getConcepts().size() <= curIndex) {
                curNode = curNode.getParent();
                curIndex = 0;
            }
            if (null != curNode && curIndex < curNode.nodeData().getConcepts().size()) {
                next = curNode.nodeData().getConcepts().get(curIndex);
            }
        }

        @Override
        public boolean hasNext() {
            return null != next;
        }

        @Override
        public IAtomicConceptOfLabel next() {
            if (null == next) {
                throw new NoSuchElementException();
            }
            IAtomicConceptOfLabel result = next;
            curIndex++;

            // find next
            while (null != curNode && curNode.nodeData().getConcepts().size() <= curIndex) {
                curNode = curNode.getParent();
                curIndex = 0;
            }
            if (null != curNode && curIndex < curNode.nodeData().getConcepts().size()) {
                next = curNode.nodeData().getConcepts().get(curIndex);
            } else {
                next = null;
            }

            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}