package it.unitn.disi.common.components;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ConfigurationKeyMissingException extends ConfigurableException {

    public ConfigurationKeyMissingException(String errorDescription) {
        super("Configuration key missing: " + errorDescription);
    }

    public ConfigurationKeyMissingException(String errorDescription, Throwable cause) {
        super("Configuration key missing: " + errorDescription, cause);
    }
}
