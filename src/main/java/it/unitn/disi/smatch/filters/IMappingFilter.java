package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * Interface for mapping filters.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMappingFilter {

    /**
     * Filters the mapping.
     *
     * @param mapping source mapping
     * @return filtered mapping
     * @throws MappingFilterException MappingFilterException
     */
    IContextMapping<INode> filter(IContextMapping<INode> mapping) throws MappingFilterException;
}