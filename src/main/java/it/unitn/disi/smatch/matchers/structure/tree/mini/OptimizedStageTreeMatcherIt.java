package it.unitn.disi.smatch.matchers.structure.tree.mini;

import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.matchers.structure.tree.TreeMatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Version with an iterator.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class OptimizedStageTreeMatcherIt extends OptimizedStageTreeMatcher {

    private static final Logger log = LoggerFactory.getLogger(OptimizedStageTreeMatcherIt.class);

    @Override
    public IContextMapping<INode> treeMatch(IContext sourceContext, IContext targetContext, IContextMapping<IAtomicConceptOfLabel> acolMapping) throws TreeMatcherException {
        this.acolMapping = acolMapping;

        total = (long) (sourceContext.getRoot().getDescendantCount() + 1) * (long) (targetContext.getRoot().getDescendantCount() + 1);
        reportInt = (total / 20) + 1;//i.e. report every 5%

        for (Iterator<INode> i = sourceContext.getNodes(); i.hasNext(); ) {
            INode sourceNode = i.next();
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

    @Override
    protected void treeDisjoint(INode n1, INode n2) throws TreeMatcherException {
        nodeTreeDisjoint(n1, n2);
        for (Iterator<INode> i = n1.getChildren(); i.hasNext(); ) {
            treeDisjoint(i.next(), n2);
        }
    }

    @Override
    protected void nodeTreeDisjoint(INode n1, INode n2) throws TreeMatcherException {
        if (findRelation(n1.getAncestors(), n2, IMappingElement.DISJOINT)) {
            // we skip n2 subtree, so adjust the counter
            final long skipTo = counter + n2.getDescendantCount();
            while (counter < skipTo) {
                progress();
            }

            return;
        }

        if (smatchMatcher.nodeDisjoint(acolMapping, nmtAcols, sourceAcols, targetAcols, n1, n2)) {
            addRelation(n1, n2, IMappingElement.DISJOINT);
            // we skip n2 subtree, so adjust the counter
            final long skipTo = counter + n2.getDescendantCount();
            while (counter < skipTo) {
                progress();
            }
            return;
        }

        progress();

        for (Iterator<INode> i = n2.getChildren(); i.hasNext(); ) {
            nodeTreeDisjoint(n1, i.next());
        }
    }

    @Override
    protected boolean treeSubsumedBy(INode n1, INode n2) throws TreeMatcherException {
        if (findRelation(n1, n2, IMappingElement.DISJOINT)) {
            // we skip n1 subtree, so adjust the counter
            final long skipTo = counter + n1.getDescendantCount();
            while (counter < skipTo) {
                progress();
            }

            return false;
        }

        progress();
        if (!smatchMatcher.nodeSubsumedBy(acolMapping, nmtAcols, sourceAcols, targetAcols, n1, n2)) {
            for (Iterator<INode> i = n1.getChildren(); i.hasNext(); ) {
                treeSubsumedBy(i.next(), n2);
            }
        } else {
            boolean lastNodeFound = false;
            for (Iterator<INode> i = n2.getChildren(); i.hasNext(); ) {
                if (treeSubsumedBy(n1, i.next())) {
                    lastNodeFound = true;
                }
            }
            if (!lastNodeFound) {
                addSubsumptionRelation(n1, n2);
            }

            // we skip n1 subtree, so adjust the counter
            final long skipTo = counter + n1.getDescendantCount();
            while (counter < skipTo) {
                progress();
            }
            return true;
        }

        return false;
    }

    protected boolean findRelation(Iterator<INode> sourceNodes, INode targetNode, char relation) {
        while (sourceNodes.hasNext()) {
            INode sourceNode = sourceNodes.next();
            if (mapping.contains(createMappingElement(sourceNode, targetNode, relation))) {
                return true;
            }
        }
        return false;
    }
}
