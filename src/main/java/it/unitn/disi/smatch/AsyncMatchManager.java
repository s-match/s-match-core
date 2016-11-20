package it.unitn.disi.smatch;

import it.unitn.disi.smatch.async.AsyncSequentialTaskList;
import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.classifiers.IAsyncContextClassifier;
import it.unitn.disi.smatch.classifiers.IContextClassifier;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.filters.IAsyncMappingFilter;
import it.unitn.disi.smatch.filters.IMappingFilter;
import it.unitn.disi.smatch.loaders.context.IAsyncContextLoader;
import it.unitn.disi.smatch.loaders.context.IBaseContextLoader;
import it.unitn.disi.smatch.loaders.mapping.IAsyncMappingLoader;
import it.unitn.disi.smatch.loaders.mapping.IMappingLoader;
import it.unitn.disi.smatch.matchers.element.IAsyncElementMatcher;
import it.unitn.disi.smatch.matchers.element.IElementMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.IAsyncTreeMatcher;
import it.unitn.disi.smatch.matchers.structure.tree.ITreeMatcher;
import it.unitn.disi.smatch.preprocessors.IAsyncContextPreprocessor;
import it.unitn.disi.smatch.preprocessors.IContextPreprocessor;
import it.unitn.disi.smatch.renderers.context.IAsyncContextRenderer;
import it.unitn.disi.smatch.renderers.context.IBaseContextRenderer;
import it.unitn.disi.smatch.renderers.mapping.IAsyncMappingRenderer;
import it.unitn.disi.smatch.renderers.mapping.IMappingRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * This MatchManager implements support for asynchronous operations.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class AsyncMatchManager extends MatchManager implements IAsyncMatchManager {

    private static final Logger log = LoggerFactory.getLogger(AsyncMatchManager.class);

    private static final int MAX_TASK_THREADS = 10;

    protected final IAsyncContextLoader asyncContextLoader;

    private static Executor eventExecutor;
    private static ExecutorService taskExecutor;

    public static class Builder {
        private IBaseContextLoader contextLoader;
        private IBaseContextRenderer contextRenderer;
        private IMappingLoader mappingLoader;
        private IMappingRenderer mappingRenderer;
        private IMappingFilter mappingFilter;
        private IMappingFactory mappingFactory;
        private IContextPreprocessor contextPreprocessor;
        private IContextClassifier contextClassifier;
        private IElementMatcher elementMatcher;
        private ITreeMatcher treeMatcher;

        public Builder contextLoader(IBaseContextLoader contextLoader) {
            this.contextLoader = contextLoader;
            return this;
        }

        public Builder contextRenderer(IBaseContextRenderer contextRenderer) {
            this.contextRenderer = contextRenderer;
            return this;
        }

        public Builder mappingLoader(IMappingLoader mappingLoader) {
            this.mappingLoader = mappingLoader;
            return this;
        }

        public Builder mappingRenderer(IMappingRenderer mappingRenderer) {
            this.mappingRenderer = mappingRenderer;
            return this;
        }

        public Builder mappingFilter(IMappingFilter mappingFilter) {
            this.mappingFilter = mappingFilter;
            return this;
        }

        public Builder mappingFactory(IMappingFactory mappingFactory) {
            this.mappingFactory = mappingFactory;
            return this;
        }

        public Builder contextPreprocessor(IContextPreprocessor contextPreprocessor) {
            this.contextPreprocessor = contextPreprocessor;
            return this;
        }

        public Builder contextClassifier(IContextClassifier contextClassifier) {
            this.contextClassifier = contextClassifier;
            return this;
        }

        public Builder elementMatcher(IElementMatcher elementMatcher) {
            this.elementMatcher = elementMatcher;
            return this;
        }

        public Builder treeMatcher(ITreeMatcher treeMatcher) {
            this.treeMatcher = treeMatcher;
            return this;
        }

        public MatchManager build() {
            return new AsyncMatchManager(contextLoader, contextRenderer,
                    mappingLoader, mappingRenderer, mappingFilter, mappingFactory,
                    contextPreprocessor, contextClassifier,
                    elementMatcher, treeMatcher);
        }
    }

    public AsyncMatchManager(IBaseContextLoader contextLoader,
                             IBaseContextRenderer contextRenderer,
                             IMappingLoader mappingLoader,
                             IMappingRenderer mappingRenderer,
                             IMappingFilter mappingFilter,
                             IMappingFactory mappingFactory,
                             IContextPreprocessor contextPreprocessor,
                             IContextClassifier contextClassifier,
                             IElementMatcher elementMatcher,
                             ITreeMatcher treeMatcher) {
        super(contextLoader,
                contextRenderer,
                mappingLoader,
                mappingRenderer,
                mappingFilter,
                mappingFactory,
                contextPreprocessor,
                contextClassifier,
                elementMatcher,
                treeMatcher);

        if (contextLoader instanceof IAsyncContextLoader) {
            asyncContextLoader = (IAsyncContextLoader) contextLoader;
        } else {
            asyncContextLoader = null;
        }
    }

    public static Executor getEventExecutor() {
        return eventExecutor;
    }

    public static void setEventExecutor(Executor executor) {
        AsyncMatchManager.eventExecutor = executor;
    }

    public static void invokeEventLater(Runnable doRun) {
        if (null == eventExecutor) {
            doRun.run();
        } else {
            eventExecutor.execute(doRun);
        }
    }

    public static ExecutorService getTaskExecutor() {
        return taskExecutor;
    }

    public static void setTaskExecutor(ExecutorService taskExecutor) {
        AsyncMatchManager.taskExecutor = taskExecutor;
    }

    public static void executeTask(Runnable doRun) {
        synchronized (MatchManager.class) {
            if (null == taskExecutor) {
                createDefaultTaskExecutor();
            }
        }
        taskExecutor.execute(doRun);
    }

    public static void createDefaultTaskExecutor() {
        //this creates daemon threads.
        ThreadFactory threadFactory = new ThreadFactory() {
            final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

            public Thread newThread(final Runnable r) {
                Thread thread = defaultFactory.newThread(r);
                thread.setName("S-Match-" + thread.getName());
                thread.setDaemon(true);
                return thread;
            }
        };

        taskExecutor = new ThreadPoolExecutor(MAX_TASK_THREADS, MAX_TASK_THREADS,
                10L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);

        scheduleTaskExecutorShutdown();
    }

    public static void scheduleTaskExecutorShutdown() {
        if (null != taskExecutor) {
            final ExecutorService es = taskExecutor;
            Runtime.getRuntime().addShutdownHook(
                    new Thread() {
                        @Override
                        public void run() {
                            final WeakReference<ExecutorService> executorServiceRef = new WeakReference<>(es);
                            final ExecutorService executorService = executorServiceRef.get();
                            if (executorService != null) {
                                AccessController.doPrivileged(
                                        new PrivilegedAction<Void>() {
                                            public Void run() {
                                                executorService.shutdown();
                                                return null;
                                            }
                                        }
                                );
                            }
                        }
                    }
            );
        }
    }

    @Override
    public AsyncTask<IContext, INode> asyncLoadContext(String location)  {
        if (!(contextLoader instanceof IAsyncContextLoader)) {
            throw new IllegalStateException("Context loader is not asynchronous.");
        }

        IAsyncContextLoader asyncContextLoader = (IAsyncContextLoader) contextLoader;
        return asyncContextLoader.asyncLoad(location);
    }

    @Override
    public AsyncTask<Void, INode> asyncRenderContext(IContext context, String location)  {
        if (!(contextRenderer instanceof IAsyncContextRenderer)) {
            throw new IllegalStateException("Context renderer is not asynchronous.");
        }

        IAsyncContextRenderer asyncContextRenderer = (IAsyncContextRenderer) contextRenderer;
        return asyncContextRenderer.asyncRender(context, location);
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncLoadMapping(IContext ctxSource, IContext ctxTarget, String location)  {
        if (!(mappingLoader instanceof IAsyncMappingLoader)) {
            throw new IllegalStateException("Mapping loader is not asynchronous.");
        }

        IAsyncMappingLoader asyncMappingLoader = (IAsyncMappingLoader) mappingLoader;
        return asyncMappingLoader.asyncLoad(ctxSource, ctxTarget, location);
    }

    @Override
    public AsyncTask<Void, IMappingElement<INode>> asyncRenderMapping(IContextMapping<INode> mapping, String location)  {
        if (!(mappingRenderer instanceof IAsyncMappingRenderer)) {
            throw new IllegalStateException("Mapping renderer is not asynchronous.");
        }

        IAsyncMappingRenderer asyncMappingRenderer = (IAsyncMappingRenderer) mappingRenderer;
        return asyncMappingRenderer.asyncRender(mapping, location);
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncFilterMapping(IContextMapping<INode> mapping)  {
        if (!(mappingFilter instanceof IAsyncMappingFilter)) {
            throw new IllegalStateException("Mapping filter is not asynchronous.");
        }

        IAsyncMappingFilter asyncMappingFilter = (IAsyncMappingFilter) mappingFilter;
        return asyncMappingFilter.asyncFilter(mapping);
    }

    @Override
    public AsyncTask<Void, INode> asyncPreprocessContext(IContext context)  {
        if (!(contextPreprocessor instanceof IAsyncContextPreprocessor)) {
            throw new IllegalStateException("Context preprocessor is not asynchronous.");
        }

        IAsyncContextPreprocessor asyncContextPreprocessor = (IAsyncContextPreprocessor) contextPreprocessor;
        return asyncContextPreprocessor.asyncPreprocess(context);
    }

    @Override
    public AsyncTask<Void, INode> asyncClassifyContext(IContext context)  {
        if (!(contextClassifier instanceof IAsyncContextClassifier)) {
            throw new IllegalStateException("Context classifier is not asynchronous.");
        }

        IAsyncContextClassifier asyncContextClassifier = (IAsyncContextClassifier) contextClassifier;
        return asyncContextClassifier.asyncClassify(context);
    }

    @Override
    public AsyncTask<Void, INode> asyncOffline(IContext context)  {
        List<AsyncTask> tasks = new ArrayList<>();
        tasks.add(asyncPreprocessContext(context));
        tasks.add(asyncClassifyContext(context));
        return new AsyncSequentialTaskList<>(tasks);
    }

    @Override
    public AsyncTask<IContextMapping<IAtomicConceptOfLabel>, IMappingElement<IAtomicConceptOfLabel>>
    asyncElementLevelMatching(IContext sourceContext, IContext targetContext) {
        if (!(elementMatcher instanceof IAsyncElementMatcher)) {
            throw new IllegalStateException("Element matcher is not asynchronous.");
        }

        IAsyncElementMatcher asyncElementMatcher = (IAsyncElementMatcher) elementMatcher;
        return asyncElementMatcher.asyncElementLevelMatching(sourceContext, targetContext);
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>>
    asyncStructureLevelMatching(IContext sourceContext, IContext targetContext,
                                IContextMapping<IAtomicConceptOfLabel> acolMapping) {
        if (!(treeMatcher instanceof IAsyncTreeMatcher)) {
            throw new IllegalStateException("Tree matcher is not asynchronous.");
        }

        IAsyncTreeMatcher asyncTreeMatcher = (IAsyncTreeMatcher) treeMatcher;
        return asyncTreeMatcher.asyncTreeMatch(sourceContext, targetContext, acolMapping);
    }

    private class OnlineAsyncTask extends AsyncTask<IContextMapping<INode>, IMappingElement<INode>> {
        private final IContext sourceContext;
        private final IContext targetContext;
        private final AsyncTask<IContextMapping<IAtomicConceptOfLabel>, IMappingElement<IAtomicConceptOfLabel>> elmTask;

        private final PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    Long oldProgress = (Long) evt.getOldValue();
                    Long newProgress = (Long) evt.getNewValue();
                    setProgress(getProgress() + (newProgress - oldProgress));
                }
            }
        };

        public OnlineAsyncTask(IMappingFactory mappingFactory, IContext sourceContext, IContext targetContext) {
            super();
            this.sourceContext = sourceContext;
            this.targetContext = targetContext;

            elmTask = asyncElementLevelMatching(sourceContext, targetContext);

            final IContextMapping<IAtomicConceptOfLabel> fake =
                    mappingFactory.getConceptMappingInstance(sourceContext, targetContext);

            // well... this is not exactly fair
            final AsyncTask<IContextMapping<INode>, IMappingElement<INode>> fakeSLM =
                    asyncStructureLevelMatching(sourceContext, targetContext, fake);

            setTotal(elmTask.getTotal() + fakeSLM.getTotal());
        }

        @Override
        protected IContextMapping<INode> doInBackground() throws Exception {
            final String threadName = Thread.currentThread().getName();
            try {
                Thread.currentThread().setName(Thread.currentThread().getName()
                        + " [" + this.getClass().getSimpleName() + ": asyncOnline" + "]");

                log.info("Online matching...");
                elmTask.addPropertyChangeListener(listener);
                elmTask.execute();
                IContextMapping<IAtomicConceptOfLabel> acolMapping = elmTask.get();
                AsyncTask<IContextMapping<INode>, IMappingElement<INode>> slmTask =
                        asyncStructureLevelMatching(sourceContext, targetContext, acolMapping);
                slmTask.addPropertyChangeListener(listener);
                slmTask.execute();

                IContextMapping<INode> mapping = slmTask.get();
                log.info("Online matching done");
                return mapping;
            } finally {
                Thread.currentThread().setName(threadName);
            }
        }
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>>
    asyncOnline(final IContext sourceContext, final IContext targetContext) {
        if (null == mappingFactory) {
            throw new IllegalStateException("Mapping factory is required.");
        }

        return new OnlineAsyncTask(mappingFactory, sourceContext, targetContext);
    }

    private class MatchAsyncTask extends AsyncTask<IContextMapping<INode>, IMappingElement<INode>> {
        private final AsyncTask<Void, INode> sourceOffline;
        private final AsyncTask<Void, INode> targetOffline;
        private final AsyncTask<IContextMapping<INode>, IMappingElement<INode>> match;

        private final PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    Long oldProgress = (Long) evt.getOldValue();
                    Long newProgress = (Long) evt.getNewValue();
                    setProgress(getProgress() + (newProgress - oldProgress));
                }
            }
        };

        public MatchAsyncTask(IContext sourceContext, IContext targetContext) {
            super();

            if (!sourceContext.getRoot().nodeData().isSubtreePreprocessed()) {
                sourceOffline = asyncOffline(sourceContext);
                sourceOffline.addPropertyChangeListener(listener);
                setTotal(getTotal() + sourceOffline.getTotal());
            } else {
                sourceOffline = null;
            }
            if (!targetContext.getRoot().nodeData().isSubtreePreprocessed()) {
                targetOffline = asyncOffline(targetContext);
                targetOffline.addPropertyChangeListener(listener);
                setTotal(getTotal() + targetOffline.getTotal());
            } else {
                targetOffline = null;
            }

            match = asyncOnline(sourceContext, targetContext);
            match.addPropertyChangeListener(listener);

            setTotal(getTotal() + match.getTotal());
        }

        @Override
        protected IContextMapping<INode> doInBackground() throws Exception {
            final String threadName = Thread.currentThread().getName();
            try {
                Thread.currentThread().setName(Thread.currentThread().getName()
                        + " [" + this.getClass().getSimpleName() + ": asyncMatch" + "]");

                log.info("Semantic matching...");
                boolean proceed = true;
                if (null != sourceOffline) {
                    sourceOffline.execute();
                }
                if (null != targetOffline) {
                    targetOffline.execute();
                }
                if (null != sourceOffline) {
                    sourceOffline.get();
                    if (sourceOffline.isCancelled()) {
                        proceed = false;
                    }
                }
                if (proceed && null != targetOffline) {
                    targetOffline.get();
                    if (targetOffline.isCancelled()) {
                        proceed = false;
                    }
                }
                IContextMapping<INode> mapping = null;
                if (proceed) {
                    match.execute();
                    mapping = match.get();
                }
                log.info("Semantic matching done");
                return mapping;
            } finally {
                Thread.currentThread().setName(threadName);
            }
        }
    }

    @Override
    public AsyncTask<IContextMapping<INode>, IMappingElement<INode>> asyncMatch(IContext sourceContext, IContext targetContext) {
        return new MatchAsyncTask(sourceContext, targetContext);
    }
}