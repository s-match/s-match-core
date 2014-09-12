package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.data.util.MappingProgressContainer;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Writes the mapping eXcluding Top node mappings.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class PlainXTopMappingRenderer extends PlainMappingRenderer {

    @Override
    protected void process(IContextMapping<INode> mapping, BufferedWriter out, MappingProgressContainer progressContainer) throws IOException {
        for (IMappingElement<INode> mappingElement : mapping) {
            if (mappingElement.getSource().hasParent() && mappingElement.getTarget().hasParent()) {
                String sourceConceptName = getNodePathToRoot(mappingElement.getSource());
                String targetConceptName = getNodePathToRoot(mappingElement.getTarget());
                char relation = mappingElement.getRelation();

                out.write(sourceConceptName + "\t" + relation + "\t" + targetConceptName + "\n");

                progressContainer.countRelation(relation);
                progressContainer.progress();
            }
        }
    }
}