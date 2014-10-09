package it.unitn.disi.smatch.data.mappings;

import it.unitn.disi.smatch.data.IIndexedObject;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.matrices.IMatchMatrixFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

import java.util.Iterator;

/**
 * Mapping between acols based on a matrix.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ConceptMatrixMapping extends MatrixMapping<IAtomicConceptOfLabel> {

    public ConceptMatrixMapping(IMatchMatrixFactory factory, IContext source, IContext target) {
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

    @Override
    protected void initCols(IContext targetContext, IIndexedObject[] targets) {
        initNodes(targetContext, targets);
    }

    @Override
    protected void initRows(IContext sourceContext, IIndexedObject[] sources) {
        initNodes(sourceContext, sources);
    }

    private void initNodes(IContext c, IIndexedObject[] o) {
        for (Iterator<INode> i = c.nodeIterator(); i.hasNext(); ) {
            INode node = i.next();
            for (IAtomicConceptOfLabel acol : node.nodeData().getConcepts()) {
                o[acol.getIndex()] = acol;
            }
        }
    }

    private int indexContext(IContext c) {
        int result = 0;
        for (Iterator<INode> i = c.nodeIterator(); i.hasNext(); ) {
            INode node = i.next();
            for (IAtomicConceptOfLabel acol : node.nodeData().getConcepts()) {
                acol.setIndex(result);
                result++;
            }
        }
        return result;
    }
}
