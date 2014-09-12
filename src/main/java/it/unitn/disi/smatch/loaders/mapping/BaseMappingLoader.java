package it.unitn.disi.smatch.loaders.mapping;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.data.util.MappingProgressContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for mapping loaders.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseMappingLoader implements IMappingLoader {

    private static final Logger log = LoggerFactory.getLogger(BaseMappingLoader.class);

    protected final IMappingFactory mappingFactory;

    protected BaseMappingLoader(IMappingFactory mappingFactory) {
        this.mappingFactory = mappingFactory;
    }

    public IContextMapping<INode> loadMapping(IContext source, IContext target, String fileName) throws MappingLoaderException {
        if (log.isInfoEnabled()) {
            log.info("Loading mapping: " + fileName);
        }

        IContextMapping<INode> mapping = mappingFactory.getContextMappingInstance(source, target);

        MappingProgressContainer progressContainer = new MappingProgressContainer(log);
        process(mapping, source, target, fileName, progressContainer);

        log.info("Mapping contains " + mapping.size() + " links");
        progressContainer.reportStats();
        return mapping;
    }

    protected abstract void process(IContextMapping<INode> mapping, IContext source, IContext target, String fileName, MappingProgressContainer progressContainer) throws MappingLoaderException;
}
