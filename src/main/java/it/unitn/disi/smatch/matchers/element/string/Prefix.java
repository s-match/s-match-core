package it.unitn.disi.smatch.matchers.element.string;

import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.matchers.element.IStringBasedElementLevelSemanticMatcher;

/**
 * Implements Prefix matcher.
 * See Element Level Semantic matchers paper for more details.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class Prefix implements IStringBasedElementLevelSemanticMatcher {

    /**
     * Computes the relation with prefix matcher.
     *
     * @param str1 the source string
     * @param str2 the target string
     * @return synonym, more general, less general or IDK relation
     */
    public char match(String str1, String str2) {
        char rel = IMappingElement.IDK;

        if (str1 == null || str2 == null) {
            rel = IMappingElement.IDK;
        } else {
            if ((str1.length() > 3) && (str2.length() > 3)) {
                if (str1.startsWith(str2)) {
                    if (str1.contains(" ")) {
                        rel = IMappingElement.LESS_GENERAL;
                    } else {
                        rel = IMappingElement.EQUIVALENCE;
                    }
                } else {
                    if (str2.startsWith(str1)) {
                        if (str2.contains(" ")) {
                            rel = IMappingElement.MORE_GENERAL;
                        } else {
                            rel = IMappingElement.EQUIVALENCE;
                        }
                    }
                }
            }//if ((str1.length() > 3) && (str2.length() > 3)) {
        }
        return rel;
    }
}