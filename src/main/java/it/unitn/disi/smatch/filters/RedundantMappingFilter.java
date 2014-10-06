package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.INode;

import java.util.Iterator;

/**
 * Filters mapping removing all links which logically follow from the other links in the mapping.
 * <p/>
 * For more details see:
 * <p/>
 * <a href="http://eprints.biblio.unitn.it/archive/00001525/">http://eprints.biblio.unitn.it/archive/00001525/</a>
 * <p/>
 * Giunchiglia, Fausto and Maltese, Vincenzo and Autayeu, Aliaksandr. Computing minimal mappings.
 * Technical Report DISI-08-078, Department of Information Engineering and Computer Science, University of Trento.
 * Proc. of the Fourth Ontology Matching Workshop at ISWC 2009.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class RedundantMappingFilter extends BaseFilter implements IAsyncMappingFilter {

    public RedundantMappingFilter(IMappingFactory mappingFactory) {
        super(mappingFactory);
    }

    public RedundantMappingFilter(IMappingFactory mappingFactory, IContextMapping<INode> mapping) {
        super(mappingFactory, mapping);
    }

    @Override
    protected IContextMapping<INode> process(IContextMapping<INode> mapping) {
        IContextMapping<INode> result = mappingFactory.getContextMappingInstance(mapping.getSourceContext(), mapping.getTargetContext());

        for (IMappingElement<INode> e : mapping) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }

            if (!isRedundant(mapping, e)) {
                result.setRelation(e.getSource(), e.getTarget(), e.getRelation());
            }

            progress();
        }

        return result;
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncFilter(IContextMapping<INode> mapping) {
        return new RedundantMappingFilter(mappingFactory, mapping);
    }

    /**
     * Checks the relation between source and target is redundant or not for minimal mapping.
     *
     * @param mapping a mapping
     * @param e       a mapping element
     * @return true for redundant relation
     */
    private boolean isRedundant(IContextMapping<INode> mapping, IMappingElement<INode> e) {
        switch (e.getRelation()) {
            case IMappingElement.LESS_GENERAL: {
                if (verifyCondition1(mapping, e)) {
                    return true;
                }
                break;
            }
            case IMappingElement.MORE_GENERAL: {
                if (verifyCondition2(mapping, e)) {
                    return true;
                }
                break;
            }
            case IMappingElement.DISJOINT: {
                if (verifyCondition3(mapping, e)) {
                    return true;
                }
                break;
            }
            case IMappingElement.EQUIVALENCE: {
                if (verifyCondition4(mapping, e)) {
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

    //because in filtering we have a matrix and we do not "discover" links
    //we need to check ancestors and descendants, and not only parents and children
    //otherwise, in case of series of redundant links we remove the first one by checking parent
    //and then all the rest is not removed because of the "gap"

    protected boolean verifyCondition1(IContextMapping<INode> mapping, IMappingElement<INode> e) {
        return findRelation(IMappingElement.LESS_GENERAL, e.getSource().getAncestors(), e.getTarget(), mapping) ||
                findRelation(IMappingElement.LESS_GENERAL, e.getSource(), e.getTarget().getDescendants(), mapping) ||
                findRelation(IMappingElement.LESS_GENERAL, e.getSource().getAncestors(), e.getTarget().getDescendants(), mapping);
    }

    protected boolean verifyCondition2(IContextMapping<INode> mapping, IMappingElement<INode> e) {
        return findRelation(IMappingElement.MORE_GENERAL, e.getSource(), e.getTarget().getAncestors(), mapping) ||
                findRelation(IMappingElement.MORE_GENERAL, e.getSource().getDescendants(), e.getTarget(), mapping) ||
                findRelation(IMappingElement.MORE_GENERAL, e.getSource().getDescendants(), e.getTarget().getAncestors(), mapping);
    }

    protected boolean verifyCondition3(IContextMapping<INode> mapping, IMappingElement<INode> e) {
        return findRelation(IMappingElement.DISJOINT, e.getSource(), e.getTarget().getAncestors(), mapping) ||
                findRelation(IMappingElement.DISJOINT, e.getSource().getAncestors(), e.getTarget(), mapping) ||
                findRelation(IMappingElement.DISJOINT, e.getSource().getAncestors(), e.getTarget().getAncestors(), mapping);
    }

    protected boolean verifyCondition4(IContextMapping<INode> mapping, IMappingElement<INode> e) {
        return (findRelation(IMappingElement.EQUIVALENCE, e.getSource(), e.getTarget().getAncestors(), mapping) &&
                findRelation(IMappingElement.EQUIVALENCE, e.getSource().getAncestors(), e.getTarget(), mapping))
                ||
                (findRelation(IMappingElement.EQUIVALENCE, e.getSource(), e.getTarget().getDescendants(), mapping) &&
                        findRelation(IMappingElement.EQUIVALENCE, e.getSource().getDescendants(), e.getTarget(), mapping))
                ||
                (findRelation(IMappingElement.EQUIVALENCE, e.getSource().getAncestors(), e.getTarget().getDescendants(), mapping) &&
                        findRelation(IMappingElement.EQUIVALENCE, e.getSource().getDescendants(), e.getTarget().getAncestors(), mapping));
    }

    public boolean findRelation(char relation, Iterator<INode> sourceNodes, INode targetNode, IContextMapping<INode> mapping) {
        while (sourceNodes.hasNext()) {
            if (relation == getRelation(mapping, sourceNodes.next(), targetNode)) {
                return true;
            }
        }
        return false;
    }

    public boolean findRelation(char relation, INode sourceNode, Iterator<INode> targetNodes, IContextMapping<INode> mapping) {
        while (targetNodes.hasNext()) {
            if (relation == getRelation(mapping, sourceNode, targetNodes.next())) {
                return true;
            }
        }
        return false;
    }

    public boolean findRelation(char relation, Iterator<INode> sourceNodes, Iterator<INode> targetNodes, IContextMapping<INode> mapping) {
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

    protected char getRelation(IContextMapping<INode> mapping, INode a, INode b) {
        return mapping.getRelation(a, b);
    }
}
