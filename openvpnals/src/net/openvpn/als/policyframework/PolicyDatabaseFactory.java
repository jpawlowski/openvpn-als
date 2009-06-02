package net.openvpn.als.policyframework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.jdbc.JDBCPolicyDatabase;

/**
 * Policy factory for creating policy databases.
 */
public class PolicyDatabaseFactory {
    static Log log = LogFactory.getLog(PolicyDatabaseFactory.class);

    static PolicyDatabase instance;
    static Class policyDatabaseImpl = JDBCPolicyDatabase.class;
    private static boolean locked = false;

    /**
     * @return An instance of the policy database factory.
     */
    public static PolicyDatabase getInstance() {
        try {
            return instance == null ? instance = (PolicyDatabase) policyDatabaseImpl.newInstance() : instance;
        } catch (Exception e) {
            log.error("Could not create instance of class " + policyDatabaseImpl.getCanonicalName(), e);
            return instance == null ? instance = new JDBCPolicyDatabase() : instance;
        }
    }


    /**
     * @param policyFactoryImpl the class of the policy database
     * @param lock weather to lock the policy database after setting it.
     * @throws IllegalStateException
     */
    public static void setFactoryImpl(Class policyDatabaseImpl, boolean lock) throws IllegalStateException {
        if (locked) {
            throw new IllegalStateException("Policy database factory has been locked by another plugin.");
        }
        PolicyDatabaseFactory.policyDatabaseImpl = policyDatabaseImpl;
        locked = lock;
    }
}
