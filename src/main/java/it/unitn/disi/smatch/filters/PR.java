package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.mapping.IMappingLoader;
import it.unitn.disi.smatch.loaders.mapping.MappingLoaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

/**
 * Computes precision and recall using positive and negative parts of the golden standard. Needs the
 * following configuration parameters:
 * <p/>
 * mappingLoader - an instance of IMappingLoader
 * <p/>
 * mappings - locations of max 2 mappings, separated with semicolon, positive comes first, negative comes second. It is
 * possible to specify only positive mapping.
 * <p/>
 * <p/>
 * For theories behind this way of calculating precision and recall check out TaxME2 paper:
 * http://eprints.biblio.unitn.it/archive/00001345/
 * A Large Scale Dataset for the Evaluation of Ontology Matching Systems by
 * Giunchiglia, Fausto and Yatskevich, Mikalai and Avesani, Paolo and Shvaiko, Pavel
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class PR extends BaseFilter {

    private static final Logger log = LoggerFactory.getLogger(PR.class);

    protected final IMappingLoader mappingLoader;

    protected final String[] mappingLocations;

    public PR(IMappingFactory mappingFactory, IMappingLoader mappingLoader, String mappingLocation) {
        super(mappingFactory);
        this.mappingLoader = mappingLoader;
        this.mappingLocations = mappingLocation.split(";");

    }

    @SuppressWarnings("unchecked")
    public IContextMapping<INode> filter(IContextMapping<INode> mapping) throws MappingFilterException {
        IContextMapping<INode>[] filterMappings = (IContextMapping<INode>[]) new IContextMapping[2];
        //load the mapping
        try {
            log.debug("Loading positive mapping...");
            filterMappings[0] = mappingLoader.loadMapping(mapping.getSourceContext(), mapping.getTargetContext(), mappingLocations[0]);
            log.debug("Loaded positive mapping...");

            if (1 < mappingLocations.length) {
                log.debug("Loading negative mapping...");
                filterMappings[1] = mappingLoader.loadMapping(mapping.getSourceContext(), mapping.getTargetContext(), mappingLocations[1]);
                log.debug("Loaded negative mapping...");
            }
        } catch (MappingLoaderException e) {
            throw new MappingFilterException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }

        long posSize = filterMappings[0].size();

        filterMappings[0].retainAll(mapping);
        if (1 < mappingLocations.length) {
            filterMappings[1].retainAll(mapping);
        }

        long posTruePositiveSize = filterMappings[0].size();
        double p = 0;
        double r = 0;

        if (1 < mappingLocations.length) {
            long negTruePositiveSize = filterMappings[1].size();
            if (0 < (posTruePositiveSize + negTruePositiveSize) && 0 < posSize) {
                if (log.isInfoEnabled()) {
                    log.info("positive true positive:\t" + posTruePositiveSize);
                    log.info("negative true positive:\t" + negTruePositiveSize);
                }
                p = posTruePositiveSize / (double) (posTruePositiveSize + negTruePositiveSize);
                r = posTruePositiveSize / (double) posSize;
            }
        } else {
            if (0 < mapping.size() && 0 < posSize) {
                if (log.isInfoEnabled()) {
                    log.info("positive true positive:\t" + posTruePositiveSize);
                }
                p = posTruePositiveSize / (double) mapping.size();
                r = posTruePositiveSize / (double) posSize;
            }
        }

        DecimalFormat df = new DecimalFormat("00.0000%");
        if (log.isInfoEnabled()) {
            log.info("Precision:\t" + df.format(p));
            log.info("Recall   :\t" + df.format(r));
            if (0 != posTruePositiveSize) {
                log.info("F-Measure:\t" + df.format((2 * p * r) / (p + r)));
            } else {
                log.info("F-Measure:\t" + df.format(0));
            }
        }

        return mapping;
    }
}