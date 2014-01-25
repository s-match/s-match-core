package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.common.components.Configurable;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;

/**
 * Generates entailed links which logically follow from the links in the mapping.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class RedundantGeneratorMappingFilter extends Configurable implements IMappingFilter {

    private static final Logger log = LoggerFactory.getLogger(RedundantGeneratorMappingFilter.class);

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

        for (INode source : sourceContext.getNodesList()) {
            for (INode target : targetContext.getNodesList()) {
                mapping.setRelation(source, target, computeMapping(mapping, source, target));
                counter++;
                if ((SMatchConstants.LARGE_TASK < total) && (0 == (counter % reportInt)) && log.isInfoEnabled()) {
                    log.info(100 * counter / total + "%");
                }
            }
        }

        for (INode source : sourceContext.getNodesList()) {
            for (INode target : targetContext.getNodesList()) {
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

    protected char computeMapping(IContextMapping<INode> mapping, INode source, INode target) {
        final char relation = mapping.getRelation(source, target);
        if (IMappingElement.DISJOINT == relation) {
            return IMappingElement.DISJOINT;
        }
        if (isRedundant(mapping, source, target, IMappingElement.DISJOINT)) {
            return IMappingElement.ENTAILED_DISJOINT;
        }
        if (IMappingElement.EQUIVALENCE == relation) {
            return IMappingElement.EQUIVALENCE;
        }
        boolean isLG = (IMappingElement.LESS_GENERAL == relation || isRedundant(mapping, source, target, IMappingElement.LESS_GENERAL));
        boolean isMG = (IMappingElement.MORE_GENERAL == relation || isRedundant(mapping, source, target, IMappingElement.MORE_GENERAL));
        if (isLG && isMG) {
            return IMappingElement.EQUIVALENCE;
        }
        if (isLG) {
            if (IMappingElement.LESS_GENERAL == relation) {
                return IMappingElement.LESS_GENERAL;
            } else {
                return IMappingElement.ENTAILED_LESS_GENERAL;
            }
        }
        if (isMG) {
            if (IMappingElement.MORE_GENERAL == relation) {
                return IMappingElement.MORE_GENERAL;
            } else {
                return IMappingElement.ENTAILED_MORE_GENERAL;
            }
        }

        return IMappingElement.IDK;
    }

    /**
     * Checks whether the relation between source and target is redundant or not for minimal mapping.
     *
     * @param mapping a mapping
     * @param source  source
     * @param target  target
     * @param R       relation between source and target node  @return true for redundant relation
     * @return true if the relation between source and target is redundant
     */
    private boolean isRedundant(IContextMapping<INode> mapping, INode source, INode target, char R) {
        switch (R) {
            case IMappingElement.LESS_GENERAL: {
                if (verifyCondition1(mapping, source, target)) {
                    return true;
                }
                break;
            }
            case IMappingElement.MORE_GENERAL: {
                if (verifyCondition2(mapping, source, target)) {
                    return true;
                }
                break;
            }
            case IMappingElement.DISJOINT: {
                if (verifyCondition3(mapping, source, target)) {
                    return true;
                }
                break;
            }
            case IMappingElement.EQUIVALENCE: {
                if (verifyCondition1(mapping, source, target) && verifyCondition2(mapping, source, target)) {
                    return true;
                }
                break;
            }
            default: {
                return false;
            }

        }// end switch

        return false;
    }

    // because in filtering we do not "discover" links
    // we need to check ancestors and descendants, and not only parents and children
    // otherwise, in case of series of redundant links we remove first by checking parent
    // and then all the rest is not removed because of the "gap"

    protected boolean verifyCondition1(IContextMapping<INode> mapping, INode source, INode target) {
        return findRelation(mapping, IMappingElement.LESS_GENERAL, source.getAncestorsList(), target) ||
                findRelation(mapping, IMappingElement.LESS_GENERAL, source, target.getDescendantsList()) ||
                findRelation(mapping, IMappingElement.LESS_GENERAL, source.getAncestorsList(), target.getDescendantsList()) ||

                findRelation(mapping, IMappingElement.EQUIVALENCE, source.getAncestorsList(), target) ||
                findRelation(mapping, IMappingElement.EQUIVALENCE, source, target.getDescendantsList()) ||
                findRelation(mapping, IMappingElement.EQUIVALENCE, source.getAncestorsList(), target.getDescendantsList());
    }

    protected boolean verifyCondition2(IContextMapping<INode> mapping, INode source, INode target) {
        return findRelation(mapping, IMappingElement.MORE_GENERAL, source, target.getAncestorsList()) ||
                findRelation(mapping, IMappingElement.MORE_GENERAL, source.getDescendantsList(), target) ||
                findRelation(mapping, IMappingElement.MORE_GENERAL, source.getDescendantsList(), target.getAncestorsList()) ||

                findRelation(mapping, IMappingElement.EQUIVALENCE, source, target.getAncestorsList()) ||
                findRelation(mapping, IMappingElement.EQUIVALENCE, source.getDescendantsList(), target) ||
                findRelation(mapping, IMappingElement.EQUIVALENCE, source.getDescendantsList(), target.getAncestorsList());
    }

    protected boolean verifyCondition3(IContextMapping<INode> mapping, INode source, INode target) {
        return findRelation(mapping, IMappingElement.DISJOINT, source, target.getAncestorsList()) ||
                findRelation(mapping, IMappingElement.DISJOINT, source.getAncestorsList(), target) ||
                findRelation(mapping, IMappingElement.DISJOINT, source.getAncestorsList(), target.getAncestorsList());
    }

    protected boolean findRelation(IContextMapping<INode> mapping, char relation, List<INode> sourceNodes, INode targetNode) {
        for (INode sourceNode : sourceNodes) {
            if (relation == getRelation(mapping, sourceNode, targetNode)) {
                return true;
            }
        }
        return false;
    }

    protected boolean findRelation(IContextMapping<INode> mapping, char relation, INode sourceNode, List<INode> targetNodes) {
        for (INode targetNode : targetNodes) {
            if (relation == getRelation(mapping, sourceNode, targetNode)) {
                return true;
            }
        }
        return false;
    }

    protected boolean findRelation(IContextMapping<INode> mapping, char relation, List<INode> sourceNodes, List<INode> targetNodes) {
        for (INode sourceNode : sourceNodes) {
            for (INode targetNode : targetNodes) {
                if (relation == getRelation(mapping, sourceNode, targetNode)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected char getRelation(IContextMapping<INode> mapping, INode a, INode b) {
        return mapping.getRelation(a, b);
    }
}
