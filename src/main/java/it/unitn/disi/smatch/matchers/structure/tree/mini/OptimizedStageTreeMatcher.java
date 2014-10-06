package it.unitn.disi.smatch.matchers.structure.tree.mini;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.mappings.ReversingMappingElement;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.matchers.structure.node.OptimizedStageNodeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.BaseTreeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.IAsyncTreeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.TreeMatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

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
public class OptimizedStageTreeMatcher extends BaseTreeMatcher implements IAsyncTreeMatcher {

    private static final Logger log = LoggerFactory.getLogger(OptimizedStageTreeMatcher.class);

    protected final OptimizedStageNodeMatcher nodeMatcher;

    public OptimizedStageTreeMatcher(IMappingFactory mappingFactory, OptimizedStageNodeMatcher nodeMatcher) {
        super(mappingFactory, nodeMatcher);
        this.nodeMatcher = nodeMatcher;
    }

    public OptimizedStageTreeMatcher(IMappingFactory mappingFactory, OptimizedStageNodeMatcher nodeMatcher,
                                     IContext sourceContext, IContext targetContext,
                                     IContextMapping<IAtomicConceptOfLabel> acolMapping) {
        super(mappingFactory, nodeMatcher, sourceContext, targetContext, acolMapping);
        this.nodeMatcher = nodeMatcher;
        setTotal(3 * (long) sourceContext.getNodesCount() * (long) targetContext.getNodesCount());
    }

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

        log.info("DJ...");
        treeDisjoint(sourceContext.getRoot(), targetContext.getRoot(),
                acolMapping, sourceAcols, targetAcols, mapping);
        int dj = mapping.size();
        log.info("Links found DJ: " + dj);

        log.info("LG...");
        treeSubsumedBy(sourceContext.getRoot(), targetContext.getRoot(),
                true, acolMapping, sourceAcols, targetAcols, mapping);
        int lg = mapping.size() - dj;
        log.info("Links found LG: " + lg);

        log.info("MG...");
        treeSubsumedBy(targetContext.getRoot(), sourceContext.getRoot(),
                false, acolMapping, sourceAcols, targetAcols, mapping);
        int mg = mapping.size() - dj - lg;
        log.info("Links found MG: " + mg);

        log.info("TreeEquiv...");
        IContextMapping<INode> result = treeEquiv(mapping, sourceContext, targetContext);
        log.info("TreeEquiv finished");

        return result;
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>>
    asyncTreeMatch(IContext sourceContext, IContext targetContext, IContextMapping<IAtomicConceptOfLabel> acolMapping) {
        return new OptimizedStageTreeMatcher(mappingFactory, nodeMatcher, sourceContext, targetContext, acolMapping);
    }

    protected void treeDisjoint(INode n1, INode n2,
                                IContextMapping<IAtomicConceptOfLabel> acolMapping,
                                Map<String, IAtomicConceptOfLabel> sourceAcols,
                                Map<String, IAtomicConceptOfLabel> targetAcols,
                                HashSet<IMappingElement<INode>> mapping) throws TreeMatcherException {
        nodeTreeDisjoint(n1, n2, acolMapping, sourceAcols, targetAcols, mapping);
        for (Iterator<INode> i = n1.getChildren(); i.hasNext(); ) {
            treeDisjoint(i.next(), n2, acolMapping, sourceAcols, targetAcols, mapping);
        }
    }

    protected void nodeTreeDisjoint(INode n1, INode n2,
                                    IContextMapping<IAtomicConceptOfLabel> acolMapping,
                                    Map<String, IAtomicConceptOfLabel> sourceAcols,
                                    Map<String, IAtomicConceptOfLabel> targetAcols,
                                    HashSet<IMappingElement<INode>> mapping) throws TreeMatcherException {
        if (findRelation(n1.getAncestors(), n2, IMappingElement.DISJOINT, mapping)) {
            // we skip n2 subtree, so adjust the counter
            progress(n2.getDescendantCount());

            return;
        }

        if (nodeMatcher.nodeDisjoint(acolMapping, sourceAcols, targetAcols, n1, n2)) {
            addRelation(n1, n2, IMappingElement.DISJOINT, mapping);
            // we skip n2 subtree, so adjust the counter
            progress(n2.getDescendantCount());
            return;
        }

        progress();

        for (Iterator<INode> i = n2.getChildren(); i.hasNext(); ) {
            nodeTreeDisjoint(n1, i.next(), acolMapping, sourceAcols, targetAcols, mapping);
        }
    }

    protected boolean treeSubsumedBy(INode n1, INode n2, boolean direction,
                                     IContextMapping<IAtomicConceptOfLabel> acolMapping,
                                     Map<String, IAtomicConceptOfLabel> sourceAcols,
                                     Map<String, IAtomicConceptOfLabel> targetAcols,
                                     HashSet<IMappingElement<INode>> mapping) throws TreeMatcherException {
        if (findRelation(n1, n2, IMappingElement.DISJOINT, mapping)) {
            // we skip n1 subtree, so adjust the counter
            progress(n1.getDescendantCount());
            return false;
        }

        progress();

        if (!nodeMatcher.nodeSubsumedBy(n1, n2, acolMapping, sourceAcols, targetAcols)) {
            for (Iterator<INode> i = n1.getChildren(); i.hasNext(); ) {
                treeSubsumedBy(i.next(), n2, direction, acolMapping, sourceAcols, targetAcols, mapping);
            }
        } else {
            boolean lastNodeFound = false;
            for (Iterator<INode> i = n2.getChildren(); i.hasNext(); ) {
                if (treeSubsumedBy(n1, i.next(), direction, acolMapping, sourceAcols, targetAcols, mapping)) {
                    lastNodeFound = true;
                }
            }
            if (!lastNodeFound) {
                addSubsumptionRelation(n1, n2, direction, mapping);
            }

            // we skip n1 subtree, so adjust the counter
            progress(n1.getDescendantCount());
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

    protected boolean findRelation(Iterator<INode> sourceNodes, INode targetNode, char relation, HashSet<IMappingElement<INode>> mapping) {
        while (sourceNodes.hasNext()) {
            INode sourceNode = sourceNodes.next();
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