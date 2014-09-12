package it.unitn.disi.smatch.matchers.element;

import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.oracles.ILinguisticOracle;
import it.unitn.disi.smatch.oracles.ISenseMatcher;
import it.unitn.disi.smatch.oracles.SenseMatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Performs all element level matching routines and provides the library of element level matchers. Needs the
 * following configuration parameters:
 * <p/>
 * senseMatcher - an instance of ISenseMatcher
 * <p/>
 * linguisticOracle - an instance of ILinguisticOracle
 * <p/>
 * Accepts the following configuration parameters:
 * <p/>
 * useWeakSemanticsElementLevelMatchersLibrary - exploit only WordNet (false) or use other element level semantic
 * matchers like string and gloss based matchers (true)
 * <p/>
 * stringMatchers - a ; separated list of class names implementing IStringBasedElementLevelSemanticMatcher interface
 * <p/>
 * senseGlossMatchers - a ; separated list of class names implementing ISenseGlossBasedElementLevelSemanticMatcher
 * interface
 * <p/>
 * mappingFactory - an instance of IMappingFactory
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ElementMatcher implements IElementMatcher {

    private static final Logger log = LoggerFactory.getLogger(ElementMatcher.class);

    protected final IMappingFactory mappingFactory;
    private final ISenseMatcher senseMatcher;
    private final ILinguisticOracle linguisticOracle;

    // exploit only WordNet (false) or use element level semantic matchers (true)
    private final boolean useWeakSemanticsElementLevelMatchersLibrary;

    // contains the classes of string matchers (Implementations of IStringBasedElementLevelSemanticMatcher interface)
    private final List<IStringBasedElementLevelSemanticMatcher> stringMatchers;

    // contains the classes of sense and gloss based matchers (Implementations of ISenseGlossBasedElementLevelSemanticMatcher interface)
    private final List<ISenseGlossBasedElementLevelSemanticMatcher> senseGlossMatchers;

    public ElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle) {
        this.mappingFactory = mappingFactory;
        this.senseMatcher = senseMatcher;
        this.linguisticOracle = linguisticOracle;

        useWeakSemanticsElementLevelMatchersLibrary = true;
        stringMatchers = Collections.emptyList();
        senseGlossMatchers = Collections.emptyList();
    }

    public ElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle,
                          boolean useWeakSemanticsElementLevelMatchersLibrary) {
        this.mappingFactory = mappingFactory;
        this.senseMatcher = senseMatcher;
        this.linguisticOracle = linguisticOracle;
        this.useWeakSemanticsElementLevelMatchersLibrary = useWeakSemanticsElementLevelMatchersLibrary;

        stringMatchers = Collections.emptyList();
        senseGlossMatchers = Collections.emptyList();
    }

    public ElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle,
                          boolean useWeakSemanticsElementLevelMatchersLibrary,
                          List<IStringBasedElementLevelSemanticMatcher> stringMatchers,
                          List<ISenseGlossBasedElementLevelSemanticMatcher> senseGlossMatchers) {
        this.mappingFactory = mappingFactory;
        this.senseMatcher = senseMatcher;
        this.linguisticOracle = linguisticOracle;
        this.useWeakSemanticsElementLevelMatchersLibrary = useWeakSemanticsElementLevelMatchersLibrary;
        if (null == stringMatchers) {
            this.stringMatchers = Collections.emptyList();
        } else {
            this.stringMatchers = stringMatchers;
        }
        if (null == senseGlossMatchers) {
            this.senseGlossMatchers = Collections.emptyList();
        } else {
            this.senseGlossMatchers = senseGlossMatchers;
        }
    }

    public IContextMapping<IAtomicConceptOfLabel> elementLevelMatching(IContext sourceContext, IContext targetContext) throws MatcherLibraryException {
        // Calculates relations between all ACoLs in both contexts and produces a mapping between them.
        // Corresponds to Step 3 of the semantic matching algorithm.

        IContextMapping<IAtomicConceptOfLabel> result = mappingFactory.getACoLMappingInstance(sourceContext, targetContext);

        long counter = 0;
        long total = getACoLCount(sourceContext) * getACoLCount(targetContext);
        long reportInt = (total / 20) + 1;//i.e. report every 5%
        for (INode sourceNode : sourceContext.getNodesList()) {
            for (IAtomicConceptOfLabel sourceACoL : sourceNode.getNodeData().getACoLsList()) {
                for (INode targetNode : targetContext.getNodesList()) {
                    for (IAtomicConceptOfLabel targetACoL : targetNode.getNodeData().getACoLsList()) {
                        //Use Element level semantic matchers library
                        //to check the relation holding between two ACoLs represented by lists of WN senses and tokens
                        final char relation = getRelation(sourceACoL, targetACoL);
                        result.setRelation(sourceACoL, targetACoL, relation);

                        counter++;
                        if ((SMatchConstants.LARGE_TASK < total) && (0 == (counter % reportInt)) && log.isInfoEnabled()) {
                            log.info(100 * counter / total + "%");
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns a semantic relation between two atomic concepts.
     *
     * @param sourceACoL source concept
     * @param targetACoL target concept
     * @return relation between concepts
     * @throws MatcherLibraryException MatcherLibraryException
     */
    protected char getRelation(IAtomicConceptOfLabel sourceACoL, IAtomicConceptOfLabel targetACoL) throws MatcherLibraryException {
        try {
            char relation = senseMatcher.getRelation(sourceACoL.getSenseList(), targetACoL.getSenseList());

            //if WN matcher did not find relation
            if (IMappingElement.IDK == relation) {
                if (useWeakSemanticsElementLevelMatchersLibrary) {
                    //use string based matchers
                    relation = getRelationFromStringMatchers(sourceACoL.getLemma(), targetACoL.getLemma());
                    //if they did not find relation
                    if (IMappingElement.IDK == relation) {
                        //use sense and gloss based matchers
                        relation = getRelationFromSenseGlossMatchers(sourceACoL.getSenseList(), targetACoL.getSenseList());
                    }
                }
            }

            return relation;
        } catch (SenseMatcherException e) {
            throw new MatcherLibraryException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Returns semantic relation holding between two labels as computed by string based matchers.
     *
     * @param sourceLabel the string of the source label
     * @param targetLabel the string of the target label
     * @return semantic relation holding between two labels as computed by string based matchers
     */
    private char getRelationFromStringMatchers(String sourceLabel, String targetLabel) {
        char relation = IMappingElement.IDK;
        int i = 0;
        while ((relation == IMappingElement.IDK) && (i < stringMatchers.size())) {
            relation = stringMatchers.get(i).match(sourceLabel, targetLabel);
            i++;
        }
        return relation;
    }

    /**
     * Returns semantic relation between two sets of senses by WordNet sense-based matchers.
     *
     * @param sourceSenses source senses
     * @param targetSenses target senses
     * @return semantic relation between two sets of senses
     * @throws MatcherLibraryException MatcherLibraryException
     */
    private char getRelationFromSenseGlossMatchers(List<ISense> sourceSenses, List<ISense> targetSenses) throws MatcherLibraryException {
        char relation = IMappingElement.IDK;
        if (0 < senseGlossMatchers.size()) {
            for (ISense sourceSense : sourceSenses) {
                //noinspection LoopStatementThatDoesntLoop
                for (ISense targetSense : targetSenses) {
                    int k = 0;
                    while ((relation == IMappingElement.IDK) && (k < senseGlossMatchers.size())) {
                        relation = senseGlossMatchers.get(k).match(sourceSense, targetSense);
                        k++;
                    }
                    return relation;
                }
            }
        }
        return relation;
    }

    protected long getACoLCount(IContext context) {
        long result = 0;
        for (INode node : context.getNodesList()) {
            result = result + node.getNodeData().getACoLCount();
        }
        return result;
    }

}