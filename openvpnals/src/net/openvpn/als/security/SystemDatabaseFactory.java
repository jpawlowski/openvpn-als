package net.openvpn.als.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.jdbc.JDBCSystemDatabase;

/**
 * System database factory for creating system databases.
 */
public class SystemDatabaseFactory {
    static Log log = LogFactory.getLog(SystemDatabaseFactory.class);

    static SystemDatabase instance;
    static Class systemDatabaseImpl = JDBCSystemDatabase.class;
    private static boolean locked = false;

    /**
     * @return An instance of the system database factory.
     */
    public static SystemDatabase getInstance() {
        try {
            return instance == null ? instance = (SystemDatabase) systemDatabaseImpl.newInstance() : instance;
        } catch (Exception e) {
            log.error("Could not create instance of class " + systemDatabaseImpl.getCanonicalName(), e);
            return instance == null ? instance = new JDBCSystemDatabase() : instance;
        }
    }


    /**
     * @param systemDatabaseImpl the class of the system database 
     * @param lock weather to lock the system database after setting it.
     * @throws IllegalStateException
     */
    public static void setFactoryImpl(Class systemDatabaseImpl, boolean lock) throws IllegalStateException {
        if (locked) {
            throw new IllegalStateException("System database factory has been locked by another plugin.");
        }
        SystemDatabaseFactory.systemDatabaseImpl = systemDatabaseImpl;
        locked = lock;
    }
}
