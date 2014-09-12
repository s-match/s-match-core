package it.unitn.disi.smatch.loaders.context;

import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;

import java.util.Iterator;

/**
 * Version with an iterator.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class TabContextLoaderIt extends TabContextLoader {

    @Override
    protected void createIds(IContext result) {
        int nodesParsed = 0;
        for (Iterator<INode> i = result.getNodes(); i.hasNext();) {
            i.next().getNodeData().setId("n" + Integer.toString(nodesParsed));
            nodesParsed++;
        }
    }

}
