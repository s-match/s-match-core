package it.unitn.disi.smatch;

import it.unitn.disi.common.components.Configurable;
import it.unitn.disi.common.components.ConfigurableException;
import it.unitn.disi.smatch.classifiers.IContextClassifier;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.Context;
import it.unitn.disi.smatch.data.trees.IBaseContext;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.filters.IMappingFilter;
import it.unitn.disi.smatch.loaders.context.IBaseContextLoader;
import it.unitn.disi.smatch.loaders.context.IContextLoader;
import it.unitn.disi.smatch.loaders.mapping.IMappingLoader;
import it.unitn.disi.smatch.matchers.element.IMatcherLibrary;
import it.unitn.disi.smatch.matchers.structure.tree.ITreeMatcher;
import it.unitn.disi.smatch.oracles.ILinguisticOracle;
import it.unitn.disi.smatch.oracles.ISenseMatcher;
import it.unitn.disi.smatch.preprocessors.IContextPreprocessor;
import it.unitn.disi.smatch.renderers.context.IBaseContextRenderer;
import it.unitn.disi.smatch.renderers.context.IContextRenderer;
import it.unitn.disi.smatch.renderers.mapping.IMappingRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * MatchManager controls the process of matching, loads contexts and performs other auxiliary work.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MatchManager extends Configurable implements IMatchManager {

    private static final Logger log = LoggerFactory.getLogger(MatchManager.class);

    // component configuration keys and component instance variables
    public static final String CONTEXT_LOADER_KEY = "ContextLoader";
    private IBaseContextLoader contextLoader = null;

    public static final String CONTEXT_RENDERER_KEY = "ContextRenderer";
    private IBaseContextRenderer contextRenderer = null;

    public static final String MAPPING_LOADER_KEY = "MappingLoader";
    private IMappingLoader mappingLoader = null;

    public static final String MAPPING_RENDERER_KEY = "MappingRenderer";
    private IMappingRenderer mappingRenderer = null;

    public static final String MAPPING_FILTER_KEY = "MappingFilter";
    private IMappingFilter mappingFilter = null;

    public static final String CONTEXT_PREPROCESSOR_KEY = "ContextPreprocessor";
    private IContextPreprocessor contextPreprocessor = null;

    public static final String CONTEXT_CLASSIFIER_KEY = "ContextClassifier";
    private IContextClassifier contextClassifier = null;

    public static final String MATCHER_LIBRARY_KEY = "MatcherLibrary";
    private IMatcherLibrary matcherLibrary = null;

    public static final String TREE_MATCHER_KEY = "TreeMatcher";
    private ITreeMatcher treeMatcher = null;

    public static final String SENSE_MATCHER_KEY = "SenseMatcher";
    private ISenseMatcher senseMatcher = null;

    public static final String LINGUISTIC_ORACLE_KEY = "LinguisticOracle";
    private ILinguisticOracle linguisticOracle = null;

    public static final String MAPPING_FACTORY_KEY = "MappingFactory";
    private IMappingFactory mappingFactory = null;

    public static IMatchManager getInstance() throws SMatchException {
        return new MatchManager();
    }

    public MatchManager() throws SMatchException {
        super();
    }

    /**
     * Constructor class with initialization.
     *
     * @param propFileName the name of the properties file
     * @throws SMatchException SMatchException
     */
    public MatchManager(String propFileName) throws SMatchException {
        this();

        // update properties
        try {
            setProperties(propFileName);
        } catch (ConfigurableException e) {
            throw new SMatchException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Constructor class with initialization.
     *
     * @param properties the properties
     * @throws SMatchException SMatchException
     */
    public MatchManager(Properties properties) throws SMatchException {
        this();

        // update properties
        try {
            setProperties(properties);
        } catch (ConfigurableException e) {
            throw new SMatchException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    public IContext createContext() {
        return new Context();
    }

    public IMappingFactory getMappingFactory() {
        return mappingFactory;
    }

    public IBaseContext loadContext(String location) throws SMatchException {
        if (null == contextLoader) {
            throw new SMatchException("Context loader is not configured.");
        }

        log.info("Loading context from: " + location);
        final IBaseContext result = contextLoader.loadContext(location);
        log.info("Loading context finished");
        return result;
    }

    public IBaseContextLoader getContextLoader() {
        return contextLoader;
    }

    @SuppressWarnings("unchecked")
    public void renderContext(IBaseContext ctxSource, String location) throws SMatchException {
        if (null == contextRenderer) {
            throw new SMatchException("Context renderer is not configured.");
        }
        log.info("Rendering context to: " + location);
        contextRenderer.render(ctxSource, location);
        log.info("Rendering context finished");
    }

    public IBaseContextRenderer getContextRenderer() {
        return contextRenderer;
    }

    public IContextMapping<INode> loadMapping(IContext ctxSource, IContext ctxTarget, String location) throws SMatchException {
        if (null == mappingLoader) {
            throw new SMatchException("Mapping loader is not configured.");
        }
        log.info("Loading mapping from: " + location);
        final IContextMapping<INode> result = mappingLoader.loadMapping(ctxSource, ctxTarget, location);
        log.info("Mapping loading finished");
        return result;
    }

    public IMappingLoader getMappingLoader() {
        return mappingLoader;
    }

    public void renderMapping(IContextMapping<INode> mapping, String location) throws SMatchException {
        if (null == mappingRenderer) {
            throw new SMatchException("Mapping renderer is not configured.");
        }
        log.info("Rendering mapping to: " + location);
        mappingRenderer.render(mapping, location);
        log.info("Mapping rendering finished");
    }

    public IMappingRenderer getMappingRenderer() {
        return mappingRenderer;
    }

    public IContextMapping<INode> filterMapping(IContextMapping<INode> mapping) throws SMatchException {
        if (null == mappingFilter) {
            throw new SMatchException("Mapping filter is not configured.");
        }
        log.info("Filtering...");
        final IContextMapping<INode> result = mappingFilter.filter(mapping);
        log.info("Filtering finished");
        return result;
    }

    public IContextMapping<IAtomicConceptOfLabel> elementLevelMatching(IContext sourceContext, IContext targetContext) throws SMatchException {
        if (null == matcherLibrary) {
            throw new SMatchException("Matcher library is not configured.");
        }

        if (!sourceContext.getRoot().getNodeData().isSubtreePreprocessed()) {
            throw new SMatchException("Source context is not preprocessed.");
        }

        if (!targetContext.getRoot().getNodeData().isSubtreePreprocessed()) {
            throw new SMatchException("Target context is not preprocessed.");
        }

        log.info("Element level matching...");
        final IContextMapping<IAtomicConceptOfLabel> acolMapping = matcherLibrary.elementLevelMatching(sourceContext, targetContext);
        log.info("Element level matching finished");
        return acolMapping;
    }

    public IContextMapping<INode> structureLevelMatching(IContext sourceContext,
                                                         IContext targetContext, IContextMapping<IAtomicConceptOfLabel> acolMapping) throws SMatchException {
        if (null == treeMatcher) {
            throw new SMatchException("Tree matcher is not configured.");
        }
        log.info("Structure level matching...");
        IContextMapping<INode> mapping = treeMatcher.treeMatch(sourceContext, targetContext, acolMapping);
        log.info("Structure level matching finished");
        log.info("Returning links: " + mapping.size());
        return mapping;
    }

    public void offline(IContext context) throws SMatchException {
        log.info("Computing concept at label formulas...");
        preprocess(context);
        log.info("Computing concept at label formulas finished");

        log.info("Computing concept at node formulas...");
        classify(context);
        log.info("Computing concept at node formulas finished");
    }

    public IContextMapping<INode> online(IContext sourceContext, IContext targetContext) throws SMatchException {
        // Performs element level matching which computes the relation between labels.
        IContextMapping<IAtomicConceptOfLabel> acolMapping = elementLevelMatching(sourceContext, targetContext);
        // Performs structure level matching which computes the relation between nodes.
        return structureLevelMatching(sourceContext, targetContext, acolMapping);
    }

    public IContextMapping<INode> match(IContext sourceContext, IContext targetContext) throws SMatchException {
        log.info("Matching started...");
        offline(sourceContext);
        offline(targetContext);
        IContextMapping<INode> result = online(sourceContext, targetContext);
        log.info("Matching finished");
        return result;
    }

    @Override
    public boolean setProperties(Properties newProperties) throws ConfigurableException {
        if (log.isInfoEnabled()) {
            log.info("Loading configuration...");
        }
        Properties oldProperties = new Properties();
        oldProperties.putAll(properties);
        boolean result = super.setProperties(newProperties);
        if (result) {
            // global ones
            linguisticOracle = (ILinguisticOracle) configureComponent(linguisticOracle, oldProperties, newProperties, "linguistic oracle", LINGUISTIC_ORACLE_KEY, ILinguisticOracle.class);
            senseMatcher = (ISenseMatcher) configureComponent(senseMatcher, oldProperties, newProperties, "sense matcher", SENSE_MATCHER_KEY, ISenseMatcher.class);
            mappingFactory = (IMappingFactory) configureComponent(mappingFactory, oldProperties, newProperties, "mapping factory", MAPPING_FACTORY_KEY, IMappingFactory.class);

            contextLoader = (IContextLoader) configureComponent(contextLoader, oldProperties, newProperties, "context loader", CONTEXT_LOADER_KEY, IContextLoader.class);
            contextRenderer = (IContextRenderer) configureComponent(contextRenderer, oldProperties, newProperties, "context renderer", CONTEXT_RENDERER_KEY, IContextRenderer.class);
            mappingLoader = (IMappingLoader) configureComponent(mappingLoader, oldProperties, newProperties, "mapping loader", MAPPING_LOADER_KEY, IMappingLoader.class);
            mappingRenderer = (IMappingRenderer) configureComponent(mappingRenderer, oldProperties, newProperties, "mapping renderer", MAPPING_RENDERER_KEY, IMappingRenderer.class);
            mappingFilter = (IMappingFilter) configureComponent(mappingFilter, oldProperties, newProperties, "mapping filter", MAPPING_FILTER_KEY, IMappingFilter.class);
            contextPreprocessor = (IContextPreprocessor) configureComponent(contextPreprocessor, oldProperties, newProperties, "context preprocessor", CONTEXT_PREPROCESSOR_KEY, IContextPreprocessor.class);
            contextClassifier = (IContextClassifier) configureComponent(contextClassifier, oldProperties, newProperties, "context classifier", CONTEXT_CLASSIFIER_KEY, IContextClassifier.class);
            matcherLibrary = (IMatcherLibrary) configureComponent(matcherLibrary, oldProperties, newProperties, "matching library", MATCHER_LIBRARY_KEY, IMatcherLibrary.class);
            treeMatcher = (ITreeMatcher) configureComponent(treeMatcher, oldProperties, newProperties, "tree matcher", TREE_MATCHER_KEY, ITreeMatcher.class);
        }
        return result;
    }

    public Properties getProperties() {
        return properties;
    }

    public void preprocess(IContext context) throws SMatchException {
        if (null == contextPreprocessor) {
            throw new SMatchException("Context preprocessor is not configured.");
        }

        log.info("Computing concepts at label...");
        contextPreprocessor.preprocess(context);
        log.info("Computing concepts at label finished");
    }

    public IContextPreprocessor getContextPreprocessor() {
        return contextPreprocessor;
    }

    public void classify(IContext context) throws SMatchException {
        if (null == contextClassifier) {
            throw new SMatchException("Context classifier is not configured.");
        }
        log.info("Computing concepts at node...");
        contextClassifier.buildCNodeFormulas(context);
        log.info("Computing concepts at node finished");
    }
}