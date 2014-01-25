package it.unitn.disi.smatch.matchers.element.gloss;

import it.unitn.disi.common.components.Configurable;
import it.unitn.disi.common.components.ConfigurableException;
import it.unitn.disi.common.components.ConfigurationKeyMissingException;
import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.matchers.element.MatcherLibraryException;
import it.unitn.disi.smatch.oracles.ILinguisticOracle;
import it.unitn.disi.smatch.oracles.ISenseMatcher;
import it.unitn.disi.smatch.oracles.LinguisticOracleException;
import it.unitn.disi.smatch.oracles.SenseMatcherException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Matches glosses of word senses. Needs the following configuration parameters:
 * <p/>
 * senseMatcher - an instance of ISenseMatcher
 * <p/>
 * linguisticOracle - an instance of ILinguisticOracle
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class BasicGlossMatcher extends Configurable {

    private static final String LINGUISTIC_ORACLE_KEY = "linguisticOracle";
    private ILinguisticOracle linguisticOracle = null;

    private static final String SENSE_MATCHER_KEY = "senseMatcher";
    private ISenseMatcher senseMatcher = null;

    @Override
    public boolean setProperties(Properties newProperties) throws ConfigurableException {
        Properties oldProperties = new Properties();
        oldProperties.putAll(properties);

        boolean result = super.setProperties(newProperties);
        if (result) {
            if (newProperties.containsKey(SENSE_MATCHER_KEY)) {
                senseMatcher = (ISenseMatcher) configureComponent(senseMatcher, oldProperties, newProperties, "sense matcher", SENSE_MATCHER_KEY, ISenseMatcher.class);
            } else {
                throw new ConfigurationKeyMissingException(SENSE_MATCHER_KEY);
            }

            if (newProperties.containsKey(LINGUISTIC_ORACLE_KEY)) {
                linguisticOracle = (ILinguisticOracle) configureComponent(linguisticOracle, oldProperties, newProperties, "linguistic oracle", LINGUISTIC_ORACLE_KEY, ILinguisticOracle.class);
            } else {
                throw new ConfigurationKeyMissingException(LINGUISTIC_ORACLE_KEY);
            }
        }
        return result;
    }

    //Next 4 method are used by element level matchers to calculate relations between words

    /**
     * Checks the source is more general than the target or not.
     *
     * @param source sense of source
     * @param target sense of target
     * @return true if the source is more general than target
     * @throws MatcherLibraryException MatcherLibraryException
     */
    public boolean isWordMoreGeneral(String source, String target) throws MatcherLibraryException {
        try {
            List<ISense> sSenses = linguisticOracle.getSenses(source);
            List<ISense> tSenses = linguisticOracle.getSenses(target);
            for (ISense sSense : sSenses) {
                for (ISense tSense : tSenses) {
                    if (senseMatcher.isSourceMoreGeneralThanTarget(sSense, tSense))
                        return true;
                }
            }
            return false;
        } catch (LinguisticOracleException e) {
            throw new MatcherLibraryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        } catch (SenseMatcherException e) {
            throw new MatcherLibraryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Checks the source is less general than the target or not.
     *
     * @param source sense of source
     * @param target sense of target
     * @return true if the source is less general than target
     * @throws MatcherLibraryException MatcherLibraryException
     */
    public boolean isWordLessGeneral(String source, String target) throws MatcherLibraryException {
        try {
            List<ISense> sSenses = linguisticOracle.getSenses(source);
            List<ISense> tSenses = linguisticOracle.getSenses(target);
            for (ISense sSense : sSenses) {
                for (ISense tSense : tSenses) {
                    if (senseMatcher.isSourceLessGeneralThanTarget(sSense, tSense))
                        return true;
                }
            }
            return false;
        } catch (LinguisticOracleException e) {
            throw new MatcherLibraryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        } catch (SenseMatcherException e) {
            throw new MatcherLibraryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Checks the source and target is synonym or not.
     *
     * @param source sense of source
     * @param target sense of target
     * @return true if they are synonym
     * @throws MatcherLibraryException MatcherLibraryException
     */
    public boolean isWordSynonym(String source, String target) throws MatcherLibraryException {
        try {
            List<ISense> sSenses = linguisticOracle.getSenses(source);
            List<ISense> tSenses = linguisticOracle.getSenses(target);
            for (ISense sSense : sSenses) {
                for (ISense tSense : tSenses) {
                    if (senseMatcher.isSourceSynonymTarget(sSense, tSense))
                        return true;
                }
            }
            return false;
        } catch (LinguisticOracleException e) {
            throw new MatcherLibraryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        } catch (SenseMatcherException e) {
            throw new MatcherLibraryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Checks the source and target is opposite or not.
     *
     * @param source sense of source
     * @param target sense of target
     * @return true if they are in opposite relation
     * @throws MatcherLibraryException MatcherLibraryException
     */
    public boolean isWordOpposite(String source, String target) throws MatcherLibraryException {
        try {
            List<ISense> sSenses = linguisticOracle.getSenses(source);
            List<ISense> tSenses = linguisticOracle.getSenses(target);
            for (ISense sSense : sSenses) {
                for (ISense tSense : tSenses) {
                    if (senseMatcher.isSourceOppositeToTarget(sSense, tSense))
                        return true;
                }
            }
            return false;
        } catch (LinguisticOracleException e) {
            throw new MatcherLibraryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        } catch (SenseMatcherException e) {
            throw new MatcherLibraryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Gets extended gloss i.e. the gloss of parents or children. <br>
     * The direction and depth is according to requirement.
     *
     * @param original  the original gloss of input string
     * @param intSource how much depth the gloss should be taken
     * @param Rel       for less than relation get child gloss and vice versa
     * @return the extended gloss
     * @throws LinguisticOracleException LinguisticOracleException
     */
    public String getExtendedGloss(ISense original, int intSource, char Rel) throws LinguisticOracleException {
        List<ISense> children = new ArrayList<ISense>();
        StringBuilder result = new StringBuilder();
        if (Rel == IMappingElement.LESS_GENERAL) {
            children = original.getChildren(intSource);
        } else if (Rel == IMappingElement.MORE_GENERAL) {
            children = original.getParents(intSource);
        }
        for (ISense ISense : children) {
            String gloss = ISense.getGloss();
            result.append(gloss).append(".");
        }
        return result.toString();
    }
}