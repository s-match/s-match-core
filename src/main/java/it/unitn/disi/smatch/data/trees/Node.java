package it.unitn.disi.smatch.data.trees;

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

    protected String cLabFormula;
    protected String cNodeFormula;
    // might be better implemented for a whole context via BitSet
    protected boolean source;
    protected String provenance;

    protected ArrayList<IAtomicConceptOfLabel> acols;

    public Node() {
        super();
        isPreprocessed = false;

        source = false;
        cLabFormula = "";
        cNodeFormula = "";
        acols = null;
        index = -1;
        provenance = null;
    }

    /**
     * Constructor class which sets the node name.
     *
     * @param name the name of the node
     */
    public Node(String name) {
        super(name);
    }


    public String getcLabFormula() {
        return cLabFormula;
    }

    public void setcLabFormula(String cLabFormula) {
        this.cLabFormula = cLabFormula;
    }

    public String getcNodeFormula() {
        return cNodeFormula;
    }

    public void setcNodeFormula(String cNodeFormula) {
        this.cNodeFormula = cNodeFormula;
    }

    public boolean getSource() {
        return source;
    }

    public void setSource(boolean source) {
        this.source = source;
    }

    public IAtomicConceptOfLabel getACoLAt(int index) {
        if (null == acols) {
            throw new ArrayIndexOutOfBoundsException("node has no ACoLs");
        }
        return acols.get(index);
    }

    public int getACoLCount() {
        if (acols == null) {
            return 0;
        } else {
            return acols.size();
        }
    }

    public int getACoLIndex(IAtomicConceptOfLabel acol) {
        if (null == acol) {
            throw new IllegalArgumentException("argument is null");
        }

        return acols.indexOf(acol);
    }

    public Iterator<IAtomicConceptOfLabel> getACoLs() {
        if (null == acols) {
            return Collections.<IAtomicConceptOfLabel>emptyList().iterator();
        } else {
            return acols.iterator();
        }
    }

    public List<IAtomicConceptOfLabel> getACoLsList() {
        if (null == acols) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(acols);
        }
    }

    public IAtomicConceptOfLabel createACoL() {
        return new AtomicConceptOfLabel();
    }

    public void addACoL(IAtomicConceptOfLabel acol) {
        addACoL(getACoLCount(), acol);
    }

    public void addACoL(int index, IAtomicConceptOfLabel acol) {
        if (null == acol) {
            throw new IllegalArgumentException("new acol is null");
        }

        if (null == acols) {
            acols = new ArrayList<>();
        }
        acols.add(index, acol);
    }

    public void removeACoL(int index) {
        acols.remove(index);
    }

    public void removeACoL(IAtomicConceptOfLabel acol) {
        acols.remove(acol);
    }

    public boolean getIsPreprocessed() {
        return isPreprocessed;
    }

    public void setIsPreprocessed(boolean isPreprocessed) {
        this.isPreprocessed = isPreprocessed;
    }

    public boolean isSubtreePreprocessed() {
        boolean result = isPreprocessed;
        if (result) {
            if (null != children) {
                for (INode child : children) {
                    result = child.getNodeData().isSubtreePreprocessed();
                    if (!result) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    public String getProvenance() {
        return provenance;
    }

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
    public Iterator<IAtomicConceptOfLabel> pathToRootACoLs() {
        return new PathToRootACoLIterator(this);
    }

    private final static class PathToRootACoLIterator implements Iterator<IAtomicConceptOfLabel> {
        private INode curNode;
        private int curIndex;
        private IAtomicConceptOfLabel next;

        public PathToRootACoLIterator(final INode node) {
            curNode = node;
            curIndex = 0;

            // find next
            while (null != curNode && curNode.getNodeData().getACoLCount() <= curIndex) {
                curNode = curNode.getParent();
                curIndex = 0;
            }
            if (null != curNode && curIndex < curNode.getNodeData().getACoLCount()) {
                next = curNode.getNodeData().getACoLAt(curIndex);
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
            while (null != curNode && curNode.getNodeData().getACoLCount() <= curIndex) {
                curNode = curNode.getParent();
                curIndex = 0;
            }
            if (null != curNode && curIndex < curNode.getNodeData().getACoLCount()) {
                next = curNode.getNodeData().getACoLAt(curIndex);
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