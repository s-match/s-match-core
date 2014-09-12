package it.unitn.disi.smatch.loaders.mapping;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.mappings.IMappingFactory;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.data.util.MappingProgressContainer;
import it.unitn.disi.smatch.loaders.ILoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(BaseFileMappingLoader.class);

    protected BaseFileMappingLoader(IMappingFactory mappingFactory) {
        super(mappingFactory);
    }

    @Override
    protected void process(IContextMapping<INode> mapping, IContext source, IContext target, String fileName, MappingProgressContainer progressContainer) throws MappingLoaderException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            process(mapping, source, target, reader, progressContainer);
        } catch (IOException e) {
            throw new MappingLoaderException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
                }
            }
        }
    }

    protected abstract void process(IContextMapping<INode> mapping, IContext source, IContext target, BufferedReader reader, MappingProgressContainer progressContainer) throws IOException, MappingLoaderException;

    public ILoader.LoaderType getType() {
        return ILoader.LoaderType.FILE;
    }
}