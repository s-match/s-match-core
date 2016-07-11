package it.unitn.disi.smatch.data.ling;

import it.unitn.disi.smatch.oracles.LinguisticOracleException;

import java.util.List;

/**
 * Interface for a dictionary sense.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface ISense {

    /**
     * Returns sense id. The format of the sense id is up to the implementation of the linguistic oracle.
     *
     * @return sense id
     */
    String getId();

    /**
     * Returns a sense gloss, that is a textual description of the meaning.
     *
     * @return a gloss
     */
    String getGloss();

    /**
     * Get lemmas of this sense.
     *
     * @return lemmas
     */
    List<String> getLemmas();

    /**
     * Returns "parents", that is hypernyms of the sense.
     *
     * @return hypernyms of the sense
     * @throws LinguisticOracleException LinguisticOracleException
     */
    List<ISense> getParents() throws LinguisticOracleException;

    /**
     * Returns "parents", that is hypernyms of the sense, up to certain depth.
     *
     * @param depth a search depth
     * @return "parents"
     * @throws LinguisticOracleException LinguisticOracleException
     */
    List<ISense> getParents(int depth) throws LinguisticOracleException;

    /**
     * Returns "children", that is hyponyms of the sense.
     *
     * @return "children"
     * @throws LinguisticOracleException LinguisticOracleException
     */
    List<ISense> getChildren() throws LinguisticOracleException;

    /**
     * Returns "children", that is hyponyms of the sense, down to certain depth.
     *
     * @param depth a search depth
     * @return "children"
     * @throws LinguisticOracleException LinguisticOracleException
     */
    List<ISense> getChildren(int depth) throws LinguisticOracleException;
}