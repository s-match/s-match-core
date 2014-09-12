package it.unitn.disi.smatch.loaders.context;

import it.unitn.disi.smatch.data.trees.IBaseContext;
import it.unitn.disi.smatch.data.trees.IBaseNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for loaders.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseContextLoader<E extends IBaseContext<? extends IBaseNode>> implements IBaseContextLoader<E> {

    private static final Logger log = LoggerFactory.getLogger(BaseContextLoader.class);

    protected void createIds(E result) {
        log.debug("Creating ids for context...");
        int nodesParsed = 0;
        for (IBaseNode node : result.getNodesList()) {
            node.getNodeData().setId("n" + Integer.toString(nodesParsed));
            nodesParsed++;
        }
    }
}