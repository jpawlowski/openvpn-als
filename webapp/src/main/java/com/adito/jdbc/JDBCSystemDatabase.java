
				/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package com.adito.jdbc;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.SystemProperties;
import com.adito.boot.Util;
import com.adito.boot.VersionInfo;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreListener;
import com.adito.core.CoreServlet;
import com.adito.navigation.DefaultFavorite;
import com.adito.navigation.Favorite;
import com.adito.security.AuthenticationModuleManager;
import com.adito.security.AuthenticationScheme;
import com.adito.security.DefaultAuthenticationScheme;
import com.adito.security.IpRestriction;
import com.adito.security.SystemDatabase;
import com.adito.security.User;
import com.adito.util.CIDRNetwork;

/**
 * Implementation of a {@link com.adito.security.SystemDatabase} that uses
 * a JDBC compliant database to store Adito's basic configuration and
 * resources.
 */
public class JDBCSystemDatabase implements SystemDatabase, CoreListener {
    private static final Log log = LogFactory.getLog(JDBCSystemDatabase.class);
    private static final String LOCAL_HOST = "localhost";
    private static final String LOCAL_LOOP_BACK_IPV4 = "127.0.0.1";
    private static final String LOCAL_LOOP_BACK_IPV6 = "::1";
    private static final String LOCAL_LOOP_BACK_IPV6_EXT = "0:0:0:0:0:0:0:1";
    
    private final Collection<String> authorizedIPAddresses = new HashSet<String>();
    private final Collection<String> excludedIPAddresses = new HashSet<String>();
    private JDBCDatabaseEngine db;

    /**
     * Constructor
     */
    public JDBCSystemDatabase() {
        initialiseIpRestrictionCache();
    }
    
    /* (non-Javadoc)
     * @see com.adito.core.Database#open(com.adito.core.CoreServlet)
     */
    public void open(CoreServlet controllingServlet) throws Exception {
        String dbName = SystemProperties.get("adito.systemDatabase.jdbc.dbName", "explorer_configuration");
        // PLUNDEN: Removing the context
        // controllingServlet.addDatabase(dbName, ContextHolder.getContext().getDBDirectory());
        controllingServlet.addDatabase(dbName, new File((String)CoreServlet.getServlet().getServletContext().getAttribute("adito.directories.db")));
        // end change
        String jdbcUser = SystemProperties.get("adito.jdbc.username", "sa");
        String jdbcPassword = SystemProperties.get("adito.jdbc.password", "");
        String vendorDB = SystemProperties.get("adito.jdbc.vendorClass", "com.adito.jdbc.hsqldb.HSQLDBDatabaseEngine");

        if (log.isInfoEnabled()) {
        	log.info("System database is being opened...");
        	log.info("JDBC vendor class implementation is " + vendorDB);
        }

        db = (JDBCDatabaseEngine) Class.forName(vendorDB).newInstance();
        db.init("systemDatabase", dbName, jdbcUser, jdbcPassword, null);

        File upgradeDir = new File("install/upgrade");
        // PLUNDEN: Removing the context
		// DBUpgrader upgrader = new DBUpgrader(ContextHolder.getContext()
	            // .getVersion(), db, ContextHolder.getContext().getDBDirectory(), upgradeDir);
        DBUpgrader upgrader = new DBUpgrader(new VersionInfo.Version((String)CoreServlet.getServlet().getServletContext().getAttribute("adito.version")), db, new File(CoreServlet.getServlet().getServletContext().getRealPath("/") + "/WEB-INF/" + SystemProperties.get("adito.directories.db", "db")), upgradeDir);
	    // end change
        upgrader.upgrade();

        CoreServlet.getServlet().addCoreListener(this);
    }

    /*
     * (non-Javadoc)
     * @see com.adito.boot.Database#close()
     */
    public void close() throws Exception {
    }

    /*
     * (non-Javadoc)
     * @see com.adito.boot.Database#cleanup()
     */
    public void cleanup() throws Exception {
    }

    /*
     * (non-Javadoc)
     * @see com.adito.core.CoreListener#coreEvent(com.adito.core.CoreEvent)
     */
    public void coreEvent(CoreEvent evt) {
        if (evt.getId() == CoreEventConstants.USER_REMOVED) {
            User user = (User) evt.getParameter();
            
            // LDP - Fix as null user might be passed?
            if(user==null)
                return;
            
            try {
                removeUser(user.getPrincipalName());
            } catch (Exception e) {
                log.error("Failed to remove user from system database.", e);
            }
        }
    }

    private void initialiseIpRestrictionCache() {
        synchronized (authorizedIPAddresses) {
            authorizedIPAddresses.clear();
            authorizedIPAddresses.addAll(getAlwaysAuthorizedIpAddresses());
            excludedIPAddresses.clear();
        }
    }
    
    private static Collection<String> getAlwaysAuthorizedIpAddresses() {
        String property = SystemProperties.get("adito.iprestrictions.allow", "");
        Collection<String> propertyAsCollection = property.length() == 0 ? Collections.<String>emptyList() : Arrays.asList(property.split(","));
        
        /* BPS - It is possible to spoof localhost, although most firewalls should prevent this happening,
         * do we really want to just allow localhost?
         */
        Collection<String> alwaysAuthorized = new HashSet<String>();
        alwaysAuthorized.add(LOCAL_HOST);
        alwaysAuthorized.add(LOCAL_LOOP_BACK_IPV4);
        alwaysAuthorized.add(LOCAL_LOOP_BACK_IPV6);
        alwaysAuthorized.add(LOCAL_LOOP_BACK_IPV6_EXT);
        alwaysAuthorized.addAll(propertyAsCollection);
        return alwaysAuthorized;
    }
    
    /* (non-Javadoc)
     * @see com.adito.security.SystemDatabase#verifyIPAddress(java.lang.String)
     */
    public boolean verifyIPAddress(String ipAddress) throws Exception {
        // Check if the state of this ip address is cached
        if (authorizedIPAddresses.contains(ipAddress)) {
            return true;
        } else if (excludedIPAddresses.contains(ipAddress)) {
            return false;
        }
        
        IpRestriction[] ipRestrictions = getIpRestrictions();
        return verifyIPAddress(ipAddress, ipRestrictions);
    }
    
    /* (non-Javadoc)
     * @see com.adito.security.SystemDatabase#verifyIPAddress(java.lang.String, com.adito.security.IpRestriction[])
     */
    public boolean verifyIPAddress(String ipAddress, IpRestriction[] ipRestrictions) throws Exception {
        synchronized (authorizedIPAddresses) {
            boolean isValid = isAddressValid(ipAddress, ipRestrictions);
            if (isValid) { // Cache IP addresses to stop us doing too many lookups
                authorizedIPAddresses.add(ipAddress);
            } else {
                excludedIPAddresses.add(ipAddress);
            }
            return isValid;
        }
    }

    /**
     * @param ipAddress
     * @param ipRestrictions
     * @return boolean
     * @throws Exception
     */
    private boolean isAddressValid(String ipAddress, IpRestriction[] ipRestrictions) throws Exception {
        /* BPS - It is possible to spoof localhost, although most firewalls should prevent this happening,
         * do we really want to just allow localhost?
         */
         
        if (LOCAL_LOOP_BACK_IPV4.equals(ipAddress) || LOCAL_LOOP_BACK_IPV6.equals(ipAddress) || LOCAL_LOOP_BACK_IPV6_EXT.equals(ipAddress) || LOCAL_HOST.equalsIgnoreCase(ipAddress)) {
            return true;
        }
        
        if (ipAddress.length() == 0) {
            return true;
        }
        
        // No restrictions at all. Allow. Should only happen on bad upgrades
        if(ipRestrictions.length == 0) {
            return true;
        }
        
        /* Get the last matching state. We use the IpRestriction type
           constants as a convenience while iterating over the 
           restrictions, nothing more */ 
        int state = -1;
        for(int i = 0 ; i < ipRestrictions.length; i++) {
            if(matchesAddress(ipRestrictions[i], ipAddress)) {
                state = ipRestrictions[i].getAllowed() ? IpRestriction.ALLOWED : IpRestriction.DENIED;
            }
        }
        return state == IpRestriction.ALLOWED;
    }

    /**
     * @param restriction
     * @param ipAddress
     * @return boolean
     */
    private boolean matchesAddress(IpRestriction restriction, String ipAddress) {
        String ipRestrictionAddress = restriction.getAddress();

        if (isIpAddressMatch(ipAddress, ipRestrictionAddress)) {
            return true;
        } else if (restriction.isWildcardMatch() && isIpAddressWildcardMatch(ipAddress, ipRestrictionAddress)) {
            return true;
        } else if (isCIDRMatch(ipAddress, ipRestrictionAddress)) {
            return true;
        }

        return false;
    }

    /**
     * @param toCheck
     * @param ipRestrictionAddress
     * @return boolean
     */
    private static boolean isIpAddressMatch(String toCheck, String ipRestrictionAddress) {
        return toCheck.equals(ipRestrictionAddress);
    }

    /**
     * @param toCheck
     * @param ipRestrictionAddress
     * @return boolean
     */
    private static boolean isIpAddressWildcardMatch(String toCheck, String ipRestrictionAddress) {
        String regex = Util.parseSimplePatternToRegExp(ipRestrictionAddress);
        return Pattern.matches(regex, toCheck);
    }

    /**
     * @param toCheck
     * @param ipRestrictionAddress
     * @return boolean
     */
    private static boolean isCIDRMatch(String toCheck, String ipRestrictionAddress) {
        try {
            CIDRNetwork network = new CIDRNetwork(ipRestrictionAddress);
            return network.isValidAddressForNetwork(toCheck);
        } catch (Exception e) {
            return false;
        }
    }

    public void addIpRestriction(String addressPattern, int type) throws Exception {
        IpRestriction[] r = getIpRestrictions();
        int priority = ( r == null || r.length == 0 ? 10 : r[r.length - 1].getPriority() ) + 10;        
        JDBCPreparedStatement ps = db.getStatement("addIpRestriction.insert");
        try {
            ps.setString(1, addressPattern);
            ps.setInt(2, type);
            ps.setInt(3, priority);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
            initialiseIpRestrictionCache();
        }
    }
    
    /* (non-Javadoc)
     * @see com.adito.security.SystemDatabase#updateIpRestriction(com.adito.security.IpRestriction)
     */
    public void updateIpRestriction(IpRestriction restriction) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("updateIpRestriction.update");
        try {
            ps.setString(1, restriction.getAddress());
            ps.setInt(2, restriction.getType());
            ps.setInt(3, restriction.getPriority());
            ps.setInt(4, restriction.getID());
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
            initialiseIpRestrictionCache();
        }
    }
    
    /* (non-Javadoc)
     * @see com.adito.security.SystemDatabase#swapIpRestrictions(com.adito.security.IpRestriction, com.adito.security.IpRestriction)
     */
    public void swapIpRestrictions(IpRestriction restriction1, IpRestriction restriction2) throws Exception {
        if(restriction1.isDefault() || restriction2.isDefault()) {
            throw new IllegalArgumentException("You may not move the default Ip restriction.");
        }
        int priority2 = restriction2.getPriority();
        restriction2.setPriority(restriction1.getPriority());
        restriction1.setPriority(priority2);
        updateIpRestriction(restriction1);
        updateIpRestriction(restriction2);
    }

    /* (non-Javadoc)
     * @see com.adito.security.SystemDatabase#getIpRestrictions()
     */
    public IpRestriction[] getIpRestrictions() throws Exception {
        JDBCPreparedStatement statement = db.getStatement("getIpRestrictions.select");
        Collection<IpRestriction> restrictions = new ArrayList<IpRestriction>();
        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                restrictions.add(buildIpRestriction(resultSet));
            }
        } finally {
            JDBCUtil.cleanup(resultSet);
            statement.releasePreparedStatement();
        }

        return restrictions.toArray(new IpRestriction[restrictions.size()]);
    }

    /* (non-Javadoc)
     * @see com.adito.security.SystemDatabase#getIpRestriction(int)
     */
    public IpRestriction getIpRestriction(int id) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getIpRestriction.select");
        ps.setInt(1, id);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return buildIpRestriction(rs);
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return null;
    }

    /**
     * @param resultSet
     * @return IpRestriction
     * @throws SQLException
     */
    private static IpRestriction buildIpRestriction(ResultSet resultSet) throws SQLException {
        return new IpRestriction(resultSet.getInt("restriction_id"), resultSet.getString("address"), resultSet.getInt("type"),
            resultSet.getInt("priority"));
    }

    /*
     * (non-Javadoc)
     * @see com.adito.security.SystemDatabase#removeIpRestriction(int)
     */
    public void removeIpRestriction(int id) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("removeIpRestriction.delete");
        try {
            ps.setInt(1, id);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
            initialiseIpRestrictionCache();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.adito.security.SystemDatabase#addFavorite(int, int, java.lang.String)
     */
    public void addFavorite(int type, int favoriteKey, String username) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("addFavorite.insert");
        try {
            ps.setInt(1, type);
            ps.setString(2, username == null ? "" : username);
            ps.setInt(3, favoriteKey);
            ps.execute();
            ps.reset();
        } finally {
            ps.releasePreparedStatement();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.adito.security.SystemDatabase#removeFavorite(int, int, java.lang.String)
     */
    public void removeFavorite(int type, int favoriteKey, String username) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("removeFavorite.delete");
        try {
            ps.setInt(1, type);
            ps.setString(2, username == null ? "" : username);
            ps.setInt(3, favoriteKey);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }

    }

    /* (non-Javadoc)
     * @see com.adito.security.SystemDatabase#getFavorites(int, com.adito.security.User)
     */
    public List<Favorite> getFavorites(int type, User user) throws Exception {
        JDBCPreparedStatement ps = null;
        if (type == -1) {
            ps = db.getStatement("getFavorites.selectAllForUser");
            ps.setString(1, user == null ? "" : user.getPrincipalName());
        } else {
            ps = db.getStatement("getFavorites.selectTypeForUser");
            ps.setString(1, user == null ? "" : user.getPrincipalName());
            ps.setInt(2, type);
        }
        try {
            Vector<Favorite> v = new Vector<Favorite>();
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {

                    v.add(new DefaultFavorite(rs.getInt("favorite_id"), type, user == null ? null : user.getPrincipalName(), rs.getInt("favorite_key")));
                }
            } finally {
                rs.close();
            }
            return v;
        } finally {
            ps.releasePreparedStatement();
        }

    }

    /*
     * (non-Javadoc)
     * @see com.adito.security.SystemDatabase#getFavorite(int, com.adito.security.User, java.lang.String)
     */
    public Favorite getFavorite(int type, User user, int resourceId) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getFavorite.select");
        ps.setString(1, user == null ? "" : user.getPrincipalName());
        ps.setInt(2, type);
        ps.setInt(3, resourceId);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return new DefaultFavorite(rs.getInt("favorite_id"), type, user == null ? null : user.getPrincipalName(), rs.getInt("favorite_key"));
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return null;
    }

    // Authentication Schemes

    /*
     * (non-Javadoc)
     * @see com.adito.security.SystemDatabase#getAuthenticationSchemeSequences()
     */
    public List<AuthenticationScheme> getAuthenticationSchemeSequences() throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getAuthenticationSchemeSequences.select");
        try {
            ResultSet rs = ps.executeQuery();
            try {
                List<AuthenticationScheme> sequences = new ArrayList<AuthenticationScheme>();
                while (rs.next()) {
                    AuthenticationScheme sequence = buildAuthenticationSchemeSequence(rs);
                    if (sequence != null) {
                        sequences.add(sequence);
                    }
                }
                return sequences;
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.adito.security.SystemDatabase#getAuthenticationSchemeSequence(int)
     */
    public AuthenticationScheme getAuthenticationSchemeSequence(int id) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getAuthenticationSchemeSequence.select");
        ResultSet rs = null;
        try {
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return buildAuthenticationSchemeSequence(rs);
            }
        } finally {
            JDBCUtil.cleanup(rs);
            ps.releasePreparedStatement();
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see com.adito.security.SystemDatabase#getAuthenticationSchemeSequence(java.lang.String, int)
     */
    public AuthenticationScheme getAuthenticationSchemeSequence(String name, int realmID) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getAuthenticationSchemeSequence.select.byName");
        ResultSet rs = null;
        try {
            ps.setInt(1, realmID);
            ps.setString(2, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                return buildAuthenticationSchemeSequence(rs);
            }
        } finally {
            JDBCUtil.cleanup(rs);
            ps.releasePreparedStatement();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.adito.security.SystemDatabase#createAuthenticationSchemeSequence(int, java.lang.String, java.lang.String, java.lang.String[], boolean, int)
     */
    public AuthenticationScheme createAuthenticationSchemeSequence(int realmID, String name, String description, String[] modules, boolean enabled, int priority) throws Exception {
        Calendar calendar = Calendar.getInstance();
        String timestamp = db.formatTimestamp(calendar);
        JDBCPreparedStatement ps = db.getStatement("createAuthenticationSchemeSequence.insert");
        try {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, timestamp);
            ps.setString(4, timestamp);
            ps.setInt(5, enabled ? 1 : 0);
            ps.setInt(6, priority);
            ps.setInt(7, realmID);
            ps.execute();
            int id = db.getLastInsertId(ps, "createAuthenticationSchemeSequence.lastInsertId");
            AuthenticationScheme sequence = getAuthenticationSchemeSequence(id);
            updateAuthenticationSequence(sequence, modules);
            return sequence;
        } finally {
            ps.releasePreparedStatement();
        }
    }

    private void updateAuthenticationSequence(AuthenticationScheme sequence, String[] modules) throws Exception {
        for (int index = 0; index < modules.length; index++) {
            sequence.addModule(modules[index]);
        }
        updateSequence(sequence);
    }

    /*
     * (non-Javadoc)
     * @see com.adito.security.SystemDatabase#updateAuthenticationSchemeSequence(com.adito.security.AuthenticationScheme)
     */
    public void updateAuthenticationSchemeSequence(AuthenticationScheme sequence) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("updateAuthenticationSchemeSequence.update");
        try {
            ps.setString(1, sequence.getResourceName());
            ps.setString(2, sequence.getResourceDescription());
            Calendar now = Calendar.getInstance();
            ps.setString(3, db.formatTimestamp(now));
            ps.setInt(4, sequence.getEnabled() ? 1 : 0);
            ps.setInt(5, sequence.getResourceId());
            ps.execute();
            updateSequence(sequence);
        } finally {
            ps.releasePreparedStatement();
        }
    }

    /**
     * @param sequence
     * @throws Exception
     */
    private void updateSequence(AuthenticationScheme sequence) throws Exception {
        deleteAuthenticationSequence(sequence.getResourceId());
        updateAuthentictionSequence(sequence);
    }

    private void updateAuthentictionSequence(AuthenticationScheme sequence) throws Exception {
        JDBCPreparedStatement statement = db.getStatement("updateSequence.insert");
        try {
            int seq = 0;
            for (Iterator itr = sequence.modules(); itr.hasNext();) {
                String module = (String) itr.next();
                statement.setInt(1, sequence.getResourceId());
                statement.setString(2, module);
                statement.setInt(3, seq);
                statement.execute();
                seq += 10;
                statement.reset();
            }
        } finally {
            statement.releasePreparedStatement();
        }
    }
    
    /* (non-Javadoc)
     * @see com.adito.security.SystemDatabase#moveAuthenticationSchemeUp(com.adito.security.AuthenticationScheme, java.util.List)
     */
    public void moveAuthenticationSchemeUp(AuthenticationScheme scheme, List<AuthenticationScheme> schemes) throws Exception {
        int indexOf = schemes.indexOf(scheme);
        if (indexOf == 0)
            throw new IllegalStateException("Scheme is already set to highest priority");

        int schemeAboveIndex = indexOf - 1;
        AuthenticationScheme schemeAbove = schemes.get(schemeAboveIndex);
        updateAuthenticationSchemePriority(scheme, schemeAbove.getPriorityInt());
        updateAuthenticationSchemePriority(schemeAbove, scheme.getPriorityInt());
    }

    /* (non-Javadoc)
     * @see com.adito.security.SystemDatabase#moveAuthenticationSchemeDown(com.adito.security.AuthenticationScheme, java.util.List)
     */
    public void moveAuthenticationSchemeDown(AuthenticationScheme scheme, List<AuthenticationScheme> schemes) throws Exception {
        int indexOf = schemes.indexOf(scheme);
        if (indexOf == schemes.size() - 1)
            throw new IllegalStateException("Scheme is already set to lowest priority");

        int schemeBelowIndex = indexOf + 1;
        AuthenticationScheme schemeBelow = schemes.get(schemeBelowIndex);
        updateAuthenticationSchemePriority(scheme, schemeBelow.getPriorityInt());
        updateAuthenticationSchemePriority(schemeBelow, scheme.getPriorityInt());
    }

    private void updateAuthenticationSchemePriority(AuthenticationScheme scheme, int priority) throws Exception  {
        JDBCPreparedStatement ps = db.getStatement("updateAuthenticationSchemeSequence.update.priority");
        try {
            ps.setInt(1, priority);
            ps.setInt(2, scheme.getResourceId());
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.adito.security.SystemDatabase#deleteAuthenticationSchemeSequence(java.lang.String)
     */
    public void deleteAuthenticationSchemeSequence(int id) throws Exception {
        AuthenticationScheme scheme = getAuthenticationSchemeSequence(id);
        if (scheme == null) {
            throw new Exception("No authentication scheme with " + id + ".");
        }
        deleteAuthenticationScheme(id);
        deleteAuthenticationSequence(id);
    }

    private void deleteAuthenticationScheme(int id) throws SQLException, ClassNotFoundException {
        JDBCPreparedStatement ps = db.getStatement("deleteAuthenticationSchemeSequence.delete.authSchemes");
        try {
            ps.setInt(1, id);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
    }

    private void deleteAuthenticationSequence(int id) throws SQLException, ClassNotFoundException {
        JDBCPreparedStatement ps;
        ps = db.getStatement("deleteAuthenticationSchemeSequence.delete.authSequence");
        try {
            ps.setInt(1, id);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
    }

    /**
     * @param resultSet
     * @return AuthenticationScheme
     * @throws Exception
     */
    private AuthenticationScheme buildAuthenticationSchemeSequence(ResultSet resultSet) throws Exception {
        Calendar created = JDBCUtil.getCalendar(resultSet, "date_created");
        Calendar amended = JDBCUtil.getCalendar(resultSet, "date_amended");
        boolean enabled = resultSet.getInt("enabled") == 1;
        AuthenticationScheme seq = new DefaultAuthenticationScheme(resultSet.getInt("realm_id"), resultSet.getInt("resource_id"),
                        resultSet.getString("resource_name"), resultSet.getString("resource_description"), created, amended,
                        enabled, resultSet.getInt("priority"));
        return isSchemeAvailable(seq) ? seq : null;
    }
    
    private boolean isSchemeAvailable(AuthenticationScheme scheme) throws Exception {
        JDBCPreparedStatement statement = db.getStatement("buildAuthenticationSchemeSequences.select.scheme");
        ResultSet resultSet = null;
        boolean available = true;
        try {
            statement.setInt(1, scheme.getResourceId());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String moduleId = resultSet.getString("module_id");
                if (!AuthenticationModuleManager.getInstance().isRegistered(moduleId)) {
                    available = false;
                }
                scheme.addModule(moduleId);
            }
        } finally {
            JDBCUtil.cleanup(resultSet);
            statement.releasePreparedStatement();
        }
        return available;
    }
    
    /**
     * @param username
     * @throws Exception
     */
    protected void removeUser(String username) throws Exception {
        String[] statements = { "removeUser.delete.explorerProperties",
                        "removeUser.delete.propertyProfiles"};
        for (int i = 0; i < statements.length; i++) {
            JDBCPreparedStatement ps = db.getStatement(statements[i]);
            try {
                ps.setString(1, username);
                ps.execute();
            } finally {
                ps.releasePreparedStatement();
            }
        }
    }

    public List<AuthenticationScheme> getAuthenticationSchemeSequences(int realmID) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getAuthenticationSchemeSequences.realm.select");
        try {
            ps.setInt(1, realmID);
            ResultSet rs = ps.executeQuery();
            try {
                List<AuthenticationScheme> sequences = new ArrayList<AuthenticationScheme>();
                while (rs.next()) {
                    AuthenticationScheme sequence = buildAuthenticationSchemeSequence(rs);
                    if (sequence != null) {
                        sequences.add(sequence);
                    }
                }
                return sequences;
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
    }
}