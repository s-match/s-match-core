package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Writes the mapping sorting it by relation: disjointness, equivalent, less and more generality.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class RelationSortingPlainMappingRenderer extends PlainMappingRenderer {

    private static final Logger log = LoggerFactory.getLogger(RelationSortingPlainMappingRenderer.class);

    @Override
    protected void process(IContextMapping<INode> mapping, BufferedWriter out) throws IOException {
        char[] relations = {IMappingElement.DISJOINT, IMappingElement.EQUIVALENCE, IMappingElement.LESS_GENERAL, IMappingElement.MORE_GENERAL};

        for (char relation : relations) {
            int relationsRendered = 0;
            if (log.isInfoEnabled()) {
                log.info("Rendering: " + relation);
            }

            for (IMappingElement<INode> mappingElement : mapping) {
                if (mappingElement.getRelation() == relation) {
                    String sourceConceptName = getNodePathToRoot(mappingElement.getSource());
                    String targetConceptName = getNodePathToRoot(mappingElement.getTarget());

                    out.write(sourceConceptName + "\t" + relation + "\t" + targetConceptName + "\n");
                    relationsRendered++;

                    reportProgress();
                }
            }

            switch (relation) {
                case IMappingElement.LESS_GENERAL: {
                    lg = relationsRendered;
                    break;
                }
                case IMappingElement.MORE_GENERAL: {
                    mg = relationsRendered;
                    break;
                }
                case IMappingElement.EQUIVALENCE: {
                    eq = relationsRendered;
                    break;
                }
                case IMappingElement.DISJOINT: {
                    dj = relationsRendered;
                    break;
                }
                default:
                    break;
            }

            if (0 < relationsRendered) {
                out.write("\n");// relation separator
            }
        }// for relation
    }
}