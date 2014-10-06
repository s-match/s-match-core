package it.unitn.disi.smatch.data.mappings;

import it.unitn.disi.smatch.data.trees.IContext;

import java.util.AbstractSet;

/**
 * Base mapping class.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseMapping<T> extends AbstractSet<IMappingElement<T>> implements IContextMapping<T> {

    protected double similarity;
    protected final IContext sourceContext;
    protected final IContext targetContext;

    protected BaseMapping() {
        this.sourceContext = null;
        this.targetContext = null;
    }

    protected BaseMapping(final IContext sourceContext, final IContext targetContext) {
        this.sourceContext = sourceContext;
        this.targetContext = targetContext;
    }

    @Override
    public double getSimilarity() {
        return similarity;
    }

    @Override
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    @Override
    public IContext getSourceContext() {
        return sourceContext;
    }

    @Override
    public IContext getTargetContext() {
        return targetContext;
    }
}
