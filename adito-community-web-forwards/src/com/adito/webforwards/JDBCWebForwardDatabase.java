
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
			
package com.adito.webforwards;

import java.io.File;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.cache.Cache;
import org.apache.commons.cache.MemoryStash;
import org.apache.commons.cache.SimpleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.http.HttpAuthenticatorFactory;
import com.adito.boot.ContextHolder;
import com.adito.boot.PropertyList;
import com.adito.boot.SystemProperties;
import com.adito.boot.Util;
import com.adito.core.CoreEvent;
import com.adito.core.CoreListener;
import com.adito.core.CoreServlet;
import com.adito.extensions.store.ExtensionStore;
import com.adito.extensions.types.PluginDefinition;
import com.adito.jdbc.DBUpgrader;
import com.adito.jdbc.JDBCDatabaseEngine;
import com.adito.jdbc.JDBCPreparedStatement;
import com.adito.jdbc.JDBCUtil;
import com.adito.replacementproxy.DefaultReplacement;
import com.adito.replacementproxy.Replacement;
import com.adito.security.User;

/**
 * Implementation of a {@link com.adito.security.SystemDatabase} that uses
 * a JDBC compliant database to store Adito's web forward configuration
 * and resources.
 */
public class JDBCWebForwardDatabase implements WebForwardDatabase, CoreListener {
    private static final Log log = LogFactory.getLog(JDBCWebForwardDatabase.class);

    private JDBCDatabaseEngine db;
    private Cache replacementsCache;

    /**
     * Constructor
     */
    public JDBCWebForwardDatabase() {
        int maxObjects = 1000;
        try {
            maxObjects = Integer.parseInt(SystemProperties.get("adito.jdbcSystemDatabase.replacementsCache", "10000"));
        } catch (Exception e) {
        }
        replacementsCache = new SimpleCache(new MemoryStash(maxObjects));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.boot.Database#close()
     */
    public void close() throws Exception {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.Database#open(com.adito.core.CoreServlet)
     */
    public void open(CoreServlet controllingServlet) throws Exception {
        throw new Exception("Plugin databases need a PluginDefinition.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.boot.Database#cleanup()
     */
    public void cleanup() throws Exception {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.CoreListener#coreEvent(com.adito.core.CoreEvent)
     */
    public void coreEvent(CoreEvent evt) {
        // The removeUser() method is not user anymore
//        if (evt.getId() == CoreEventConstants.USER_REMOVED) {
//            User user = (User) evt.getParameter();
//
//            // LDP - Fix as null user might be passed?
//            if (user == null)
//                return;
//
//            try {
//                removeUser(user.getPrincipalName());
//            } catch (Exception e) {
//                log.error("Failed to remove user from system database.", e);
//            }
//        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.webforwards.WebForwardDatabase#getReverseProxyWebForward(com.adito.security.User,
     *      java.lang.String)
     */
    public WebForward getReverseProxyWebForward(User user, String pathInContext) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getReverseProxyWebForward.select");
        ps.setString(1, "");
        ps.setString(2, user == null ? "" : user.getPrincipalName());
        ps.setString(3, pathInContext);

        try {
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return getWebForward(rs.getInt("webforward_id"));
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.webforwards.WebForwardDatabase#getWebForward(int)
     */
    public WebForward getWebForward(int id) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getWebForward.selectById");
        ps.setInt(1, id);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return buildWebForward(rs);
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.webforwards.WebForwardDatabase#getWebForwards(int)
     */
    public List<WebForward> getWebForwards(int realmID) throws Exception {
        JDBCPreparedStatement ps = null;
        ps = db.getStatement("getWebForwards.realm.select");
        ps.setInt(1, realmID);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                List<WebForward> v = new ArrayList<WebForward>();
                while (rs.next()) {
                    v.add(buildWebForward(rs));
                }
                return v;
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        } finally {
            ps.releasePreparedStatement();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.webforwards.WebForwardDatabase#getWebForwards()
     */
    public List<WebForward> getWebForwards() throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getWebForwards.select.allTypes");
        try {
            ResultSet rs = ps.executeQuery();
            try {
                List<WebForward> v = new ArrayList<WebForward>();
                while (rs.next()) {
                    v.add(buildWebForward(rs));
                }
                return v;
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        } finally {
            ps.releasePreparedStatement();
        }
    }

    /* (non-Javadoc)
     * @see com.adito.webforwards.WebForwardDatabase#getWebForward(java.lang.String, int)
     */
    public WebForward getWebForward(String name, int realmID) throws Exception {
        JDBCPreparedStatement ps = null;
        try {
            ps = db.getStatement("getWebForward.select.name");
            ps.setString(1, name);
            ps.setInt(2, realmID);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return buildWebForward(rs);
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.webforwards.WebForwardDatabase#reverseProxyPathExists(java.lang.String)
     */
    public boolean reverseProxyPathExists(String path) throws Exception {
        JDBCPreparedStatement ps = null;
        try {
            ps = db.getStatement("createWebForward.reverseProxy.path.exists");
            ps.setString(1, path);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return true;
                } else {
                    return false;
                }
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
     * @see com.adito.webforwards.WebForwardDatabase#reverseProxyPathExists(java.lang.String,
     *      int)
     */
    public boolean reverseProxyPathExists(String path, int webforward_id) throws Exception {
        JDBCPreparedStatement ps = null;
        try {
            ps = db.getStatement("createWebForward.reverseProxy.path.already.exists");
            ps.setString(1, path);
            ps.setInt(2, webforward_id);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return true;
                } else {
                    return false;
                }
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
     * @see com.adito.webforwards.WebForwardDatabase#createWebForward(com.adito.webforwards.WebForward)
     */
    public WebForward createWebForward(WebForward webForward) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("createWebForward.insert");

        /*
         * For this method, we could get errors when inserting proxy paths (if
         * path already exists). To deal with this the whole operation is run as
         * a transaction.
         */
        ps.startTransaction();

        try {
            int id = -1;
            ps.setString(1, webForward.getDestinationURL());
            ps.setInt(2, webForward.getType());
            ps.setString(3, webForward.getResourceName());
            ps.setString(4, webForward.getResourceDescription());
            ps.setString(5, webForward.getCategory());
            ps.setInt(6, webForward.isAutoStart() ? 1 : 0);
            Calendar now = Calendar.getInstance();
            ps.setString(7, db.formatTimestamp(now));
            ps.setString(8, db.formatTimestamp(now));
            ps.setInt(9, webForward.getRealmID());
            ps.execute();
            webForward.setResourceId(id = db.getLastInsertId(ps, "createWebForward.lastInsertId"));

            if (webForward instanceof ReverseProxyWebForward) {
                ps = db.getStatement(ps, "createWebForward.reverseProxy.insert");
                StringTokenizer t = new StringTokenizer(((ReverseProxyWebForward) webForward).getPaths(), "\n\r");
                while (t.hasMoreTokens()) {
                    String path = t.nextToken();
                    ps.setString(1, path);
                    ps.setInt(2, id);
                    ps.execute();
                    ps.reset();
                }
                ps = db.getStatement(ps, "createWebForward.reverseProxyOptions.insert");
                ps.setInt(1, webForward.getResourceId());
                ps.setString(2, ((ReverseProxyWebForward) webForward).getAuthenticationUsername());
                ps.setString(3, Util.emptyWhenNull(((ReverseProxyWebForward) webForward).getAuthenticationPassword()));
                ps.setString(4, ((ReverseProxyWebForward) webForward).getPreferredAuthenticationScheme());
                ps.setInt(5, ((ReverseProxyWebForward) webForward).getActiveDNS() ? 1 : 0);
                ps.setString(6, ((ReverseProxyWebForward) webForward).getHostHeader());
                ps.setString(7, ((ReverseProxyWebForward) webForward).getFormType());
                ps.setString(8, ((ReverseProxyWebForward) webForward).getFormParameters());
                ps.setString(9, ((ReverseProxyWebForward) webForward).getCharset());
                ps.execute();
            }

            if (webForward instanceof ReplacementProxyWebForward) {
                ps = db.getStatement(ps, "createWebForward.replacementProxyOptions.insert");
                ps.setInt(1, webForward.getResourceId());
                ps.setString(2, ((ReplacementProxyWebForward) webForward).getAuthenticationUsername());
                ps.setString(3, Util.emptyWhenNull(((ReplacementProxyWebForward) webForward).getAuthenticationPassword()));
                ps.setString(4, ((ReplacementProxyWebForward) webForward).getPreferredAuthenticationScheme());
                ps.setString(5, ((ReplacementProxyWebForward) webForward).getEncoding());
                ps.setString(6, ((ReplacementProxyWebForward) webForward).getRestrictToHosts().getAsPropertyText());
                ps.setString(7, ((ReplacementProxyWebForward) webForward).getFormType());
                ps.setString(8, ((ReplacementProxyWebForward) webForward).getFormParameters());
                ps.execute();
            }

            ps.commit();
        } catch (Exception e) {
            ps.rollback();
            throw e;
        } finally {
            ps.releasePreparedStatement();
            ps.endTransaction();
        }
        return getWebForward(webForward.getResourceId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.webforwards.WebForwardDatabase#updateWebForward(com.adito.webforwards.WebForward)
     */
    public void updateWebForward(WebForward webForward) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("updateWebForward.update");

        /*
         * For this method, we could get errors when inserting proxy paths (if
         * path already exists). To deal with this the whole operation is run as
         * a transaction.
         */
        ps.startTransaction();

        try {

            ps.setInt(1, webForward.getType());
            ps.setString(2, webForward.getResourceName());
            ps.setString(3, webForward.getDestinationURL());
            ps.setString(4, webForward.getResourceDescription());
            ps.setString(5, webForward.getCategory());
            ps.setInt(6, webForward.isAutoStart() ? 1 : 0);
            Calendar c = Calendar.getInstance();
            ps.setString(7, db.formatTimestamp(c));
            ps.setInt(8, webForward.getResourceId());
            ps.execute();

            if (webForward instanceof ReverseProxyWebForward) {
                ps = db.getStatement(ps, "updateWebForward.reverseProxy.delete");
                ps.setInt(1, webForward.getResourceId());
                ps.execute();
                ps = db.getStatement(ps, "updateWebForward.reverseProxy.insert");
                StringTokenizer t = new StringTokenizer(((ReverseProxyWebForward) webForward).getPaths(), "\n\r");
                while (t.hasMoreTokens()) {
                    String path = t.nextToken();
                    ps.setString(1, path);
                    ps.setInt(2, webForward.getResourceId());
                    ps.execute();
                    ps.reset();
                }
                ps = db.getStatement(ps, "updateWebForward.reverseProxyOptions.update");
                ps.setString(1, ((ReverseProxyWebForward) webForward).getAuthenticationUsername());
                ps.setString(2, ((ReverseProxyWebForward) webForward).getAuthenticationPassword());
                ps.setString(3, ((ReverseProxyWebForward) webForward).getPreferredAuthenticationScheme());
                ps.setInt(4, ((ReverseProxyWebForward) webForward).getActiveDNS() ? 1 : 0);
                ps.setString(5, ((ReverseProxyWebForward) webForward).getHostHeader());
                ps.setString(6, ((ReverseProxyWebForward) webForward).getFormType());
                ps.setString(7, ((ReverseProxyWebForward) webForward).getFormParameters());
                ps.setString(8, ((ReverseProxyWebForward) webForward).getCharset());
                ps.setInt(9, webForward.getResourceId());

                ps.execute();
            }

            if (webForward instanceof ReplacementProxyWebForward) {
                ps = db.getStatement(ps, "updateWebForward.replacementProxyOptions.update");
                ps.setString(1, ((ReplacementProxyWebForward) webForward).getEncoding());
                ps.setString(2, ((ReplacementProxyWebForward) webForward).getRestrictToHosts().getAsPropertyText());
                ps.setString(3, ((ReplacementProxyWebForward) webForward).getAuthenticationUsername());
                ps.setString(4, ((ReplacementProxyWebForward) webForward).getAuthenticationPassword());
                ps.setString(5, ((ReplacementProxyWebForward) webForward).getPreferredAuthenticationScheme());
                ps.setString(6, ((ReplacementProxyWebForward) webForward).getFormType());
                ps.setString(7, ((ReplacementProxyWebForward) webForward).getFormParameters());
                ps.setInt(8, webForward.getResourceId());
                ps.execute();
            }

            ps.commit();
        } catch (Exception e) {
            ps.rollback();
            throw e;
        } finally {
            ps.releasePreparedStatement();
            ps.endTransaction();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.webforwards.WebForwardDatabase#deleteWebForward(int)
     */
    public WebForward deleteWebForward(int webForwardId) throws Exception {
        WebForward wf = getWebForward(webForwardId);
        if (wf == null) {
            throw new Exception("No web forward with id of " + webForwardId);
        }
        JDBCPreparedStatement ps = db.getStatement("deleteWebForward.delete.favorites");
        try {
            ps.setInt(1, WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE_ID);
            ps.setString(2, String.valueOf(webForwardId));
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
        ps = db.getStatement("deleteWebForward.delete.webForward");
        try {
            ps.setInt(1, webForwardId);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
        if (wf.getType() == WebForward.TYPE_REPLACEMENT_PROXY) {
            ps = db.getStatement("deleteWebForward.delete.replacementProxy.options");
            try {
                ps.setInt(1, wf.getResourceId());
                ps.execute();
            } finally {
                ps.releasePreparedStatement();
            }
        }
        if (wf.getType() == WebForward.TYPE_PATH_BASED_REVERSE_PROXY || wf.getType() == WebForward.TYPE_HOST_BASED_REVERSE_PROXY) {
            ps = db.getStatement("deleteWebForward.delete.reverseProxy");
            try {
                ps.setInt(1, wf.getResourceId());
                ps.execute();
            } finally {
                ps.releasePreparedStatement();
            }
            ps = db.getStatement("deleteWebForward.delete.reverseProxy.options");
            try {
                ps.setInt(1, wf.getResourceId());
                ps.execute();
            } finally {
                ps.releasePreparedStatement();
            }
        }
        return wf;
    }

    /**
     * @param rs
     * @return WebForward
     * @throws Exception
     */
    WebForward buildWebForward(ResultSet rs) throws Exception {
        int id = rs.getInt("id");
        int type = rs.getInt("type");
        String category = rs.getString("category");
        String name = rs.getString("short_name");
        String description = rs.getString("description");
        String url = rs.getString("destination_url");
        boolean autoStart = rs.getBoolean("auto_start");
        Calendar created = JDBCUtil.getCalendar(rs, "date_created");
        Calendar amended = JDBCUtil.getCalendar(rs, "date_amended");
        int realmID = rs.getInt("realm_id");
        
        if (type == WebForward.TYPE_PATH_BASED_REVERSE_PROXY || type == WebForward.TYPE_HOST_BASED_REVERSE_PROXY) {
            JDBCPreparedStatement ps2 = db.getStatement("getWebForward.reverseProxy.selectById");
            try {
                ps2.setInt(1, id);
                ResultSet rs2 = ps2.executeQuery();
                try {
                    StringBuffer paths = new StringBuffer();
                    String hostHeader = "";
                    while (rs2.next()) {
                        if (paths.length() > 0) {
                            paths.append('\n');
                        }
                        paths.append(rs2.getString("path"));
                    }
                    JDBCPreparedStatement ps3 = db.getStatement("getWebForward.reverseProxyOptions.selectById");
                    try {
                        ps3.setInt(1, id);
                        ResultSet rs3 = ps3.executeQuery();
                        try {
                            String authUsername = "";
                            String authPassword = "";
                            String preferredAuthScheme = HttpAuthenticatorFactory.BASIC;
                            boolean activeDNS = false;
                            String formType = "";
                            String formParameters = "";
                            String charset = null;
                            if (rs3.next()) {
                                authUsername = rs3.getString("authentication_username");
                                authPassword = rs3.getString("authentication_password");
                                preferredAuthScheme = rs3.getString("preferred_authentication_scheme");
                                activeDNS = rs3.getInt("active_dns") == 1;
                                hostHeader = rs3.getString("host_header");
                                formType = rs3.getString("form_type");
                                formParameters = rs3.getString("form_parameters");
                                charset = rs3.getString("charset");
                            }
                            return new ReverseProxyWebForward(realmID, id, type, url, name, description, category, authUsername, authPassword,
                                            preferredAuthScheme, formType, formParameters, paths.toString(), hostHeader, activeDNS, autoStart,
                                            created, amended, charset);
                        } finally {
                            rs3.close();
                        }
                    } finally {
                        ps3.releasePreparedStatement();
                    }
                } finally {
                    rs2.close();
                }
            } finally {
                ps2.releasePreparedStatement();
            }

        } else if (type == WebForward.TYPE_REPLACEMENT_PROXY) {
            JDBCPreparedStatement ps3 = db.getStatement("getWebForward.replacementProxyOptions.selectById");
            try {
                ps3.setInt(1, id);
                ResultSet rs3 = ps3.executeQuery();
                try {
                    String authUsername = "";
                    String authPassword = "";
                    String preferredAuthScheme = HttpAuthenticatorFactory.BASIC;
                    String encoding = "";
                    String formType = "";
                    String formParameters = "";
                    PropertyList restrictToHosts = new PropertyList();
                    if (rs3.next()) {
                        authUsername = rs3.getString("authentication_username");
                        authPassword = rs3.getString("authentication_password");
                        preferredAuthScheme = rs3.getString("preferred_authentication_scheme");
                        encoding = rs3.getString("encoding");
                        restrictToHosts.setAsPropertyText(rs3.getString("restrict_to_hosts"));
                        formType = rs3.getString("form_type");
                        formParameters = rs3.getString("form_parameters");
                    }
                    return new ReplacementProxyWebForward(realmID, id, url, name, description, category, authUsername, authPassword,
                                    preferredAuthScheme, encoding, restrictToHosts, formType, formParameters, autoStart, created, amended);
                } finally {
                    rs3.close();
                }
            } finally {
                ps3.releasePreparedStatement();
            }

        } else {
            return new TunneledSiteWebForward(realmID, id, url, name, description, category, autoStart, created, amended);
        }

    }

    /* (non-Javadoc)
     * @see com.adito.plugin.PluginDatabase#open(com.adito.core.CoreServlet, com.adito.plugin.PluginDefinition)
     */
    public void open(CoreServlet controllingServlet, PluginDefinition def) throws Exception {
        String dbName = SystemProperties.get("adito.systemDatabase.jdbc.dbName", "explorer_configuration");
        controllingServlet.addDatabase(dbName, ContextHolder.getContext().getDBDirectory());
        String jdbcUser = SystemProperties.get("adito.jdbc.username", "sa");
        String jdbcPassword = SystemProperties.get("adito.jdbc.password", "");
        String vendorDB = SystemProperties.get("adito.jdbc.vendorClass", "com.adito.jdbc.hsqldb.HSQLDBDatabaseEngine");

        if (log.isInfoEnabled()) {
            log.info("System database is being opened...");
            log.info("JDBC vendor class implementation is " + vendorDB);
        }

        db = (JDBCDatabaseEngine) Class.forName(vendorDB).newInstance();
        db.init("webForwardsDatabase", dbName, jdbcUser, jdbcPassword, null);

        File upgradeDir = new File(def.getDescriptor().getApplicationBundle().getBaseDir(), "upgrade");
        DBUpgrader upgrader = new DBUpgrader(ExtensionStore.getInstance()
            .getExtensionBundle(WebForwardPlugin.BUNDLE_ID)
            .getVersion(), db, ContextHolder.getContext()
                        .getDBDirectory(), upgradeDir);
        upgrader.upgrade();

        CoreServlet.getServlet().addCoreListener(this);
    }
    
    //
    // Replacements
    //

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.SystemDatabase#getReplacementsForContent(java.lang.String,
     *      int, java.lang.String, java.lang.String)
     */
    public List getReplacementsForContent(String username, int replaceType, String mimeType, String site) throws Exception {
        String cacheKey = (username == null ? "" : username) + "_" + replaceType + "_" + (mimeType == null ? "" : mimeType) + "_"
                        + (site == null ? "" : site);
        synchronized (replacementsCache) {
            List cachedReplacements = (List) replacementsCache.retrieve(cacheKey);
            if (replacementsCache.contains(cacheKey)) {
                return cachedReplacements;
            }
        }
        JDBCPreparedStatement ps = null;

        // global=SELECT * FROM replacements WHERE username=? AND mime_type=?
        // AND replace_type=? AND MATCHES(?,site_pattern=?) = ?
        // ORDER BY username,replace_type,mime_type,sequence ASC
        // user=SELECT * FROM replacements WHERE ( username=? OR username=? )
        // AND mime_type=? AND replace_type=? AND MATCHES(?,site_pattern=?) = ?
        // ORDER BY username,replace_type,mime_type,sequence ASC

        String sitePattern = site == null || site.equals("") ? ".*" : site;
        if (site == null || site.equals("")) {
            ps = db.getStatement("getReplacementsForContent.select.allSites");
            ps.setString(3, "");

        } else {
            ps = db.getStatement("getReplacementsForContent.select");
            ps.setString(3, sitePattern);
            ps.setString(4, sitePattern);
        }
        ps.setString(1, mimeType == null ? "" : mimeType);
        ps.setInt(2, replaceType);

        try {
            ResultSet rs = ps.executeQuery();
            CacheList v = new CacheList();
            try {
                while (rs.next()) {
                    v.add(new DefaultReplacement(rs.getString("mime_type"), replaceType, rs.getInt("sequence"), rs
                                    .getString("site_pattern"), rs.getString("match_pattern"), rs.getString("replace_pattern")));
                }
                replacementsCache.store(cacheKey, v, new Long(Long.MAX_VALUE), null);
                return v;
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
     * @see com.adito.security.SystemDatabase#deleteReplacement(int)
     */
    public void deleteReplacement(int sequence) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("deleteReplacements.delete");
        try {
            ps.setInt(1, sequence);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
        replacementsCache.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.SystemDatabase#updateReplacement(com.adito.services.Replacement)
     */
    public void updateReplacement(Replacement replacement) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("updateReplacements.update");
        try {
            ps.setString(1, replacement.getMimeType());
            ps.setString(2, replacement.getSitePattern());
            ps.setString(3, replacement.getMatchPattern());
            ps.setString(4, replacement.getReplacePattern());
            ps.setInt(5, replacement.getReplaceType());
            ps.setInt(6, replacement.getSequence());
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
        replacementsCache.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.SystemDatabase#createReplacement(com.adito.services.Replacement)
     */
    public Replacement createReplacement(Replacement replacement) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("createReplacement.insert");
        try {
            ps.setInt(1, replacement.getReplaceType());
            ps.setString(2, replacement.getMimeType());
            ps.setString(3, replacement.getSitePattern());
            ps.setString(4, replacement.getMatchPattern());
            ps.setString(5, replacement.getReplacePattern());
            ps.execute();
            return new DefaultReplacement(replacement.getMimeType(), replacement.getReplaceType(), db.getLastInsertId(ps,
                "createReplacement.lastInsertId"), replacement.getSitePattern(), replacement.getMatchPattern(), replacement
                            .getReplacePattern());
        } finally {
            replacementsCache.clear();
            ps.releasePreparedStatement();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.SystemDatabase#getReplacements()
     */
    public List<Replacement> getReplacements() throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getReplacements.select");
        try {
            ResultSet rs = ps.executeQuery();
            try {
                List<Replacement> v = new ArrayList<Replacement>();
                while (rs.next()) {
                    v.add(new DefaultReplacement(rs.getString("mime_type"), rs.getInt("replace_type"), rs.getInt("sequence"), rs
                                    .getString("site_pattern"), rs.getString("match_pattern"), rs.getString("replace_pattern")));
                }
                return v;
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
     * @see com.adito.security.SystemDatabase#getReplacement(int)
     */
    public Replacement getReplacement(int sequence) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getReplacement.select");
        ps.setInt(1, sequence);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return new DefaultReplacement(rs.getString("mime_type"), rs.getInt("replace_type"), rs.getInt("sequence"), rs
                                    .getString("site_pattern"), rs.getString("match_pattern"), rs.getString("replace_pattern"));
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return null;
    }

    // 
    // Supporting classes
    //

    class CacheList extends ArrayList<Replacement> implements Serializable {
        private static final long serialVersionUID = 6613983448357872637L;
    }

}
