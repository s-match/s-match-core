package it.unitn.disi.smatch.matchers.structure.tree.mini;

import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.data.util.ProgressContainer;
import it.unitn.disi.smatch.matchers.structure.node.OptimizedStageNodeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.TreeMatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Version with an iterator.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class OptimizedStageTreeMatcherIt extends OptimizedStageTreeMatcher {

    private static final Logger log = LoggerFactory.getLogger(OptimizedStageTreeMatcherIt.class);

    public OptimizedStageTreeMatcherIt(OptimizedStageNodeMatcher nodeMatcher, IMappingFactory mappingFactory) {
        super(nodeMatcher, mappingFactory);
    }

    @Override
    public IContextMapping<INode> treeMatch(IContext sourceContext, IContext targetContext,
                                            IContextMapping<IAtomicConceptOfLabel> acolMapping) throws TreeMatcherException {
        for (Iterator<INode> i = sourceContext.getNodes(); i.hasNext(); ) {
            INode sourceNode = i.next();
            // this is to distinguish below, in matcher, for axiom creation
            sourceNode.getNodeData().setSource(true);
        }

        Map<String, IAtomicConceptOfLabel> sourceAcols = new HashMap<>();
        Map<String, IAtomicConceptOfLabel> targetAcols = new HashMap<>();

        // need another mapping because here we allow < and > between the same pair of nodes
        HashSet<IMappingElement<INode>> mapping = new HashSet<>();
        Map<INode, ArrayList<IAtomicConceptOfLabel>> nmtAcols = new HashMap<>();

        long sourceNodeCount = (long) (sourceContext.getRoot().getDescendantCount() + 1);
        long targetNodeCount = (long) (targetContext.getRoot().getDescendantCount() + 1);
        ProgressContainer progressContainer = new ProgressContainer(sourceNodeCount * targetNodeCount, log);
        log.info("DJ...");
        treeDisjoint(sourceContext.getRoot(), targetContext.getRoot(),
                acolMapping, nmtAcols, sourceAcols, targetAcols, mapping, progressContainer);
        int dj = mapping.size();
        log.info("Links found DJ: " + dj);

        progressContainer = new ProgressContainer(sourceNodeCount * targetNodeCount, log);
        log.info("LG...");
        treeSubsumedBy(sourceContext.getRoot(), targetContext.getRoot(),
                true, acolMapping, nmtAcols, sourceAcols, targetAcols, mapping, progressContainer);
        int lg = mapping.size() - dj;
        log.info("Links found LG: " + lg);

        progressContainer = new ProgressContainer(sourceNodeCount * targetNodeCount, log);
        log.info("MG...");
        treeSubsumedBy(targetContext.getRoot(), sourceContext.getRoot(),
                false, acolMapping, nmtAcols, sourceAcols, targetAcols, mapping, progressContainer);
        int mg = mapping.size() - dj - lg;
        log.info("Links found MG: " + mg);

        log.info("TreeEquiv...");
        IContextMapping<INode> result = treeEquiv(mapping, sourceContext, targetContext);
        log.info("TreeEquiv finished");

        return result;
    }

    @Override
    protected void treeDisjoint(INode n1, INode n2,
                                IContextMapping<IAtomicConceptOfLabel> acolMapping,
                                Map<INode, ArrayList<IAtomicConceptOfLabel>> nmtAcols,
                                Map<String, IAtomicConceptOfLabel> sourceAcols,
                                Map<String, IAtomicConceptOfLabel> targetAcols,
                                HashSet<IMappingElement<INode>> mapping,
                                ProgressContainer progressContainer) throws TreeMatcherException {
        nodeTreeDisjoint(n1, n2, acolMapping, nmtAcols, sourceAcols, targetAcols, mapping, progressContainer);
        for (Iterator<INode> i = n1.getChildren(); i.hasNext(); ) {
            treeDisjoint(i.next(), n2, acolMapping, nmtAcols, sourceAcols, targetAcols, mapping, progressContainer);
        }
    }

    @Override
    protected void nodeTreeDisjoint(INode n1, INode n2,
                                    IContextMapping<IAtomicConceptOfLabel> acolMapping,
                                    Map<INode, ArrayList<IAtomicConceptOfLabel>> nmtAcols,
                                    Map<String, IAtomicConceptOfLabel> sourceAcols,
                                    Map<String, IAtomicConceptOfLabel> targetAcols,
                                    HashSet<IMappingElement<INode>> mapping,
                                    ProgressContainer progressContainer) throws TreeMatcherException {
        if (findRelation(n1.getAncestors(), n2, IMappingElement.DISJOINT, mapping)) {
            // we skip n2 subtree, so adjust the counter
            final long skipTo = progressContainer.getCounter() + n2.getDescendantCount();
            while (progressContainer.getCounter() < skipTo) {
                progressContainer.progress();
            }

            return;
        }

        if (nodeMatcher.nodeDisjoint(acolMapping, nmtAcols, sourceAcols, targetAcols, n1, n2)) {
            addRelation(n1, n2, IMappingElement.DISJOINT, mapping);
            // we skip n2 subtree, so adjust the counter
            final long skipTo = progressContainer.getCounter() + n2.getDescendantCount();
            while (progressContainer.getCounter() < skipTo) {
                progressContainer.progress();
            }
            return;
        }

        progressContainer.progress();

        for (Iterator<INode> i = n2.getChildren(); i.hasNext(); ) {
            nodeTreeDisjoint(n1, i.next(), acolMapping, nmtAcols, sourceAcols, targetAcols, mapping, progressContainer);
        }
    }

    @Override
    protected boolean treeSubsumedBy(INode n1, INode n2, boolean direction,
                                     IContextMapping<IAtomicConceptOfLabel> acolMapping,
                                     Map<INode, ArrayList<IAtomicConceptOfLabel>> nmtAcols,
                                     Map<String, IAtomicConceptOfLabel> sourceAcols,
                                     Map<String, IAtomicConceptOfLabel> targetAcols,
                                     HashSet<IMappingElement<INode>> mapping,
                                     ProgressContainer progressContainer) throws TreeMatcherException {
        if (findRelation(n1, n2, IMappingElement.DISJOINT, mapping)) {
            // we skip n1 subtree, so adjust the counter
            final long skipTo = progressContainer.getCounter() + n1.getDescendantCount();
            while (progressContainer.getCounter() < skipTo) {
                progressContainer.progress();
            }

            return false;
        }

        progressContainer.progress();
        if (!nodeMatcher.nodeSubsumedBy(n1, n2, acolMapping, nmtAcols, sourceAcols, targetAcols)) {
            for (Iterator<INode> i = n1.getChildren(); i.hasNext(); ) {
                treeSubsumedBy(i.next(), n2, direction, acolMapping, nmtAcols, sourceAcols, targetAcols, mapping, progressContainer);
            }
        } else {
            boolean lastNodeFound = false;
            for (Iterator<INode> i = n2.getChildren(); i.hasNext(); ) {
                if (treeSubsumedBy(n1, i.next(), direction, acolMapping, nmtAcols, sourceAcols, targetAcols, mapping, progressContainer)) {
                    lastNodeFound = true;
                }
            }
            if (!lastNodeFound) {
                addSubsumptionRelation(n1, n2, direction, mapping);
            }

            // we skip n1 subtree, so adjust the counter
            final long skipTo = progressContainer.getCounter() + n1.getDescendantCount();
            while (progressContainer.getCounter() < skipTo) {
                progressContainer.progress();
            }
            return true;
        }

        return false;
    }

    protected boolean findRelation(Iterator<INode> sourceNodes, INode targetNode, char relation, HashSet<IMappingElement<INode>> mapping) {
        while (sourceNodes.hasNext()) {
            INode sourceNode = sourceNodes.next();
            if (mapping.contains(createMappingElement(sourceNode, targetNode, relation))) {
                return true;
            }
        }
        return false;
    }
}
