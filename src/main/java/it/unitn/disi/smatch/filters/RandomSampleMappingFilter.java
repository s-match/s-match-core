package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.async.AsyncTask;
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
public class RandomSampleMappingFilter extends BaseFilter implements IMappingFilter, IAsyncMappingFilter {

    private static final Logger log = LoggerFactory.getLogger(RandomSampleMappingFilter.class);

    private final int sampleSize;

    public RandomSampleMappingFilter(IMappingFactory mappingFactory, int sampleSize) {
        super(mappingFactory);
        this.sampleSize = sampleSize;
    }

    public RandomSampleMappingFilter(IMappingFactory mappingFactory, IContextMapping<INode> mapping, int sampleSize) {
        super(mappingFactory, mapping);
        this.sampleSize = sampleSize;
    }

    @Override
    protected IContextMapping<INode> process(IContextMapping<INode> mapping) {
        IContextMapping<INode> result = mappingFactory.getContextMappingInstance(mapping.getSourceContext(), mapping.getTargetContext());

        //sampling
        int oneIn = (mapping.size() / sampleSize) - (mapping.size() / (10 * sampleSize));
        Random r = new Random();
        if (log.isInfoEnabled()) {
            log.info("Sampling...");
        }
        for (IMappingElement<INode> e : mapping) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            if (0 == r.nextInt(oneIn) && result.size() < sampleSize) {
                result.add(e);
            }

            progress();
        }
        return result;
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncFilter(IContextMapping<INode> mapping) {
        return new RandomSampleMappingFilter(mappingFactory, mapping, sampleSize);
    }
}