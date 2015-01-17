package it.unitn.disi.smatch.data.ling;

import it.unitn.disi.smatch.data.IndexedObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents atomic concept of label (ACoL) as a
 * concept label and list of associated senses.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class AtomicConceptOfLabel extends IndexedObject implements IAtomicConceptOfLabel, Serializable {

    // average polysemy in WordNet
    private final static int AVERAGE_POLYSEMY = 2;

    private int id;
    private String token;
    private String lemma;
    private List<ISense> senses = new ArrayList<>(AVERAGE_POLYSEMY);

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public List<ISense> getSenses() {
        return senses;
    }

    @Override
    public void setSenses(List<ISense> senses) {
        this.senses = senses;
    }

    public String toString() {
        return token;
    }
}