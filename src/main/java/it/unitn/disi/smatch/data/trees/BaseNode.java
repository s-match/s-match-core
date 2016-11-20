package it.unitn.disi.smatch.data.trees;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.unitn.disi.smatch.data.IndexedObject;

import javax.swing.event.EventListenerList;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Base node class.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class BaseNode<E extends IBaseNode, I extends IBaseNodeData> extends IndexedObject implements IBaseNode<E, I>, IBaseNodeData {

    @JsonBackReference("children")
    protected E parent;
    @JsonManagedReference("children")
    protected List<E> children;

    @JsonIgnore
    protected EventListenerList listenerList;

    // id is needed to store nodeFormulas correctly.
    // nodeFormula is made of labelFormulas, each of which refers to tokens and tokens should have unique id
    // within a context. This is achieved by using node id + token id for each token
    protected String id;
    protected String name;

    @JsonIgnore
    protected Object userObject;

    // node counter to set unique node id during creation
    protected static final AtomicLong nodeCounter = new AtomicLong();

    // iterator which iterates over all parent nodes
    private final class AncestorsIterator implements Iterator<E> {
        private E current;

        public AncestorsIterator(E start) {
            if (null == start) {
                throw new IllegalArgumentException("argument is null");
            }
            this.current = start;
        }

        public boolean hasNext() {
            return current.hasParent();
        }

        @SuppressWarnings({"unchecked"})
        public E next() {
            current = (E) current.getParent();
            return current;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class BreadthFirstSearchIterator implements Iterator<E> {
        private final Deque<E> queue;

        @SuppressWarnings({"unchecked"})
        public BreadthFirstSearchIterator(E start) {
            if (null == start) {
                throw new IllegalArgumentException("start is required");
            }
            this.queue = new ArrayDeque<>();
            this.queue.addAll(start.getChildren());
        }

        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @SuppressWarnings({"unchecked"})
        public E next() {
            E current = queue.pollFirst();
            if (null != current) {
                this.queue.addAll(current.getChildren());
            } else {
                throw new NoSuchElementException();
            }
            return current;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class DepthFirstSearchIterator implements Iterator<E> {
        private final Deque<E> queue;

        @SuppressWarnings("unchecked")
        public DepthFirstSearchIterator(E start) {
            if (null == start) {
                throw new IllegalArgumentException("start is required");
            }
            this.queue = new ArrayDeque<>();

            for (int i = start.getChildCount() - 1; 0 <= i; i--) {
                queue.addFirst((E) start.getChildAt(i));
            }
        }

        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @SuppressWarnings({"unchecked"})
        public E next() {
            E current = queue.pollFirst();
            if (null != current) {
                for (int i = current.getChildCount() - 1; 0 <= i; i--) {
                    queue.addFirst((E) current.getChildAt(i));
                }

                return current;
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static final Comparator<IBaseNode> NODE_NAME_COMPARATOR = new Comparator<IBaseNode>() {
        public int compare(IBaseNode e1, IBaseNode e2) {
            return e1.nodeData().getName().compareTo(e2.nodeData().getName());
        }
    };

    public BaseNode() {
        super();
        // need to set node id to keep track of concepts in c@node formulas
        id = "n" + nodeCounter.getAndIncrement() + "_" + ((System.currentTimeMillis() / 1000) % (365 * 24 * 3600));
        name = "";
    }

    /**
     * Constructor class which sets the node name.
     *
     * @param name the name of the node
     */
    public BaseNode(String name) {
        this();
        this.name = name;
    }

    @Override
    public E getChildAt(int index) {
        if (children == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        return children.get(index);
    }

    @Override
    @JsonIgnore
    public int getChildCount() {
        if (children == null) {
            return 0;
        } else {
            return children.size();
        }
    }

    @Override
    public int getChildIndex(E child) {
        if (null == child) {
            throw new IllegalArgumentException("argument is null");
        }

        if (!isChild(child)) {
            return -1;
        }
        return children.indexOf(child);
    }

    @Override
    public Iterator<E> childrenIterator() {
        if (null == children) {
            return Collections.<E>emptyList().iterator();
        } else {
            return children.iterator();
        }
    }

    @Override
    public List<E> getChildren() {
        if (null != children) {
            return Collections.unmodifiableList(children);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<E> getModifiableChildren() {
        if (null != children) {
            return children;
        } else {
            return Collections.emptyList();
        }
    }
    
    public void setChildren(List<E> children) {
        for (E child : children){
            checkChild(child);
        }
        
        this.children = children;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public E createChild() {
        E child = (E) new BaseNode<E, I>();
        addChild(child);
        return child;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public E createChild(String name) {
        E child = (E) new BaseNode<E, I>(name);
        addChild(child);
        return child;
    }

    @Override
    public void addChild(E child) {
        addChild(getChildCount(), child);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void addChild(int index, E child) {
        checkChild(child);
        
        IBaseNode oldParent = child.getParent();

        if (null != oldParent) {
            oldParent.removeChild(child);
        }

        child.setParent(this);
        if (null == children) {
            children = new ArrayList<>();
        }
        children.add(index, child);
        fireTreeStructureChanged((E) this);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void removeChild(int index) {
        E child = getChildAt(index);
        children.remove(index);
        fireTreeStructureChanged((E) this);
        child.setParent(null);
    }

    @Override
    public void removeChild(E child) {
        if (null == child) {
            throw new IllegalArgumentException("argument is null");
        }

        if (isChild(child)) {
            removeChild(getChildIndex(child));
        }
    }

    @Override
    public E getParent() {
        return parent;
    }

    @Override
    public void setParent(E newParent) {
        removeFromParent();
        parent = newParent;
    }

    @Override
    @JsonIgnore
    public boolean hasParent() {
        return null != parent;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void removeFromParent() {
        if (null != parent) {
            parent.removeChild(this);
            parent = null;
        }
    }

    @Override
    @JsonIgnore
    public boolean isLeaf() {
        return 0 == getChildCount();
    }

    @Override
    public int ancestorCount() {
        int result = 0;
        if (null != parent) {
            result = parent.ancestorCount() + 1;
        }
        return result;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Iterator<E> ancestorsIterator() {
        return new AncestorsIterator((E) this);
    }

    @Override
    public int descendantCount() {
        int descendantCount = 0;
        for (Iterator<E> i = descendantsIterator(); i.hasNext(); ) {
            i.next();
            descendantCount++;
        }
        return descendantCount;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Iterator<E> descendantsIterator() {
        return new DepthFirstSearchIterator((E) this);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    @JsonIgnore
    public I nodeData() {
        return (I) this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String newName) {
        name = newName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String newId) {
        id = newId;
    }

    @Override
    public Object getUserObject() {
        return userObject;
    }

    @Override
    public void setUserObject(Object object) {
        userObject = object;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public int getIndex(TreeNode node) {
        if (node instanceof IBaseNode) {
            return getChildIndex((E) node);
        } else {
            return -1;
        }
    }

    @Override
    @JsonIgnore
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public Enumeration children() {
        return Collections.enumeration(children);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void insert(MutableTreeNode child, int index) {
        if (child instanceof IBaseNode) {
            addChild(index, (E) child);
        }
    }

    @Override
    public void remove(int index) {
        removeChild(index);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void remove(MutableTreeNode node) {
        if (node instanceof IBaseNode) {
            removeChild((E) node);
        }
    }

    @Override
    public void setParent(MutableTreeNode newParent) {
        if (newParent instanceof IBaseNode) {
            setParent(newParent);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void addTreeStructureChangedListener(IBaseTreeStructureChangedListener<E> l) {
        if (null == listenerList) {
            listenerList = new EventListenerList();
        }
        listenerList.add(IBaseTreeStructureChangedListener.class, l);
    }

    @Override
    public void removeTreeStructureChangedListener(IBaseTreeStructureChangedListener<E> l) {
        if (null != listenerList) {
            listenerList.remove(IBaseTreeStructureChangedListener.class, l);
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void fireTreeStructureChanged(E node) {
        if (null != listenerList) {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i + 1] instanceof IBaseTreeStructureChangedListener) {
                    // Lazily create the event:
                    ((IBaseTreeStructureChangedListener<E>) listeners[i + 1]).treeStructureChanged(node);
                }
            }
        }
        if (null != parent) {
            parent.fireTreeStructureChanged(node);
        }
    }

    @SuppressWarnings({"unchecked"})
    private boolean isAncestor(E node) {
        if (null == node) {
            return false;
        }

        E ancestor = (E) this;

        do {
            if (ancestor == node) {
                return true;
            }
        } while ((ancestor = (E) ancestor.getParent()) != null);

        return false;
    }

    private boolean isChild(E node) {
        return null != node && 0 != getChildCount() && node.getParent() == this && -1 < children.indexOf(node);
    }
    
    /**
     * @since 2.0.0
     * @throws IllegalArgumentException
     */
    private void checkChild(E child) {
        if (null == child) {
            throw new IllegalArgumentException("new child is null");
        } else if (isAncestor(child)) {
            throw new IllegalArgumentException("new child is an ancestor");
        }
    }

}