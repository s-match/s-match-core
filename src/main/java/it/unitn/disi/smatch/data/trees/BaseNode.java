package it.unitn.disi.smatch.data.trees;

import it.unitn.disi.smatch.data.IndexedObject;

import javax.swing.event.EventListenerList;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;

/**
 * Base node class.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class BaseNode<E extends IBaseNode, I extends IBaseNodeData> extends IndexedObject implements IBaseNode<E, I>, IBaseNodeData {

    protected E parent;
    protected ArrayList<E> children;
    protected ArrayList<E> ancestors;
    protected int ancestorCount;
    protected ArrayList<E> descendants;
    protected int descendantCount;

    protected EventListenerList listenerList;

    // id is needed to store cNodeFormulas correctly.
    // cNodeFormula is made of cLabFormulas, each of which refers to tokens and tokens should have unique id
    // within a context. This is achieved by using node id + token id for each token
    protected String id;
    protected String name;
    protected Object userObject;

    // node counter to set unique node id during creation
    protected static long countNode = 0;

    // iterator which iterates over all parent nodes

    private final class Ancestors implements Iterator<E> {
        private E current;

        public Ancestors(E start) {
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

    class BreadthFirstSearch implements Iterator<E> {
        private final Deque<E> queue;

        @SuppressWarnings({"unchecked"})
        public BreadthFirstSearch(E start) {
            if (null == start) {
                throw new IllegalArgumentException("start is required");
            }
            this.queue = new ArrayDeque<>();
            this.queue.addAll(start.getChildrenList());
        }

        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @SuppressWarnings({"unchecked"})
        public E next() {
            E current = queue.pollFirst();
            if (null != current) {
                this.queue.addAll(current.getChildrenList());
            } else {
                throw new NoSuchElementException();
            }
            return current;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    class DepthFirstSearch implements Iterator<E> {
        private final Deque<E> queue;

        @SuppressWarnings("unchecked")
        public DepthFirstSearch(E start) {
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
            return e1.getNodeData().getName().compareTo(e2.getNodeData().getName());
        }
    };

    public BaseNode() {
        parent = null;
        children = null;
        ancestors = null;
        ancestorCount = -1;
        descendants = null;
        descendantCount = -1;
        listenerList = null;
        // need to set node id to keep track of acols in c@node formulas
        // synchronized to make counts unique within JVM and decrease the chance of creating the same id
        synchronized (Node.class) {
            id = "n" + countNode + "_" + ((System.currentTimeMillis() / 1000) % (365 * 24 * 3600));
            countNode++;
        }
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

        if (!isNodeChild(child)) {
            return -1;
        }
        return children.indexOf(child);
    }

    @Override
    public Iterator<E> getChildren() {
        if (null == children) {
            return Collections.<E>emptyList().iterator();
        } else {
            return children.iterator();
        }
    }

    @Override
    public List<E> getChildrenList() {
        if (null != children) {
            return Collections.unmodifiableList(children);
        } else {
            return Collections.emptyList();
        }
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
        if (null == child) {
            throw new IllegalArgumentException("new child is null");
        } else if (isNodeAncestor(child)) {
            throw new IllegalArgumentException("new child is an ancestor");
        }

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

        if (isNodeChild(child)) {
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
    public boolean isLeaf() {
        return 0 == getChildCount();
    }

    @Override
    public int getAncestorCount() {
        if (-1 == ancestorCount) {
            if (null == ancestors) {
                ancestorCount = 0;
                if (null != parent) {
                    ancestorCount = parent.getAncestorCount() + 1;
                }
            } else {
                ancestorCount = ancestors.size();
            }
        }
        return ancestorCount;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Iterator<E> getAncestors() {
        return new Ancestors((E) this);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public List<E> getAncestorsList() {
        if (null == ancestors) {
            ancestors = new ArrayList<>(getAncestorCount());
            if (null != parent) {
                ancestors.add(parent);
                ancestors.addAll(parent.getAncestorsList());
            }
        }
        return Collections.unmodifiableList(ancestors);
    }

    @Override
    public int getLevel() {
        return getAncestorCount();
    }

    @Override
    public int getDescendantCount() {
        if (-1 == descendantCount) {
            if (null == descendants) {
                descendantCount = 0;
                for (Iterator<E> i = getDescendants(); i.hasNext();) {
                    i.next();
                    descendantCount++;
                }
            } else {
                descendantCount = descendants.size();
            }
        }
        return descendantCount;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Iterator<E> getDescendants() {
        return new DepthFirstSearch((E) this);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public List<E> getDescendantsList() {
        if (null == descendants) {
            descendants = new ArrayList<>(getChildCount());
            if (null != children) {
                descendants.addAll(children);
                for (IBaseNode child : children) {
                    descendants.addAll(child.getDescendantsList());
                }
                descendants.trimToSize();
            }
        }
        return Collections.unmodifiableList(descendants);
    }

    @SuppressWarnings({"unchecked"})
    public Iterator<E> getSubtree() {
        return new StartIterator<>((E) this, getDescendants());
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public I getNodeData() {
        return (I) this;
    }

    @SuppressWarnings({"unchecked"})
    private boolean isNodeAncestor(E anotherNode) {
        if (null == anotherNode) {
            return false;
        }

        E ancestor = (E) this;

        do {
            if (ancestor == anotherNode) {
                return true;
            }
        } while ((ancestor = (E) ancestor.getParent()) != null);

        return false;
    }

    private boolean isNodeChild(E node) {
        if (null == node) {
            return false;
        } else {
            if (getChildCount() == 0) {
                return false;
            } else {
                return (node.getParent() == this && -1 < children.indexOf(node));
            }
        }
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
        descendants = null;
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
}