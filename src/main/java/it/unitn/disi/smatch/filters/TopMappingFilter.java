package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * Removes links having a root node on the one or the other side from the mapping.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class TopMappingFilter extends BaseFilter implements IMappingFilter, IAsyncMappingFilter {

    public TopMappingFilter(IMappingFactory mappingFactory) {
        super(mappingFactory);
    }

    public TopMappingFilter(IMappingFactory mappingFactory, IContextMapping<INode> mapping) {
        super(mappingFactory, mapping);
    }

    protected IContextMapping<INode> process(IContextMapping<INode> mapping) {
        IContextMapping<INode> result = mappingFactory.getContextMappingInstance(mapping.getSourceContext(), mapping.getTargetContext());

        //sampling
        for (IMappingElement<INode> e : mapping) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            if (e.getSource().hasParent() && e.getTarget().hasParent()) {
                result.add(e);
            }

            progress();
        }

        return result;
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncFilter(IContextMapping<INode> mapping) {
        return new TopMappingFilter(mappingFactory, mapping);
    }
}