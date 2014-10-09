package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.ILoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Renders the mapping in a plain text file.
 * Format: source-node tab relation target-node.
 * Source and target nodes are rendered with \ separating path to root levels.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class PlainMappingRenderer extends BaseFileMappingRenderer implements IMappingRenderer, IAsyncMappingRenderer {

    private static final Logger log = LoggerFactory.getLogger(PlainMappingRenderer.class);

    public PlainMappingRenderer() {
    }

    public PlainMappingRenderer(String location, IContextMapping<INode> mapping) {
        super(location, mapping);
    }

    @Override
    public AsyncTask<Void, IMappingElement<INode>> asyncRender(IContextMapping<INode> mapping, String location) {
        return new PlainMappingRenderer(location, mapping);
    }

    @Override
    protected void process(IContextMapping<INode> mapping, BufferedWriter out) throws IOException {
        for (IMappingElement<INode> mappingElement : mapping) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }

            String sourceConceptName = getNodePathToRoot(mappingElement.getSource());
            String targetConceptName = getNodePathToRoot(mappingElement.getTarget());
            char relation = mappingElement.getRelation();

            out.write(sourceConceptName + "\t" + relation + "\t" + targetConceptName + "\n");

            progress();
        }
    }

    protected String getNodePathToRoot(INode node) {
        StringBuilder sb = new StringBuilder();
        INode parent = node;
        while (null != parent) {
            if (parent.nodeData().getName().contains("\\")) {
                log.debug("source: replacing \\ in: " + parent.nodeData().getName());
                sb.insert(0, "\\" + parent.nodeData().getName().replaceAll("\\\\", "/"));
            } else {
                sb.insert(0, "\\" + parent.nodeData().getName());
            }
            parent = parent.getParent();
        }
        return sb.toString();
    }

    public String getDescription() {
        return ILoader.TXT_FILES;
    }
}