package it.unitn.disi.smatch.matchers.structure.tree.mini;

import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.common.components.ConfigurableException;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.ReversingMappingElement;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.matchers.structure.node.OptimizedStageNodeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.BaseTreeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.ITreeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.TreeMatcherException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
public class OptimizedStageTreeMatcher extends BaseTreeMatcher implements ITreeMatcher {

    private static final Logger log = LoggerFactory.getLogger(OptimizedStageTreeMatcher.class);

    protected OptimizedStageNodeMatcher smatchMatcher;
    protected Map<String, IAtomicConceptOfLabel> sourceAcols;
    protected Map<String, IAtomicConceptOfLabel> targetAcols;

    protected IContextMapping<IAtomicConceptOfLabel> acolMapping;
    protected Map<INode, ArrayList<IAtomicConceptOfLabel>> nmtAcols;

    // need another mapping because here we allow < and > between the same pair of nodes
    protected HashSet<IMappingElement<INode>> mapping;

    protected long counter = 0;
    protected long total;
    protected long reportInt;

    protected boolean direction;

    @Override
    public boolean setProperties(Properties newProperties) throws ConfigurableException {
        boolean result = super.setProperties(newProperties);
        if (result) {
            if (nodeMatcher instanceof OptimizedStageNodeMatcher) {
                smatchMatcher = (OptimizedStageNodeMatcher) nodeMatcher;
            } else {
                throw new TreeMatcherException("OptimizedStageTreeMatcher works only with OptimizedStageNodeMatcher");
            }
        }
        return result;
    }

    public IContextMapping<INode> treeMatch(IContext sourceContext, IContext targetContext, IContextMapping<IAtomicConceptOfLabel> acolMapping) throws TreeMatcherException {
        this.acolMapping = acolMapping;

        total = (long) sourceContext.getNodesList().size() * (long) targetContext.getNodesList().size();
        reportInt = (total / 20) + 1;//i.e. report every 5%

        for (INode sourceNode : sourceContext.getNodesList()) {
            // this is to distinguish below, in matcher, for axiom creation
            sourceNode.getNodeData().setSource(true);
        }

        sourceAcols = new HashMap<String, IAtomicConceptOfLabel>();
        targetAcols = new HashMap<String, IAtomicConceptOfLabel>();

        mapping = new HashSet<IMappingElement<INode>>();
        nmtAcols = new HashMap<INode, ArrayList<IAtomicConceptOfLabel>>();

        log.info("DJ...");
        treeDisjoint(sourceContext.getRoot(), targetContext.getRoot());
        int dj = mapping.size();
        log.info("Links found DJ: " + dj);
        counter = 0;

        log.info("LG...");
        direction = true;
        treeSubsumedBy(sourceContext.getRoot(), targetContext.getRoot());
        int lg = mapping.size() - dj;
        log.info("Links found LG: " + lg);
        counter = 0;

        log.info("MG...");
        direction = false;
        treeSubsumedBy(targetContext.getRoot(), sourceContext.getRoot());
        int mg = mapping.size() - dj - lg;
        log.info("Links found MG: " + mg);
        counter = 0;

        log.info("TreeEquiv...");
        IContextMapping<INode> result = treeEquiv(mapping, sourceContext, targetContext);
        log.info("TreeEquiv finished");

        return result;
    }

    protected void treeDisjoint(INode n1, INode n2) throws TreeMatcherException {
        nodeTreeDisjoint(n1, n2);
        for (INode child : n1.getChildrenList()) {
            treeDisjoint(child, n2);
        }
    }

    protected void nodeTreeDisjoint(INode n1, INode n2) throws TreeMatcherException {
        if (findRelation(n1.getAncestorsList(), n2, IMappingElement.DISJOINT)) {
            // we skip n2 subtree, so adjust the counter
            final long skipTo = counter + n2.getDescendantsList().size();
            while (counter < skipTo) {
                progress();
            }

            return;
        }

        if (smatchMatcher.nodeDisjoint(acolMapping, nmtAcols, sourceAcols, targetAcols, n1, n2)) {
            addRelation(n1, n2, IMappingElement.DISJOINT);
            // we skip n2 subtree, so adjust the counter
            final long skipTo = counter + n2.getDescendantsList().size();
            while (counter < skipTo) {
                progress();
            }
            return;
        }

        progress();

        for (INode child : n2.getChildrenList()) {
            nodeTreeDisjoint(n1, child);
        }
    }

    protected boolean treeSubsumedBy(INode n1, INode n2) throws TreeMatcherException {
        if (findRelation(n1, n2, IMappingElement.DISJOINT)) {
            // we skip n1 subtree, so adjust the counter
            final long skipTo = counter + n1.getDescendantsList().size();
            while (counter < skipTo) {
                progress();
            }

            return false;
        }

        progress();
        if (!smatchMatcher.nodeSubsumedBy(acolMapping, nmtAcols, sourceAcols, targetAcols, n1, n2)) {
            for (INode child : n1.getChildrenList()) {
                treeSubsumedBy(child, n2);
            }
        } else {
            boolean lastNodeFound = false;
            for (INode child : n2.getChildrenList()) {
                if (treeSubsumedBy(n1, child)) {
                    lastNodeFound = true;
                }
            }
            if (!lastNodeFound) {
                addSubsumptionRelation(n1, n2);
            }

            // we skip n1 subtree, so adjust the counter
            final long skipTo = counter + n1.getDescendantsList().size();
            while (counter < skipTo) {
                progress();
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

    protected void addSubsumptionRelation(INode n1, INode n2) {
        if (direction) {
            mapping.add(createMappingElement(n1, n2, IMappingElement.LESS_GENERAL));
        } else {
            mapping.add(createMappingElement(n2, n1, IMappingElement.MORE_GENERAL));
        }
    }

    protected void addRelation(INode n1, INode n2, char relation) {
        mapping.add(createMappingElement(n1, n2, relation));
    }

    protected boolean findRelation(INode sourceNode, INode targetNode, char relation) {
        return mapping.contains(createMappingElement(sourceNode, targetNode, relation));
    }

    protected boolean findRelation(List<INode> sourceNodes, INode targetNode, char relation) {
        for (INode sourceNode : sourceNodes) {
            if (mapping.contains(createMappingElement(sourceNode, targetNode, relation))) {
                return true;
            }
        }
        return false;
    }

    protected void progress() {
        counter++;
        if ((SMatchConstants.LARGE_TASK < total) && (0 == (counter % reportInt)) && log.isInfoEnabled()) {
            log.info(100 * counter / total + "%");
        }
    }

    protected static IMappingElement<INode> createMappingElement(INode source, INode target, char relation) {
        return new ReversingMappingElement(source, target, relation);
    }
}