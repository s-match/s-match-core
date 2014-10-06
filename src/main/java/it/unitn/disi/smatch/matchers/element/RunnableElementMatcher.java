package it.unitn.disi.smatch.matchers.element;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.oracles.ISenseMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An Element Matcher that executes element level matchers in parallel.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class RunnableElementMatcher extends ElementMatcher {

    private static final Logger log = LoggerFactory.getLogger(RunnableElementMatcher.class);

    /**
     * An executor which runs the matchers.
     */
    private final Executor executor;

    /**
     * Maximum amount of matcher threads allowed. And 1 producer on top of those.
     * The executor supplied should allow all those to run.
     */
    private final int maxThreadCount;

    private final Semaphore threadLimiter;

    private static final ThreadFactory NAMING_THREAD_FACTORY = new ThreadFactory() {
        final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

        public Thread newThread(final Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            thread.setName("S-Match-" + RunnableElementMatcher.class.getSimpleName() + "-" + thread.getName());
            thread.setDaemon(true);
            return thread;
        }
    };

    public RunnableElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher) {
        super(mappingFactory, senseMatcher);
        this.maxThreadCount = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(maxThreadCount, NAMING_THREAD_FACTORY);
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                                  IContext sourceContext, IContext targetContext) {
        super(mappingFactory, senseMatcher, sourceContext, targetContext);
        this.maxThreadCount = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(maxThreadCount, NAMING_THREAD_FACTORY);
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                                  boolean useWeakSemanticsElementLevelMatchersLibrary) {
        super(mappingFactory, senseMatcher, useWeakSemanticsElementLevelMatchersLibrary);
        this.maxThreadCount = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(maxThreadCount, NAMING_THREAD_FACTORY);
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                                  boolean useWeakSemanticsElementLevelMatchersLibrary,
                                  IContext sourceContext, IContext targetContext) {
        super(mappingFactory, senseMatcher, useWeakSemanticsElementLevelMatchersLibrary, sourceContext, targetContext);
        this.maxThreadCount = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(maxThreadCount, NAMING_THREAD_FACTORY);
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                                  boolean useWeakSemanticsElementLevelMatchersLibrary,
                                  List<IStringBasedElementLevelSemanticMatcher> stringMatchers,
                                  List<ISenseGlossBasedElementLevelSemanticMatcher> senseGlossMatchers) {
        super(mappingFactory, senseMatcher, useWeakSemanticsElementLevelMatchersLibrary, stringMatchers, senseGlossMatchers);
        this.maxThreadCount = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(maxThreadCount, NAMING_THREAD_FACTORY);
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                                  boolean useWeakSemanticsElementLevelMatchersLibrary,
                                  List<IStringBasedElementLevelSemanticMatcher> stringMatchers,
                                  List<ISenseGlossBasedElementLevelSemanticMatcher> senseGlossMatchers,
                                  IContext sourceContext, IContext targetContext) {
        super(mappingFactory, senseMatcher, useWeakSemanticsElementLevelMatchersLibrary, stringMatchers, senseGlossMatchers, sourceContext, targetContext);
        this.maxThreadCount = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(maxThreadCount, NAMING_THREAD_FACTORY);
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                                  Executor executor, int maxThreadCount) {
        super(mappingFactory, senseMatcher);
        this.executor = executor;
        this.maxThreadCount = maxThreadCount;
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                                  Executor executor, int maxThreadCount,
                                  IContext sourceContext, IContext targetContext) {
        super(mappingFactory, senseMatcher, sourceContext, targetContext);
        this.executor = executor;
        this.maxThreadCount = maxThreadCount;
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                                  boolean useWeakSemanticsElementLevelMatchersLibrary,
                                  Executor executor, int maxThreadCount) {
        super(mappingFactory, senseMatcher, useWeakSemanticsElementLevelMatchersLibrary);
        this.executor = executor;
        this.maxThreadCount = maxThreadCount;
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                                  boolean useWeakSemanticsElementLevelMatchersLibrary,
                                  Executor executor, int maxThreadCount,
                                  IContext sourceContext, IContext targetContext) {
        super(mappingFactory, senseMatcher, useWeakSemanticsElementLevelMatchersLibrary, sourceContext, targetContext);
        this.executor = executor;
        this.maxThreadCount = maxThreadCount;
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                                  boolean useWeakSemanticsElementLevelMatchersLibrary,
                                  List<IStringBasedElementLevelSemanticMatcher> stringMatchers,
                                  List<ISenseGlossBasedElementLevelSemanticMatcher> senseGlossMatchers,
                                  Executor executor, int maxThreadCount) {
        super(mappingFactory, senseMatcher, useWeakSemanticsElementLevelMatchersLibrary, stringMatchers, senseGlossMatchers);
        this.executor = executor;
        this.maxThreadCount = maxThreadCount;
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableElementMatcher(IMappingFactory mappingFactory, ISenseMatcher senseMatcher,
                                  boolean useWeakSemanticsElementLevelMatchersLibrary,
                                  List<IStringBasedElementLevelSemanticMatcher> stringMatchers,
                                  List<ISenseGlossBasedElementLevelSemanticMatcher> senseGlossMatchers,
                                  Executor executor, int maxThreadCount,
                                  IContext sourceContext, IContext targetContext) {
        super(mappingFactory, senseMatcher, useWeakSemanticsElementLevelMatchersLibrary, stringMatchers, senseGlossMatchers, sourceContext, targetContext);
        this.executor = executor;
        this.maxThreadCount = maxThreadCount;
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    @Override
    public AsyncTask<IContextMapping<IAtomicConceptOfLabel>, IMappingElement<IAtomicConceptOfLabel>>
    asyncElementLevelMatching(IContext sourceContext, IContext targetContext) {
        return new RunnableElementMatcher(mappingFactory, senseMatcher, useWeakSemanticsElementLevelMatchersLibrary,
                stringMatchers, senseGlossMatchers, sourceContext, targetContext);
    }

    @Override
    public IContextMapping<IAtomicConceptOfLabel> elementLevelMatching(final IContext sourceContext, final IContext targetContext) throws ElementMatcherException {
        if (0 == getTotal()) {
            setTotal((long) sourceContext.getNodesCount() * (long) targetContext.getNodesCount());
        }
        log.debug("Running with maxThreadCount threads: " + maxThreadCount);
        final AtomicReference<ElementMatcherException> productionException = new AtomicReference<>(null);

        final IContextMapping<IAtomicConceptOfLabel> mapping = mappingFactory.getACoLMappingInstance(sourceContext, targetContext);

        for (Iterator<INode> i = sourceContext.getNodes(); i.hasNext(); ) {
            final INode sourceNode = i.next();
            for (Iterator<INode> j = targetContext.getNodes(); j.hasNext(); ) {
                final INode targetNode = j.next();
                for (Iterator<IAtomicConceptOfLabel> ii = sourceNode.getNodeData().getACoLs(); ii.hasNext(); ) {
                    final IAtomicConceptOfLabel sourceACoL = ii.next();
                    for (Iterator<IAtomicConceptOfLabel> jj = targetNode.getNodeData().getACoLs(); jj.hasNext(); ) {
                        //noinspection ThrowableResultOfMethodCallIgnored
                        if (Thread.currentThread().isInterrupted() || null != productionException.get()) {
                            break;
                        }

                        final IAtomicConceptOfLabel targetACoL = jj.next();

                        try {
                            threadLimiter.acquire();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }

                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mapping.setRelation(sourceACoL, targetACoL, getRelation(sourceACoL, targetACoL));
                                } catch (ElementMatcherException e) {
                                    productionException.compareAndSet(null, e);
                                } finally {
                                    threadLimiter.release();
                                }
                            }
                        });
                    }
                }

                // progress by node rather than by acol because task can be created on non-preprocessed contexts...
                progress();
            }
        }

        // wait remaining threads
        try {
            threadLimiter.acquire(maxThreadCount);
            threadLimiter.release(maxThreadCount);
        } catch (InterruptedException e) {
            throw new ElementMatcherException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }

        //noinspection ThrowableResultOfMethodCallIgnored
        if (null != productionException.get()) {
            throw productionException.get();
        }

        return mapping;
    }
}
