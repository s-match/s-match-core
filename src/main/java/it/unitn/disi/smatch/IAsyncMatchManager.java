package it.unitn.disi.smatch;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * Asynchronous version of the {@link it.unitn.disi.smatch.IMatchManager IMatchManager}.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IAsyncMatchManager extends IMatchManager {

    /**
     * Uses current loader to load the context from external source into internal data structure.
     *
     * @param location a string passed to the current loader
     * @return async task instance
     */
    AsyncTask<IContext, INode> asyncLoadContext(String location);

    /**
     * Renders the context using a current renderer.
     *
     * @param context  context to be rendered
     * @param location a render destination passed to the context renderer
     * @return async task instance
     */
    AsyncTask<Void, INode> asyncRenderContext(IContext context, String location);

    /**
     * Loads the mapping between source and target contexts using the current mapping loader.
     *
     * @param ctxSource source context
     * @param ctxTarget target context
     * @param location  a mapping location passed to the mapping loader
     * @return async task instance
     */
    AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncLoadMapping(IContext ctxSource, IContext ctxTarget, String location);

    /**
     * Renders the mapping using a current mapping renderer.
     *
     * @param mapping  a mapping
     * @param location a render destination passed to the mapping renderer
     * @return async task instance
     */
    AsyncTask<Void, IMappingElement<INode>> asyncRenderMapping(IContextMapping<INode> mapping, String location);

    /**
     * Filters a mapping. For example, filtering could be a minimization.
     *
     * @param mapping a mapping to filter
     * @return async task instance
     */
    AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncFilterMapping(IContextMapping<INode> mapping);

    /**
     * Performs the first step of the semantic matching algorithm.
     *
     * @param context interface to a context to be preprocessed
     * @return async task instance
     */
    AsyncTask<Void, INode> asyncPreprocessContext(IContext context);

    /**
     * Performs the second step of the semantic matching algorithm.
     *
     * @param context interface to the preprocessed context
     * @return async task instance
     */
    AsyncTask<Void, INode> asyncClassifyContext(IContext context);

    /**
     * Performs the third step of semantic matching algorithm.
     *
     * @param sourceContext interface of source context with concept at node formula
     * @param targetContext interface of target context with concept at node formula
     * @return async task instance
     */
    AsyncTask<IContextMapping<IAtomicConceptOfLabel>, IMappingElement<IAtomicConceptOfLabel>>
    asyncElementLevelMatching(IContext sourceContext, IContext targetContext);

    /**
     * Performs the fourth step of semantic matching algorithm.
     *
     * @param sourceContext interface of source context with concept at node formula
     * @param targetContext interface of target context with concept at node formula
     * @param acolMapping   mapping between atomic concepts of labels in the contexts
     * @return async task instance
     */
    AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncStructureLevelMatching(
            IContext sourceContext, IContext targetContext,
            IContextMapping<IAtomicConceptOfLabel> acolMapping);

    /**
     * Performs the first two steps of the semantic matching algorithm.
     *
     * @param context interface to context to be preprocessed
     * @return async task instance
     */
    AsyncTask<Void, INode> asyncOffline(IContext context);

    /**
     * Performs the last two steps of the semantic matching algorithm.
     *
     * @param sourceContext interface to preprocessed source context to be matched
     * @param targetContext interface to preprocessed target context to be matched
     * @return async task instance
     */
    AsyncTask<IContextMapping<INode>, IMappingElement<INode>>
    asyncOnline(IContext sourceContext, IContext targetContext);

    /**
     * Performs the whole matching process.
     *
     * @param sourceContext interface to source context to be matched
     * @param targetContext interface to target context to be matched
     * @return async task instance
     */
    AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncMatch(IContext sourceContext, IContext targetContext);
}