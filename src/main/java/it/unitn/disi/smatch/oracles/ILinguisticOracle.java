package it.unitn.disi.smatch.oracles;

import it.unitn.disi.common.components.IConfigurable;
import it.unitn.disi.smatch.data.ling.ISense;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface to Linguistic Oracles, such as WordNet.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface ILinguisticOracle extends IConfigurable {

    /**
     * Checks if lemmas of two strings are equal (e. g. the string are the same modulo inflections).
     *
     * @param str1 source string
     * @param str2 target string
     * @return true if lemmas are equal
     * @throws LinguisticOracleException LinguisticOracleException
     */
    boolean isEqual(String str1, String str2) throws LinguisticOracleException;

    /**
     * Returns all senses of a word.
     *
     * @param word the word to which the sense will be retrieve
     * @return word senses
     * @throws LinguisticOracleException LinguisticOracleException
     */
    List<ISense> getSenses(String word) throws LinguisticOracleException;

    /**
     * Returns base forms (lemmas) of a word.
     *
     * @param derivation the word to get a base form for
     * @return base forms of a derivation
     * @throws LinguisticOracleException LinguisticOracleException
     */
    List<String> getBaseForms(String derivation) throws LinguisticOracleException;

    /**
     * Creates an instance of a sense.
     *
     * @param id sense id.
     * @return an instance of a senses.
     * @throws LinguisticOracleException LinguisticOracleException
     */
    ISense createSense(String id) throws LinguisticOracleException;

    /**
     * Returns list of possible multiword endings.
     * @param beginning first word of a multiword
     * @return list of possible multiword endings
     * @throws LinguisticOracleException LinguisticOracleException
     */
    ArrayList<ArrayList<String>> getMultiwords(String beginning) throws LinguisticOracleException;
}