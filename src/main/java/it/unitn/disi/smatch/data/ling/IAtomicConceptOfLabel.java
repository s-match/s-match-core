package it.unitn.disi.smatch.data.ling;

import it.unitn.disi.smatch.data.IIndexedObject;

import java.util.Iterator;
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
     * Returns the sense at index index.
     *
     * @param index index
     * @return sense at index index
     */
    ISense getSenseAt(int index);

    /**
     * Returns the number of senses.
     *
     * @return the number of senses
     */
    int getSenseCount();

    /**
     * Returns the index of sense in the receivers senses. If the receiver does not contain sense, -1 will be
     * returned.
     *
     * @param sense a sense to search for
     * @return the index of sense in the receivers senses
     */
    int getSenseIndex(ISense sense);

    /**
     * Returns the iterator over the senses of the receiver.
     *
     * @return the iterator over the senses of the receiver
     */
    Iterator<ISense> getSenses();

    /**
     * Returns unmodifiable list of senses of the receiver.
     *
     * @return unmodifiable list of senses of the receiver
     */
    List<ISense> getSenseList();

    /**
     * Adds a sense to the acol as the last sense.
     *
     * @param sense sense to add
     */
    void addSense(ISense sense);

    /**
     * Adds a sense to the receiver senses at the index.
     *
     * @param index index where the sense will be added
     * @param sense sense to add
     */
    void addSense(int index, ISense sense);

    /**
     * Removes the sense at the index from the receiver senses.
     *
     * @param index index of a sense to remove
     */
    void removeSense(int index);

    /**
     * Removes the sense from the receiver senses.
     *
     * @param sense sense to remove
     */
    void removeSense(ISense sense);
}