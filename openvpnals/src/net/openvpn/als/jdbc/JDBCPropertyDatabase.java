package net.openvpn.als.jdbc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cache.Cache;
import org.apache.commons.cache.CacheStat;
import org.apache.commons.cache.MemoryStash;
import org.apache.commons.cache.SimpleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.PropertyClass;
import net.openvpn.als.boot.PropertyClassManager;
import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.boot.SystemProperties;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.properties.DefaultPropertyProfile;
import net.openvpn.als.properties.Property;
import net.openvpn.als.properties.PropertyDatabase;
import net.openvpn.als.properties.PropertyProfile;
import net.openvpn.als.properties.attributes.AbstractAttributeKey;
import net.openvpn.als.properties.attributes.AttributeDefinition;
import net.openvpn.als.properties.attributes.DefaultAttributeDefinition;
import net.openvpn.als.properties.impl.profile.ProfileProperties;
import net.openvpn.als.properties.impl.profile.ProfilePropertyKey;

/**
 * Implementation of a {@link net.openvpn.als.properties.PropertyDatabase} that
 * stores profiles, categories and properties in a JDBC compliant database.
 * <p>
 * To improve performance, the values of the properties themselves are cached in
 * memory.
 * <p>
 * The behaviour of this cache is effected by two Java system properties,
 * <code>openvpnals.jdbcPropertyDatabase.cacheTTL</code> and
 * <code>openvpnals.jdbcPropertyDatabase.cacheMaxObjs</code>
 */

public class JDBCPropertyDatabase implements PropertyDatabase {

    static Log log = LogFactory.getLog(JDBCPropertyDatabase.class);

    final static Long CACHE_TTL = new Long(SystemProperties.get("openvpnals.jdbcPropertyDatabase.cacheTTL", "180000"));
    final static Integer CACHE_MAXOBJS = new Integer(SystemProperties.get("openvpnals.jdbcPropertyDatabase.cacheMaxObjs", "2000"));
    final static Long CACHE_COST = new Long(1);

    // Private instance variables
    private Cache propertyCache;
    private JDBCDatabaseEngine db;

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.Database#open(net.openvpn.als.core.CoreServlet)
     */
    public void open(CoreServlet controllingServlet) throws Exception {
        String dbName = SystemProperties.get("openvpnals.propertyDatabase.jdbc.dbName", "explorer_configuration");
        controllingServlet.addDatabase(dbName, ContextHolder.getContext().getDBDirectory());
        String jdbcUser = SystemProperties.get("openvpnals.jdbc.username", "sa");
        String jdbcPassword = SystemProperties.get("openvpnals.jdbc.password", "");
        String vendorDB = SystemProperties.get("openvpnals.jdbc.vendorClass", "net.openvpn.als.jdbc.hsqldb.HSQLDBDatabaseEngine");

        if (log.isInfoEnabled()) {
            log.info("Property database is being opened...");
            log.info("JDBC vendor class implementation is " + vendorDB);
        }

        db = (JDBCDatabaseEngine) Class.forName(vendorDB).newInstance();
        db.init("propertyDatabase", dbName, jdbcUser, jdbcPassword, null);

        File upgradeDir = new File("install/upgrade");
        DBUpgrader upgrader = new DBUpgrader(ContextHolder.getContext().getVersion(), db, ContextHolder.getContext()
                        .getDBDirectory(), upgradeDir);
        upgrader.upgrade();

        int maxObjs = CACHE_MAXOBJS.intValue();
        propertyCache = new SimpleCache(new MemoryStash(maxObjs));
        loadAttributeDefinitions();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.Database#close()
     */
    public void close() throws Exception {
    	if(propertyCache != null)
    		propertyCache.clear();
    }

    public String retrieveGenericProperty(String key1, String key2, String key3, String key4, String key5) throws Exception {
        String cacheKey = buildPropertyCacheKey(key1, key2, key3, key4, key5);
        String retrieve = (String) propertyCache.retrieve(cacheKey);
        if (propertyCache.contains(cacheKey)) {
            return retrieve;
        }
        
        JDBCPreparedStatement ps = db.getStatement("select.property");
        ps.setString(1, key1);
        ps.setString(2, key2);
        ps.setString(3, key3);
        ps.setString(4, key4);
        ps.setString(5, key5);
        ResultSet rs = ps.executeQuery();
        try {
            String val = rs.next() ? rs.getString("value") : null;
            storeToCache(cacheKey, val);
            return val;
        } finally {
            rs.close();
            ps.releasePreparedStatement();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDatabase#storeProperty(int,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public void storeGenericProperty(String key1, String key2, String key3, String key4, String key5, String value) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("select.property");
        ps.setString(1, key1);
        ps.setString(2, key2);
        ps.setString(3, key3);
        ps.setString(4, key4);
        ps.setString(5, key5);

        ResultSet rs = ps.executeQuery();
        JDBCPreparedStatement ps2;
        try {
            if (!rs.next()) {
                if (log.isDebugEnabled())
                    log.debug("Property doesnt currently exist, inserting value '" + value + "'");

                ps2 = db.getStatement("insert.property");
                ps2.setString(1, key1);
                ps2.setString(2, key2);
                ps2.setString(3, key3);
                ps2.setString(4, key4);
                ps2.setString(5, key5);
                // Insert new property
                ps2.setString(6, value);

                try {
                    ps2.execute();
                } finally {
                    ps2.releasePreparedStatement();
                }
            } else {
                if (log.isDebugEnabled())
                    log.debug("Property exists, updating value '" + value + "'");

                ps2 = db.getStatement("update.property");

                // Insert new property
                ps2.setString(1, value);
                ps2.setString(2, key1);
                ps2.setString(3, key2);
                ps2.setString(4, key3);
                ps2.setString(5, key4);
                ps2.setString(6, key5);
                try {
                    ps2.execute();
                } finally {
                    ps2.releasePreparedStatement();
                }
            }
        } finally {
            rs.close();
            ps.releasePreparedStatement();
        }
        
        final String cacheKey = buildPropertyCacheKey(key1, key2, key3, key4, key5);
        storeToCache(cacheKey, value);
    }

    private static String buildPropertyCacheKey(String key1, String key2, String key3, String key4, String key5) {
        return key1 + "_" + key2 + "_" + key3 + "_" + key4 + "_" + key5;
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.properties.PropertyDatabase#getPropertyProfiles(java.lang.String, boolean, int)
     */
    public List<PropertyProfile> getPropertyProfiles(String username, boolean includeGlobal, int realmID) throws Exception {

        JDBCPreparedStatement ps;
        if (includeGlobal) {
            ps = db.getStatement("select.global.profiles");
        } else {
            ps = db.getStatement("select.profiles");
        }

        try {
            ps.setString(1, username == null ? "" : username);
            ps.setInt(2, realmID);

            ResultSet rs = ps.executeQuery();
            try {

                Set<PropertyProfile> v = new HashSet<PropertyProfile>();
                while (rs.next()) {
                    v.add(buildPropertyProfile(rs));
                }
                return new ArrayList<PropertyProfile>(v);
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDatabase#getPropertyProfile(int)
     */
    public PropertyProfile getPropertyProfile(int id) throws Exception {

        JDBCPreparedStatement ps = db.getStatement("select.profile");
        ResultSet rs = null;
        try {
            ps.setInt(1, id);

            rs = ps.executeQuery();
            if (rs.next()) {
                return buildPropertyProfile(rs);
            }
        } finally {
            if (rs != null)
                rs.close();
            ps.releasePreparedStatement();
        }
        return null;
    }


    /* (non-Javadoc)
     * @see net.openvpn.als.properties.PropertyDatabase#createPropertyProfile(java.lang.String, java.lang.String, java.lang.String, int, int)
     */
    public PropertyProfile createPropertyProfile(String username, String shortName, String description, int baseOn,
                                                 int realmID) throws Exception {

        JDBCPreparedStatement ps = db.getStatement("insert.profile");
        Calendar c = Calendar.getInstance();

        try {
            ps.setString(1, username == null ? "" : username);
            ps.setString(2, shortName);
            ps.setString(3, description);
            ps.setString(4, db.formatTimestamp(c));
            ps.setString(5, db.formatTimestamp(c));
            ps.setInt(6, realmID);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
        int id = db.getLastInsertId(ps, "insert.profile.lastInsertId");
        PropertyProfile profile = new DefaultPropertyProfile(realmID, id, username == null ? "" : username, shortName, description,
                        c, c);
        profile.setResourceName(shortName);
        profile.setResourceDescription(description);

        if (baseOn != -1) {
            for (PropertyDefinition def : PropertyClassManager.getInstance()
                            .getPropertyClass(ProfileProperties.NAME)
                            .getDefinitions()) {
                String val = Property.getProperty(new ProfilePropertyKey(baseOn, username, def.getName(), realmID)); 
                storeGenericProperty(def.getName(), username == null ? "" : username, String.valueOf(id), String.valueOf(realmID), "", val);
            }
        }

        return profile;

    }

    /* (non-Javadoc)
     * @see net.openvpn.als.properties.PropertyDatabase#updatePropertyProfile(int, java.lang.String, java.lang.String)
     */
    public void updatePropertyProfile(int id, String shortName, String description) throws Exception {

        JDBCPreparedStatement ps = db.getStatement("update.profile");

        try {
            ps.setString(1, shortName);
            ps.setString(2, description);
            ps.setString(3, db.formatTimestamp(Calendar.getInstance()));
            ps.setInt(4, id);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDatabase#deletePropertyProfile(int)
     */
    public PropertyProfile deletePropertyProfile(int id) throws Exception {
        PropertyProfile prof = getPropertyProfile(id);
        if (prof == null) {
            throw new Exception("No property profile with " + id + ".");
        }
        propertyCache.clear();
        JDBCPreparedStatement ps = db.getStatement("delete.profile.1");
        ps.setInt(1, id);

        try {
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }

        ps = db.getStatement("delete.profile.2");
        ps.setInt(1, id);

        try {
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
        return prof;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.properties.PropertyDatabase#getPropertyProfile(java.lang.String, java.lang.String, int)
     */
    public PropertyProfile getPropertyProfile(String username, String name, int realmID) throws Exception {

        JDBCPreparedStatement ps = db.getStatement("select.profile.short");
        ResultSet rs = null;

        try {
            ps.setString(1, username == null ? "" : username);
            ps.setString(2, name);
            ps.setInt(3, realmID);

            rs = ps.executeQuery();

            if (rs.next()) {
                return buildPropertyProfile(rs);
            }
            return null;
        } finally {
            if (rs != null)
                rs.close();
            ps.releasePreparedStatement();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.Database#cleanup()
     */
    public void cleanup() throws Exception {
    }

    public void storeAttributeValue(AbstractAttributeKey key, String value) throws Exception {
        // Delete the entry if there is 1.
        JDBCPreparedStatement ps = db.getStatement("storeAttributeValue.delete");
        try {
            ps.setString(1, key.getPropertyClassName());
            ps.setString(2, key.getAttributeClassKey());
            ps.setString(3, key.getName());
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
        // now re-insert the attribute.
        if (value != null) {
            JDBCPreparedStatement ps2 = db.getStatement(ps, "storeAttributeValue.insert");
            try {
                ps2.setString(1, key.getPropertyClassName());
                ps2.setString(2, key.getAttributeClassKey());
                ps2.setString(3, key.getName());
                ps2.setString(4, value);
                ps2.execute();
            } finally {
                ps2.releasePreparedStatement();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.security.UserDatabase#loadAttributes(net.openvpn.als.security.User)
     */
    public String retrieveAttributeValue(AbstractAttributeKey attribute) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("retrieveAttributeValue.select");
        try {
            ps.setString(1, attribute.getAttributeClassKey());
            ps.setString(2, attribute.getPropertyClassName());
            ps.setString(3, attribute.getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("attribute_value");
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDatabase#createAttributeDefinition(net.openvpn.als.properties.AttributeDefinition)
     */
    public void createAttributeDefinition(AttributeDefinition definition) throws Exception {
        if (definition.isSystem()) {
            throw new Exception("System attribute definitions may not be created.");
        }
        JDBCPreparedStatement ps = db.getStatement("createAttributeDefinition.create");
        try {
            ps.setString(1, definition.getName());
            ps.setString(2, definition.getPropertyClass().getName());
            ps.setInt(3, definition.getVisibility());
            ps.setInt(4, definition.getType());
            ps.setInt(5, definition.getSortOrder());
            ps.setString(6, definition.getLabel());
            ps.setString(7, definition.getDescription());
            ps.setString(8, definition.getTypeMeta());
            ps.setInt(9, definition.getCategory());
            ps.setString(10, definition.getCategoryLabel());
            ps.setString(11, definition.getDefaultValue());
            ps.setInt(12, definition.isHidden() ? 1 : 0);
            ps.setString(13, definition.getValidationString());
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDatabase#deleteAttributeDefinition(java.lang.String,
     *      java.lang.String)
     */
    public void deleteAttributeDefinition(String propertyClassName, String definitionName) throws Exception {
        AttributeDefinition def = (AttributeDefinition) PropertyClassManager.getInstance()
                        .getPropertyClass(propertyClassName)
                        .getDefinition(definitionName);
        if (def == null) {
            throw new Exception("Definition with name " + definitionName + " cannot be deleted as it does not exist.");
        }
        if (def.isSystem()) {
            throw new Exception("Definition with name " + definitionName + " cannot be deleted as it is a system definition.");
        }
        JDBCPreparedStatement ps = db.getStatement("deleteAttributeDefinition.delete");
        try {
            ps.setString(1, propertyClassName);
            ps.setString(2, definitionName);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDatabase#updateAttributeDefinition(net.openvpn.als.properties.AttributeDefinition)
     */
    public void updateAttributeDefinition(AttributeDefinition definition) throws Exception {
        if (definition.isSystem()) {
            throw new Exception("System attribute definitions may not be updated.");
        }
        JDBCPreparedStatement ps = db.getStatement("updateAttributeDefinition.update");
        try {
            ps.setInt(1, definition.getVisibility());
            ps.setInt(2, definition.getType());
            ps.setInt(3, definition.getSortOrder());
            ps.setString(4, definition.getLabel());
            ps.setString(5, definition.getDescription());
            ps.setString(6, definition.getTypeMeta());
            ps.setInt(7, definition.getCategory());
            ps.setString(8, definition.getCategoryLabel());
            ps.setString(9, definition.getDefaultValue());
            ps.setInt(10, definition.isHidden() ? 1 : 0);
            ps.setString(11, definition.getValidationString());
            ps.setString(12, definition.getName());
            ps.setString(13, definition.getPropertyClass().getName());
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }

    }

    void loadAttributeDefinitions() throws Exception {
        JDBCPreparedStatement ps = db.getStatement("loadAttributeDefinitions.select");
        ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                String propertyClassName = rs.getString("property_class");
                PropertyClass ua = PropertyClassManager.getInstance().getPropertyClass(propertyClassName);
                if (ua == null) {
                    log.warn("Found user attribute '" + rs.getString("name")
                        + "' with a property class of '"
                        + propertyClassName
                        + "'. This property class does not exist. Perhaps a plugin has been uninstalled?");
                } else {
                    ua.registerPropertyDefinition(new DefaultAttributeDefinition(rs.getInt("type"),
                                    rs.getString("name"),
                                    rs.getString("type_meta"),
                                    rs.getInt("category"),
                                    rs.getString("category_label"),
                                    rs.getString("default_value"),
                                    rs.getInt("visibility"),
                                    rs.getInt("sort_order"),
                                    null,
                                    rs.getInt("hidden") == 1,
                                    rs.getString("text_label"),
                                    rs.getString("text_description"),
                                    false,
                                    true,
                                    rs.getString("validation_string")));
                }
            }
        } finally {
            rs.close();
            ps.releasePreparedStatement();
        }
    }

    PropertyProfile buildPropertyProfile(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        Timestamp cd = rs.getTimestamp("date_created");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(cd == null ? System.currentTimeMillis() : cd.getTime());
        Timestamp ad = rs.getTimestamp("date_amended");
        Calendar a = Calendar.getInstance();
        a.setTimeInMillis(ad == null ? System.currentTimeMillis() : ad.getTime());
        return new DefaultPropertyProfile(rs.getInt("realm_id"), rs.getInt("id"), username.equals("") ? null : username, rs
                        .getString("short_name"), rs.getString("description"), c, a);
    }

    void storeToCache(Serializable key, Serializable object) {
        if (log.isDebugEnabled()) {
            log.debug("Caching under " + key + ", ttl=" + CACHE_TTL + ", cost=" + CACHE_COST);
        }
        
        // NOTE Temporary code to make sure policy objects are serializable, in development and testing
        if ("true".equals(SystemProperties.get("openvpnals.useDevConfig")) | "true".equals(SystemProperties.get("openvpnals.testing"))) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
            } catch (Exception e) {
                String string = "********** Failed to cache policy database object. There is probably a non-serializable object somewhere in the object graph. PLEASE FIX ME ****************";
                System.err
                        .println(string);
                e.printStackTrace();
                throw new RuntimeException(string);
            }
        }
        
        propertyCache.store(key, object, new Long(CACHE_TTL.longValue() + System.currentTimeMillis()), CACHE_COST);
        if (log.isDebugEnabled()) {
            log.debug("NUM_RETRIEVE_REQUESTED " + propertyCache.getStat(CacheStat.NUM_RETRIEVE_REQUESTED));
            log.debug("NUM_RETRIEVE_FOUND " + propertyCache.getStat(CacheStat.NUM_RETRIEVE_FOUND));
            log.debug("NUM_RETRIEVE_NOT_FOUND " + propertyCache.getStat(CacheStat.NUM_RETRIEVE_NOT_FOUND));
            log.debug("NUM_STORE_REQUESTED " + propertyCache.getStat(CacheStat.NUM_STORE_REQUESTED));
            log.debug("NUM_STORE_STORED " + propertyCache.getStat(CacheStat.NUM_STORE_STORED));
            log.debug("NUM_STORE_NOT_STORED " + propertyCache.getStat(CacheStat.NUM_STORE_NOT_STORED));
            log.debug("CUR_CAPACITY " + propertyCache.getStat(CacheStat.CUR_CAPACITY));
        }
    }

}
