package com.adito.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract Factory 
 */
public class AbstractFactory {
    static Log log = LogFactory.getLog(AbstractFactory.class);

    static AbstractFactory instance;
    static Class factoryImpl = null;
    private static boolean locked = false;

    /**
     * Return the created factory. Implementations will probably want to 
     * provide their own get instance that provides a correctly type
     * instance.
     * 
     * @return An instance of the factory.
     */
    public static AbstractFactory getAbstractInstance() {
        try {
        	if(instance == null) {
        		if(factoryImpl == null) {
        			throw new Exception("Not factory implementation class has been set.");
        		}
        		instance = (AbstractFactory) factoryImpl.newInstance();
        	}
            return instance;
        } catch (Exception e) {
            log.error("Could not create instance of class " + factoryImpl.getCanonicalName(), e);
            return instance == null ? instance = new AbstractFactory() : instance;
        }
    }

    /**
     * Set the class the use for the factory implementation. This must
     * be called before the first {@link #getAbstractInstance()} call.
     * 
     * @param factoryImpl the class of the factory implementation
     * @param lock whether to lock the policy database after setting it.
     * @throws IllegalStateException if factory implementation has already been locked
     */
    public static void setFactoryImpl(Class factoryImpl, boolean lock) throws IllegalStateException {
        if (locked) {
            throw new IllegalStateException("Factory has been locked by someone else.");
        }
        AbstractFactory.factoryImpl = factoryImpl;
        locked = lock;
    }
}
