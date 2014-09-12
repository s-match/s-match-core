package it.unitn.disi.smatch.data.trees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Base class for contexts.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
@SuppressWarnings({"unchecked"})
public class BaseContext<E extends IBaseNode> implements IBaseContext<E>, IBaseTreeStructureChangedListener<E> {

    protected E root;
    protected ArrayList<E> nodes;

    public BaseContext() {
        root = null;
        nodes = null;
    }

    public void setRoot(E root) {
        this.root = root;
        root.addTreeStructureChangedListener(this);
    }

    public E getRoot() {
        return root;
    }

    public boolean hasRoot() {
        return null != root;
    }

    public E createNode() {
        return (E) new BaseNode();
    }

    public E createNode(String name) {
        return (E) new BaseNode(name);
    }

    public E createRoot() {
        root = (E) new BaseNode();
        root.addTreeStructureChangedListener(this);
        return root;
    }

    public E createRoot(String name) {
        E result = createRoot();
        result.getNodeData().setName(name);
        return result;
    }

    public Iterator<E> getNodes() {
        if (hasRoot()) {
            return new StartIterator<E>(root, root.getDescendants());
        } else {
            return Collections.<E>emptyList().iterator();
        }
    }

    public List<E> getNodesList() {
        if (null != nodes) {
            return Collections.unmodifiableList(nodes);
        } else {
            if (hasRoot()) {
                nodes = new ArrayList<>();
                nodes.add(root);
                nodes.addAll(root.getDescendantsList());
                nodes.trimToSize();
                return Collections.unmodifiableList(nodes);
            } else {
                return Collections.emptyList();
            }
        }
    }

    public void treeStructureChanged(E node) {
        nodes = null;
    }

    public void trim() {
        if (root instanceof BaseNode) {
            ((BaseNode) root).trim();
        }
    }
}