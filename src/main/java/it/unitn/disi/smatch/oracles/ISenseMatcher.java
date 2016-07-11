package it.unitn.disi.smatch.oracles;

import it.unitn.disi.smatch.data.ling.ISense;

import java.util.List;

/**
 * An interface to sense matchers.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface ISenseMatcher {

    /**
     * Returns semantic relation holding between two sets of senses.
     *
     * @param sourceSenses source set of senses
     * @param targetSenses target set of senses
     * @return a relation
     * @throws SenseMatcherException SenseMatcherException
     */
    char getRelation(List<ISense> sourceSenses, List<ISense> targetSenses) throws SenseMatcherException;

    /**
     * Checks whether the source is more general than target.
     *
     * @param source source sense
     * @param target target sense
     * @return whether relation holds
     * @throws SenseMatcherException SenseMatcherException
     */
    boolean isSourceMoreGeneralThanTarget(ISense source, ISense target) throws SenseMatcherException;

    /**
     * Checks whether the source is less general than target.
     *
     * @param source source sense
     * @param target target sense
     * @return whether relation holds
     * @throws SenseMatcherException SenseMatcherException
     */
    boolean isSourceLessGeneralThanTarget(ISense source, ISense target) throws SenseMatcherException;

    /**
     * Checks whether the source is a synonym of the target.
     *
     * @param source source sense
     * @param target target sense
     * @return whether relation holds
     * @throws SenseMatcherException SenseMatcherException
     */
    boolean isSourceSynonymTarget(ISense source, ISense target) throws SenseMatcherException;

    /**
     * Checks whether the source is disjoint with the target.
     *
     * @param source source sense
     * @param target target sense
     * @return whether relation holds
     * @throws SenseMatcherException SenseMatcherException
     */
    boolean isSourceOppositeToTarget(ISense source, ISense target) throws SenseMatcherException;
}
