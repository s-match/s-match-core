package it.unitn.disi.smatch.matchers.structure.tree;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.matchers.structure.node.INodeMatcher;

/**
 * Base class for tree matchers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseTreeMatcher extends AsyncTask<IContextMapping<INode>, IMappingElement<INode>> implements ITreeMatcher {

    protected final INodeMatcher nodeMatcher;
    protected final IMappingFactory mappingFactory;

    protected final IContext sourceContext;
    protected final IContext targetContext;
    protected final IContextMapping<IAtomicConceptOfLabel> acolMapping;

    protected BaseTreeMatcher(IMappingFactory mappingFactory, INodeMatcher nodeMatcher) {
        this.nodeMatcher = nodeMatcher;
        this.mappingFactory = mappingFactory;
        this.sourceContext = null;
        this.targetContext = null;
        this.acolMapping = null;
    }

    protected BaseTreeMatcher(IMappingFactory mappingFactory, INodeMatcher nodeMatcher,
                              IContext sourceContext, IContext targetContext,
                              IContextMapping<IAtomicConceptOfLabel> acolMapping) {
        this.nodeMatcher = nodeMatcher;
        this.mappingFactory = mappingFactory;
        this.sourceContext = sourceContext;
        this.targetContext = targetContext;
        this.acolMapping = acolMapping;
    }

    @Override
    protected IContextMapping<INode> doInBackground() throws Exception {
        final String threadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(Thread.currentThread().getName()
                    + " [" + this.getClass().getSimpleName()
                    + ": source.size=" + sourceContext.getNodesCount()
                    + ", target.size=" + targetContext.getNodesCount() + "]");

            return treeMatch(sourceContext, targetContext, acolMapping);
        } finally {
            Thread.currentThread().setName(threadName);
        }

    }
}