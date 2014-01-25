package it.unitn.disi.smatch.data.trees;

/**
 * An interface for the context data structure. A context is basically a tree made of nodes with natural language
 * labels, organized into a hierarchy with mostly (assumed) subsumption and is-a relations between the nodes.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IContext extends IBaseContext<INode> {

}