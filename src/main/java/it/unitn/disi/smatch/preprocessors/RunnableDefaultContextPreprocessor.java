package it.unitn.disi.smatch.preprocessors;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.data.trees.Node;
import it.unitn.disi.smatch.oracles.ILinguisticOracle;
import it.unitn.disi.smatch.oracles.ISenseMatcher;
import it.unitn.disi.smatch.oracles.SenseMatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Preprocesses in parallel.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class RunnableDefaultContextPreprocessor extends DefaultContextPreprocessor {

    private static final Logger log = LoggerFactory.getLogger(RunnableDefaultContextPreprocessor.class);

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
            thread.setName("S-Match-" + RunnableDefaultContextPreprocessor.class.getSimpleName() + "-" + thread.getName());
            thread.setDaemon(true);
            return thread;
        }
    };

    public RunnableDefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle) {
        super(senseMatcher, linguisticOracle);
        this.maxThreadCount = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(maxThreadCount, NAMING_THREAD_FACTORY);
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableDefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle, IContext context) {
        super(senseMatcher, linguisticOracle, context);
        this.maxThreadCount = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(maxThreadCount, NAMING_THREAD_FACTORY);
        this.threadLimiter = new Semaphore(maxThreadCount);
        setTotal(5 * context.getNodesCount());
    }

    public RunnableDefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle,
                                              String meaninglessWords, String andWords, String orWords, String notWords,
                                              String numberCharacters) {
        super(senseMatcher, linguisticOracle, meaninglessWords, andWords, orWords, notWords, numberCharacters);
        this.maxThreadCount = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(maxThreadCount, NAMING_THREAD_FACTORY);
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableDefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle,
                                              String meaninglessWords, String andWords, String orWords, String notWords,
                                              String numberCharacters, IContext context) {
        super(senseMatcher, linguisticOracle, meaninglessWords, andWords, orWords, notWords, numberCharacters, context);
        this.maxThreadCount = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(maxThreadCount, NAMING_THREAD_FACTORY);
        this.threadLimiter = new Semaphore(maxThreadCount);
        setTotal(5 * context.getNodesCount());
    }

    public RunnableDefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle,
                                              Executor executor, int maxThreadCount) {
        super(senseMatcher, linguisticOracle);
        this.executor = executor;
        this.maxThreadCount = maxThreadCount;
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableDefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle,
                                              Executor executor, int maxThreadCount, IContext context) {
        super(senseMatcher, linguisticOracle, context);
        this.executor = executor;
        this.maxThreadCount = maxThreadCount;
        this.threadLimiter = new Semaphore(maxThreadCount);
        setTotal(5 * context.getNodesCount());
    }

    public RunnableDefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle,
                                              String meaninglessWords, String andWords, String orWords, String notWords,
                                              String numberCharacters, Executor executor, int maxThreadCount) {
        super(senseMatcher, linguisticOracle, meaninglessWords, andWords, orWords, notWords, numberCharacters);
        this.executor = executor;
        this.maxThreadCount = maxThreadCount;
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableDefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle,
                                              String meaninglessWords, String andWords, String orWords, String notWords,
                                              String numberCharacters, Executor executor, int maxThreadCount,
                                              IContext context) {
        super(senseMatcher, linguisticOracle, meaninglessWords, andWords, orWords, notWords, numberCharacters, context);
        this.executor = executor;
        this.maxThreadCount = maxThreadCount;
        this.threadLimiter = new Semaphore(maxThreadCount);
        setTotal(5 * context.getNodesCount());
    }

    @Override
    public AsyncTask<Void, INode> asyncPreprocess(IContext context) {
        return new RunnableDefaultContextPreprocessor(senseMatcher, linguisticOracle,
                meaninglessWords, andWords, orWords, notWords, numberCharacters,
                executor, maxThreadCount, context);
    }

    @Override
    public void preprocess(IContext context) throws ContextPreprocessorException {
        log.debug("Running with maxThreadCount threads: " + maxThreadCount);
        if (0 == getTotal()) {
            setTotal(4 * context.getNodesCount());
        }
        Set<String> unrecognizedWords = new ConcurrentSkipListSet<>();

        context = buildCLabs(context, unrecognizedWords);
        context = findMultiwordsInContextStructure(context);
        senseFiltering(context);

        reportUnrecognizedWords(unrecognizedWords);
    }

    @Override
    protected IContext buildCLabs(final IContext context, final Set<String> unrecognizedWords) throws ContextPreprocessorException {
        final AtomicReference<ContextPreprocessorException> productionException = new AtomicReference<>(null);

        for (final Iterator<INode> i = context.getNodes(); i.hasNext(); ) {
            //noinspection ThrowableResultOfMethodCallIgnored
            if (Thread.currentThread().isInterrupted() || null != productionException.get()) {
                break;
            }

            if (waitThreadLimit()) {
                break;
            }

            final INode node = i.next();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        processNode(node, unrecognizedWords);
                    } catch (ContextPreprocessorException e) {
                        productionException.compareAndSet(null, e);
                    } finally {
                        threadLimiter.release();
                    }
                }
            });

            progress();
        }
        waitCompletion(productionException);

        return context;
    }

    private void waitCompletion(AtomicReference<ContextPreprocessorException> productionException) throws ContextPreprocessorException {
        // wait remaining threads
        try {
            threadLimiter.acquire(maxThreadCount);
            threadLimiter.release(maxThreadCount);
        } catch (InterruptedException e) {
            throw new ContextPreprocessorException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }

        //noinspection ThrowableResultOfMethodCallIgnored
        if (null != productionException.get()) {
            throw productionException.get();
        }
    }

    private boolean waitThreadLimit() {
        try {
            threadLimiter.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return true;
        }
        return false;
    }
}