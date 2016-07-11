package it.unitn.disi.smatch.loaders.context;

import it.unitn.disi.smatch.data.trees.IBaseContext;
import it.unitn.disi.smatch.data.trees.IBaseNode;
import it.unitn.disi.smatch.loaders.ILoader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Base class for file loaders.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseFileContextLoader<E extends IBaseContext<T>, T extends IBaseNode> extends BaseContextLoader<E, T> implements IBaseContextLoader<E, T> {

    protected BaseFileContextLoader() {
    }

    protected BaseFileContextLoader(String location) {
        super(location);
    }

    public E loadContext(String location) throws ContextLoaderException {
        E result;
        try (BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(location), "UTF-8"))) {
            result = process(input);
            createIds(result);
        } catch (IOException e) {
            throw new ContextLoaderException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
        return result;
    }

    abstract protected E process(BufferedReader input) throws IOException, ContextLoaderException;

    public ILoader.LoaderType getType() {
        return ILoader.LoaderType.FILE;
    }
}