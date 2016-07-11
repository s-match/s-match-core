package it.unitn.disi.smatch.test.preprocessors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.disi.smatch.SMatchException;
import it.unitn.disi.smatch.data.trees.Context;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.oracles.DummyLinguisticOracle;
import it.unitn.disi.smatch.preprocessors.DefaultContextPreprocessor;

/**
 * @since 2.0.0
 * @author <a rel="author" href="http://davidleoni.it/">David Leoni</a>
 *
 */
public class DefaultContextPreprocessorTest {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultContextPreprocessorTest.class);      

    /**
     * Test for <a href="https://github.com/s-match/s-match-core/issues/4">Issue #4 </a>
     * 
     * @since 2.0.0
     * @author <a rel="author" href="http://davidleoni.it/">David Leoni</a>
     *
     */
    @Test
    public void testProgress() throws SMatchException {
              
        DummyLinguisticOracle oracle = new DummyLinguisticOracle();
                      
        DefaultContextPreprocessor preprocessor = new DefaultContextPreprocessor(
                oracle, // senseMatcher
                oracle); // linguisticOracle
        
        String example = "Courses";        
        IContext s = new Context(); //mm.createContext();
        s.createRoot(example);
        
        IContext t = new Context();
        INode root = t.createRoot("Course");
        INode node = root.createChild("College of Arts and Sciences");
        node.createChild("English");
        
        node = root.createChild("College Engineering");
        node.createChild("Civil and Environmental Engineering");
           
        preprocessor.preprocess(s);
        preprocessor.preprocess(t);
        
    }
}
