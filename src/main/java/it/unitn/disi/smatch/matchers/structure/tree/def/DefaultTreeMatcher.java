package it.unitn.disi.smatch.matchers.structure.tree.def;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.matchers.structure.node.INodeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.BaseTreeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.IAsyncTreeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.TreeMatcherException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Matches all nodes of the source context with all nodes of the target context.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class DefaultTreeMatcher extends BaseTreeMatcher implements IAsyncTreeMatcher {

    public DefaultTreeMatcher(INodeMatcher nodeMatcher, IMappingFactory mappingFactory) {
        super(mappingFactory, nodeMatcher);
    }

    public DefaultTreeMatcher(INodeMatcher nodeMatcher, IMappingFactory mappingFactory,
                              IContext sourceContext, IContext targetContext,
                              IContextMapping<IAtomicConceptOfLabel> acolMapping) {
        super(mappingFactory, nodeMatcher, sourceContext, targetContext, acolMapping);
        setTotal((long) sourceContext.nodesCount() * (long) targetContext.nodesCount());
    }

    public IContextMapping<INode> treeMatch(IContext sourceContext, IContext targetContext, IContextMapping<IAtomicConceptOfLabel> acolMapping) throws TreeMatcherException {
        setTotal((long) sourceContext.nodesCount() * (long) targetContext.nodesCount());
        setProgress(0);

        IContextMapping<INode> mapping = mappingFactory.getContextMappingInstance(sourceContext, targetContext);

        // semantic relation for particular node matching task
        char relation;

        Map<String, IAtomicConceptOfLabel> sourceACoLs = new HashMap<>();
        Map<String, IAtomicConceptOfLabel> targetACoLs = new HashMap<>();

        for (Iterator<INode> i = sourceContext.nodeIterator(); i.hasNext(); ) {
            INode sourceNode = i.next();
            for (Iterator<INode> j = targetContext.nodeIterator(); j.hasNext(); ) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                INode targetNode = j.next();
                relation = nodeMatcher.nodeMatch(acolMapping, sourceACoLs, targetACoLs, sourceNode, targetNode);
                mapping.setRelation(sourceNode, targetNode, relation);

                progress();
            }
        }

        return mapping;
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncTreeMatch(IContext sourceContext, IContext targetContext, IContextMapping<IAtomicConceptOfLabel> acolMapping) {
        return new DefaultTreeMatcher(nodeMatcher, mappingFactory, sourceContext, targetContext, acolMapping);
    }
}