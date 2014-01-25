package it.unitn.disi.smatch.renderers.mapping;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.ILoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Base class for file mapping renderers.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseFileMappingRenderer extends BaseMappingRenderer {

    private static final Logger log = LoggerFactory.getLogger(BaseFileMappingRenderer.class);

    @Override
    protected void process(IContextMapping<INode> mapping, String outputFile) throws MappingRendererException {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
            try {
                process(mapping, out);
            } catch (IOException e) {
                throw new MappingRendererException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new MappingRendererException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    protected abstract void process(IContextMapping<INode> mapping, BufferedWriter out) throws IOException, MappingRendererException;

    public ILoader.LoaderType getType() {
        return ILoader.LoaderType.FILE;
    }
}