package it.unitn.disi.smatch.oracles;

import java.util.Collections;
import java.util.List;
import it.unitn.disi.smatch.data.ling.DummySense;
import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.oracles.ILinguisticOracle;
import it.unitn.disi.smatch.oracles.ISenseMatcher;
import it.unitn.disi.smatch.oracles.LinguisticOracleException;
import it.unitn.disi.smatch.oracles.SenseMatcherException;

/**
 * An inept linguistic oracle that never finds relations.
 *
 * @since 2.0.0
 * @author <a rel="author" href="http://davidleoni.it/">David Leoni</a>
 */
public class DummyLinguisticOracle implements ILinguisticOracle, ISenseMatcher {      

    public DummyLinguisticOracle(){                
    }
    
    @Override
    public boolean isEqual(String str1, String str2) throws LinguisticOracleException {
        return false;
    }

    @Override
    public List<ISense> getSenses(String word) throws LinguisticOracleException {
        return Collections.emptyList();
    }

    @Override
    public List<String> getBaseForms(String derivation) throws LinguisticOracleException {
        return Collections.emptyList();
    }

    @Override
    public ISense createSense(String id) throws LinguisticOracleException {
        return new DummySense(id);
    }

    @Override
    public List<List<String>> getMultiwords(String beginning) throws LinguisticOracleException {
        return Collections.emptyList();
    }

    
    /**
     * Always return {@link #IMappingElement#IDK}
     */
    @Override
    public char getRelation(List<ISense> sourceSenses, List<ISense> targetSenses) throws SenseMatcherException {
        return IMappingElement.IDK;
    }
    
    @Override
    public boolean isSourceMoreGeneralThanTarget(ISense source, ISense target) throws SenseMatcherException {
        return false;
    }

    @Override
    public boolean isSourceLessGeneralThanTarget(ISense source, ISense target) throws SenseMatcherException {
        return false;
    }
    
    @Override
    public boolean isSourceSynonymTarget(ISense source, ISense target) throws SenseMatcherException {
        return false;
    }

    @Override
    public boolean isSourceOppositeToTarget(ISense source, ISense target) throws SenseMatcherException {
        return false;
    }
}
