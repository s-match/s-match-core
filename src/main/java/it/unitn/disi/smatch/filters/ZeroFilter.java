package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * Does nothing.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ZeroFilter extends AsyncTask<IContextMapping<INode>, IMappingElement<INode>> implements IMappingFilter, IAsyncMappingFilter {

    private final IContextMapping<INode> mapping;

    public ZeroFilter() {
        this.mapping = null;
    }

    public ZeroFilter(IContextMapping<INode> mapping) {
        this.mapping = mapping;
    }

    public IContextMapping<INode> filter(IContextMapping<INode> mapping) throws MappingFilterException {
        return mapping;
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncFilter(IContextMapping<INode> mapping) {
        return new ZeroFilter(mapping);
    }

    @Override
    protected IContextMapping<INode> doInBackground() throws Exception {
        return mapping;
    }
}
