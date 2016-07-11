package it.unitn.disi.smatch.loaders.mapping;

import it.unitn.disi.smatch.data.mappings.IContextMapping;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.ILoader;

/**
 * Interface for mapping loaders.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMappingLoader {

    /**
     * Loads the mapping.
     *
     * @param source   interface of data structure of source
     * @param target   interface of data structure of target
     * @param fileName file with a mapping
     * @return interface to a mapping
     * @throws MappingLoaderException MappingLoaderException
     */
    IContextMapping<INode> loadMapping(IContext source, IContext target, String fileName) throws MappingLoaderException;

    /**
     * Returns the description of the format.
     *
     * @return the description of the format
     */
    String getDescription();

    /**
     * Returns the type of the loader.
     *
     * @return the type of the loader
     */
    ILoader.LoaderType getType();
}