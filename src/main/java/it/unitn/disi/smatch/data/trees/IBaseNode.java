package it.unitn.disi.smatch.data.trees;

import javax.swing.tree.MutableTreeNode;
import java.util.Iterator;
import java.util.List;

/**
 * Base node interface.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IBaseNode<E extends IBaseNode, I extends IBaseNodeData> extends MutableTreeNode {
    /**
     * Returns the child node at index childIndex.
     */
    E getChildAt(int childIndex);

    /**
     * Returns the number of children Es.
     */
    int getChildCount();

    /**
     * Returns the index of node in the receivers children.
     * If the receiver does not contain node, -1 will be returned.
     *
     * @param child a node to search for
     * @return the index of node in the receivers children
     */
    int getChildIndex(E child);

    /**
     * Returns the iterator over the children of the receiver.
     *
     * @return the iterator over the children of the receiver
     */
    Iterator<E> childrenIterator();

    /**
     * Returns unmodifiable list of receivers children.
     *
     * @return unmodifiable list of receivers children
     */
    List<E> getChildren();

    /**
     * Sets list of children.
     *
     * @param children new list of children
     */
    void setChildren(List<E> children);

    /**
     * Creates a child to the given node as the last child.
     *
     * @return a newly created child
     */
    E createChild();

    /**
     * Creates a child with a name to the given node as the last child.
     *
     * @param name a name for a new child
     * @return a newly created child
     */
    E createChild(String name);

    /**
     * Adds a child to the given node as the last child.
     *
     * @param child node to add
     */
    void addChild(E child);

    /**
     * Adds child to the receiver at index.
     *
     * @param index index where the child will be added
     * @param child node to add
     */
    void addChild(int index, E child);

    /**
     * Removes the child at index from the receiver.
     *
     * @param index index of a child to remove
     */
    void removeChild(int index);

    /**
     * Removes node from the receiver.
     *
     * @param node child to remove
     */
    void removeChild(E node);

    /**
     * Returns the parent of the receiver.
     */
    E getParent();

    /**
     * Sets the parent of the receiver to newParent.
     *
     * @param newParent new parent
     */
    void setParent(E newParent);

    /**
     * Returns true if the receiver has a parent and false otherwise.
     *
     * @return true if the receiver has a parent and false otherwise
     */
    boolean hasParent();

    /**
     * Removes the subtree rooted at this node from the tree, giving this node a null parent.
     * Does nothing if this node is the root of its tree.
     */
    void removeFromParent();

    /**
     * Returns true if the receiver is a leaf.
     *
     * @return true if the receiver is a leaf
     */
    boolean isLeaf();

    /**
     * Returns the count of ancestor nodes.
     *
     * @return the count of ancestor nodes
     */
    int ancestorCount();

    /**
     * Returns ancestors of the receiver. The returned list is ordered from the parent node to the root.
     *
     * @return ancestors of the receiver
     */
    Iterator<E> ancestorsIterator();

    /**
     * Returns the count of descendant nodes.
     *
     * @return the count of descendant nodes
     */
    int descendantCount();

    /**
     * Returns descendants of the receiver. The descendants are ordered breadth first.
     *
     * @return descendants of the receiver
     */
    Iterator<E> descendantsIterator();

    /**
     * Returns interface to the node metadata.
     *
     * @return interface to the node metadata
     */
    I nodeData();

    /**
     * Adds a listener <code>l</code> to the the listener list.
     *
     * @param l listener
     */
    void addTreeStructureChangedListener(IBaseTreeStructureChangedListener<E> l);

    /**
     * Removes a listener <code>l</code> from the listeners list.
     *
     * @param l listener
     */
    void removeTreeStructureChangedListener(IBaseTreeStructureChangedListener<E> l);

    /**
     * Fires the tree structure changed event. Parent needs to know the changes in children. It can subscribe
     * to changes in its children, but this will create listenerList in each child. Therefore the method is public
     * to allow children to signal parent directly, propagating event up the tree, resetting caches
     * and finally allowing context to reset its caches too.
     *
     * @param node the root of the tree which is changed
     */
    void fireTreeStructureChanged(E node);
}