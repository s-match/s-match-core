package it.unitn.disi.smatch.data.ling;

import it.unitn.disi.smatch.oracles.LinguisticOracleException;

import java.util.Collections;
import java.util.List;

/**
 * Default sense implementation.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class Sense implements ISense {

    String id;

    public Sense(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getGloss() {
        return null;
    }

    public List<String> getLemmas() {
        return Collections.emptyList();
    }

    public List<ISense> getParents() throws LinguisticOracleException {
        return Collections.emptyList();
    }

    public List<ISense> getParents(int depth) throws LinguisticOracleException {
        return Collections.emptyList();
    }

    public List<ISense> getChildren() throws LinguisticOracleException {
        return Collections.emptyList();
    }

    public List<ISense> getChildren(int depth) throws LinguisticOracleException {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sense)) {
            return false;
        }

        Sense sense = (Sense) o;

        if (id != null ? !id.equals(sense.id) : sense.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
