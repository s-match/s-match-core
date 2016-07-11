package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.filters.BaseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for mapping renderers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseMappingRenderer extends AsyncTask<Void, IMappingElement<INode>> implements IMappingRenderer {

    private static final Logger log = LoggerFactory.getLogger(BaseMappingRenderer.class);

    // for task parameters
    protected final String location;
    protected final IContextMapping<INode> mapping;

    protected BaseMappingRenderer() {
        this.location = null;
        this.mapping = null;
    }

    protected BaseMappingRenderer(String location, IContextMapping<INode> mapping) {
        this.location = location;
        this.mapping = mapping;
        setTotal(mapping.size());
    }

    @Override
    protected Void doInBackground() throws Exception {
        final String threadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(Thread.currentThread().getName()
                    + " [" + this.getClass().getSimpleName()
                    + ": mappping.size=" + mapping.size()
                    + ", location=" + location + "]");

            render(mapping, location);
            return null;
        } finally {
            Thread.currentThread().setName(threadName);
        }

    }

    public void render(IContextMapping<INode> mapping, String location) throws MappingRendererException {
        if (log.isInfoEnabled()) {
            log.info("Mapping contains " + mapping.size() + " links");
        }

        process(mapping, location);

        if (log.isInfoEnabled()) {
            log.info("Mapping rendering finished. Links: " + getProgress());
        }
        if (log.isTraceEnabled()) {
            BaseFilter.countRelationStats(mapping);
        }
    }

    protected abstract void process(IContextMapping<INode> mapping, String outputFile) throws MappingRendererException;
}