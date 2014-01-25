package it.unitn.disi.smatch.matchers.element;

import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
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
public class MatcherLibraryIt extends MatcherLibrary {

    private static final Logger log = LoggerFactory.getLogger(MatcherLibraryIt.class);

    @Override
    public IContextMapping<IAtomicConceptOfLabel> elementLevelMatching(IContext sourceContext, IContext targetContext) throws MatcherLibraryException {
        // Calculates relations between all ACoLs in both contexts and produces a mapping between them.
        // Corresponds to Step 3 of the semantic matching algorithm.

        IContextMapping<IAtomicConceptOfLabel> result = mappingFactory.getACoLMappingInstance(sourceContext, targetContext);

        long counter = 0;
        long total = getACoLCount(sourceContext) * getACoLCount(targetContext);
        long reportInt = (total / 20) + 1;//i.e. report every 5%
        for (Iterator<INode> i = sourceContext.getNodes(); i.hasNext();) {
            INode sourceNode = i.next();
            for (Iterator<IAtomicConceptOfLabel> ii = sourceNode.getNodeData().getACoLs(); ii.hasNext();) {
                IAtomicConceptOfLabel sourceACoL = ii.next();
                for (Iterator<INode> j = targetContext.getNodes(); j.hasNext();) {
                    INode targetNode = j.next();
                    for (Iterator<IAtomicConceptOfLabel> jj = targetNode.getNodeData().getACoLs(); jj.hasNext();) {
                        IAtomicConceptOfLabel targetACoL = jj.next();
                        //Use Element level semantic matchers library
                        //to check the relation holding between two ACoLs represented by lists of WN senses and tokens
                        final char relation = getRelation(sourceACoL, targetACoL);
                        result.setRelation(sourceACoL, targetACoL, relation);

                        counter++;
                        if ((SMatchConstants.LARGE_TASK < total) && (0 == (counter % reportInt)) && log.isInfoEnabled()) {
                            log.info(100 * counter / total + "%");
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected long getACoLCount(IContext context) {
        long result = 0;
        for (Iterator<INode> i = context.getNodes(); i.hasNext();) {
            for (Iterator<IAtomicConceptOfLabel> j = i.next().getNodeData().getACoLs(); j.hasNext();) {
                j.next();
                result++;
            }
        }
        return result;
    }
}
