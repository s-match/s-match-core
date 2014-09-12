package it.unitn.disi.smatch.matchers.structure.tree;

import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.matchers.structure.node.INodeMatcher;

/**
 * Base class for tree matchers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseTreeMatcher {

    protected final INodeMatcher nodeMatcher;
    protected final IMappingFactory mappingFactory;

    protected BaseTreeMatcher(INodeMatcher nodeMatcher, IMappingFactory mappingFactory) {
        this.nodeMatcher = nodeMatcher;
        this.mappingFactory = mappingFactory;
    }
}