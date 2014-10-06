package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Writes the mapping sorting it by relation: disjointness, equivalent, less and more generality.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class RelationSortingPlainMappingRenderer extends PlainMappingRenderer {

    private static final Logger log = LoggerFactory.getLogger(RelationSortingPlainMappingRenderer.class);

    public RelationSortingPlainMappingRenderer() {
        super(null, null);
    }

    public RelationSortingPlainMappingRenderer(String location, IContextMapping<INode> mapping) {
        super(location, mapping);
    }

    @Override
    public AsyncTask<Void, IMappingElement<INode>> asyncRender(IContextMapping<INode> mapping, String location) {
        return new RelationSortingPlainMappingRenderer(location, mapping);
    }

    @Override
    protected void process(IContextMapping<INode> mapping, BufferedWriter out) throws IOException {
        char[] relations = {IMappingElement.DISJOINT, IMappingElement.EQUIVALENCE, IMappingElement.LESS_GENERAL, IMappingElement.MORE_GENERAL};

        for (char relation : relations) {
            int relationsRendered = 0;
            if (log.isInfoEnabled()) {
                log.info("Rendering: " + relation);
            }

            for (IMappingElement<INode> mappingElement : mapping) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                if (mappingElement.getRelation() == relation) {
                    String sourceConceptName = getNodePathToRoot(mappingElement.getSource());
                    String targetConceptName = getNodePathToRoot(mappingElement.getTarget());

                    out.write(sourceConceptName + "\t" + relation + "\t" + targetConceptName + "\n");
                    relationsRendered++;

                    progress();
                }
            }

            if (0 < relationsRendered) {
                out.write("\n");// relation separator
            }
        }
    }
}