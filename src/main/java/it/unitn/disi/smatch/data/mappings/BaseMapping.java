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
    protected IContext sourceContext;
    protected IContext targetContext;

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public IContext getSourceContext() {
        return sourceContext;
    }

    public IContext getTargetContext() {
        return targetContext;
    }
}
