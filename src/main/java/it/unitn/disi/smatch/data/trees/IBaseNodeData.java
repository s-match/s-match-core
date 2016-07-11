package it.unitn.disi.smatch.data.trees;

/**
 * Basic data for nodes.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IBaseNodeData {

    /**
     * Returns the id of the node. The id of the node should be unique within a context.
     *
     * @return the id of the node
     */
    String getId();

    /**
     * Sets the id of the node.
     *
     * @param newId the new id of the node
     */
    void setId(String newId);

    /**
     * Returns the node name, that is a natural language label associated with it.
     *
     * @return the node name
     */
    String getName();

    /**
     * Sets the node name.
     *
     * @param newName new node name
     */
    void setName(String newName);

    Object getUserObject();

    void setUserObject(Object newObject);
}
