package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.data.util.MappingProgressContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for mapping renderers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseMappingRenderer implements IMappingRenderer {

    private static final Logger log = LoggerFactory.getLogger(BaseMappingRenderer.class);

    public void render(IContextMapping<INode> mapping, String outputFile) throws MappingRendererException {
        MappingProgressContainer progressContainer = new MappingProgressContainer(mapping.size(), log);

        process(mapping, outputFile, progressContainer);

        log.info("mapping size: " + mapping.size());
        progressContainer.reportStats();
    }

    protected abstract void process(IContextMapping<INode> mapping, String outputFile, MappingProgressContainer progressContainer) throws MappingRendererException;
}
