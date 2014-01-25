package it.unitn.disi.common.components;

import java.util.Properties;

/**
 * Represents a component that supports configuration.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IConfigurable {

    /**
     * Sets component configuration properties. The component might check for properties change and
     * reconfigure or reload subcomponents.
     *
     * @param newProperties a new configuration
     * @return true if properties have been changed
     * @throws ConfigurableException ConfigurableException
     */
    boolean setProperties(Properties newProperties) throws ConfigurableException;

    /**
     * Sets component configuration by reading it from a file.
     *
     * @param fileName .properties file name
     * @return true if properties have been changed
     * @throws ConfigurableException ConfigurableException
     */
    boolean setProperties(String fileName) throws ConfigurableException;

    Properties getProperties();
}
