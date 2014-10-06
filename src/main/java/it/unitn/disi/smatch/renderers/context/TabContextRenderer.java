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
 * Renders a context in a tab-indented file.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class TabContextRenderer extends BaseFileContextRenderer<IBaseContext<IBaseNode>, IBaseNode> implements IAsyncBaseContextRenderer<IBaseContext<IBaseNode>, IBaseNode> {

    public TabContextRenderer() {
        super();
    }

    public TabContextRenderer(boolean sort) {
        super(sort);
    }

    public TabContextRenderer(String location, IBaseContext<IBaseNode> context) {
        super(location, context);
    }

    public TabContextRenderer(String location, IBaseContext<IBaseNode> context, boolean sort) {
        super(location, context, sort);
    }

    @SuppressWarnings("unchecked")
    protected void process(IBaseContext<IBaseNode> context, BufferedWriter out) throws IOException, ContextRendererException {
        ArrayList<IBaseNode> nodeQ = new ArrayList<>();
        String level = "";
        nodeQ.add(context.getRoot());
        IBaseNode<IBaseNode, IBaseNodeData> curNode;
        String line;
        while (!nodeQ.isEmpty() &&
                !Thread.currentThread().isInterrupted()) {
            curNode = nodeQ.remove(0);
            if (null == curNode) {
                level = level.substring(1);
            } else {
                line = level + curNode.getNodeData().getName() + "\n";
                out.write(line);
                progress();

                if (curNode.getChildCount() > 0) {
                    level = level + "\t";
                    nodeQ.add(0, null);
                    Iterator<IBaseNode> children;
                    if (sort) {
                        ArrayList<IBaseNode> childrenList = new ArrayList<>(curNode.getChildrenList());
                        Collections.sort(childrenList, Node.NODE_NAME_COMPARATOR);
                        children = childrenList.iterator();
                    } else {
                        children = curNode.getChildren();
                    }
                    int idx = 0;
                    //adding to the top of the queue
                    while (children.hasNext()) {
                        nodeQ.add(idx, children.next());
                        idx++;
                    }
                }
            }
        }
    }

    public String getDescription() {
        return ILoader.TXT_FILES;
    }

    @Override
    public AsyncTask<Void, IBaseNode> asyncRender(IBaseContext<IBaseNode> context, String location) {
        return new TabContextRenderer(location, context, sort);
    }
}