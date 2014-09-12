package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.data.util.MappingProgressContainer;
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
public class PlainMappingRenderer extends BaseFileMappingRenderer implements IMappingRenderer {

    private static final Logger log = LoggerFactory.getLogger(PlainMappingRenderer.class);

    @Override
    protected void process(IContextMapping<INode> mapping, BufferedWriter out, MappingProgressContainer progressContainer) throws IOException {
        for (IMappingElement<INode> mappingElement : mapping) {
            String sourceConceptName = getNodePathToRoot(mappingElement.getSource());
            String targetConceptName = getNodePathToRoot(mappingElement.getTarget());
            char relation = mappingElement.getRelation();

            out.write(sourceConceptName + "\t" + relation + "\t" + targetConceptName + "\n");

            progressContainer.countRelation(relation);
            progressContainer.progress();
        }
    }

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

    public String getDescription() {
        return ILoader.TXT_FILES;
    }
}