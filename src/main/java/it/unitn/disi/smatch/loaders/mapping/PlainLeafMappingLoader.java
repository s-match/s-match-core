package it.unitn.disi.smatch.loaders.mapping;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.ILoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Loads the mapping consisting of only leaf nodes. Sometimes happens in thesauri.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class PlainLeafMappingLoader extends PlainMappingLoader {

    private static final Logger log = LoggerFactory.getLogger(PlainLeafMappingLoader.class);

    public PlainLeafMappingLoader(IMappingFactory mappingFactory) {
        super(mappingFactory);
    }

    public PlainLeafMappingLoader(IMappingFactory mappingFactory, IContext source, IContext target, String fileName) {
        super(mappingFactory, source, target, fileName);
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncLoad(IContext source, IContext target, String fileName) {
        return new PlainLeafMappingLoader(mappingFactory, source, target, fileName);
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
        for (Iterator<INode> i = context.nodeIterator(); i.hasNext(); ) {
            INode node = i.next();
            result.put("\\Top\\" + node.nodeData().getName(), node);
            result.put(node.nodeData().getName(), node);
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
