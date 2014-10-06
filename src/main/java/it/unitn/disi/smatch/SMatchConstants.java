package it.unitn.disi.smatch;

/**
 * Holds various S-Match constants.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class SMatchConstants {

    //log progress only for tasks with more than LARGE_TASK node pairs
    public static final long LARGE_TASK = 10000;
    //log progress only for tasks with more than LARGE_TREE nodes
    public static final long LARGE_TREE = 100;
    //log progress every thousand pieces
    public static final long TASK_REPORT_PIECES = 1000;
    //log progress every 5% (1/20)
    public static final long TASK_REPORT_FRACTION = 20;
}
