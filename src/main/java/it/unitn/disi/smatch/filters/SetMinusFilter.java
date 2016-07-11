package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.mapping.IMappingLoader;

/**
 * Removes from the mapping passed as a parameter the mapping specified in the configuration.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class SetMinusFilter extends BaseMappingBasedFilter implements IAsyncMappingFilter {

    public SetMinusFilter(IMappingFactory mappingFactory, IMappingLoader mappingLoader, String mappingLocation) {
        super(mappingFactory, mappingLoader, mappingLocation);
    }

    public SetMinusFilter(IMappingFactory mappingFactory, IMappingLoader mappingLoader, String mappingLocation, IContextMapping<INode> mapping) {
        super(mappingFactory, mappingLoader, mappingLocation, mapping);
    }

    @Override
    protected IContextMapping<INode> process(IContextMapping<INode> mapping) throws MappingFilterException {
        mapping.removeAll(loadMapping(mapping));
        return mapping;
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncFilter(IContextMapping<INode> mapping) {
        return new SetMinusFilter(mappingFactory, mappingLoader, mappingLocation, mapping);
    }
}