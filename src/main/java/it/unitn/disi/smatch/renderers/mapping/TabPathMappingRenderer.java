package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.data.util.MappingProgressContainer;
import it.unitn.disi.smatch.loaders.ILoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Renders the mapping in a plain text file.
 * Format: source-node tab tab relation tab tab target-node.
 * Source and target nodes are rendered with tabs separating path to root levels.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class TabPathMappingRenderer extends BaseFileMappingRenderer implements IMappingRenderer {

    @Override
    protected void process(IContextMapping<INode> mapping, BufferedWriter out, MappingProgressContainer progressContainer) throws IOException {
        for (IMappingElement<INode> mappingElement : mapping) {
            String sourceConceptName = getPathToRoot(mappingElement.getSource());
            String targetConceptName = getPathToRoot(mappingElement.getTarget());
            char relation = mappingElement.getRelation();

            out.write(sourceConceptName + "\t\t" + relation + "\t\t" + targetConceptName + "\n");

            progressContainer.countRelation(relation);
            progressContainer.progress();
        }
    }

    private String getPathToRoot(INode node) {
        StringBuilder result = new StringBuilder();
        ArrayList<String> path = new ArrayList<>();
        INode curNode = node;
        while (null != curNode) {
            path.add(0, curNode.getNodeData().getName());
            curNode = curNode.getParent();
        }
        for (int i = 0; i < path.size(); i++) {
            if (0 == i) {
                result.append(path.get(i));
            } else {
                result.append("\t").append(path.get(i));
            }
        }
        return result.toString();
    }

    public String getDescription() {
        return ILoader.TXT_FILES;
    }
}