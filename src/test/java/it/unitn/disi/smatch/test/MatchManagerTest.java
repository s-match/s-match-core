package it.unitn.disi.smatch.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.disi.smatch.IMatchManager;
import it.unitn.disi.smatch.MatchManager;
import it.unitn.disi.smatch.SMatchException;
import it.unitn.disi.smatch.classifiers.BaseContextClassifier;
import it.unitn.disi.smatch.classifiers.ContextClassifierException;
import it.unitn.disi.smatch.classifiers.ZeroContextClassifier;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.oracles.ZeroLinguisticOracle;
import it.unitn.disi.smatch.preprocessors.DefaultContextPreprocessor;

/**
 * @since 2.0.0
 * @author <a rel="author" href="http://davidleoni.it/">David Leoni</a>
 *
 */
public class MatchManagerTest {
    
    private static final Logger log = LoggerFactory.getLogger(MatchManagerTest.class);      

    /* keep here for reference 
    IMatchManager mm = new MatchManager(
          null, //contextLoader,   
          null, // contextRenderer,
          null, // mappingLoader,
          null, // mappingRenderer,
          null, // mappingFilter,
          null, // mappingFactory,
          null, // contextPreprocessor,
          null, // contextClassifier,
          null, // elementMatcher,
          null); // treeMatcher) {
     */
    
    
    /**
     * @since 2.0.0
     * @author <a rel="author" href="http://davidleoni.it/">David Leoni</a>
     *
     */
    // TODO IMPROVE IT!
    @Test
    public void testMatchManager() throws SMatchException {
        log.info("Starting example...");
        log.info("Creating MatchManager...");
              
        
        ZeroLinguisticOracle oracle = new ZeroLinguisticOracle();
                      
        IMatchManager mm = new MatchManager(null, //contextLoader,                
                null, // contextRenderer
                null, // mappingLoader
                null, // mappingRenderer
                null, // mappingFilter
                null, // mappingFactory
                new DefaultContextPreprocessor(
                        oracle, // senseMatcher
                        oracle), // linguisticOracle)
                new ZeroContextClassifier(), // contextClassifier
                null, // elementMatcher
                null); // treeMatcher)
        
        String example = "Courses";        
        IContext s = mm.createContext();
        s.createRoot(example);
        
        IContext t = mm.createContext();
        INode root = t.createRoot("Course");
        INode node = root.createChild("College of Arts and Sciences");
        node.createChild("English");
        
        node = root.createChild("College Engineering");
        node.createChild("Civil and Environmental Engineering");
                
        mm.offline(s);
                
        mm.offline(t);
    }
}
