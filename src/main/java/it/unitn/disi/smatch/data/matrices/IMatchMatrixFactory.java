package it.unitn.disi.smatch.data.matrices;

import it.unitn.disi.common.components.IConfigurable;

/**
 * Produces matching matrices.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMatchMatrixFactory extends IConfigurable {

    IMatchMatrix getInstance();
    
}
