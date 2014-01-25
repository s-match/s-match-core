package it.unitn.disi.smatch.filters;

import it.unitn.disi.common.components.ConfigurableException;
import it.unitn.disi.common.components.ConfigurationKeyMissingException;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.mapping.IMappingLoader;
import it.unitn.disi.smatch.loaders.mapping.MappingLoaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Base class for filters which use another mapping for filtering. Needs the
 * following configuration parameters:
 * <p/>
 * mappingLoader - an instance of IMappingLoader
 * <p/>
 * mapping - location of the mapping
 * <p/>
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseMappingBasedFilter extends BaseFilter {

    private static final Logger log = LoggerFactory.getLogger(BaseMappingBasedFilter.class);

    private static final String MAPPING_LOADER_KEY = "mappingLoader";
    protected IMappingLoader mappingLoader = null;

    private static final String MAPPING_KEY = "mapping";
    protected String mappingLocation = null;
    protected IContextMapping<INode> filterMapping = null;

    @Override
    public boolean setProperties(Properties newProperties) throws ConfigurableException {
        Properties oldProperties = new Properties();
        oldProperties.putAll(properties);

        boolean result = super.setProperties(newProperties);
        if (result) {
            if (newProperties.containsKey(MAPPING_LOADER_KEY)) {
                mappingLoader = (IMappingLoader) configureComponent(mappingLoader, oldProperties, newProperties, "mapping loader", MAPPING_LOADER_KEY, IMappingLoader.class);
            } else {
                throw new ConfigurationKeyMissingException(MAPPING_LOADER_KEY);
            }

            if (newProperties.containsKey(MAPPING_KEY)) {
                mappingLocation = newProperties.getProperty(MAPPING_KEY);
            } else {
                throw new ConfigurationKeyMissingException(MAPPING_KEY);
            }
        }
        return result;
    }

    public IContextMapping<INode> filter(IContextMapping<INode> mapping) throws MappingFilterException {
        //load the mapping
        try {
            log.debug("Loading filter mapping...");
            filterMapping = mappingLoader.loadMapping(mapping.getSourceContext(), mapping.getTargetContext(), mappingLocation);
            log.debug("Loaded filter mapping...");
        } catch (MappingLoaderException e) {
            throw new MappingFilterException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
        return mapping;
    }
}
