package it.unitn.disi.smatch.data.trees;

import java.util.Collections;
import java.util.Iterator;

/**
 * Base class for contexts.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
@SuppressWarnings({"unchecked"})
public class BaseContext<E extends IBaseNode> implements IBaseContext<E>, IBaseTreeStructureChangedListener<E> {

    protected E root;

    public BaseContext() {
        root = null;
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
        result.nodeData().setName(name);
        return result;
    }

    @Override
    public Iterator<E> nodeIterator() {
        if (hasRoot()) {
            return new StartIterator<>(root, root.descendantsIterator());
        } else {
            return Collections.<E>emptyList().iterator();
        }
    }

    @Override
    public int nodesCount() {
        if (null == root) {
            return 0;
        } else {
            return root.descendantCount() + 1;
        }
    }

    @Override
    public void treeStructureChanged(E node) {
    }
}