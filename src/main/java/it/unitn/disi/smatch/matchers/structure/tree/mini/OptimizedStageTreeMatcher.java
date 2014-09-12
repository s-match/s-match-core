package it.unitn.disi.smatch.matchers.structure.tree.mini;

import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.mappings.ReversingMappingElement;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.data.util.ProgressContainer;
import it.unitn.disi.smatch.matchers.structure.node.OptimizedStageNodeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.ITreeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.TreeMatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Matches first disjoint, then subsumptions, then joins subsumption into equivalence. For more details see technical
 * report <a href="http://eprints.biblio.unitn.it/archive/00001525/">http://eprints.biblio.unitn.it/archive/00001525/</a>
 * <p/>
 * Giunchiglia, Fausto and Maltese, Vincenzo and Autayeu, Aliaksandr. Computing minimal mappings.
 * Technical Report DISI-08-078, Department of Information Engineering and Computer Science, University of Trento.
 * Proc. of the Fourth Ontology Matching Workshop at ISWC 2009.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class OptimizedStageTreeMatcher implements ITreeMatcher {

    private static final Logger log = LoggerFactory.getLogger(OptimizedStageTreeMatcher.class);

    protected final IMappingFactory mappingFactory;
    protected final OptimizedStageNodeMatcher nodeMatcher;

    protected OptimizedStageTreeMatcher(OptimizedStageNodeMatcher nodeMatcher, IMappingFactory mappingFactory) {
        this.nodeMatcher = nodeMatcher;
        this.mappingFactory = mappingFactory;
    }

    public IContextMapping<INode> treeMatch(IContext sourceContext, IContext targetContext,
                                            IContextMapping<IAtomicConceptOfLabel> acolMapping) throws TreeMatcherException {
        for (INode sourceNode : sourceContext.getNodesList()) {
            // this is to distinguish below, in matcher, for axiom creation
            sourceNode.getNodeData().setSource(true);
        }

        Map<String, IAtomicConceptOfLabel> sourceAcols = new HashMap<>();
        Map<String, IAtomicConceptOfLabel> targetAcols = new HashMap<>();

        // need another mapping because here we allow < and > between the same pair of nodes
        HashSet<IMappingElement<INode>> mapping = new HashSet<>();
        Map<INode, ArrayList<IAtomicConceptOfLabel>> nmtAcols = new HashMap<>();

        long sourceNodeCount = (long) sourceContext.getRoot().getDescendantCount() + 1;
        long targetNodeCount = (long) targetContext.getRoot().getDescendantCount() + 1;
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

    protected void treeDisjoint(INode n1, INode n2,
                                IContextMapping<IAtomicConceptOfLabel> acolMapping,
                                Map<INode, ArrayList<IAtomicConceptOfLabel>> nmtAcols,
                                Map<String, IAtomicConceptOfLabel> sourceAcols,
                                Map<String, IAtomicConceptOfLabel> targetAcols,
                                HashSet<IMappingElement<INode>> mapping,
                                ProgressContainer progressContainer) throws TreeMatcherException {
        nodeTreeDisjoint(n1, n2, acolMapping, nmtAcols, sourceAcols, targetAcols, mapping, progressContainer);
        for (INode child : n1.getChildrenList()) {
            treeDisjoint(child, n2, acolMapping, nmtAcols, sourceAcols, targetAcols, mapping, progressContainer);
        }
    }

    protected void nodeTreeDisjoint(INode n1, INode n2,
                                    IContextMapping<IAtomicConceptOfLabel> acolMapping,
                                    Map<INode, ArrayList<IAtomicConceptOfLabel>> nmtAcols,
                                    Map<String, IAtomicConceptOfLabel> sourceAcols,
                                    Map<String, IAtomicConceptOfLabel> targetAcols,
                                    HashSet<IMappingElement<INode>> mapping,
                                    ProgressContainer progressContainer) throws TreeMatcherException {
        if (findRelation(n1.getAncestorsList(), n2, IMappingElement.DISJOINT, mapping)) {
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

        for (INode child : n2.getChildrenList()) {
            nodeTreeDisjoint(n1, child, acolMapping, nmtAcols, sourceAcols, targetAcols, mapping, progressContainer);
        }
    }

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
            for (INode child : n1.getChildrenList()) {
                treeSubsumedBy(child, n2, direction, acolMapping, nmtAcols, sourceAcols, targetAcols, mapping, progressContainer);
            }
        } else {
            boolean lastNodeFound = false;
            for (INode child : n2.getChildrenList()) {
                if (treeSubsumedBy(n1, child, direction, acolMapping, nmtAcols, sourceAcols, targetAcols, mapping, progressContainer)) {
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

    protected IContextMapping<INode> treeEquiv(HashSet<IMappingElement<INode>> mapping, IContext sourceContext, IContext targetContext) {
        IContextMapping<INode> result = mappingFactory.getContextMappingInstance(sourceContext, targetContext);
        if (log.isInfoEnabled()) {
            log.info("Mapping before TreeEquiv: " + mapping.size());
        }
        for (IMappingElement<INode> me : mapping) {
            if (IMappingElement.LESS_GENERAL == me.getRelation()) {
                IMappingElement<INode> mg = createMappingElement(me.getSource(), me.getTarget(), IMappingElement.MORE_GENERAL);
                if (mapping.contains(mg)) {
                    result.setRelation(me.getSource(), me.getTarget(), IMappingElement.EQUIVALENCE);
                } else {
                    result.add(me);
                }
            } else {
                if (IMappingElement.MORE_GENERAL == me.getRelation()) {
                    IMappingElement<INode> lg = createMappingElement(me.getSource(), me.getTarget(), IMappingElement.LESS_GENERAL);
                    if (mapping.contains(lg)) {
                        result.setRelation(me.getSource(), me.getTarget(), IMappingElement.EQUIVALENCE);
                    } else {
                        result.add(me);
                    }
                } else {
                    result.add(me);
                }
            }
        }
        if (log.isInfoEnabled()) {
            log.info("Mapping after TreeEquiv: " + result.size());
        }
        return result;
    }

    protected void addSubsumptionRelation(INode n1, INode n2, boolean direction, HashSet<IMappingElement<INode>> mapping) {
        if (direction) {
            mapping.add(createMappingElement(n1, n2, IMappingElement.LESS_GENERAL));
        } else {
            mapping.add(createMappingElement(n2, n1, IMappingElement.MORE_GENERAL));
        }
    }

    protected void addRelation(INode n1, INode n2, char relation, HashSet<IMappingElement<INode>> mapping) {
        mapping.add(createMappingElement(n1, n2, relation));
    }

    protected boolean findRelation(INode sourceNode, INode targetNode, char relation, HashSet<IMappingElement<INode>> mapping) {
        return mapping.contains(createMappingElement(sourceNode, targetNode, relation));
    }

    protected boolean findRelation(List<INode> sourceNodes, INode targetNode, char relation, HashSet<IMappingElement<INode>> mapping) {
        for (INode sourceNode : sourceNodes) {
            if (mapping.contains(createMappingElement(sourceNode, targetNode, relation))) {
                return true;
            }
        }
        return false;
    }

    protected static IMappingElement<INode> createMappingElement(INode source, INode target, char relation) {
        return new ReversingMappingElement(source, target, relation);
    }
}