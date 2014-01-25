package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Iterator;

/**
 * Version with an iterator.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class RedundantGeneratorMappingFilterIt extends RedundantGeneratorMappingFilter {

    private static final Logger log = LoggerFactory.getLogger(RedundantGeneratorMappingFilterIt.class);

    @Override
    public IContextMapping<INode> filter(IContextMapping<INode> mapping) {
        if (log.isInfoEnabled()) {
            log.info("Filtering started...");
        }
        long start = System.currentTimeMillis();

        IContext sourceContext = mapping.getSourceContext();
        IContext targetContext = mapping.getTargetContext();

        long counter = 0;
        long total = 2 * (long) (sourceContext.getRoot().getDescendantCount() + 1) * (long) (targetContext.getRoot().getDescendantCount() + 1);
        long reportInt = (total / 20) + 1;//i.e. report every 5%

        for (Iterator<INode> i = sourceContext.getNodes(); i.hasNext();) {
            INode source = i.next();
            for (Iterator<INode> j = targetContext.getNodes(); j.hasNext();) {
                INode target = j.next();
                mapping.setRelation(source, target, computeMapping(mapping, source, target));

                counter++;
                if ((SMatchConstants.LARGE_TASK < total) && (0 == (counter % reportInt)) && log.isInfoEnabled()) {
                    log.info(100 * counter / total + "%");
                }
            }
        }

        for (Iterator<INode> i = sourceContext.getNodes(); i.hasNext();) {
            INode source = i.next();
            for (Iterator<INode> j = targetContext.getNodes(); j.hasNext();) {
                INode target = j.next();
                switch (mapping.getRelation(source, target)) {
                    case IMappingElement.ENTAILED_LESS_GENERAL: {
                        mapping.setRelation(source, target, IMappingElement.LESS_GENERAL);
                        break;
                    }
                    case IMappingElement.ENTAILED_MORE_GENERAL: {
                        mapping.setRelation(source, target, IMappingElement.MORE_GENERAL);
                        break;
                    }
                    case IMappingElement.ENTAILED_DISJOINT: {
                        mapping.setRelation(source, target, IMappingElement.DISJOINT);
                        break;
                    }
                    default: {
                    }
                }

                counter++;
                if ((SMatchConstants.LARGE_TASK < total) && (0 == (counter % reportInt)) && log.isInfoEnabled()) {
                    log.info(100 * counter / total + "%");
                }
            }
        }

        if (log.isInfoEnabled()) {
            log.info("Filtering finished: " + (System.currentTimeMillis() - start) + " ms");
        }

        return mapping;
    }

    @Override
    protected boolean verifyCondition1(IContextMapping<INode> mapping, INode source, INode target) {
        return findRelation(mapping, IMappingElement.LESS_GENERAL, source.getAncestors(), target) ||
                findRelation(mapping, IMappingElement.LESS_GENERAL, source, target.getDescendants()) ||
                findRelation(mapping, IMappingElement.LESS_GENERAL, source.getAncestors(), target.getDescendants()) ||

                findRelation(mapping, IMappingElement.EQUIVALENCE, source.getAncestors(), target) ||
                findRelation(mapping, IMappingElement.EQUIVALENCE, source, target.getDescendants()) ||
                findRelation(mapping, IMappingElement.EQUIVALENCE, source.getAncestors(), target.getDescendants());
    }

    @Override
    protected boolean verifyCondition2(IContextMapping<INode> mapping, INode source, INode target) {
        return findRelation(mapping, IMappingElement.MORE_GENERAL, source, target.getAncestors()) ||
                findRelation(mapping, IMappingElement.MORE_GENERAL, source.getDescendants(), target) ||
                findRelation(mapping, IMappingElement.MORE_GENERAL, source.getDescendants(), target.getAncestors()) ||

                findRelation(mapping, IMappingElement.EQUIVALENCE, source, target.getAncestors()) ||
                findRelation(mapping, IMappingElement.EQUIVALENCE, source.getDescendants(), target) ||
                findRelation(mapping, IMappingElement.EQUIVALENCE, source.getDescendants(), target.getAncestors());
    }

    @Override
    protected boolean verifyCondition3(IContextMapping<INode> mapping, INode source, INode target) {
        return findRelation(mapping, IMappingElement.DISJOINT, source, target.getAncestors()) ||
                findRelation(mapping, IMappingElement.DISJOINT, source.getAncestors(), target) ||
                findRelation(mapping, IMappingElement.DISJOINT, source.getAncestors(), target.getAncestors());
    }

    protected boolean findRelation(IContextMapping<INode> mapping, char relation, Iterator<INode> sourceNodes, INode targetNode) {
        while (sourceNodes.hasNext()) {
            if (relation == getRelation(mapping, sourceNodes.next(), targetNode)) {
                return true;
            }
        }
        return false;
    }

    protected boolean findRelation(IContextMapping<INode> mapping, char relation, INode sourceNode, Iterator<INode> targetNodes) {
        while (targetNodes.hasNext()) {
            if (relation == getRelation(mapping, sourceNode, targetNodes.next())) {
                return true;
            }
        }
        return false;
    }

    protected boolean findRelation(IContextMapping<INode> mapping, char relation, Iterator<INode> sourceNodes, Iterator<INode> targetNodes) {
        while (sourceNodes.hasNext()) {
            INode sourceNode = sourceNodes.next();
            while (targetNodes.hasNext()) {
                if (relation == getRelation(mapping, sourceNode, targetNodes.next())) {
                    return true;
                }
            }
        }
        return false;
    }
}
