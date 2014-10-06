package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Writes the mapping eXcluding Top node mappings.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class PlainXTopMappingRenderer extends PlainMappingRenderer {

    public PlainXTopMappingRenderer() {
        super(null, null);
    }

    public PlainXTopMappingRenderer(String location, IContextMapping<INode> mapping) {
        super(location, mapping);
    }

    @Override
    public AsyncTask<Void, IMappingElement<INode>> asyncRender(IContextMapping<INode> mapping, String location) {
        return new PlainXTopMappingRenderer(location, mapping);
    }

    @Override
    protected void process(IContextMapping<INode> mapping, BufferedWriter out) throws IOException {
        for (IMappingElement<INode> mappingElement : mapping) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }

            if (mappingElement.getSource().hasParent() && mappingElement.getTarget().hasParent()) {
                String sourceConceptName = getNodePathToRoot(mappingElement.getSource());
                String targetConceptName = getNodePathToRoot(mappingElement.getTarget());
                char relation = mappingElement.getRelation();

                out.write(sourceConceptName + "\t" + relation + "\t" + targetConceptName + "\n");

                progress();
            }
        }
    }
}