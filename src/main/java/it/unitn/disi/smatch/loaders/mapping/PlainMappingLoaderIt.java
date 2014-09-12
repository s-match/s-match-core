package it.unitn.disi.smatch.loaders.mapping;

import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Version with an iterator.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class PlainMappingLoaderIt extends PlainMappingLoader {

    private static final Logger log = LoggerFactory.getLogger(PlainMappingLoaderIt.class);

    protected PlainMappingLoaderIt(IMappingFactory mappingFactory) {
        super(mappingFactory);
    }

    @Override
    protected HashMap<String, INode> createHash(IContext context) {
        HashMap<String, INode> result = new HashMap<>();

        int nodeCount = 0;
        for (Iterator<INode> i = context.getNodes(); i.hasNext(); ) {
            INode node = i.next();
            result.put(getNodePathToRoot(node), node);
            nodeCount++;
        }

        if (log.isInfoEnabled()) {
            log.info("Created hash for " + nodeCount + " nodes...");
        }

        return result;
    }

}
