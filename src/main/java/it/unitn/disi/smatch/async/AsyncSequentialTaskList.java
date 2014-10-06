package it.unitn.disi.smatch.async;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * A task that combines several tasks and runs them sequentially.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class AsyncSequentialTaskList<T, E> extends AsyncTask<T, E> {

    private final List<AsyncTask> tasks;

    public AsyncSequentialTaskList(List<AsyncTask> tasks) {
        this.tasks = tasks;
        for (AsyncTask task : tasks) {
            if (0 == task.getTotal()) {
                setTotal(0);
                break;
            } else {
                setTotal(getTotal() + task.getTotal());
            }
        }

        // if certain, set up listeners to transmit
        if (0 < getTotal()) {
            for (AsyncTask task : tasks) {
                task.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("progress".equals(evt.getPropertyName())) {
                            Long oldProgress = (Long) evt.getOldValue();
                            Long newProgress = (Long) evt.getNewValue();
                            setProgress(getProgress() + (newProgress - oldProgress));
                        }
                    }
                });
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T doInBackground() throws Exception {
        final String threadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(Thread.currentThread().getName() + " [" + this.getClass().getSimpleName() + ": " + tasks.size() + "]");
            for (AsyncTask task : tasks) {
                task.execute();
                task.get();
            }
            return (T) tasks.get(tasks.size() - 1).get();
        } finally {
            Thread.currentThread().setName(threadName);
        }
    }
}