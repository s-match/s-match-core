package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * Filters the mapping, expanding equivalence links into pairs of more general and less general links.
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
public class RedundantMappingFilterEQ extends RedundantMappingFilter {

    public RedundantMappingFilterEQ(IMappingFactory mappingFactory) {
        super(mappingFactory);
    }

    // because in filtering we do not "discover" links
    // we need to check ancestors and descendants, and not only parents and children
    // otherwise, in case of series of redundant links we remove first by checking parent
    // and then all the rest is not removed because of the "gap"

    protected boolean verifyCondition1(IContextMapping<INode> mapping, IMappingElement<INode> e) {
        return findRelation(IMappingElement.LESS_GENERAL, e.getSource().getAncestors(), e.getTarget(), mapping) ||
                findRelation(IMappingElement.EQUIVALENCE, e.getSource().getAncestors(), e.getTarget(), mapping) ||

                findRelation(IMappingElement.LESS_GENERAL, e.getSource(), e.getTarget().getDescendants(), mapping) ||
                findRelation(IMappingElement.EQUIVALENCE, e.getSource(), e.getTarget().getDescendants(), mapping) ||

                findRelation(IMappingElement.LESS_GENERAL, e.getSource().getAncestors(), e.getTarget().getDescendants(), mapping) ||
                findRelation(IMappingElement.EQUIVALENCE, e.getSource().getAncestors(), e.getTarget().getDescendants(), mapping);
    }

    protected boolean verifyCondition2(IContextMapping<INode> mapping, IMappingElement<INode> e) {
        return findRelation(IMappingElement.MORE_GENERAL, e.getSource(), e.getTarget().getAncestors(), mapping) ||
                findRelation(IMappingElement.EQUIVALENCE, e.getSource(), e.getTarget().getAncestors(), mapping) ||

                findRelation(IMappingElement.MORE_GENERAL, e.getSource().getDescendants(), e.getTarget(), mapping) ||
                findRelation(IMappingElement.EQUIVALENCE, e.getSource().getDescendants(), e.getTarget(), mapping) ||

                findRelation(IMappingElement.MORE_GENERAL, e.getSource().getDescendants(), e.getTarget().getAncestors(), mapping) ||
                findRelation(IMappingElement.EQUIVALENCE, e.getSource().getDescendants(), e.getTarget().getAncestors(), mapping);
    }
}
