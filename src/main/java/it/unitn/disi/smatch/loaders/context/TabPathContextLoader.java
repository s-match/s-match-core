package it.unitn.disi.smatch.loaders.context;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.trees.Context;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.ILoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * Loads context from a tab-delimited file, one line per path to root.
 * Expects a single-rooted hierarchy, otherwise adds an artificial "Top" node.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class TabPathContextLoader extends BaseFileContextLoader<IContext, INode> implements IContextLoader, IAsyncContextLoader {

    private static final Logger log = LoggerFactory.getLogger(TabPathContextLoader.class);

    public TabPathContextLoader() {
    }

    public TabPathContextLoader(String location) {
        super(location);
    }

    @Override
    protected IContext process(BufferedReader input) throws IOException {
        IContext result = new Context();
        result.createRoot("Top");

        String line;
        while ((line = input.readLine()) != null &&
                !line.startsWith("#") &&
                !line.isEmpty() &&
                !Thread.currentThread().isInterrupted()) {

            createNode(result, line);

            setProgress(getProgress() + 1);
        }

        if (1 == result.getRoot().getChildCount()) {
            INode newRoot = result.getRoot().getChildAt(0);
            newRoot.setParent(null);
            result.setRoot(newRoot);
        }

        if (Thread.currentThread().isInterrupted()) {
            result = null;
        } else {
            log.info("Parsed nodes: " + getProgress());
        }
        return result;
    }

    @Override
    public AsyncTask<IContext, INode> asyncLoad(String location) {
        return new TabPathContextLoader(location);
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

    private INode findNode(INode curNode, String nodeName) {
        INode result = null;
        for (Iterator<INode> i = curNode.getChildren(); i.hasNext(); ) {
            INode child = i.next();
            if (nodeName.equals(child.getNodeData().getName())) {
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