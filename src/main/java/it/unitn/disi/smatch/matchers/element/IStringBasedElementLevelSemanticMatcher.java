package it.unitn.disi.smatch.matchers.element;

/**
 * Interface for string-based element-level matchers.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IStringBasedElementLevelSemanticMatcher {

    /**
     * Returns a relation between source and target strings.
     *
     * @param source the string of source label
     * @param target the string of target label
     * @return a relation between source and target labels
     */
    char match(String source, String target);
}
