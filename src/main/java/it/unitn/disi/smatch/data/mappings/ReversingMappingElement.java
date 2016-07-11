package it.unitn.disi.smatch.data.mappings;

import it.unitn.disi.smatch.data.trees.INode;

/**
 * Reverses the mapping element so that source node is always on the left (as source).
 * Used in minimal mapping in OptimizedStageTreeMatcher.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ReversingMappingElement extends MappingElement<INode> {

    public ReversingMappingElement(INode sourceNode, INode targetNode, char relation) {
        super(sourceNode, targetNode, relation);
        if (null != sourceNode && sourceNode.nodeData().getSource()) {
            this.relation = relation;
            this.source = sourceNode;
            this.target = targetNode;
        } else {
            this.source = targetNode;
            this.target = sourceNode;
            if (LESS_GENERAL == relation) {
                this.relation = MORE_GENERAL;
            } else {
                if (MORE_GENERAL == relation) {
                    this.relation = LESS_GENERAL;
                } else {
                    this.relation = relation;
                }
            }
        }
    }
}