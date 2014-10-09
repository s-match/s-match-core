package it.unitn.disi.smatch.data.ling;

import it.unitn.disi.smatch.data.IIndexedObject;

import java.util.List;

/**
 * An interface for atomic concept of label. Atomic concept of label roughly corresponds to the natural language token.
 * In most cases the correspondence is one-to-one, but multiwords can be represented as a single atomic concept.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IAtomicConceptOfLabel extends IIndexedObject {

    /**
     * Returns the token(s) corresponding to the atomic concept.
     *
     * @return the token(s) corresponding to the atomic concept
     */
    String getToken();

    /**
     * Sets the token(s) corresponding to the atomic concept.
     *
     * @param token token(s) corresponding to the atomic concept.
     */
    void setToken(String token);

    /**
     * Returns lemmatized version of the token(s).
     *
     * @return lemmatized version of the token(s)
     */
    String getLemma();

    /**
     * Sets lemmatized version of the token(s).
     *
     * @param lemma lemmatized version of the token(s)
     */
    void setLemma(String lemma);

    /**
     * Returns token identifier in the label. In most cases equals to token index.
     *
     * @return token identifier in the label
     */
    int getId();

    /**
     * Sets token identifier in the label. In most cases equals to token index.
     *
     * @param id token identifier in the label
     */
    void setId(int id);

    /**
     * Returns a list of senses.
     *
     * @return list of senses
     */
    List<ISense> getSenses();

    /**
     * Sets a list of senses.
     *
     * @param senses a list of senses to set
     */
    void setSenses(List<ISense> senses);
}