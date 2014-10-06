package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.mapping.IMappingLoader;
import it.unitn.disi.smatch.loaders.mapping.MappingLoaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for filters which use another mapping for filtering.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseMappingBasedFilter extends BaseFilter {

    private static final Logger log = LoggerFactory.getLogger(BaseMappingBasedFilter.class);

    protected final IMappingLoader mappingLoader;
    protected final String mappingLocation;

    protected BaseMappingBasedFilter(IMappingFactory mappingFactory, IMappingLoader mappingLoader, String mappingLocation) {
        super(mappingFactory);

        if (null == mappingLoader) {
            throw new IllegalArgumentException("mappingLoader required!");
        }
        if (null == mappingLocation) {
            throw new IllegalArgumentException("mappingLocation required!");
        }
        this.mappingLoader = mappingLoader;
        this.mappingLocation = mappingLocation;
    }

    protected BaseMappingBasedFilter(IMappingFactory mappingFactory, IMappingLoader mappingLoader, String mappingLocation, IContextMapping<INode> mapping) {
        super(mappingFactory, mapping);

        if (null == mappingLoader) {
            throw new IllegalArgumentException("mappingLoader required!");
        }
        if (null == mappingLocation) {
            throw new IllegalArgumentException("mappingLocation required!");
        }
        this.mappingLoader = mappingLoader;
        this.mappingLocation = mappingLocation;
    }

    protected IContextMapping<INode> loadMapping(IContextMapping<INode> mapping) throws MappingFilterException {
        try {
            log.debug("Loading filter mapping...");
            return mappingLoader.loadMapping(mapping.getSourceContext(), mapping.getTargetContext(), mappingLocation);
        } catch (MappingLoaderException e) {
            throw new MappingFilterException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }
}