package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.common.components.Configurable;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Base class for mapping renderers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseMappingRenderer extends Configurable implements IMappingRenderer {

    private static final Logger log = LoggerFactory.getLogger(BaseMappingRenderer.class);

    protected int lg;
    protected int mg;
    protected int eq;
    protected int dj;

    protected long counter;
    protected long total;
    protected long reportInt;

    public void render(IContextMapping<INode> mapping, String outputFile) throws MappingRendererException {
        lg = mg = eq = dj = 0;
        counter = 0;
        total = mapping.size();
        reportInt = (total / 20) + 1;//i.e. report every 5%

        process(mapping, outputFile);

        reportStats(mapping);
    }

    protected abstract void process(IContextMapping<INode> mapping, String outputFile) throws MappingRendererException;

    protected void reportStats(IContextMapping<INode> mapping) {
        if (log.isInfoEnabled()) {
            log.info("rendered links: " + mapping.size());
            log.info("LG: " + lg);
            log.info("MG: " + mg);
            log.info("EQ: " + eq);
            log.info("DJ: " + dj);
        }
    }

    protected void reportProgress() {
        counter++;
        if ((SMatchConstants.LARGE_TASK < total) && (0 == (counter % reportInt)) && log.isInfoEnabled()) {
            log.info(100 * counter / total + "%");
        }
    }

    protected void countRelation(final char relation) {
        switch (relation) {
            case IMappingElement.LESS_GENERAL: {
                lg++;
                break;
            }
            case IMappingElement.MORE_GENERAL: {
                mg++;
                break;
            }
            case IMappingElement.EQUIVALENCE: {
                eq++;
                break;
            }
            case IMappingElement.DISJOINT: {
                dj++;
                break;
            }
            default:
                break;
        }
    }
}
