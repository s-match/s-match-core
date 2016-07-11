package it.unitn.disi.smatch.data.util;

import it.unitn.disi.smatch.SMatchConstants;
import org.slf4j.Logger;

/**
 * A container for progress items.
 */
public class ProgressContainer {
    private final Logger log;
    private long counter;
    private final long total;
    private final long reportInt;

    public ProgressContainer(long total, Logger log) {
        this.log = log;
        this.total = total;
        this.counter = 0;
        this.reportInt = (total / 20) + 1;//i.e. report every 5%
    }

    public long getCounter() {
        return counter;
    }

    public void progress() {
        counter++;
        if ((SMatchConstants.LARGE_TASK < total) && (0 == (counter % reportInt)) && log.isInfoEnabled()) {
            log.info(100 * counter / total + "%");
        }
    }
}
