
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
			
package com.adito.activedirectory;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.PropertyList;
import com.adito.properties.Property;
import com.adito.properties.impl.realms.RealmKey;
import com.adito.realms.Realm;
import com.adito.security.UserDatabaseException;

public final class ActiveDirectoryUserDatabaseConfiguration {
    private static final Log logger = LogFactory.getLog(ActiveDirectoryUserDatabaseConfiguration.class);
    private static final String COMMON_NAME = "CN=";
    private static final String CN_USERS = COMMON_NAME + "Users";
    private static final String CN_BUILTIN = COMMON_NAME + "Builtin";
    private static final String LDAP_PROTOCOL = "ldap://";
    private static final String PORT_SEPARATOR = ":";
    private static final String ESCAPE_BACKSLASH = "\\\\";
    private static final String ESCAPE_QUOTE = "\"";
    private static final String[] ESCAPED_CHARACTERS = {ESCAPE_BACKSLASH, "/", "#", ",,", "\\+", ESCAPE_QUOTE, "<", ">", ";"};
    private static final int SSL_SECURED_PORT = 636;
    private static final int CLEAR_TEXT_PORT = 389;
    
    /** The Kerberos GSSAPI authentication type. */
    public static final String GSSAPI_AUTHENTICATION_METHOD = "GSSAPI";
    /** The JNDI simple bind authentication type. */
    public static final String SIMPLE_AUTHENTICATION_METHOD = "simple";
    
    /** The plain text protocol. */
    public static final String PLAIN_PROTOCOL = "plain";
    /** The SSL protocol. */
    public static final String SSL_PROTOCOL = "ssl";

    private final Realm realm;
    private final ActiveDirectoryPropertyManager propertyManager;
    private String domain; 
    private String controllerHost; 
    private Collection<String> backupControllerHosts;
    private String serviceAuthenticationType;
    private String userAuthenticationType;
    private String serviceAccountName;
    private String serviceAccountPassword;
    private String baseDn; 
    private String protocolType;
    private boolean followReferrals;
    private int userCacheSize;
    private int groupCacheSize;
    private boolean inMemoryCache;
    private int timeToLive;
    private final List<String> includedOuBasesList = new ArrayList<String>(); 
    private final List<String> excludedOuBasesList = new ArrayList<String>(); 
    private boolean hasFilteredOus; 
    private boolean includeDistributionGroups;
    private boolean memberOfSupported;
    private boolean usernamesAreCaseSensitive; 
    private final Collection<URI> activeDirectoryUrls = new ArrayList<URI>();
    private URI lastContactedActiveDirectoryUrl;
    private int pageSize;
    private int timeOut;
    private PagedResultTemplate template;

    public ActiveDirectoryUserDatabaseConfiguration(Realm realm, Properties propertyNames) throws IllegalArgumentException, Exception {
        this.realm = realm;
        propertyManager = new ActiveDirectoryPropertyManager(realm, propertyNames);
        initialize(propertyNames);
    }

    private void initialize(Properties propertyNames) throws Exception {
        // Get the domain and active directory root
        setControllerHost(getProperty("activeDirectory.controllerHost", propertyNames));
        setBackupControllerHosts(getPropertyList("activeDirectory.backupControllerHosts", propertyNames));
        setDomain(getProperty("activeDirectory.domain", propertyNames));
        setServiceAuthenticationType(getProperty("activeDirectory.serviceAuthenticationType", propertyNames));
        setUserAuthenticationType(getProperty("activeDirectory.userAuthenticationType", propertyNames));
        setServiceAccountName(getProperty("activeDirectory.serviceAccountUsername", propertyNames));
        setServiceAccountPassword(getProperty("activeDirectory.serviceAccountPassword", propertyNames));
        
        setFollowReferrals(getPropertyBoolean("activeDirectory.followReferrals", propertyNames));
        setUserCacheSize(getPropertyInt("activeDirectory.cacheUserMaxObjects", propertyNames));
        setGroupCacheSize(getPropertyInt("activeDirectory.cacheGroupMaxObjects", propertyNames));
        setInMemoryCache(getPropertyBoolean("activeDirectory.cacheInMemory", propertyNames));
        setTimeToLive(getPropertyInt("activeDirectory.userCacheTTL", propertyNames));

        Collection<String> includedOuFilterList = getPropertyList("activeDirectory.organizationalUnitFilter", propertyNames);
        Collection<String> excludedOuFilterList = getPropertyList("activeDirectory.excludedOrganizationalUnitFilter", propertyNames);
        setValidOus(baseDn, includedOuFilterList, excludedOuFilterList);
        setIncludeStandardUsers(getPropertyBoolean("activeDirectory.includeStandardUsers", propertyNames));
        setIncludeBuiltInGroups(getPropertyBoolean("activeDirectory.includeBuiltInGroups", propertyNames));
        setIncludeDistributionGroups(getPropertyBoolean("activeDirectory.includeDistributionGroups", propertyNames));
        
        setMemberOfSupported(getPropertyBoolean("activeDirectory.memberOfSupported", propertyNames));
        setUsernamesAreCaseSensitive(getPropertyBoolean("activeDirectory.usernamesAreCaseSensitive", propertyNames));
        setPageSize(getPropertyInt("activeDirectory.pageSize", propertyNames));
        setTimeOut(getPropertyInt("activeDirectory.connection.timeout", propertyNames));
    }

    private String getProperty(String key, Properties propertyNames) {
        return Property.getProperty(getRealmKey(key, propertyNames));
    }

    private boolean getPropertyBoolean(String key, Properties propertyNames) {
        return Property.getPropertyBoolean(getRealmKey(key, propertyNames));
    }
    
    private int getPropertyInt(String key, Properties propertyNames) {
        return Property.getPropertyInt(getRealmKey(key, propertyNames));
    }
    
    private Collection<String> getPropertyList(String key, Properties propertyNames) {
        return Property.getPropertyList(getRealmKey(key, propertyNames));
    }
    
    private RealmKey getRealmKey(String key, Properties propertyNames) {
        String propertyOrDefault = propertyNames.getProperty(key, key);
        return new RealmKey(propertyOrDefault, realm);
    }
    
    void postInitialize() throws URISyntaxException {
        setActiveDirectoryUrls();
        if(!isServiceAuthenticationGssApi() && !serviceAccountName.toLowerCase().endsWith(baseDn)) {
            serviceAccountName = appendBaseDn(formatUsername(serviceAccountName));
        }
        includedOuBasesList.removeAll(excludedOuBasesList); // just to make sure
        
        Collection<String> escapedIncludedOuBasesList = getEscapedDns(includedOuBasesList, false);
        Collection<String> escapedExcludedOuBasesList = getEscapedDns(excludedOuBasesList, false);
        Collection<String> escapedOuSearchBase = getEscapedDns(includedOuBasesList, true);
        template = new PagedResultTemplate(escapedIncludedOuBasesList, escapedExcludedOuBasesList, escapedOuSearchBase, pageSize);
        refresh();
    }

    private static Collection<String> getEscapedDns(Collection<String> toEscapeDns, boolean requiresSecondEscape) {
        Collection<String> escapedDns = new HashSet<String>(toEscapeDns.size());
        for (String toEscapeDn : toEscapeDns) {
            String escapedDn = getEscapedDn(toEscapeDn, requiresSecondEscape);
            escapedDns.add(escapedDn);
        }
        return Collections.unmodifiableCollection(escapedDns);
    }
       
    static String getEscapedDn(String toEscape, boolean requiresSecondEscape) {
        for (int index = 0; index < ESCAPED_CHARACTERS.length; index++) {
            String character = ESCAPED_CHARACTERS[index];
            if (requiresSecondEscape) {
                if(character.equals(ESCAPE_BACKSLASH)) {
                    toEscape = toEscape.replaceAll(character, ESCAPE_BACKSLASH + ESCAPE_BACKSLASH + ESCAPE_BACKSLASH + ESCAPE_BACKSLASH);
                } else if(character.equals(ESCAPE_QUOTE)) {
                    toEscape = toEscape.replaceAll(character, ESCAPE_BACKSLASH + ESCAPE_BACKSLASH + ESCAPE_QUOTE);
                } else {
                    toEscape = toEscape.replaceAll(character, ESCAPE_BACKSLASH + character);
                }
            } else {
                toEscape = toEscape.replaceAll(character, ESCAPE_BACKSLASH + character);
            }
        }
        return toEscape;
    }
    
    private static String formatUsername(String username) {
    	return username.toUpperCase().startsWith(COMMON_NAME) ? username : COMMON_NAME + username;
    }
    
    public String appendBaseDn(String commonName) {
        if (commonName.toLowerCase().endsWith(baseDn.toLowerCase())) {
            return commonName;
        } else {
            return commonName.endsWith(",") ? commonName : commonName + "," + baseDn;
        }
    }
    
    void refresh() {
        propertyManager.refresh();
    }

    public String getDomain() {
        return domain;
    }
    
    private void setDomain(String domain) throws Exception {
        this.domain = domain.toUpperCase().trim();
        if (this.domain.equals("")) {
            throw new IllegalArgumentException("No active directory domain configured.");
        }
        setBaseDn(splitDomain(domain));
    }

    private void setControllerHost(String controllerHost) {
        if (controllerHost.equals("")) {
            throw new IllegalArgumentException("No active directory controller host configured.");
        }
        this.controllerHost = controllerHost;
    }

    private Collection<String> getBackupControllerHosts() {
        return backupControllerHosts;
    }
    
    private void setBackupControllerHosts(Collection<String> backupControllerHosts) {
        this.backupControllerHosts = backupControllerHosts;
    }

    String getServiceAuthenticationType() {
        return serviceAuthenticationType;
    }

    public void setServiceAuthenticationType(String serviceAuthenticationType) {
        this.serviceAuthenticationType = serviceAuthenticationType;
    }

    boolean isServiceAuthenticationGssApi() {
        return GSSAPI_AUTHENTICATION_METHOD.equals(getServiceAuthenticationType());
    }

    boolean isUserAuthenticationGssApi() {
        return GSSAPI_AUTHENTICATION_METHOD.equals(getUserAuthenticationType());
    }
    
    String getUserAuthenticationType() {
        return userAuthenticationType;
    }

    public void setUserAuthenticationType(String userAuthenticationType) {
        this.userAuthenticationType = userAuthenticationType;
    }

    public String getServiceAccountName() {
        return serviceAccountName;
    }

    void setServiceAccountName(String serviceAccountName) {
        this.serviceAccountName = serviceAccountName == null ? null : serviceAccountName.trim();
    }

    public String getServiceAccountPassword() {
        return serviceAccountPassword;
    }

    void setServiceAccountPassword(String serviceAccountPassword) {
        this.serviceAccountPassword = serviceAccountPassword;
    }

    private String getProtocolType() {
        return protocolType;
    }

    public boolean isSslProtcolType() {
        return "ssl".equals(getProtocolType());
    }
    
    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }
    
    private boolean isFollowReferrals() {
        return followReferrals;
    }

    void setFollowReferrals(boolean followReferrals) {
        this.followReferrals = followReferrals;
    }

    public String getBaseDn() {
        return baseDn;
    }

    void setBaseDn(String baseDn) {
        this.baseDn = baseDn == null ? null : baseDn.toLowerCase().trim();
    }

    void setUserCacheSize(int userCacheSize) {
        this.userCacheSize = userCacheSize;
    }
    
    void setGroupCacheSize(int groupCacheSize) {
        this.groupCacheSize = groupCacheSize;
    }

    void setInMemoryCache(boolean inMemoryCache) {
        this.inMemoryCache = inMemoryCache;
    }

    int getTimeToLive() {
        return timeToLive;
    }

    void setTimeToLive(int timeToLive) {
        if (timeToLive < 1) {
            logger.warn("Cache TTL is less than 1 minute. This would cause serious performance problems. The minimum value of 1 minute will now be used");
            timeToLive = 1;
        }
        this.timeToLive = minutesToMillis(timeToLive);
    }
    
    private static int minutesToMillis(int minutes) {
        return minutes * 60 * 1000;
    }

    UserContainer createUserContainer() {
        return new UserContainer(userCacheSize, inMemoryCache, isUsernamesAreCaseSensitive(), getDomain());
    }

    GroupContainer createRoleContainer() {
        return new GroupContainer(groupCacheSize, inMemoryCache);
    }
    
    void setValidOus(String baseDn, Collection<String> includedOuFilterList, Collection<String> excludedOuFilterList) {
        includedOuBasesList.clear();
        includedOuBasesList.addAll(getFormattedOuFilterList(baseDn, includedOuFilterList));
        excludedOuBasesList.clear();
        excludedOuBasesList.addAll(getFormattedOuFilterList(baseDn, excludedOuFilterList));
        includedOuBasesList.removeAll(excludedOuBasesList);

        hasFilteredOus = !includedOuBasesList.isEmpty();
        if (!hasFilteredOus) {
            includedOuBasesList.add(baseDn);
        } 

        if (logger.isDebugEnabled()) {
            logger.debug("Included OU Bases:");
            for (String dn : includedOuBasesList) {
                logger.debug(" " + dn);
            }

            logger.debug("Excluded OU Bases:");
            for (String dn : excludedOuBasesList) {
                logger.debug(" " + dn);
            }
        }
    }

    private static Collection<String> getFormattedOuFilterList(String baseDn, Collection<String> ouFilterList) {
        Collection<String> formattedOuFilterList = new HashSet<String>();
        for (String dn : ouFilterList) {
            if (!dn.trim().toLowerCase().endsWith(baseDn.trim().toLowerCase())) {
                dn = dn + "," + baseDn;
            }
            formattedOuFilterList.add(dn);
        }
        return formattedOuFilterList;
    }

    private void setIncludeStandardUsers(boolean includeStandardUsers) {
        if (includeStandardUsers) {
            if (hasFilteredOus) {
                includedOuBasesList.add(0, appendBaseDn(CN_USERS));
            }
        } else {
            excludedOuBasesList.add(0, appendBaseDn(CN_USERS));
        }
    }

    private void setIncludeBuiltInGroups(boolean includeBuiltInGroups) {
        if (includeBuiltInGroups) {
            if (hasFilteredOus) {
                includedOuBasesList.add(0, appendBaseDn(CN_BUILTIN));
            }
        } else {
            excludedOuBasesList.add(0, appendBaseDn(CN_BUILTIN));
        }
    }
    
    boolean isIncludeDistributionGroups() {
        return includeDistributionGroups;
    }
    
    private void setIncludeDistributionGroups(boolean includeDistributionGroups) {
        this.includeDistributionGroups = includeDistributionGroups;
    }

    boolean isMemberOfSupported() {
        return memberOfSupported;
    }

    private void setMemberOfSupported(boolean memberOfSupported) {
        this.memberOfSupported = memberOfSupported;
    }
    
    private boolean isUsernamesAreCaseSensitive() {
        return usernamesAreCaseSensitive;
    }

    private void setUsernamesAreCaseSensitive(boolean usernamesAreCaseSensitive) {
        this.usernamesAreCaseSensitive = usernamesAreCaseSensitive;
    }
    
    private void setActiveDirectoryUrls() throws URISyntaxException {
        activeDirectoryUrls.clear();
        lastContactedActiveDirectoryUrl = null;

        int controllerPort = getControllerPort();
        URI primaryUri = controllerHost.contains(PORT_SEPARATOR) ? buildURI(controllerHost) : buildURI(controllerHost, controllerPort);
        activeDirectoryUrls.add(primaryUri);

        for (String uri : getBackupControllerHosts()) {
            if (uri.contains(PORT_SEPARATOR)) {
                activeDirectoryUrls.add(buildURI(uri));
            } else {
                activeDirectoryUrls.add(buildURI(uri, controllerPort));
            }
        }

        setLastContactedActiveDirectoryUrl(primaryUri);
    }
    
    private int getControllerPort() {
        int indexOf = controllerHost.indexOf(PORT_SEPARATOR);
        if(indexOf == -1 || indexOf == controllerHost.length() - 1) {
            if(isServiceAuthenticationGssApi()) {
                return CLEAR_TEXT_PORT;
            }
            return isSslProtcolType() ? SSL_SECURED_PORT : CLEAR_TEXT_PORT;
        } else {
            String port = controllerHost.substring(indexOf + 1);
            Integer valueOf = Integer.valueOf(port);
            return valueOf;
        }
    }

    private static URI buildURI(String host, int port) throws URISyntaxException {
        return buildURI(host + PORT_SEPARATOR + port);
    }

    private static URI buildURI(String url) throws URISyntaxException {
        return new URI(LDAP_PROTOCOL + url);
    }

    String getContactableActiveDirectories() {
        URI lastContactedUrl = getLastContactedActiveDirectoryUrl();
        URI firstUrl = activeDirectoryUrls.isEmpty() ? null : activeDirectoryUrls.iterator().next();
        boolean isDifferent = !lastContactedUrl.equals(firstUrl);

        Collection<URI> hosts = new ArrayList<URI>(activeDirectoryUrls.size() + 1);
        if (isDifferent) {
            hosts.add(lastContactedUrl);
        }
        hosts.addAll(activeDirectoryUrls);
        return getHosts(hosts);
    }

    private static String getHosts(Collection<URI> urls) {
        StringBuffer buffer = new StringBuffer();
        for (Iterator<URI> itr = urls.iterator(); itr.hasNext();) {
            buffer.append(itr.next().toString());
            if(itr.hasNext()) {
                buffer.append(" ");
            }
        }
        return buffer.toString();
    }
    
    synchronized URI getLastContactedActiveDirectoryUrl() {
        return lastContactedActiveDirectoryUrl;
    }

    synchronized void setLastContactedActiveDirectoryUrl(String url) {
        try {
            setLastContactedActiveDirectoryUrl(new URI(url));
        } catch (URISyntaxException e) {
            // ignore
        }
    }

    private synchronized void setLastContactedActiveDirectoryUrl(URI url) {
        if (lastContactedActiveDirectoryUrl == null || !lastContactedActiveDirectoryUrl.equals(url)) {
            PropertyList kerbrosControllerSettings = getKerbrosControllerSettings(url);
            propertyManager.refresh(Collections.singletonMap("activeDirectory.backupControllerHosts", kerbrosControllerSettings.getAsPropertyText()));
        }
        lastContactedActiveDirectoryUrl = url;
    }

    private PropertyList getKerbrosControllerSettings(URI contactedUrl) {
        PropertyList values = new PropertyList();
        values.add(getKerbrosController(contactedUrl));

        for (URI url : activeDirectoryUrls) {
            String kerbrosController = getKerbrosController(url);
            if (!values.contains(kerbrosController)) {
                values.add(kerbrosController);
            }
        }
        return values;
    }

    private static String getKerbrosController(URI url) {
        String toParse = url.toString();
        return toParse.substring(LDAP_PROTOCOL.length(), toParse.lastIndexOf(PORT_SEPARATOR));
    }

    void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    private int getTimeout() {
        return timeOut;
    }

    private void setTimeOut(int timeOut) {
        this.timeOut = timeOut * 1000;
    }
    
    PagedResultTemplate getPagedResultTemplate() {
        return this.template;
    }
    
    public Object doAs(PrivilegedAction<?> action) throws UserDatabaseException {
        Object result = null;
        if (isServiceAuthenticationGssApi()) {
            try {
                LoginContext context = getServiceAccountLoginContext();
                result = Subject.doAs(context.getSubject(), action);
                logoutContext(context);
            } catch (Exception e) {
                logger.error("Failure to create Login Context", e);
                throw new UserDatabaseException("", e);
            }
        } else {
            result = action.run();
        }
        
        if (result instanceof Throwable) {
            Throwable e = (Throwable) result;
            logger.error("Failure to doAs", e);
            throw new UserDatabaseException("", e);
        }
        return result;
    }
    
    private LoginContext getServiceAccountLoginContext() throws Exception {
        /*
         * Only attempt to load the service account context if it has not been
         * loaded, if the username has changed or if the password has changed
         */
        try {
            return createLoginContext(getServiceAccountName(), getServiceAccountPassword());
        } catch (LoginException e) {
            Throwable cause = e.getCause();
            // Check the class by name to allow non Sun Javas to compile
            if (cause != null && cause.getClass().getName().equals("sun.security.krb5.KrbException")) {
                throw new Exception("Failed to logon. Please check your Active Directory configuration.", e);
            }
            throw e;
        } 
    }
    
    LoginContext createLoginContext(String username, String password) throws LoginException {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating login context for " + username);
        }

        UserPasswordCallbackHandler callbackHandler = new UserPasswordCallbackHandler();
        callbackHandler.setUserId(username);
        callbackHandler.setPassword(password);

        LoginContext context = new LoginContext(ActiveDirectoryUserDatabase.class.getName(), callbackHandler);
        context.login();
        return context;
    }

    static void logoutContext(LoginContext context) {
        try {
            if (context != null) {
                context.logout();
            }
        } catch (LoginException e) {
            // ignore
        }
    }

    InitialLdapContext getAuthenticatedContext(String url, Map<String, String> properties) throws NamingException {
        Hashtable<String, String> variables = new Hashtable<String, String>(properties);
        variables.put(Context.SECURITY_AUTHENTICATION, getServiceAuthenticationType());
        if (!isServiceAuthenticationGssApi()) {
            variables.put(Context.SECURITY_PRINCIPAL, getServiceAccountName());
            variables.put(Context.SECURITY_CREDENTIALS, getServiceAccountPassword());
        }
        return getInitialContext(url, variables);
    }

    InitialLdapContext getAuthenticatedContext(String url) throws NamingException {
        return getAuthenticatedContext(url, Collections.<String, String> emptyMap());
    }

    public InitialLdapContext getInitialContext(String url, Map<String, String> properties) throws NamingException {
        Hashtable<String, String> variables = new Hashtable<String, String>(properties);
        variables.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        variables.put(Context.PROVIDER_URL, url); // Must use fully qualified hostname

        if (isSslProtcolType()) {
            variables.put("java.naming.ldap.factory.socket", "com.adito.boot.CustomSSLSocketFactory"); 
            // Add the custom socket factory
        }

        if (isFollowReferrals()) {
            variables.put(Context.REFERRAL, "follow");
        }

        variables.put("com.sun.jndi.ldap.connect.timeout", String.valueOf(getTimeout()));
        variables.put("java.naming.ldap.version", "3");
        variables.put("com.sun.jndi.ldap.connect.pool", "true");
        variables.put("javax.security.sasl.qop", "auth-conf,auth-int,auth");
        variables.put(Context.SECURITY_PROTOCOL, getProtocolType());

        InitialLdapContext context = new InitialLdapContext(variables, null);
        String usedUrl = (String) context.getEnvironment().get(Context.PROVIDER_URL);
        setLastContactedActiveDirectoryUrl(usedUrl);
        return context;
    }

    private static String splitDomain(String domain) {
        StringBuffer buffer = new StringBuffer();
        for (StringTokenizer tokenizer = new StringTokenizer(domain, "."); tokenizer.hasMoreTokens();) {
            if (buffer.length() > 0) {
                buffer.append(",");
            }
            buffer.append("DC=" + tokenizer.nextToken());
        }
        return buffer.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("[realm='").append(realm);
        builder.append("', domain='").append(getDomain());
        builder.append("', controllerHost='").append(controllerHost);
        builder.append("', backupControllerHosts='").append(getBackupControllerHosts());
        builder.append("', serviceAuthenticationType='").append(getServiceAuthenticationType());
        builder.append("', userAuthenticationType='").append(getUserAuthenticationType());
        builder.append("', serviceAccountName='").append(getServiceAccountName());
        builder.append("', serviceAccountPassword='************");
        builder.append("', baseDn='").append(getBaseDn());
        builder.append("', protocolType='").append(getProtocolType());
        builder.append("', followReferrals='").append(isFollowReferrals());
        builder.append("', userCacheSize='").append(userCacheSize);
        builder.append("', groupCacheSize='").append(groupCacheSize);
        builder.append("', inMemoryCache='").append(inMemoryCache);
        builder.append("', timeToLive='").append(timeToLive);
        builder.append("', includedOuBasesList='").append(includedOuBasesList);
        builder.append("', excludedOuBasesList='").append(excludedOuBasesList);
        builder.append("', hasFilteredOus='").append(hasFilteredOus);
        builder.append("', includeDistributionGroups='").append(includeDistributionGroups);
        builder.append("', usernamesAreCaseSensitive='").append(usernamesAreCaseSensitive);
        builder.append("', pageSize='").append(pageSize);
        builder.append("', timeOut='").append(timeOut).append("']");
        return builder.toString();
    }
    
    /**
     * Converts an Active Directory long value into a
     * <code>java.util.Date</code>.
     * 
     * @param timeStamp the time to convert
     * @return the <code>java.util.Date</code> representing the long
     */
    public static Date adTimeToJavaDate(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(1601, 0, 1, 0, 0);
        timeStamp = timeStamp / 10000 + calendar.getTime().getTime();
        return new Date(timeStamp);
    }

    /**
     * Converts an Active Directory long value into a number of days.
     * 
     * @param timeStamp the time to convert
     * @return days representing the long
     */
    public static int adTimeToJavaDays(long timeStamp) {
        return (int) (timeStamp / -86400) / 10000000;
    }
}