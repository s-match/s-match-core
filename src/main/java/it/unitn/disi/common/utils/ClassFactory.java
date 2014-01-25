package it.unitn.disi.common.utils;

import it.unitn.disi.common.DISIException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Constructs class instances.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ClassFactory {

    /**
     * Returns object instance from the string representing its class name.
     *
     * @param className className
     * @return Object instance
     * @throws DISIException DISIException
     */
    public static Object getClassForName(String className) throws DISIException {
        Object object;
        try {
            Class classDefinition = Class.forName(className);
            object = classDefinition.newInstance();
        } catch (ClassNotFoundException e) {
            throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
        return object;
    }


    /**
     * Creates an instance of the class whose name is passed as the parameter.
     *
     * @param className  name of the class those instance is to be created
     * @param attrTypes  attrTypes
     * @param attrValues attrValues
     * @return instance of the class
     * @throws DISIException DISIException
     */
    @SuppressWarnings("unchecked")
    public static Object getClassInstance(String className,
                                          Class[] attrTypes,
                                          Object[] attrValues) throws DISIException {

        Constructor constr;
        try {
            Class cl = Class.forName(className);
            constr = cl.getConstructor(attrTypes);
        } catch (ClassNotFoundException e) {
            throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }

        Object classInst;

        try {
            classInst = constr.newInstance(attrValues);
        } catch (InstantiationException e) {
            throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new DISIException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
        return classInst;
    }


    /**
     * Parses a string of class names separated by separator into a list of objects.
     *
     * @param str       names of classes
     * @param separator separator characters
     * @return ArrayList of class instances
     */
    public static List<Object> stringToClasses(String str, String separator) throws DISIException {
        ArrayList<Object> tmp = new ArrayList<Object>();
        StringTokenizer stringTokenizer = new StringTokenizer(str, separator);
        while (stringTokenizer.hasMoreTokens()) {
            Object obj = getClassForName(stringTokenizer.nextToken());
            if (obj != null) {
                tmp.add(obj);
            }
        }
        return tmp;
    }

    /**
     * Parses a string of class names separated by separator into a list of objects.
     *
     * @param str        names of classes
     * @param separator  separator characters
     * @param attrTypes  attrTypes
     * @param attrValues attrValues
     * @return ArrayList of class instances
     * @throws DISIException DISIException
     */
    public static List<Object> stringToClassInstances(String str, String separator, Class[] attrTypes,
                                                      Object[] attrValues) throws DISIException {
        ArrayList<Object> tmp = new ArrayList<Object>();
        StringTokenizer stringTokenizer = new StringTokenizer(str, separator);
        while (stringTokenizer.hasMoreTokens()) {
            Object obj = getClassInstance(stringTokenizer.nextToken(), attrTypes, attrValues);
            if (obj != null) {
                tmp.add(obj);
            }
        }
        return tmp;
    }
}