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

    @Override
    public void setRoot(E root) {
        this.root = root;
        root.addTreeStructureChangedListener(this);
    }

    @Override
    public E getRoot() {
        return root;
    }

    @Override
    public boolean hasRoot() {
        return null != root;
    }

    @Override
    public E createNode() {
        return (E) new BaseNode();
    }

    @Override
    public E createNode(String name) {
        return (E) new BaseNode(name);
    }

    @Override
    public E createRoot() {
        root = (E) new BaseNode();
        root.addTreeStructureChangedListener(this);
        return root;
    }

    @Override
    public E createRoot(String name) {
        E result = createRoot();
        result.getNodeData().setName(name);
        return result;
    }

    @Override
    public Iterator<E> getNodes() {
        if (hasRoot()) {
            return new StartIterator<>(root, root.getDescendants());
        } else {
            return Collections.<E>emptyList().iterator();
        }
    }

    @Override
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

    @Override
    public int getNodesCount() {
        if (null == root) {
            return 0;
        } else {
            return root.getDescendantCount() + 1;
        }
    }

    @Override
    public void treeStructureChanged(E node) {
        nodes = null;
    }
}