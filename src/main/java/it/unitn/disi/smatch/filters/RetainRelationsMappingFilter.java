package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.INode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retains only specified kind of links in the mapping.
 * For relation kinds see constants in {@link it.unitn.disi.smatch.data.mappings.IMappingElement}.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class RetainRelationsMappingFilter extends BaseFilter implements IMappingFilter {

    private static final Logger log = LoggerFactory.getLogger(RetainRelationsMappingFilter.class);

    private final String retainRelations;

    protected RetainRelationsMappingFilter(IMappingFactory mappingFactory, String retainRelations) {
        super(mappingFactory);
        this.retainRelations = retainRelations;
    }

    public IContextMapping<INode> filter(IContextMapping<INode> mapping) {
        if (log.isInfoEnabled()) {
            log.info("Filtering started...");
        }
        long start = System.currentTimeMillis();

        IContextMapping<INode> result = mappingFactory.getContextMappingInstance(mapping.getSourceContext(), mapping.getTargetContext());

        long counter = 0;
        long total = mapping.size();
        long reportInt = (total / 20) + 1;//i.e. report every 5%

        //check each mapping
        for (IMappingElement<INode> e : mapping) {
            if (-1 < retainRelations.indexOf(e.getRelation())) {
                result.add(e);
            }

            counter++;
            if ((SMatchConstants.LARGE_TASK < total) && (0 == (counter % reportInt)) && log.isInfoEnabled()) {
                log.info(100 * counter / total + "%");
            }
        }

        if (log.isInfoEnabled()) {
            log.info("Filtering finished: " + (System.currentTimeMillis() - start) + " ms");
        }
        return result;
    }
}
