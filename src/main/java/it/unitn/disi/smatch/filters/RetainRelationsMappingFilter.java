package it.unitn.disi.smatch.filters;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.INode;

/**
 * Retains only specified kind of links in the mapping.
 * For relation kinds see constants in {@link it.unitn.disi.smatch.data.mappings.IMappingElement}.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class RetainRelationsMappingFilter extends BaseFilter implements IMappingFilter, IAsyncMappingFilter {

    private final String retainRelations;

    protected RetainRelationsMappingFilter(IMappingFactory mappingFactory, String retainRelations) {
        super(mappingFactory);
        this.retainRelations = retainRelations;
    }

    public RetainRelationsMappingFilter(IMappingFactory mappingFactory, IContextMapping<INode> mapping, String retainRelations) {
        super(mappingFactory, mapping);
        this.retainRelations = retainRelations;
    }

    @Override
    protected IContextMapping<INode> process(IContextMapping<INode> mapping) {
        IContextMapping<INode> result = mappingFactory.getContextMappingInstance(mapping.getSourceContext(), mapping.getTargetContext());

        //check each mapping
        for (IMappingElement<INode> e : mapping) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            if (-1 < retainRelations.indexOf(e.getRelation())) {
                result.add(e);
            }

            progress();
        }

        return result;
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncFilter(IContextMapping<INode> mapping) {
        return new RetainRelationsMappingFilter(mappingFactory, mapping, retainRelations);
    }
}
