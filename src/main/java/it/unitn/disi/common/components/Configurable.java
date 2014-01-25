package it.unitn.disi.common.components;

import it.unitn.disi.common.DISIException;
import it.unitn.disi.common.utils.ClassFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configurable component base class.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class Configurable implements IConfigurable {

    private static final Logger log = LoggerFactory.getLogger(Configurable.class);

    public static final String GLOBAL_PREFIX = "Global.";
    protected static final String GLOBAL_COMPONENTS_KEY = "GlobalComponents";

    protected Properties properties;

    public Configurable() {
        properties = new Properties();
    }

    public Configurable(Properties properties) {
        this.properties = properties;
    }

    public boolean setProperties(Properties newProperties) throws ConfigurableException {
        boolean result = !newProperties.equals(properties);
        if (result) {
            properties.clear();
            properties.putAll(newProperties);
        }
        return result;
    }

    public boolean setProperties(String fileName) throws ConfigurableException {
        return setProperties(loadProperties(fileName));
    }

    public Properties getProperties() {
        return properties;
    }

    /**
     * Returns only properties that start with componentPrefix, removing this prefix. Copies global components.
     *
     * @param componentPrefix a prefix to search
     * @param properties      properties
     * @return properties that start with componentPrefix
     */
    protected static Properties getComponentProperties(String componentPrefix, Properties properties) {
        Properties result = new Properties();
        if (null != componentPrefix) {
            int componentPrefixLength = componentPrefix.length();
            for (String propertyName : properties.stringPropertyNames()) {
                if (propertyName.startsWith(componentPrefix)) {
                    result.put(propertyName.substring(componentPrefixLength), properties.getProperty(propertyName));
                }
            }
        }

        if (properties.containsKey(GLOBAL_COMPONENTS_KEY)) {
            result.put(GLOBAL_COMPONENTS_KEY, properties.get(GLOBAL_COMPONENTS_KEY));
        }
        return result;
    }

    /**
     * Creates a prefix for component to search for its properties in properties.
     *
     * @param tokenName a component configuration key
     * @param className a component class name
     * @return prefix
     */
    protected static String makeComponentPrefix(String tokenName, String className) {
        String simpleClassName = className;
        if (null != className) {
            int lastDotIdx = className.lastIndexOf(".");
            if (lastDotIdx > -1) {
                simpleClassName = className.substring(lastDotIdx + 1, className.length());
            }
        }
        return tokenName + "." + simpleClassName + ".";
    }

    @SuppressWarnings("unchecked")
    public static IConfigurable configureComponent(IConfigurable component, Properties oldProperties, Properties newProperties, String componentName, String componentKey, Class componentInterface) throws ConfigurableException {
        IConfigurable result = null;
        boolean addToGlobal = false;

        Map<String, IConfigurable> globalComponents;
        // for components prefixed with Global.
        if (oldProperties.containsKey(GLOBAL_COMPONENTS_KEY)) {
            globalComponents = (Map<String, IConfigurable>) oldProperties.get(GLOBAL_COMPONENTS_KEY);
        } else {
            if (newProperties.containsKey(GLOBAL_COMPONENTS_KEY)) {
                globalComponents = (Map<String, IConfigurable>) newProperties.get(GLOBAL_COMPONENTS_KEY);
                oldProperties.put(GLOBAL_COMPONENTS_KEY, globalComponents);
            } else {
                globalComponents = new HashMap<String, IConfigurable>();
                oldProperties.put(GLOBAL_COMPONENTS_KEY, globalComponents);
                newProperties.put(GLOBAL_COMPONENTS_KEY, globalComponents);
            }
        }

        // check global property
        final String globalComponentKey = GLOBAL_PREFIX + componentKey;
        if (newProperties.containsKey(globalComponentKey)) {
            // component becomes or stays global
            addToGlobal = true;
            componentKey = globalComponentKey;
        } else {
            // component becomes local
            globalComponents.remove(globalComponentKey);
        }

        String oldClassName = oldProperties.getProperty(componentKey);
        if (null != oldClassName && oldClassName.isEmpty()) {
            oldClassName = null;
        }
        String newClassName = newProperties.getProperty(componentKey);
        if (null != newClassName && newClassName.isEmpty()) {
            newClassName = null;
        }
        Properties oldComponentProperties = getComponentProperties(makeComponentPrefix(componentKey, oldClassName), oldProperties);
        Properties newComponentProperties = getComponentProperties(makeComponentPrefix(componentKey, newClassName), newProperties);

        boolean reload = !oldComponentProperties.equals(newComponentProperties);
        boolean create = false;
        if (null != oldClassName) {
            if (oldClassName.equals(newClassName)) {
                result = component;
            } else {
                if (null != newClassName) {
                    create = true;
                }
            }
        } else {
            if (null != newClassName) {
                create = true;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("No " + componentName);
                }
            }
        }

        if (create) {
            synchronized (Configurable.class) {
                if (newClassName.startsWith(GLOBAL_PREFIX)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Looking up global " + componentName + ": " + newClassName + "...");
                    }
                    result = globalComponents.get(newClassName);
                    if (null == result) {
                        throw new ConfigurableException("Cannot find global " + componentName + ": " + newClassName + "...");
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Creating " + componentName + ": " + newClassName + "...");
                    }
                    Object o;
                    try {
                        o = ClassFactory.getClassForName(newClassName);
                    } catch (DISIException e) {
                        throw new ConfigurableException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
                    }

                    if (componentInterface.isInstance(o)) {
                        result = (IConfigurable) o;
                        reload = true;
                    } else {
                        throw new ConfigurableException("Specified for " + componentName + " " + newClassName +
                                " does not support " + componentInterface.getSimpleName() + " interface");
                    }
                }
            }
        }

        if (reload && null != result) {
            result.setProperties(newComponentProperties);
        }


        if (addToGlobal) {
            if (null != result) {
                globalComponents.put(globalComponentKey, result);
            } else {
                globalComponents.remove(globalComponentKey);
            }
        }

        return result;
    }

    /**
     * Loads the properties from the properties file.
     *
     * @param filename the properties file name
     * @return Properties instance
     * @throws ConfigurableException ConfigurableException
     */
    public static Properties loadProperties(String filename) throws ConfigurableException {
        log.info("Loading properties from " + filename);
        Properties properties = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream(filename);
            properties.load(input);
        } catch (IOException e) {
            throw new ConfigurableException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
                }
            }
        }

        return properties;
    }

}
