package it.unitn.disi.smatch.matchers.element;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.oracles.ISenseMatcher;
import it.unitn.disi.smatch.oracles.SenseMatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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
public class ElementMatcher extends AsyncTask<IContextMapping<IAtomicConceptOfLabel>, IMappingElement<IAtomicConceptOfLabel>>
        implements IAsyncElementMatcher {

    private static final Logger log = LoggerFactory.getLogger(ElementMatcher.class);

    protected final IMappingFactory mappingFactory;
    protected final ISenseMatcher senseMatcher;

    // exploit only WordNet (false) or use element level semantic matchers (true)
    protected final boolean useWeakSemanticsElementLevelMatchersLibrary;

    // contains the classes of string matchers (Implementations of IStringBasedElementLevelSemanticMatcher interface)
    protected final List<IStringBasedElementLevelSemanticMatcher> stringMatchers;

    // contains the classes of sense and gloss based matchers (Implementations of ISenseGlossBasedElementLevelSemanticMatcher interface)
    protected final List<ISenseGlossBasedElementLevelSemanticMatcher> senseGlossMatchers;

    // for task parameters
    protected final IContext sourceContext;
    protected final IContext targetContext;

    public ElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher) {
        this.mappingFactory = mappingFactory;
        this.senseMatcher = senseMatcher;

        useWeakSemanticsElementLevelMatchersLibrary = true;
        stringMatchers = Collections.emptyList();
        senseGlossMatchers = Collections.emptyList();

        this.sourceContext = null;
        this.targetContext = null;
    }

    public ElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher, IContext sourceContext, IContext targetContext) {
        this.mappingFactory = mappingFactory;
        this.senseMatcher = senseMatcher;

        useWeakSemanticsElementLevelMatchersLibrary = true;
        stringMatchers = Collections.emptyList();
        senseGlossMatchers = Collections.emptyList();

        this.sourceContext = sourceContext;
        this.targetContext = targetContext;

        // progress by node rather than by acol because task can be created on non-preprocessed contexts...
        setTotal((long) sourceContext.nodesCount() * (long) targetContext.nodesCount());
    }

    public ElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                          boolean useWeakSemanticsElementLevelMatchersLibrary) {
        this.mappingFactory = mappingFactory;
        this.senseMatcher = senseMatcher;
        this.useWeakSemanticsElementLevelMatchersLibrary = useWeakSemanticsElementLevelMatchersLibrary;

        stringMatchers = Collections.emptyList();
        senseGlossMatchers = Collections.emptyList();

        this.sourceContext = null;
        this.targetContext = null;
    }

    public ElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                          boolean useWeakSemanticsElementLevelMatchersLibrary,
                          IContext sourceContext, IContext targetContext) {
        this.mappingFactory = mappingFactory;
        this.senseMatcher = senseMatcher;
        this.useWeakSemanticsElementLevelMatchersLibrary = useWeakSemanticsElementLevelMatchersLibrary;

        stringMatchers = Collections.emptyList();
        senseGlossMatchers = Collections.emptyList();

        this.sourceContext = sourceContext;
        this.targetContext = targetContext;
        setTotal((long) sourceContext.nodesCount() * (long) targetContext.nodesCount());
    }

    public ElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                          boolean useWeakSemanticsElementLevelMatchersLibrary,
                          List<IStringBasedElementLevelSemanticMatcher> stringMatchers,
                          List<ISenseGlossBasedElementLevelSemanticMatcher> senseGlossMatchers) {
        this.mappingFactory = mappingFactory;
        this.senseMatcher = senseMatcher;
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
        this.sourceContext = null;
        this.targetContext = null;
    }

    public ElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                          boolean useWeakSemanticsElementLevelMatchersLibrary,
                          List<IStringBasedElementLevelSemanticMatcher> stringMatchers,
                          List<ISenseGlossBasedElementLevelSemanticMatcher> senseGlossMatchers,
                          IContext sourceContext, IContext targetContext) {
        this.mappingFactory = mappingFactory;
        this.senseMatcher = senseMatcher;
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
        this.sourceContext = sourceContext;
        this.targetContext = targetContext;
        setTotal((long) sourceContext.nodesCount() * (long) targetContext.nodesCount());
    }

    public IContextMapping<IAtomicConceptOfLabel> elementLevelMatching(IContext sourceContext, IContext targetContext) throws ElementMatcherException {
        setTotal((long) sourceContext.nodesCount() * (long) targetContext.nodesCount());
        setProgress(0);
        // Calculates relations between all ACoLs in both contexts and produces a mapping between them.
        // Corresponds to Step 3 of the semantic matching algorithm.

        final IContextMapping<IAtomicConceptOfLabel> result = mappingFactory.getConceptMappingInstance(sourceContext, targetContext);

        for (Iterator<INode> i = sourceContext.nodeIterator(); i.hasNext(); ) {
            final INode sourceNode = i.next();
            for (Iterator<INode> j = targetContext.nodeIterator(); j.hasNext(); ) {
                final INode targetNode = j.next();
                for (IAtomicConceptOfLabel sourceACoL : sourceNode.nodeData().getConcepts()) {
                    for (IAtomicConceptOfLabel targetACoL : targetNode.nodeData().getConcepts()) {
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }

                        //Use Element level semantic matchers library
                        //to check the relation holding between two ACoLs represented by lists of WN senses and tokens
                        final char relation = getRelation(sourceACoL, targetACoL);
                        result.setRelation(sourceACoL, targetACoL, relation);


                        if (log.isTraceEnabled()) {
                            if (IMappingElement.IDK != relation) {
                                log.trace(sourceNode.nodeData().getId() +
                                                ".[" + sourceNode.nodeData().getName() + "]." +
                                                sourceACoL.getId() + "." + sourceACoL.getToken() +
                                                "\t" + relation + "\t" +
                                                targetNode.nodeData().getId() +
                                                ".[" + targetNode.nodeData().getName() + "]." +
                                                targetACoL.getId() + "." + targetACoL.getToken()
                                );
                            }
                        }
                    }
                }

                // progress by node rather than by acol because task can be created on non-preprocessed contexts...
                progress();
            }
        }

        return result;
    }

    @Override
    protected IContextMapping<IAtomicConceptOfLabel> doInBackground() throws Exception {
        final String threadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(Thread.currentThread().getName()
                    + " [" + this.getClass().getSimpleName()
                    + ": source.size=" + sourceContext.nodesCount()
                    + ", target.size=" + targetContext.nodesCount() + "]");

            return elementLevelMatching(sourceContext, targetContext);
        } finally {
            Thread.currentThread().setName(threadName);
        }
    }

    @Override
    public AsyncTask<IContextMapping<IAtomicConceptOfLabel>, IMappingElement<IAtomicConceptOfLabel>> asyncElementLevelMatching(IContext sourceContext, IContext targetContext) {
        return new ElementMatcher(mappingFactory, senseMatcher, useWeakSemanticsElementLevelMatchersLibrary,
                stringMatchers, senseGlossMatchers, sourceContext, targetContext);
    }

    /**
     * Returns a semantic relation between two atomic concepts.
     *
     * @param sourceACoL source concept
     * @param targetACoL target concept
     * @return relation between concepts
     * @throws ElementMatcherException ElementMatcherException
     */
    protected char getRelation(IAtomicConceptOfLabel sourceACoL, IAtomicConceptOfLabel targetACoL) throws ElementMatcherException {
        try {
            char relation = senseMatcher.getRelation(sourceACoL.getSenses(), targetACoL.getSenses());

            //if WN matcher did not find relation
            if (IMappingElement.IDK == relation) {
                if (useWeakSemanticsElementLevelMatchersLibrary) {
                    //use string based matchers
                    relation = getRelationFromStringMatchers(sourceACoL.getLemma(), targetACoL.getLemma());
                    //if they did not find relation
                    if (IMappingElement.IDK == relation) {
                        //use sense and gloss based matchers
                        relation = getRelationFromSenseGlossMatchers(sourceACoL.getSenses(), targetACoL.getSenses());
                    }
                }
            }

            if (log.isTraceEnabled()) {
                if (IMappingElement.IDK != relation) {
                    log.trace(sourceACoL.getId() + "\t" + relation + "\t" + targetACoL.getId() +
                            "\t\t" + sourceACoL.getToken() + "\t" + relation + "\t" + targetACoL.getToken());
                }
            }

            return relation;
        } catch (SenseMatcherException e) {
            throw new ElementMatcherException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
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
            if (log.isTraceEnabled()) {
                if (IMappingElement.IDK != relation) {
                    String className = stringMatchers.get(i).getClass().getSimpleName();
                    log.trace(className + ":\t" + sourceLabel + "\t" + relation + "\t" + targetLabel);
                }
            }
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
     * @throws ElementMatcherException ElementMatcherException
     */
    private char getRelationFromSenseGlossMatchers(List<ISense> sourceSenses, List<ISense> targetSenses) throws ElementMatcherException {
        char relation = IMappingElement.IDK;
        if (0 < senseGlossMatchers.size()) {
            for (ISense sourceSense : sourceSenses) {
                //noinspection LoopStatementThatDoesntLoop
                for (ISense targetSense : targetSenses) {
                    int k = 0;
                    while ((relation == IMappingElement.IDK) && (k < senseGlossMatchers.size())) {
                        relation = senseGlossMatchers.get(k).match(sourceSense, targetSense);
                        if (log.isTraceEnabled()) {
                            if (IMappingElement.IDK != relation) {
                                String className = senseGlossMatchers.get(k).getClass().getSimpleName();
                                log.trace(className + "\t" + sourceSense.getId() + "\t" + relation + "\t" + targetSense.getId()
                                        + "\t\t" + Arrays.toString(sourceSense.getLemmas().toArray())
                                        + "\t" + relation
                                        + "\t" + Arrays.toString(targetSense.getLemmas().toArray()));
                            }
                        }
                        k++;
                    }
                    return relation;
                }
            }
        }
        return relation;
    }
}