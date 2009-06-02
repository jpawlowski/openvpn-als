package net.openvpn.als.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.jdbc.JDBCPropertyDatabase;
import net.openvpn.als.jdbc.JDBCSystemDatabase;

/**
 * System profiles factory for creating and manageing profiles.
 */
public class ProfilesFactory {
    static Log log = LogFactory.getLog(ProfilesFactory.class);

    static PropertyDatabase instance;
    static Class propertyDatabaseImpl = JDBCPropertyDatabase.class;
    private static boolean locked = false;

    /**
     * @return An instance of the profile database.
     */
    public static PropertyDatabase getInstance() {
        try {
            return instance == null ? instance = (PropertyDatabase) propertyDatabaseImpl.newInstance() : instance;
        } catch (Exception e) {
            log.error("Could not create instance of class " + propertyDatabaseImpl.getCanonicalName(), e);
            return instance == null ? instance = new JDBCPropertyDatabase() : instance;
        }
    }


    /**
     * @param propertyDatabaseImpl the class of the system database 
     * @param lock weather to lock the property database after setting it.
     * @throws IllegalStateException
     */
    public static void setFactoryImpl(Class propertyDatabaseImpl, boolean lock) throws IllegalStateException {
        if (locked) {
            throw new IllegalStateException("System database factory has been locked by another plugin.");
        }
        ProfilesFactory.propertyDatabaseImpl = propertyDatabaseImpl;
        locked = lock;
    }
}
