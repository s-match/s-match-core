package it.unitn.disi.smatch.matchers.structure.tree.def;

import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.matchers.structure.tree.BaseTreeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.ITreeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.TreeMatcherException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Matches all nodes of the source context with all nodes of the target context.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class DefaultTreeMatcher extends BaseTreeMatcher implements ITreeMatcher {

    private static final Logger log = LoggerFactory.getLogger(DefaultTreeMatcher.class);

    public IContextMapping<INode> treeMatch(IContext sourceContext, IContext targetContext, IContextMapping<IAtomicConceptOfLabel> acolMapping) throws TreeMatcherException {
        IContextMapping<INode> mapping = mappingFactory.getContextMappingInstance(sourceContext, targetContext);

        // semantic relation for particular node matching task
        char relation;

        long counter = 0;
        long total = (long) sourceContext.getNodesList().size() * (long) targetContext.getNodesList().size();
        long reportInt = (total / 20) + 1;//i.e. report every 5%

        Map<String, IAtomicConceptOfLabel> sourceAcols = new HashMap<String, IAtomicConceptOfLabel>();
        Map<String, IAtomicConceptOfLabel> targetAcols = new HashMap<String, IAtomicConceptOfLabel>();

        Map<INode, ArrayList<IAtomicConceptOfLabel>> nmtAcols = new HashMap<INode, ArrayList<IAtomicConceptOfLabel>>();

        for (INode sourceNode : sourceContext.getNodesList()) {
            for (INode targetNode : targetContext.getNodesList()) {
                relation = nodeMatcher.nodeMatch(acolMapping, nmtAcols, sourceAcols, targetAcols, sourceNode, targetNode);
                mapping.setRelation(sourceNode, targetNode, relation);

                counter++;
                if ((SMatchConstants.LARGE_TASK < total) && (0 == (counter % reportInt)) && log.isInfoEnabled()) {
                    log.info(100 * counter / total + "%");
                }
            }
        }

        return mapping;
    }
}