package it.unitn.disi.smatch.loaders.mapping;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.data.util.MappingProgressContainer;
import it.unitn.disi.smatch.loaders.ILoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Loads the mapping as written by {@link it.unitn.disi.smatch.renderers.mapping.PlainMappingRenderer}.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class PlainMappingLoader extends BaseFileMappingLoader {

    private static final Logger log = LoggerFactory.getLogger(PlainMappingLoader.class);

    protected PlainMappingLoader(IMappingFactory mappingFactory) {
        super(mappingFactory);
    }

    @Override
    protected void process(IContextMapping<INode> mapping, IContext source, IContext target, BufferedReader reader, MappingProgressContainer progressContainer) throws IOException {
        HashMap<String, INode> sNodes = createHash(source);
        HashMap<String, INode> tNodes = createHash(target);

        String line;
        while ((line = reader.readLine()) != null &&
                !line.startsWith("#") &&
                !line.isEmpty()) {

            INode sourceNode;
            INode targetNode;
            char rel;

            String[] tokens = line.split("\t");
            if (3 != tokens.length) {
                if (log.isWarnEnabled()) {
                    log.warn("Unrecognized mapping format: " + line);
                }
            } else {
                //tokens = left \t relation \t right
                rel = tokens[1].toCharArray()[0];

                sourceNode = sNodes.get(tokens[0]);
                if (null == sourceNode) {
                    if (log.isWarnEnabled()) {
                        log.warn("Could not find source node: " + tokens[0]);
                    }
                }

                targetNode = tNodes.get(tokens[2]);
                if (!tNodes.containsKey(tokens[2])) {
                    if (log.isWarnEnabled()) {
                        log.warn("Could not find target node: " + tokens[2]);
                    }
                }

                if ((null != sourceNode) && (null != targetNode)) {
                    mapping.setRelation(sourceNode, targetNode, rel);
                    progressContainer.countRelation(rel);
                    progressContainer.progress();
                } else {
                    if (log.isWarnEnabled()) {
                        log.warn("Could not find mapping: " + line);
                    }
                }
            }
        }
    }

    /**
     * Gets the path of a node from root for hash mapping.
     *
     * @param node the interface of data structure of input node
     * @return the string of the path from root to node
     */
    protected String getNodePathToRoot(INode node) {
        StringBuilder sb = new StringBuilder();
        INode parent = node;
        while (null != parent) {
            if (parent.getNodeData().getName().contains("\\")) {
                log.debug("source: replacing \\ in: " + parent.getNodeData().getName());
                sb.insert(0, "\\" + parent.getNodeData().getName().replaceAll("\\\\", "/"));
            } else {
                sb.insert(0, "\\" + parent.getNodeData().getName());
            }
            parent = parent.getParent();
        }
        return sb.toString();
    }

    /**
     * Creates hash map for nodes which contains path from root to node for each node.
     *
     * @param context a context
     * @return a hash table which contains path from root to node for each node
     */
    protected HashMap<String, INode> createHash(IContext context) {
        HashMap<String, INode> result = new HashMap<>();

        int nodeCount = 0;
        for (INode node : context.getNodesList()) {
            result.put(getNodePathToRoot(node), node);
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
