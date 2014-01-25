package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.common.components.ConfigurableException;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Properties;

/**
 * Retains only specified kind of links in the mapping. Accepts relations kinds in a parameter retainRelations.
 * By default retains only equivalences (=). For other relation kinds see constants in
 * {@link it.unitn.disi.smatch.data.mappings.IMappingElement}.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class RetainRelationsMappingFilter extends BaseFilter implements IMappingFilter {

    private static final Logger log = LoggerFactory.getLogger(RetainRelationsMappingFilter.class);

    private static final String RETAIN_RELATIONS_KEY = "retainRelations";
    private String retainRelations = "=";

    @Override
    public boolean setProperties(Properties newProperties) throws ConfigurableException {
        boolean result = super.setProperties(newProperties);
        if (result) {
            if (newProperties.containsKey(RETAIN_RELATIONS_KEY)) {
                retainRelations = newProperties.getProperty(RETAIN_RELATIONS_KEY);
            }
        }
        return result;
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
