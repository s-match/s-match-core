package it.unitn.disi.smatch.renderers.context;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.trees.IBaseContext;
import it.unitn.disi.smatch.data.trees.IBaseNode;
import it.unitn.disi.smatch.data.trees.IBaseNodeData;
import it.unitn.disi.smatch.data.trees.Node;
import it.unitn.disi.smatch.loaders.ILoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Renders a context in a tab-separated file, one line per path to root.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class TabPathContextRenderer extends BaseFileContextRenderer<IBaseContext<IBaseNode>, IBaseNode> implements IAsyncBaseContextRenderer<IBaseContext<IBaseNode>, IBaseNode> {

    public TabPathContextRenderer() {
        super();
    }

    public TabPathContextRenderer(boolean sort) {
        super(sort);
    }

    public TabPathContextRenderer(String location, IBaseContext<IBaseNode> context) {
        super(location, context);
    }

    public TabPathContextRenderer(String location, IBaseContext<IBaseNode> context, boolean sort) {
        super(location, context, sort);
    }

    @SuppressWarnings({"unchecked"})
    protected void process(IBaseContext<IBaseNode> context, BufferedWriter out) throws IOException, ContextRendererException {
        ArrayList<IBaseNode<IBaseNode, IBaseNodeData>> nodeQ = new ArrayList<>();
        nodeQ.add(context.getRoot());
        IBaseNode<IBaseNode, IBaseNodeData> curNode;
        while (!nodeQ.isEmpty() &&
                !Thread.currentThread().isInterrupted()) {
            curNode = nodeQ.remove(0);
            if (0 == curNode.getChildCount()) {
                out.write(getPathToRoot(curNode));
            }
            progress();
            if (curNode.getChildCount() > 0) {
                Iterator<IBaseNode> children;
                if (sort) {
                    ArrayList<IBaseNode> childrenList = new ArrayList<>(curNode.getChildrenList());
                    Collections.sort(childrenList, Node.NODE_NAME_COMPARATOR);
                    children = childrenList.iterator();
                } else {
                    children = curNode.getChildren();
                }
                while (children.hasNext()) {
                    nodeQ.add(children.next());
                }
            }
        }
    }

    private String getPathToRoot(IBaseNode node) {
        StringBuilder result = new StringBuilder(node.getNodeData().getName());
        IBaseNode curNode = node.getParent();
        while (null != curNode) {
            result.insert(0, curNode.getNodeData().getName() + "\t");
            curNode = curNode.getParent();
        }
        result.append("\n");
        return result.toString();
    }

    public String getDescription() {
        return ILoader.TXT_FILES;
    }

    @Override
    public AsyncTask<Void, IBaseNode> asyncRender(IBaseContext<IBaseNode> context, String location) {
        return new TabPathContextRenderer(location, context, sort);
    }
}