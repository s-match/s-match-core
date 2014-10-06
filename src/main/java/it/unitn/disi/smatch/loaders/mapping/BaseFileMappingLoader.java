package it.unitn.disi.smatch.loaders.mapping;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.ILoader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Base class for file mapping loaders.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseFileMappingLoader extends BaseMappingLoader {

    protected BaseFileMappingLoader(IMappingFactory mappingFactory) {
        super(mappingFactory);
    }

    protected BaseFileMappingLoader(IMappingFactory mappingFactory, IContext source, IContext target, String fileName) {
        super(mappingFactory, source, target, fileName);
    }

    @Override
    protected IContextMapping<INode> process(IContext source, IContext target, String fileName) throws MappingLoaderException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"))) {
            return process(source, target, reader);
        } catch (IOException e) {
            throw new MappingLoaderException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    protected abstract IContextMapping<INode> process(IContext source, IContext target, BufferedReader reader) throws IOException, MappingLoaderException;

    public ILoader.LoaderType getType() {
        return ILoader.LoaderType.FILE;
    }
}