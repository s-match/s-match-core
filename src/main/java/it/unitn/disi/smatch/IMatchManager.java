package it.unitn.disi.smatch;

import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IBaseContext;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.context.IBaseContextLoader;
import it.unitn.disi.smatch.loaders.mapping.IMappingLoader;
import it.unitn.disi.smatch.preprocessors.IContextPreprocessor;
import it.unitn.disi.smatch.renderers.context.IBaseContextRenderer;
import it.unitn.disi.smatch.renderers.mapping.IMappingRenderer;

/**
 * Interface for matching related functionality.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMatchManager {

    /**
     * Creates a context instance.
     *
     * @return a context instance
     */
    IContext createContext();

    /**
     * Returns mapping factory.
     *
     * @return mapping factory
     */
    IMappingFactory getMappingFactory();

    /**
     * Uses current loader to load the context from external source into internal data structure.
     *
     * @param location a string passed to the current loader
     * @return interface to internal context representation
     * @throws SMatchException SMatchException
     */
    IBaseContext loadContext(String location) throws SMatchException;

    /**
     * Returns currently configured context loader.
     *
     * @return currently configured context loader
     */
    IBaseContextLoader getContextLoader();

    /**
     * Renders the context using a current renderer.
     *
     * @param context context to be rendered
     * @param location  a render destination passed to the context renderer
     * @throws SMatchException SMatchException
     */
    void renderContext(IBaseContext context, String location) throws SMatchException;

    /**
     * Returns currently configured context renderer.
     *
     * @return currently configured context renderer
     */
    IBaseContextRenderer getContextRenderer();

    /**
     * Loads the mapping between source and target contexts using the current mapping loader.
     *
     * @param ctxSource source context
     * @param ctxTarget target context
     * @param location  a mapping location passed to the mapping loader
     * @return a mapping
     * @throws SMatchException SMatchException
     */
    IContextMapping<INode> loadMapping(IContext ctxSource, IContext ctxTarget, String location) throws SMatchException;

    /**
     * Returns currently configured mapping loader.
     *
     * @return currently configured mapping loader
     */
    IMappingLoader getMappingLoader();

    /**
     * Renders the mapping using a current mapping renderer.
     *
     * @param mapping  a mapping
     * @param location a render destination passed to the mapping renderer
     * @throws SMatchException SMatchException
     */
    void renderMapping(IContextMapping<INode> mapping, String location) throws SMatchException;

    /**
     * Returns currently configured mapping renderer.
     *
     * @return currently configured mapping renderer
     */
    IMappingRenderer getMappingRenderer();

    /**
     * Filters a mapping. For example, filtering could be a minimization.
     *
     * @param mapping a mapping to filter
     * @return a filtered mapping
     * @throws SMatchException SMatchException
     */
    IContextMapping<INode> filterMapping(IContextMapping<INode> mapping) throws SMatchException;

    /**
     * Performs the first step of the semantic matching algorithm.
     *
     * @param context interface to a context to be preprocessed
     * @throws SMatchException SMatchException
     */
    void preprocess(IContext context) throws SMatchException;

    /**
     * Returns currently configured context preprocessor.
     *
     * @return currently configured context preprocessor
     */
    IContextPreprocessor getContextPreprocessor();

    /**
     * Performs the second step of the semantic matching algorithm.
     *
     * @param context interface to the preprocessed context
     * @throws SMatchException SMatchException
     */
    void classify(IContext context) throws SMatchException;

    /**
     * Performs the third step of semantic matching algorithm.
     *
     * @param sourceContext interface of source context with concept at node formula
     * @param targetContext interface of target context with concept at node formula
     * @return interface to a matrix of semantic relations between atomic concepts of labels in the contexts
     * @throws SMatchException SMatchException
     */
    IContextMapping<IAtomicConceptOfLabel> elementLevelMatching(IContext sourceContext, IContext targetContext) throws SMatchException;

    /**
     * Performs the fourth step of semantic matching algorithm.
     *
     * @param sourceContext interface of source context with concept at node formula
     * @param targetContext interface of target context with concept at node formula
     * @param acolMapping   mapping between atomic concepts of labels in the contexts
     * @return mapping between the concepts at nodes in the contexts
     * @throws SMatchException SMatchException
     */
    IContextMapping<INode> structureLevelMatching(IContext sourceContext, IContext targetContext,
                                                  IContextMapping<IAtomicConceptOfLabel> acolMapping) throws SMatchException;

    /**
     * Performs the first two steps of the semantic matching algorithm.
     *
     * @param context interface to context to be preprocessed
     * @throws SMatchException SMatchException
     */
    void offline(IContext context) throws SMatchException;

    /**
     * Performs the last two steps of the semantic matching algorithm.
     *
     * @param sourceContext interface to preprocessed source context to be matched
     * @param targetContext interface to preprocessed target context to be matched
     * @return interface to resulting mapping
     * @throws SMatchException SMatchException
     */
    IContextMapping<INode> online(IContext sourceContext, IContext targetContext) throws SMatchException;

    /**
     * Performs the whole matching process.
     *
     * @param sourceContext interface to source context to be matched
     * @param targetContext interface to target context to be matched
     * @return interface to resulting mapping
     * @throws SMatchException SMatchException
     */
    IContextMapping<INode> match(IContext sourceContext, IContext targetContext) throws SMatchException;
}