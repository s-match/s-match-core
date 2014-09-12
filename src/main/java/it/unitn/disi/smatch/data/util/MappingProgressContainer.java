package it.unitn.disi.smatch.data.util;

import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import org.slf4j.Logger;

public class MappingProgressContainer {
    private final Logger log;
    private int lg = 0;
    private int mg = 0;
    private int eq = 0;
    private int dj = 0;
    private long counter = 0;
    private final long total;
    private final long reportInt;

    public MappingProgressContainer(long total, Logger log) {
        this.log = log;
        this.total = total;
        this.reportInt = (total / 20) + 1;//i.e. report every 5%
    }

    public MappingProgressContainer(Logger log) {
        this.log = log;
        this.total = -1;
        this.reportInt = -1;
    }

    public long getCounter() {
        return counter;
    }

    public void progress() {
        counter++;
        if (-1 == total) {
            if (0 == (counter % 1000)) {
                if (log.isInfoEnabled()) {
                    log.info("Links: " + counter);
                }
            }
        } else {
            if ((SMatchConstants.LARGE_TASK < total) && (0 == (counter % reportInt)) && log.isInfoEnabled()) {
                log.info(100 * counter / total + "%");
            }
        }
    }

    public void reportStats() {
        if (log.isInfoEnabled()) {
            log.info("Mapping processing finished. Links: " + counter);
            log.info("LG: " + lg);
            log.info("MG: " + mg);
            log.info("EQ: " + eq);
            log.info("DJ: " + dj);
        }
    }

    public void countRelation(final char relation) {
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
