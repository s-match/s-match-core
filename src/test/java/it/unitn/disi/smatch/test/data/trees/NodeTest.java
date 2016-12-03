package it.unitn.disi.smatch.test.data.trees;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.data.trees.Node;

/**
* @since 2.0.0
*/
public class NodeTest {

    /**
     * Shows setParent is not looping, see https://github.com/s-match/s-match-core/issues/7
     * 
     * @since 2.0.0
     */
    @Test
    public void testNode(){
        Node parent = new Node();
        
        Node node = new Node();
        
        node.setParent(parent);
    }

    /**
     * Shows setChidren should store a copy of the array, 
     * see https://github.com/s-match/s-match-core/issues/7
     * 
     * @since 2.0.0
     */    
    @Test
    public void testSetChildren(){
        Node nodeA = new Node();
        Node nodeB = new Node();
        
        List<INode> children = new ArrayList<>();
        nodeA.setChildren(children);
        children.add(nodeB);        
        assertEquals(0, nodeA.getChildCount());
        
    }
    
}
