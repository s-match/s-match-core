package it.unitn.disi.common.utils;

import it.unitn.disi.common.DISIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;

/**
 * Utility class.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MiscUtils {

    private static final Logger log = LoggerFactory.getLogger(MiscUtils.class);

    private static final String CLASSPATH_SCHEME = "classpath://";

    /**
     * Writes Java object to url.
     * Recognizes classpath:// prefix in <code>url</code> to write to classpath (hypothetically :).
     * Treats url as a filename if url scheme marker (<code>://</code>) is absent.
     *
     * @param object the object
     * @param url    object location
     * @throws DISIException DISIException
     */
    public static void writeObject(Object object, String url) throws DISIException {
        if (log.isDebugEnabled()) {
            log.debug("Writing: " + url);
        }
        OutputStream os = getOutputStream(url);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(os);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            try {
                oos.writeObject(object);
            } catch (IOException e) {
                throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            } finally {
                oos.close();
                bos.close();
            }
        } catch (IOException e) {
            throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Reads Java object from url.
     * Recognizes classpath:// prefix in <code>url</code> to load from classpath.
     * Treats url as a filename if url scheme marker (<code>://</code>) is absent.
     *
     * @param url object location
     * @return the object
     * @throws DISIException DISIException
     */
    public static Object readObject(String url) throws DISIException {
        if (log.isDebugEnabled()) {
            log.debug("Reading: " + url);
        }
        Object result;
        InputStream is = getInputStream(url);
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            ObjectInputStream oos = new ObjectInputStream(bis);
            try {
                result = oos.readObject();
            } catch (IOException e) {
                throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            } finally {
                oos.close();
                bis.close();
            }
        } catch (IOException e) {
            throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
                }
            }
        }
        return result;
    }

    /**
     * Returns <code>InputStream</code> as in {@link java.net.URL#openStream()}, but supports also
     * "classpath://" scheme.
     *
     * @param url resource location
     * @return InputStream instance
     */
    public static InputStream getInputStream(String url) throws DISIException {
        InputStream result;
        try {
            if (url.startsWith(CLASSPATH_SCHEME)) {
                String cpLocation = url.substring(CLASSPATH_SCHEME.length());
                result = Thread.currentThread().getContextClassLoader().getResourceAsStream(cpLocation);
            } else {
                if (url.contains("://")) {
                    result = new URL(url).openStream();
                } else {
                    result = new FileInputStream(url);
                }
            }
        } catch (IOException e) {
            throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
        return result;
    }

    /**
     * Returns <code>OutputStream</code> as in {@link java.net.URL#openConnection().openStream()}, but supports also
     * "classpath://" scheme.
     *
     * @param url resource location
     * @return InputStream instance
     */
    public static OutputStream getOutputStream(String url) throws DISIException {
        OutputStream result;
        try {
            if (url.startsWith(CLASSPATH_SCHEME)) {
                String cpLocation = url.substring(CLASSPATH_SCHEME.length());
                URL location = Thread.currentThread().getContextClassLoader().getResource(cpLocation);
                if (null != location) {
                    result = location.openConnection().getOutputStream();
                } else {
                    throw new DISIException("Resource is not found: " + url);
                }
            } else {
                if (url.contains("://")) {
                    result = new URL(url).openConnection().getOutputStream();
                } else {
                    result = new FileOutputStream(url);
                }
            }
        } catch (IOException e) {
            throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
        return result;
    }
}