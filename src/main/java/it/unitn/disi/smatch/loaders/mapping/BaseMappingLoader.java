package it.unitn.disi.smatch.loaders.mapping;

import it.unitn.disi.common.components.Configurable;
import it.unitn.disi.common.components.ConfigurableException;
import it.unitn.disi.common.components.ConfigurationKeyMissingException;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Properties;

/**
 * Base class for mapping loaders.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseMappingLoader extends Configurable implements IMappingLoader {

    private static final Logger log = LoggerFactory.getLogger(BaseMappingLoader.class);

    private static final String MAPPING_FACTORY_KEY = "mappingFactory";
    protected IMappingFactory mappingFactory = null;

    protected int lg;
    protected int mg;
    protected int eq;
    protected int dj;

    protected long counter;
    protected long cntLoaded;

    @Override
    public boolean setProperties(Properties newProperties) throws ConfigurableException {
        Properties oldProperties = new Properties();
        oldProperties.putAll(properties);

        boolean result = super.setProperties(newProperties);
        if (result) {
            if (newProperties.containsKey(MAPPING_FACTORY_KEY)) {
                mappingFactory = (IMappingFactory) configureComponent(mappingFactory, oldProperties, newProperties, "mapping factory", MAPPING_FACTORY_KEY, IMappingFactory.class);
            } else {
                throw new ConfigurationKeyMissingException(MAPPING_FACTORY_KEY);
            }
        }
        return result;
    }

    public IContextMapping<INode> loadMapping(IContext source, IContext target, String fileName) throws MappingLoaderException {
        if (log.isInfoEnabled()) {
            log.info("Loading mapping: " + fileName);
        }
        lg = mg = eq = dj = 0;
        cntLoaded = 0;

        IContextMapping<INode> mapping = mappingFactory.getContextMappingInstance(source, target);

        process(mapping, source, target, fileName);

        reportStats(mapping);
        return mapping;
    }

    protected abstract void process(IContextMapping<INode> mapping, IContext source, IContext target, String fileName) throws MappingLoaderException;

    protected void reportStats(IContextMapping<INode> mapping) {
        if (log.isInfoEnabled()) {
            log.info("Loading mapping finished. Loaded " + cntLoaded + " links");
            log.info("Mapping contains " + mapping.size() + " links");
            log.info("LG: " + lg);
            log.info("MG: " + mg);
            log.info("EQ: " + eq);
            log.info("DJ: " + dj);
        }
    }

    protected void reportProgress() {
        counter++;
        if (0 == (counter % 1000)) {
            if (log.isInfoEnabled()) {
                log.info("Loaded links: " + counter);
            }
        }
    }

    protected void countRelation(final char relation) {
        switch (relation) {
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
}
