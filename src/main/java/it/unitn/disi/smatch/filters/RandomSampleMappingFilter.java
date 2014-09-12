package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.INode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Selects random sample.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class RandomSampleMappingFilter extends BaseFilter implements IMappingFilter {

    private static final Logger log = LoggerFactory.getLogger(RandomSampleMappingFilter.class);

    private final int sampleSize;

    public RandomSampleMappingFilter(IMappingFactory mappingFactory, int sampleSize) {
        super(mappingFactory);
        this.sampleSize = sampleSize;
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

        //sampling
        int oneIn = (mapping.size() / sampleSize) - (mapping.size() / (10 * sampleSize));
        Random r = new Random();
        if (log.isInfoEnabled()) {
            log.info("Sampling...");
        }
        for (IMappingElement<INode> e : mapping) {
            if (0 == r.nextInt(oneIn) && result.size() < sampleSize) {
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
