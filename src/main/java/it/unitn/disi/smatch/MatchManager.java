package it.unitn.disi.smatch;

import it.unitn.disi.smatch.classifiers.IContextClassifier;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.Context;
import it.unitn.disi.smatch.data.trees.IBaseContext;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.filters.IMappingFilter;
import it.unitn.disi.smatch.loaders.context.IBaseContextLoader;
import it.unitn.disi.smatch.loaders.mapping.IMappingLoader;
import it.unitn.disi.smatch.matchers.element.IElementMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.ITreeMatcher;
import it.unitn.disi.smatch.preprocessors.IContextPreprocessor;
import it.unitn.disi.smatch.renderers.context.IBaseContextRenderer;
import it.unitn.disi.smatch.renderers.mapping.IMappingRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.Arrays;
import java.util.Iterator;

/**
 * MatchManager controls the process of matching, loads contexts and performs other auxiliary work.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MatchManager implements IMatchManager {

    private static final Logger log = LoggerFactory.getLogger(MatchManager.class);

    protected final IBaseContextLoader contextLoader;

    protected final IBaseContextRenderer contextRenderer;

    protected final IMappingLoader mappingLoader;

    protected final IMappingRenderer mappingRenderer;

    protected final IMappingFilter mappingFilter;

    protected final IMappingFactory mappingFactory;

    protected final IContextPreprocessor contextPreprocessor;

    protected final IContextClassifier contextClassifier;

    protected final IElementMatcher elementMatcher;

    protected final ITreeMatcher treeMatcher;

    public static IMatchManager getInstanceFromResource(String... paths) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(paths);
        return (IMatchManager) applicationContext.getBean("matchManager");
    }

    public static IMatchManager getInstanceFromConfigFile(String... paths) {
        ApplicationContext applicationContext = new FileSystemXmlApplicationContext(paths);
        return (IMatchManager) applicationContext.getBean("matchManager");
    }

    public MatchManager(IBaseContextLoader contextLoader,
                        IBaseContextRenderer contextRenderer,
                        IMappingLoader mappingLoader,
                        IMappingRenderer mappingRenderer,
                        IMappingFilter mappingFilter,
                        IMappingFactory mappingFactory,
                        IContextPreprocessor contextPreprocessor,
                        IContextClassifier contextClassifier,
                        IElementMatcher elementMatcher,
                        ITreeMatcher treeMatcher) {
        this.contextLoader = contextLoader;
        this.contextRenderer = contextRenderer;
        this.mappingLoader = mappingLoader;
        this.mappingRenderer = mappingRenderer;
        this.mappingFilter = mappingFilter;
        this.mappingFactory = mappingFactory;
        this.contextPreprocessor = contextPreprocessor;
        this.contextClassifier = contextClassifier;
        this.elementMatcher = elementMatcher;
        this.treeMatcher = treeMatcher;
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

        final long start = System.currentTimeMillis();
        log.info("Loading context from: " + location);
        final IBaseContext result = contextLoader.loadContext(location);
        log.info("Loading context finished: " + (System.currentTimeMillis() - start) + " ms");
        return result;
    }

    public IBaseContextLoader getContextLoader() {
        return contextLoader;
    }

    @SuppressWarnings("unchecked")
    public void renderContext(IBaseContext context, String location) throws SMatchException {
        if (null == contextRenderer) {
            throw new SMatchException("Context renderer is not configured.");
        }

        final long start = System.currentTimeMillis();
        log.info("Rendering context to: " + location);
        contextRenderer.render(context, location);
        log.info("Rendering context finished: " + (System.currentTimeMillis() - start) + " ms");
    }

    public IBaseContextRenderer getContextRenderer() {
        return contextRenderer;
    }

    public IContextMapping<INode> loadMapping(IContext ctxSource, IContext ctxTarget, String location) throws SMatchException {
        if (null == mappingLoader) {
            throw new SMatchException("Mapping loader is not configured.");
        }

        final long start = System.currentTimeMillis();
        log.info("Loading mapping from: " + location);
        final IContextMapping<INode> result = mappingLoader.loadMapping(ctxSource, ctxTarget, location);
        log.info("Mapping loading finished: " + (System.currentTimeMillis() - start) + " ms");
        return result;
    }

    public IMappingLoader getMappingLoader() {
        return mappingLoader;
    }

    public void renderMapping(IContextMapping<INode> mapping, String location) throws SMatchException {
        if (null == mappingRenderer) {
            throw new SMatchException("Mapping renderer is not configured.");
        }

        final long start = System.currentTimeMillis();
        log.info("Rendering mapping to: " + location);
        mappingRenderer.render(mapping, location);
        log.info("Mapping rendering finished: " + (System.currentTimeMillis() - start) + " ms");
    }

    public IMappingRenderer getMappingRenderer() {
        return mappingRenderer;
    }

    public IContextMapping<INode> filterMapping(IContextMapping<INode> mapping) throws SMatchException {
        if (null == mappingFilter) {
            throw new SMatchException("Mapping filter is not configured.");
        }

        final long start = System.currentTimeMillis();
        log.info("Filtering...");
        final IContextMapping<INode> result = mappingFilter.filter(mapping);
        log.info("Filtering finished: " + (System.currentTimeMillis() - start) + " ms");
        return result;
    }

    public IContextMapping<IAtomicConceptOfLabel> elementLevelMatching(IContext sourceContext, IContext targetContext) throws SMatchException {
        if (null == elementMatcher) {
            throw new SMatchException("Matcher library is not configured.");
        }

        if (!sourceContext.getRoot().nodeData().isSubtreePreprocessed()) {
            throw new SMatchException("Source context is not preprocessed.");
        }

        if (!targetContext.getRoot().nodeData().isSubtreePreprocessed()) {
            throw new SMatchException("Target context is not preprocessed.");
        }

        final long start = System.currentTimeMillis();
        log.info("Element level matching...");
        final IContextMapping<IAtomicConceptOfLabel> acolMapping = elementMatcher.elementLevelMatching(sourceContext, targetContext);
        log.info("Element level matching finished: " + (System.currentTimeMillis() - start) + " ms");
        return acolMapping;
    }

    public IContextMapping<INode> structureLevelMatching(IContext sourceContext,
                                                         IContext targetContext, IContextMapping<IAtomicConceptOfLabel> acolMapping) throws SMatchException {
        if (null == treeMatcher) {
            throw new SMatchException("Tree matcher is not configured.");
        }

        final long start = System.currentTimeMillis();
        log.info("Structure level matching...");
        IContextMapping<INode> mapping = treeMatcher.treeMatch(sourceContext, targetContext, acolMapping);
        log.info("Structure level matching finished: " + (System.currentTimeMillis() - start) + " ms");
        log.info("Returning links: " + mapping.size());
        return mapping;
    }

    public void offline(IContext context) throws SMatchException {
        long start = System.currentTimeMillis();
        log.info("Preprocessing...");
        preprocess(context);
        classify(context);
        log.info("Preprocessing finished: " + (System.currentTimeMillis() - start) + " ms");
    }

    public IContextMapping<INode> online(IContext sourceContext, IContext targetContext) throws SMatchException {
        long start = System.currentTimeMillis();
        log.info("Online matching...");
        // Performs element level matching which computes the relation between labels.
        IContextMapping<IAtomicConceptOfLabel> acolMapping = elementLevelMatching(sourceContext, targetContext);
        // Performs structure level matching which computes the relation between nodes.
        IContextMapping<INode> mapping = structureLevelMatching(sourceContext, targetContext, acolMapping);
        log.info("Online matching finished: " + (System.currentTimeMillis() - start) + " ms");
        return mapping;
    }

    public IContextMapping<INode> match(IContext sourceContext, IContext targetContext) throws SMatchException {
        final long start = System.currentTimeMillis();
        log.info("Matching started...");
        offline(sourceContext);
        offline(targetContext);
        IContextMapping<INode> result = online(sourceContext, targetContext);
        log.info("Matching finished: " + (System.currentTimeMillis() - start) + " ms");
        return result;
    }

    public void preprocess(IContext context) throws SMatchException {
        if (null == contextPreprocessor) {
            throw new SMatchException("Context preprocessor is not configured.");
        }

        final long start = System.currentTimeMillis();
        log.info("Computing concepts at label...");
        contextPreprocessor.preprocess(context);
        log.info("Computing concepts at label finished: " + (System.currentTimeMillis() - start) + " ms");
        if (log.isTraceEnabled()) {
            for (Iterator<INode> i = context.nodeIterator(); i.hasNext(); ) {
                INode node = i.next();
                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < node.ancestorCount(); j++) {
                    builder.append("\t");
                }
                String level = builder.toString() + "\t" + node.nodeData().getId() + "\t";
                log.trace(level + node.nodeData().getName() + "\t" + node.nodeData().getLabelFormula());
                for (IAtomicConceptOfLabel acol : node.nodeData().getConcepts()) {
                    log.trace(level + acol.getId() + "\t" + acol.getLemma() + "\t" + acol.getToken());
                    String acolId = level + acol.getId() + "\t";
                    for (ISense sense : acol.getSenses()) {
                        log.trace(acolId + sense.getId() + "\t" + Arrays.toString(sense.getLemmas().toArray()) + "\t" + sense.getGloss());
                    }
                }
            }

        }
    }

    public IContextPreprocessor getContextPreprocessor() {
        return contextPreprocessor;
    }

    public void classify(IContext context) throws SMatchException {
        if (null == contextClassifier) {
            throw new SMatchException("Context classifier is not configured.");
        }

        final long start = System.currentTimeMillis();
        log.info("Computing concepts at node...");
        contextClassifier.buildCNodeFormulas(context);
        log.info("Computing concepts at node finished: " + (System.currentTimeMillis() - start) + " ms");
    }
}