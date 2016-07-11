package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.INode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for filters.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseFilter extends AsyncTask<IContextMapping<INode>, IMappingElement<INode>> implements IMappingFilter {

    private static final Logger log = LoggerFactory.getLogger(BaseFilter.class);

    protected final IMappingFactory mappingFactory;

    // for task parameters
    protected final IContextMapping<INode> mapping;

    protected BaseFilter(IMappingFactory mappingFactory) {
        this.mappingFactory = mappingFactory;
        this.mapping = null;

        if (null == mappingFactory) {
            throw new IllegalArgumentException("mappingFactory required!");
        }
    }

    protected BaseFilter(IMappingFactory mappingFactory, IContextMapping<INode> mapping) {
        if (null == mappingFactory) {
            throw new IllegalArgumentException("mappingFactory required!");
        }
        if (null == mapping) {
            throw new IllegalArgumentException("mapping required!");
        }
        this.mappingFactory = mappingFactory;
        this.mapping = mapping;
        setTotal(mapping.size());
    }

    @Override
    public IContextMapping<INode> filter(IContextMapping<INode> mapping) throws MappingFilterException {
        if (log.isInfoEnabled()) {
            log.info("Filtering started. Elements: " + mapping.size());
        }
        if (log.isTraceEnabled()) {
            countRelationStats(mapping);
        }


        IContextMapping<INode> result = process(mapping);

        if (log.isInfoEnabled()) {
            log.info("Filtering finished. Elements: " + result.size());
        }
        if (log.isTraceEnabled()) {
            countRelationStats(mapping);
        }
        return result;
    }

    protected abstract IContextMapping<INode> process(IContextMapping<INode> mapping) throws MappingFilterException;

    @Override
    protected IContextMapping<INode> doInBackground() throws Exception {
        final String threadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(Thread.currentThread().getName()
                    + " [" + this.getClass().getSimpleName() + ": mapping.size=" + mapping.size() + "]");

            return filter(mapping);
        } finally {
            Thread.currentThread().setName(threadName);
        }
    }

    public static void countRelationStats(IContextMapping<INode> mapping) {
        // stats
        long lg = 0;
        long mg = 0;
        long eq = 0;
        long dj = 0;
        for (IMappingElement<INode> e : mapping) {
            switch (e.getRelation()) {
                case IMappingElement.LESS_GENERAL: {
                    lg++;
                    break;
                }
                case IMappingElement.MORE_GENERAL: {
                    mg++;
                    break;
                }
                case IMappingElement.EQUIVALENCE: {
                    eq++;
                    break;
                }
                case IMappingElement.DISJOINT: {
                    dj++;
                    break;
                }
                default:
                    break;
            }
        }
        log.trace("LG:\t" + lg + "\tMG:\t" + mg + "\tEQ:\t" + eq + "\tDJ:\t" + dj);
    }
}