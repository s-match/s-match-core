package it.unitn.disi.smatch.loaders.mapping;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.filters.BaseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for mapping loaders.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseMappingLoader extends AsyncTask<IContextMapping<INode>, IMappingElement<INode>> implements IMappingLoader {

    private static final Logger log = LoggerFactory.getLogger(BaseMappingLoader.class);

    protected final IMappingFactory mappingFactory;

    // for task parameters
    protected final String location;
    protected final IContext source;
    protected final IContext target;

    protected BaseMappingLoader(IMappingFactory mappingFactory) {
        this.mappingFactory = mappingFactory;
        this.location = null;
        this.source = null;
        this.target = null;
    }

    protected BaseMappingLoader(IMappingFactory mappingFactory, IContext source, IContext target, String location) {
        this.mappingFactory = mappingFactory;
        this.location = location;
        this.source = source;
        this.target = target;
    }

    public IContextMapping<INode> loadMapping(IContext source, IContext target, String fileName) throws MappingLoaderException {
        if (log.isInfoEnabled()) {
            log.info("Loading mapping: " + fileName);
        }

        IContextMapping<INode> mapping = process(source, target, fileName);

        if (null != mapping) {
            if (log.isInfoEnabled()) {
                log.info("Mapping contains " + mapping.size() + " links");
            }
            if (log.isTraceEnabled()) {
                BaseFilter.countRelationStats(mapping);
            }
        }
        return mapping;
    }

    @Override
    protected IContextMapping<INode> doInBackground() throws Exception {
        final String threadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(Thread.currentThread().getName()
                    + " [" + this.getClass().getSimpleName() + ": mapping.location=" + location + "]");

            return loadMapping(source, target, location);
        } finally {
            Thread.currentThread().setName(threadName);
        }

    }

    protected abstract IContextMapping<INode> process(IContext source, IContext target, String fileName) throws MappingLoaderException;
}
