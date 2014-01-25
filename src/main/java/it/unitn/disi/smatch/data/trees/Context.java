package it.unitn.disi.smatch.data.trees;

/**
 * A Context that contains tree data structure.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class Context extends BaseContext<INode> implements IContext, ITreeStructureChangedListener {

    public Context() {
        super();
    }

    @Override
    public INode createNode() {
        return new Node();
    }

    @Override
    public INode createNode(String name) {
        return new Node(name);
    }

    @Override
    public INode createRoot(String name) {
        INode result = createRoot();
        result.getNodeData().setName(name);
        return result;
    }

    @Override
    public INode createRoot() {
        root = new Node();
        root.addTreeStructureChangedListener(this);
        return root;
    }
}