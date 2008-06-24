package com.adito.agent.client.util;

import java.util.Hashtable;

/**
 * Class loader that creates classes from arrays of bytes
 */
public class ByteArrayClassLoader extends ClassLoader {

    static ByteArrayClassLoader instance;

    private Hashtable classes = new Hashtable();
    private ClassLoader parent;

    /**
     * Constructor.
     *
     * @param parent parent class loader
     */
    public ByteArrayClassLoader(ClassLoader parent) {
        this.parent = parent;
    }

    Class createFromByteArray(String name, byte[] buf, int off, int len) {

        if (!classes.containsKey(name)) {
            classes.put(name, defineClass(name, buf, off, len));
        }
        return (Class) classes.get(name);

    }

    /* (non-Javadoc)
     * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
     */
    public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {

        if (classes.containsKey(name)) {
            return (Class) classes.get(name);
        } else
            return parent.loadClass(name);
    }

    /**
     * Get a static instance of this class loader
     *  
     * @return class loader
     */
    public static ByteArrayClassLoader getInstance() {
        return instance == null ? instance = new ByteArrayClassLoader(ByteArrayClassLoader.class.getClassLoader()) : instance;
    }
}
