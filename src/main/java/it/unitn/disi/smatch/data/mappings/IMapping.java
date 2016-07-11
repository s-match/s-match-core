package it.unitn.disi.smatch.data.mappings;

import java.util.Set;

/**
 * Interface for mappings.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMapping<T> extends Set<IMappingElement<T>> {

    /**
     * Returns the relation between the source and the target.
     *
     * @param source source
     * @param target target
     * @return relation between source and target
     */
    char getRelation(T source, T target);

    /**
     * Sets the relation between the source and the target.
     *
     * @param source   source
     * @param target   target
     * @param relation relation
     * @return true if the mapping was modified
     */
    boolean setRelation(T source, T target, char relation);

    /**
     * Sets the similarity between two trees.
     *
     * @param similarity the similarity between two trees
     */
    void setSimilarity(double similarity);

    /**
     * Returns the similarity between two trees.
     *
     * @return the similarity between two trees
     */
    double getSimilarity();

    /**
     * Returns mapping elements with source element equal to <code>source</code>.
     *
     * @param source source element
     * @return mapping elements with source element equal to <code>source</code>
     */
    Set<IMappingElement<T>> getSources(T source);

    /**
     * Returns mapping elements with target element equal to <code>target</code>.
     *
     * @param target target element
     * @return mapping elements with target element equal to <code>target</code>
     */
    Set<IMappingElement<T>> getTargets(T target);
}
