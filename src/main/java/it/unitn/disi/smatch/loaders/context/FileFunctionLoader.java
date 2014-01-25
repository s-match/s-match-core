package it.unitn.disi.smatch.loaders.context;

import it.unitn.disi.smatch.data.trees.Context;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.loaders.ILoader;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Loads a function from a file into a tree structure.
 * The file should contain one string to be converted in the form: fn(arg,arg,..), where arg can be fn(arg,..)
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class FileFunctionLoader extends BaseFileContextLoader<IContext> implements IContextLoader {

    protected IContext process(BufferedReader input) throws IOException {
        final String function = input.readLine();
        IContext result = new Context();
        if (null != function) {
            StringFunctionLoader.parse(function, result, null);
        }
        return result;
    }

    public String getDescription() {
        return ILoader.TXT_FILES;
    }
}