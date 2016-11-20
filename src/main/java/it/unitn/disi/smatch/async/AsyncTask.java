package it.unitn.disi.smatch.async;

import it.unitn.disi.smatch.AsyncMatchManager;
import it.unitn.disi.smatch.SMatchConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.concurrent.*;

/**
 * A task which runs asynchronously and can report its progress.
 * Follows closely {@link javax.swing.SwingWorker SwingWorker}.
 * {@link it.unitn.disi.smatch.AsyncMatchManager#invokeEventLater(Runnable) AsyncMatchManager#invokeEventLater}
 * plays the role of Swing Event Dispatch Thread.
 *
 * @author Igor Kushnirskiy
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class AsyncTask<T, V> implements RunnableFuture<T> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * current progress.
     */
    private volatile long progress;

    /**
     * max progress. progress is from 0 to total.
     * tasks are free to define upper bound
     * or indicate that they don't know how much work remains by setting total to -1.
     * if upper bound is unknown, the progress can be indicated by increasing and decreasing
     * a progress bar with a suitable range.
     */
    private volatile long total;

    /**
     * Progress reporting interval.
     */
    private long progressInterval;

    /**
     * current state.
     */
    private volatile StateValue state;

    /**
     * everything is run inside this FutureTask. Also it is used as
     * a delegatee for the Future API.
     */
    private final FutureTask<T> future;

    /**
     * all propertyChangeSupport goes through this.
     */
    private final PropertyChangeSupport propertyChangeSupport;

    /**
     * handler for {@code process} method.
     */
    private AccumulativeRunnable<V> doProcess;

    /**
     * handler for progress property change notifications.
     */
    private AccumulativeRunnable<Long> doNotifyProgressChange;

    private final AccumulativeRunnable<Runnable> doSubmit = getDoSubmit();

    /**
     * Values for the {@code state} bound property.
     *
     * @since 1.6
     */
    public enum StateValue {
        /**
         * Initial {@code AsyncTask} state.
         */
        PENDING,
        /**
         * {@code AsyncTask} is {@code STARTED}
         * before invoking {@code doInBackground}.
         */
        STARTED,

        /**
         * {@code AsyncTask} is {@code DONE}
         * after {@code doInBackground} method
         * is finished.
         */
        DONE
    }

    protected AsyncTask() {
        Callable<T> callable =
                new Callable<T>() {
                    public T call() throws Exception {
                        setState(StateValue.STARTED);
                        return doInBackground();
                    }
                };

        future = new FutureTask<T>(callable) {
            @Override
            protected void done() {
                doneEDT();
                setState(StateValue.DONE);
            }
        };

        state = StateValue.PENDING;
        propertyChangeSupport = new AsyncTaskPropertyChangeSupport(this);
        doProcess = null;
        doNotifyProgressChange = null;
        // indeterminate by default
        total = 0;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     * <p/>
     * <p/>
     * Note that this method is executed only once.
     * <p/>
     * <p/>
     * Note: this method is executed in a background thread.
     *
     * @return the computed result
     * @throws Exception if unable to compute a result
     */
    protected abstract T doInBackground() throws Exception;

    /**
     * Sets this {@code Future} to the result of computation unless
     * it has been cancelled.
     */
    public final void run() {
        future.run();
    }

    /**
     * Sends data chunks to the {@link #process} method. This method is to be
     * used from inside the {@code doInBackground} method to deliver
     * intermediate results
     * for processing on the <i>Event Dispatch Thread</i> inside the
     * {@code process} method.
     * <p/>
     * <p/>
     * Because the {@code process} method is invoked asynchronously on
     * the <i>Event Dispatch Thread</i>
     * multiple invocations to the {@code publish} method
     * might occur before the {@code process} method is executed. For
     * performance purposes all these invocations are coalesced into one
     * invocation with concatenated arguments.
     * <p/>
     * <p/>
     * For example:
     * <p/>
     * <pre>
     * publish(&quot;1&quot;);
     * publish(&quot;2&quot;, &quot;3&quot;);
     * publish(&quot;4&quot;, &quot;5&quot;, &quot;6&quot;);
     * </pre>
     * <p/>
     * might result in:
     * <p/>
     * <pre>
     * process(&quot;1&quot;, &quot;2&quot;, &quot;3&quot;, &quot;4&quot;, &quot;5&quot;, &quot;6&quot;)
     * </pre>
     * <p/>
     * <p/>
     * <b>Sample Usage</b>. This code snippet loads some tabular data and
     * updates {@code DefaultTableModel} with it. Note that it safe to mutate
     * the tableModel from inside the {@code process} method because it is
     * invoked on the <i>Event Dispatch Thread</i>.
     * <p/>
     * <pre>
     * class TableAsyncTask extends
     *         AsyncTask&lt;DefaultTableModel, Object[]&gt; {
     *     private final DefaultTableModel tableModel;
     *
     *     public TableAsyncTask(DefaultTableModel tableModel) {
     *         this.tableModel = tableModel;
     *     }
     *
     *     {@code @Override}
     *     protected DefaultTableModel doInBackground() throws Exception {
     *         for (Object[] row = loadData();
     *                  ! isCancelled() &amp;&amp; row != null;
     *                  row = loadData()) {
     *             publish((Object[]) row);
     *         }
     *         return tableModel;
     *     }
     *
     *     {@code @Override}
     *     protected void process(List&lt;Object[]&gt; chunks) {
     *         for (Object[] row : chunks) {
     *             tableModel.addRow(row);
     *         }
     *     }
     * }
     * </pre>
     *
     * @param chunks intermediate results to process
     * @see #process
     */
    @SafeVarargs
    protected final void publish(V... chunks) {
        synchronized (this) {
            if (doProcess == null) {
                doProcess = new AccumulativeRunnable<V>() {
                    @Override
                    public void run(List<V> args) {
                        process(args);
                    }

                    @Override
                    protected void submit() {
                        doSubmit.add(this);
                    }
                };
            }
        }
        doProcess.add(chunks);
    }

    /**
     * Receives data chunks from the {@code publish} method asynchronously on the
     * <i>Event Dispatch Thread</i>.
     * <p/>
     * <p/>
     * Please refer to the {@link #publish} method for more details.
     *
     * @param chunks intermediate results to process
     * @see #publish
     */
    protected void process(List<V> chunks) {
    }

    /**
     * Executed on the <i>Event Dispatch Thread</i> after the {@code doInBackground}
     * method is finished. The default
     * implementation does nothing. Subclasses may override this method to
     * perform completion actions on the <i>Event Dispatch Thread</i>. Note
     * that you can query status inside the implementation of this method to
     * determine the result of this task or whether this task has been cancelled.
     *
     * @see #doInBackground
     * @see #isCancelled()
     * @see #get
     */
    protected void done() {
    }

    /**
     * Sets the {@code progress} bound property.
     * The value should be from 0 to <code>total</code>.
     * <p/>
     * <p/>
     * Because {@code PropertyChangeListener}s are notified asynchronously on
     * the <i>Event Dispatch Thread</i> multiple invocations to the
     * {@code setProgress} method might occur before any
     * {@code PropertyChangeListeners} are invoked. For performance purposes
     * all these invocations are coalesced into one invocation with the last
     * invocation argument only.
     * <p/>
     * <p/>
     * For example, the following invocations:
     * <p/>
     * <pre>
     * setProgress(1);
     * setProgress(2);
     * setProgress(3);
     * </pre>
     * <p/>
     * might result in a single {@code PropertyChangeListener} notification with
     * the value {@code 3}.
     *
     * @param progress the progress value to set
     * @throws IllegalArgumentException is value not from 0 to {@link #getTotal() total}
     */
    protected final void setProgress(long progress) {
        if (progress < 0 || (total > 0 && progress > total)) {
            throw new IllegalArgumentException("the value should be from 0 to " + Long.toString(total) 
                                                + ", found instead: " + progress);
        }
        if (this.progress == progress) {
            return;
        }
        long oldProgress = this.progress;
        this.progress = progress;
        if (!getPropertyChangeSupport().hasListeners("progress")) {
            return;
        }
        synchronized (this) {
            if (doNotifyProgressChange == null) {
                doNotifyProgressChange =
                        new AccumulativeRunnable<Long>() {
                            @Override
                            public void run(List<Long> args) {
                                firePropertyChange("progress",
                                        args.get(0),
                                        args.get(args.size() - 1));
                            }

                            @Override
                            protected void submit() {
                                doSubmit.add(this);
                            }
                        };
            }
        }
        doNotifyProgressChange.add(oldProgress, progress);
    }

    /**
     * Returns the {@code progress} bound property.
     *
     * @return the progress bound property.
     */
    public final long getProgress() {
        return progress;
    }

    /**
     * Sets the total property.
     *
     * @param total the total value to set
     */
    protected void setTotal(long total) {
        if (total < 0) {
            throw new IllegalArgumentException("the value should be non-negative");
        }
        if (this.total == total) {
            return;
        }
        this.total = total;
        progressInterval = (total / SMatchConstants.TASK_REPORT_FRACTION) + 1;
    }

    /**
     * Returns the {@code total}  property. 0 indicates unknown (indeterminate) amount.
     *
     * @return the total property.
     */
    public final long getTotal() {
        return total;
    }

    /**
     * Schedules this {@code AsyncTask} for execution on a <i>worker</i>
     * thread. There are a number of <i>worker</i> threads available. In the
     * event all <i>worker</i> threads are busy handling other
     * {@code AsyncTasks} this {@code AsyncTask} is placed in a waiting
     * queue.
     * <p/>
     * <p/>
     * Note:
     * {@code AsyncTask} is only designed to be executed once.  Executing a
     * {@code AsyncTask} more than once will not result in invoking the
     * {@code doInBackground} method twice.
     */
    public final void execute() {
        AsyncMatchManager.executeTask(this);
    }

    // Future methods START

    /**
     * {@inheritDoc}
     */
    public final boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isCancelled() {
        return future.isCancelled();
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isDone() {
        return future.isDone();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Note: calling {@code get} on the <i>Event Dispatch Thread</i> blocks
     * <i>all</i> events, including repaints, from being processed until this
     * {@code AsyncTask} is complete.
     * <p/>
     * <p/>
     * When you want the {@code AsyncTask} to block on the <i>Event
     * Dispatch Thread</i> we recommend that you use a <i>modal dialog</i>.
     * <p/>
     * <p/>
     * For example:
     * <p/>
     * <pre>
     * class AsyncTaskCompletionWaiter extends PropertyChangeListener {
     *     private JDialog dialog;
     *
     *     public AsyncTaskCompletionWaiter(JDialog dialog) {
     *         this.dialog = dialog;
     *     }
     *
     *     public void propertyChange(PropertyChangeEvent event) {
     *         if (&quot;state&quot;.equals(event.getPropertyName())
     *                 &amp;&amp; AsyncTask.StateValue.DONE == event.getNewValue()) {
     *             dialog.setVisible(false);
     *             dialog.dispose();
     *         }
     *     }
     * }
     * JDialog dialog = new JDialog(owner, true);
     * AsyncTask.addPropertyChangeListener(
     *     new AsyncTaskCompletionWaiter(dialog));
     * AsyncTask.execute();
     * //the dialog will be visible until the AsyncTask is done
     * dialog.setVisible(true);
     * </pre>
     */
    public final T get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Please refer to {@link #get} for more details.
     */
    public final T get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    // Future methods END

    // PropertyChangeSupports methods START

    /**
     * Adds a {@code PropertyChangeListener} to the listener list. The listener
     * is registered for all properties. The same listener object may be added
     * more than once, and will be called as many times as it is added. If
     * {@code listener} is {@code null}, no exception is thrown and no action is taken.
     * <p/>
     * <p/>
     * Note: This is merely a convenience wrapper. All work is delegated to
     * {@code PropertyChangeSupport} from {@link #getPropertyChangeSupport}.
     *
     * @param listener the {@code PropertyChangeListener} to be added
     */
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().addPropertyChangeListener(listener);
    }

    /**
     * Removes a {@code PropertyChangeListener} from the listener list. This
     * removes a {@code PropertyChangeListener} that was registered for all
     * properties. If {@code listener} was added more than once to the same
     * event source, it will be notified one less time after being removed. If
     * {@code listener} is {@code null}, or was never added, no exception is
     * thrown and no action is taken.
     * <p/>
     * <p/>
     * Note: This is merely a convenience wrapper. All work is delegated to
     * {@code PropertyChangeSupport} from {@link #getPropertyChangeSupport}.
     *
     * @param listener the {@code PropertyChangeListener} to be removed
     */
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().removePropertyChangeListener(listener);
    }

    /**
     * Reports a bound property update to any registered listeners. No event is
     * fired if {@code old} and {@code new} are equal and non-null.
     * <p/>
     * <p/>
     * This {@code AsyncTask} will be the source for
     * any generated events.
     * <p/>
     * <p/>
     * When called off the <i>Event Dispatch Thread</i>
     * {@code PropertyChangeListeners} are notified asynchronously on
     * the <i>Event Dispatch Thread</i>.
     * <p/>
     * Note: This is merely a convenience wrapper. All work is delegated to
     * {@code PropertyChangeSupport} from {@link #getPropertyChangeSupport}.
     *
     * @param propertyName the programmatic name of the property that was
     *                     changed
     * @param oldValue     the old value of the property
     * @param newValue     the new value of the property
     */
    public final void firePropertyChange(String propertyName, Object oldValue,
                                         Object newValue) {
        getPropertyChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Returns the {@code PropertyChangeSupport} for this {@code AsyncTask}.
     * This method is used when flexible access to bound properties support is
     * needed.
     * <p/>
     * This {@code AsyncTask} will be the source for any generated events.
     * <p/>
     * <p/>
     * Note: The returned {@code PropertyChangeSupport} notifies any
     * {@code PropertyChangeListener}s asynchronously on the <i>Event Dispatch
     * Thread</i> in the event that {@code firePropertyChange} or
     * {@code fireIndexedPropertyChange} are called off the <i>Event Dispatch
     * Thread</i>.
     *
     * @return {@code PropertyChangeSupport} for this {@code AsyncTask}
     */
    public final PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    // PropertyChangeSupports methods END

    /**
     * Returns the {@code AsyncTask} state bound property.
     *
     * @return the current state
     */
    public final StateValue getState() {
        /*
         * DONE is a special case
         * to keep getState and isDone is sync
         */
        if (isDone()) {
            return StateValue.DONE;
        } else {
            return state;
        }
    }

    /**
     * Sets this {@code AsyncTask} state bound property.
     *
     * @param state the state to set
     */
    private void setState(StateValue state) {
        StateValue old = this.state;
        this.state = state;
        firePropertyChange("state", old, state);
    }

    /**
     * Invokes {@code done} on the EDT.
     */
    private void doneEDT() {
        Runnable doDone = new Runnable() {
            public void run() {
                done();
            }
        };
        doSubmit.add(doDone);
    }

    protected void progress() {
        progress(1);
    }

    protected void progress(long bit) {
        long progress = this.progress + bit;
        if (0 == total) {
            if (0 == (progress % SMatchConstants.TASK_REPORT_PIECES)) {
                if (log.isInfoEnabled()) {
                    log.info("progress: " + progress);
                }
            }
        } else {
            if (0 < total && (SMatchConstants.LARGE_TASK < total) && (0 == (progress % progressInterval)) && log.isInfoEnabled()) {
                log.info(100 * (progress / (double) total) + "%");
            }
        }
        setProgress(progress);
    }


    private static final Object DO_SUBMIT_KEY = new StringBuilder("doSubmit");
    private static AccumulativeRunnable<Runnable> staticDoSubmit;

    private static AccumulativeRunnable<Runnable> getDoSubmit() {
        synchronized (DO_SUBMIT_KEY) {
            if (null == staticDoSubmit) {
                staticDoSubmit = new DoSubmitAccumulativeRunnable();
            }
            return staticDoSubmit;
        }
    }

    private static class DoSubmitAccumulativeRunnable extends AccumulativeRunnable<Runnable> {

        @Override
        protected void run(List<Runnable> args) {
            for (Runnable runnable : args) {
                runnable.run();
            }
        }

        @Override
        protected void submit() {
            AsyncMatchManager.invokeEventLater(this);
        }
    }

    private class AsyncTaskPropertyChangeSupport extends PropertyChangeSupport {
        AsyncTaskPropertyChangeSupport(Object source) {
            super(source);
        }

        @Override
        public void firePropertyChange(final PropertyChangeEvent evt) {
            doSubmit.add(new Runnable() {
                public void run() {
                    AsyncTaskPropertyChangeSupport.super.firePropertyChange(evt);
                }
            });
        }
    }
}
