package it.unitn.disi.smatch.loaders.context;

import it.unitn.disi.smatch.data.trees.Context;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.ILoader;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Loads context from a tab-delimited file, one line per path to root.
 * Expects a single-rooted hierarchy, otherwise adds an artificial "Top" node.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class TabPathContextLoader extends BaseFileContextLoader<IContext> implements IContextLoader {

    @Override
    protected IContext process(BufferedReader input) throws IOException {
        IContext result = new Context();
        result.createRoot("Top");

        nodesParsed = 0;
        String line;
        while ((line = input.readLine()) != null &&
                !line.startsWith("#") &&
                !line.isEmpty()) {

            createNode(result, line);

            nodesParsed++;
        }

        if (1 == result.getRoot().getChildCount()) {
            INode newRoot = result.getRoot().getChildAt(0);
            newRoot.setParent(null);
            result.setRoot(newRoot);
        }
        return result;
    }

    private void createNode(IContext result, String line) {
        String[] path = line.split("\t");
        INode curNode = result.getRoot();
        for (String node : path) {
            INode child = findNode(curNode, node);
            if (null == child) {
                child = result.createNode(node);
                curNode.addChild(child);
            }
            curNode = child;
        }
    }

    private INode findNode(INode curNode, String node) {
        INode result = null;
        for (INode child : curNode.getChildrenList()) {
            if (node.equals(child.getNodeData().getName())) {
                result = child;
                break;
            }
        }
        return result;
    }

    public String getDescription() {
        return ILoader.TXT_FILES;
    }
}