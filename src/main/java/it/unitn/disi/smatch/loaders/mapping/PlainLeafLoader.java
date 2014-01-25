package it.unitn.disi.smatch.loaders.mapping;

import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.ILoader;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;

/**
 * Loads the mapping consisting of only leaf nodes. Sometimes happens in thesauri.
 * <p/>
 * Needs mappingFactory configuration parameter, which should point to an instance of a class implementing
 * {@link it.unitn.disi.smatch.data.mappings.IMappingFactory} interface.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class PlainLeafLoader extends PlainMappingLoader {

    private static final Logger log = LoggerFactory.getLogger(PlainLeafLoader.class);

    /**
     * Creates hash map for nodes which contains path from root to node for each node.
     *
     * @param context a context
     * @return a hash table which contains path from root to node for each node
     */
    protected HashMap<String, INode> createHash(IContext context) {
        HashMap<String, INode> result = new HashMap<String, INode>();

        int nodeCount = 0;
        for (INode node : context.getNodesList()) {
            result.put("\\Top\\" + node.getNodeData().getName(), node);
            result.put(node.getNodeData().getName(), node);
            nodeCount++;
        }

        if (log.isInfoEnabled()) {
            log.info("Created hash for " + nodeCount + " nodes...");
        }

        return result;
    }

    public String getDescription() {
        return ILoader.TXT_FILES;
    }
}
