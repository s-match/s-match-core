package it.unitn.disi.smatch.data.ling;

/**
 * A {@link Sense} that is not connected to any other sense and always returns default values.
 *
 * @since 2.0.0
 * @author <a rel="author" href="http://davidleoni.it/">David Leoni</a> 
 */
public class DummySense extends Sense {

    private static final long serialVersionUID = 1L;

    public DummySense(String id) {
        super(id);
    }       

}
