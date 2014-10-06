package it.unitn.disi.smatch.matchers.structure.tree.def;

import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.matchers.structure.node.INodeMatcher;
import it.unitn.disi.smatch.matchers.structure.node.NodeMatcherException;
import it.unitn.disi.smatch.matchers.structure.tree.TreeMatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A Tree Matcher that executes matching in parallel.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class RunnableTreeMatcher extends DefaultTreeMatcher {

    private static final Logger log = LoggerFactory.getLogger(RunnableTreeMatcher.class);

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
            thread.setName("S-Match-" + RunnableTreeMatcher.class.getSimpleName() + "-" + thread.getName());
            thread.setDaemon(true);
            return thread;
        }
    };

    public RunnableTreeMatcher(INodeMatcher nodeMatcher, IMappingFactory mappingFactory) {
        super(nodeMatcher, mappingFactory);
        this.maxThreadCount = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(maxThreadCount, NAMING_THREAD_FACTORY);
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableTreeMatcher(INodeMatcher nodeMatcher, IMappingFactory mappingFactory,
                               IContext sourceContext, IContext targetContext,
                               IContextMapping<IAtomicConceptOfLabel> acolMapping) {
        super(nodeMatcher, mappingFactory, sourceContext, targetContext, acolMapping);
        this.maxThreadCount = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(maxThreadCount, NAMING_THREAD_FACTORY);
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableTreeMatcher(INodeMatcher nodeMatcher, IMappingFactory mappingFactory,
                               Executor executor, int maxThreadCount) {
        super(nodeMatcher, mappingFactory);
        this.executor = executor;
        this.maxThreadCount = maxThreadCount;
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    public RunnableTreeMatcher(INodeMatcher nodeMatcher, IMappingFactory mappingFactory,
                               Executor executor, int maxThreadCount,
                               IContext sourceContext, IContext targetContext,
                               IContextMapping<IAtomicConceptOfLabel> acolMapping) {
        super(nodeMatcher, mappingFactory, sourceContext, targetContext, acolMapping);
        this.executor = executor;
        this.maxThreadCount = maxThreadCount;
        this.threadLimiter = new Semaphore(maxThreadCount);
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>>
    asyncTreeMatch(IContext sourceContext, IContext targetContext, IContextMapping<IAtomicConceptOfLabel> acolMapping) {
        return new RunnableTreeMatcher(nodeMatcher, mappingFactory, executor, maxThreadCount,
                sourceContext, targetContext, acolMapping);
    }

    @Override
    public IContextMapping<INode> treeMatch(final IContext sourceContext, final IContext targetContext,
                                            final IContextMapping<IAtomicConceptOfLabel> acolMapping) throws TreeMatcherException {
        if (0 == getTotal()) {
            setTotal((long) sourceContext.getNodesCount() * (long) targetContext.getNodesCount());
        }
        log.debug("Running with maxThreadCount threads: " + maxThreadCount);
        final AtomicReference<TreeMatcherException> productionException = new AtomicReference<>(null);

        final IContextMapping<INode> mapping = mappingFactory.getContextMappingInstance(sourceContext, targetContext);

        final Map<String, IAtomicConceptOfLabel> sourceAcols = new ConcurrentHashMap<>();
        final Map<String, IAtomicConceptOfLabel> targetAcols = new ConcurrentHashMap<>();

        for (Iterator<INode> i = sourceContext.getNodes(); i.hasNext(); ) {
            final INode sourceNode = i.next();
            for (Iterator<INode> j = targetContext.getNodes(); j.hasNext(); ) {
                //noinspection ThrowableResultOfMethodCallIgnored
                if (Thread.currentThread().isInterrupted() || null != productionException.get()) {
                    break;
                }

                final INode targetNode = j.next();
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
                            mapping.setRelation(sourceNode, targetNode, nodeMatcher.nodeMatch(acolMapping,
                                    sourceAcols, targetAcols, sourceNode, targetNode));
                        } catch (NodeMatcherException e) {
                            productionException.compareAndSet(null, e);
                        } finally {
                            threadLimiter.release();
                        }
                    }
                });

                progress();
            }
        }

        // wait remaining threads
        try {
            threadLimiter.acquire(maxThreadCount);
            threadLimiter.release(maxThreadCount);
        } catch (InterruptedException e) {
            throw new TreeMatcherException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }

        //noinspection ThrowableResultOfMethodCallIgnored
        if (null != productionException.get()) {
            throw productionException.get();
        }

        return mapping;
    }
}