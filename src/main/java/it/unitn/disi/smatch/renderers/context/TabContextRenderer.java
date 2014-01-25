package it.unitn.disi.smatch.renderers.context;

import it.unitn.disi.smatch.data.trees.*;
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
public class TabContextRenderer extends BaseFileContextRenderer<IBaseContext<IBaseNode<IBaseNode, IBaseNodeData>>> {

    @SuppressWarnings({"unchecked"})
    protected void process(IBaseContext<IBaseNode<IBaseNode, IBaseNodeData>> context, BufferedWriter out) throws IOException, ContextRendererException {
        ArrayList<IBaseNode<IBaseNode, IBaseNodeData>> nodeQ = new ArrayList<IBaseNode<IBaseNode, IBaseNodeData>>();
        String level = "";
        nodeQ.add(context.getRoot());
        IBaseNode<IBaseNode, IBaseNodeData> curNode;
        String line;
        while (!nodeQ.isEmpty()) {
            curNode = nodeQ.remove(0);
            if (null == curNode) {
                level = level.substring(1);
            } else {
                line = level + curNode.getNodeData().getName() + "\n";
                out.write(line);
                reportProgress();

                if (curNode.getChildCount() > 0) {
                    level = level + "\t";
                    nodeQ.add(0, null);
                    Iterator<IBaseNode> children;
                    if (sort) {
                        ArrayList<IBaseNode> childrenList = new ArrayList<IBaseNode>(curNode.getChildrenList());
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
        reportStats(context);
    }

    public String getDescription() {
        return ILoader.TXT_FILES;
    }
}