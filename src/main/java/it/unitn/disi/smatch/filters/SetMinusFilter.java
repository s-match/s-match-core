package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * Removes from the mapping passed as a parameter the mapping specified in the configuration. Needs the
 * following configuration parameters:
 * <p/>
 * mappingLoader - an instance of IMappingLoader
 * <p/>
 * mapping - location of the mapping
 * <p/>
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class SetMinusFilter extends BaseMappingBasedFilter {

    public IContextMapping<INode> filter(IContextMapping<INode> mapping) throws MappingFilterException {
        super.filter(mapping);

        mapping.removeAll(filterMapping);

        return mapping;
    }

}
