package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.data.mappings.IMappingFactory;

/**
 * Base class for filters.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseFilter implements IMappingFilter {

    protected final IMappingFactory mappingFactory;

    protected BaseFilter(IMappingFactory mappingFactory) {
        this.mappingFactory = mappingFactory;
    }
}
