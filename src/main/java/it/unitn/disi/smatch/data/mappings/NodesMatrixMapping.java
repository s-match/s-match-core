package it.unitn.disi.smatch.data.mappings;

import it.unitn.disi.smatch.data.IIndexedObject;
import it.unitn.disi.smatch.data.matrices.IMatchMatrixFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

import java.util.Iterator;

/**
 * Mapping between context nodes based on a matrix.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class NodesMatrixMapping extends MatrixMapping<INode> {

    public NodesMatrixMapping(IMatchMatrixFactory factory, IContext source, IContext target) {
        super(factory, source, target);
    }

    @Override
    protected int indexSource(IContext c) {
        return indexContext(c);
    }

    @Override
    protected int indexTarget(IContext c) {
        return indexContext(c);
    }

    /**
     * Indexes and counts nodes in the context
     *
     * @param c context
     * @return node count
     */
    private int indexContext(IContext c) {
        int result = 0;
        for (Iterator<INode> i = c.getNodes(); i.hasNext(); ) {
            INode node = i.next();
            node.setIndex(result);
            result++;
        }
        return result;
    }

    @Override
    protected void initCols(IContext targetContext, IIndexedObject[] targets) {
        initNodes(targetContext, targets);
    }

    @Override
    protected void initRows(IContext sourceContext, IIndexedObject[] sources) {
        initNodes(sourceContext, sources);
    }

    private void initNodes(IContext c, IIndexedObject[] o) {
        for (Iterator<INode> i = c.getNodes(); i.hasNext();) {
            INode node = i.next();
            o[node.getIndex()] = node;
        }
    }
}
