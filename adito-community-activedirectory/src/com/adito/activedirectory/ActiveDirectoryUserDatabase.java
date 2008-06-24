
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

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.management.relation.RoleNotFoundException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ContextHolder;
import com.adito.core.CoreEvent;
import com.adito.core.CoreJAASConfiguration;
import com.adito.core.CoreListener;
import com.adito.core.CoreServlet;
import com.adito.properties.Property;
import com.adito.properties.PropertyChangeEvent;
import com.adito.properties.impl.userattributes.UserAttributeKey;
import com.adito.realms.Realm;
import com.adito.security.DefaultUserDatabase;
import com.adito.security.InvalidLoginCredentialsException;
import com.adito.security.Role;
import com.adito.security.User;
import com.adito.security.UserDatabaseException;
import com.adito.security.UserNotFoundException;
import com.adito.util.ThreadRunner;

/**
 * <p>
 * Microsoft Active Directory implementation of
 * {@link com.adito.security.UserDatabase}.
 * 
 * @author Lee Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 * @author Brett Smith <a href="mailto:brett@localhost">&lt;brett@localhost&gt;</a>
 */
public class ActiveDirectoryUserDatabase extends DefaultUserDatabase implements CoreListener {
    private static final String OBJECT_SID_ATTRIBUTE = "objectSID";
    private static final String HOME_DIRECTORY_ATTRIBUTE = "homeDirectory";
    private static final String HOME_DRIVE_ATTRIBUTE = "homeDrive";
    private static final String MEMBER_OF_ATTIBUTE = "memberOf";
    private static final String GROUPNAME_FILTER_ATTRIBUTE = "%GROUPNAME%";
    private static final String USERNAME_FILTER_PARAMETER = "%USERNAME%";
    private static final String USER_PRINCIPAL_NAME_PARAMETER = "%PRINCIPAL_NAME%";

    public static final String SAM_ACCOUNT_NAME_ATTRIBUTE = "sAMAccountName";
    public static final String COMMON_NAME_ATTRIBUTE = "cn";
    public static final String DISPLAY_NAME_ATTRIBUTE = "displayName";
    public static final String USER_PRINCIPAL_NAME_ATTRIBUTE = "userPrincipalName";
    public static final String MAIL_ATTRIBUTE = "mail";
    public static final String USER_ACCOUNT_CONTROL_ATTRIBUTE = "userAccountControl";
    public static final String PWD_LAST_SET_ATTRIBUTE = "pwdLastSet";
    public static final String GROUP_TYPE_ATTRIBUTE = "groupType";
    public static final String PRIMARY_GROUP_ID_ATTRIBUTE = "primaryGroupId";
    public static final String OBJECT_CLASS_ATTRIBUTE = "objectClass";

    private static final String USER_FILTER = "(&(!(" + OBJECT_CLASS_ATTRIBUTE + "=computer))(" + OBJECT_CLASS_ATTRIBUTE
                    + "=user)(|(" + SAM_ACCOUNT_NAME_ATTRIBUTE + "=" + USERNAME_FILTER_PARAMETER + ")("
                    + USER_PRINCIPAL_NAME_ATTRIBUTE + "=" + USER_PRINCIPAL_NAME_PARAMETER + ")))";
    private static final String USER_GROUPS_FILTER = "(&(" + OBJECT_CLASS_ATTRIBUTE + "=group)(member=" + GROUPNAME_FILTER_ATTRIBUTE + "))";
    private static final String GROUP_FILTER = "&(" + OBJECT_CLASS_ATTRIBUTE + "=group)(" + COMMON_NAME_ATTRIBUTE + "="
                    + GROUPNAME_FILTER_ATTRIBUTE + ")";
    private static final String USER_ATTRS[] = { SAM_ACCOUNT_NAME_ATTRIBUTE, USER_PRINCIPAL_NAME_ATTRIBUTE, DISPLAY_NAME_ATTRIBUTE,
                    MAIL_ATTRIBUTE, HOME_DIRECTORY_ATTRIBUTE, HOME_DRIVE_ATTRIBUTE, MEMBER_OF_ATTIBUTE, PRIMARY_GROUP_ID_ATTRIBUTE,
                    PWD_LAST_SET_ATTRIBUTE, USER_ACCOUNT_CONTROL_ATTRIBUTE };
    private static final String GROUP_ATTRS[] = { COMMON_NAME_ATTRIBUTE, OBJECT_SID_ATTRIBUTE, MEMBER_OF_ATTIBUTE };
    private static final String WILDCARD_SEARCH = "*";
    private static final Log logger = LogFactory.getLog(ActiveDirectoryUserDatabase.class);

    private ActiveDirectoryUserDatabaseConfiguration configuration;
    private UserContainer userContainer = UserContainer.EMPTY_CACHE;
    private GroupContainer groupContainer = GroupContainer.EMPTY_CACHE;
    private ThreadRunner threadRunner;

    /** Constructor */
    public ActiveDirectoryUserDatabase() {
        this(false, false);
    }

    /** Constructor */
    public ActiveDirectoryUserDatabase(boolean supportsAccountCreation, boolean supportsPasswordChange) {
        super("Active Directory", supportsAccountCreation, supportsPasswordChange);
        addJAASConfiguration();
    }

    private void addJAASConfiguration() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("client", "TRUE");
        parameters.put("debug", String.valueOf(logger.isDebugEnabled()).toUpperCase());
        parameters.put("useSubjectCredsOnly", "FALSE");
        parameters.put("useTicketCache", "FALSE");
        parameters.put("refreshKrb5Config", "TRUE");

        AppConfigurationEntry entry = new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule",
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, parameters);
        CoreJAASConfiguration config = (CoreJAASConfiguration) Configuration.getConfiguration();
        config.addAppConfigurationEntry(ActiveDirectoryUserDatabase.class.getName(), entry);
    }

    protected ActiveDirectoryUserDatabaseConfiguration getConfiguration() {
        return configuration;
    }

    protected UserContainer getUserContainer() {
        return userContainer;
    }

    protected GroupContainer getGroupContainer() {
        return groupContainer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.UserDatabase#logon(java.lang.String,
     *      java.lang.String)
     */
    public User logon(String username, String password) throws UserDatabaseException, InvalidLoginCredentialsException {
        ActiveDirectoryUser user = null;
        try {
            user = (ActiveDirectoryUser) getAccount(username);
        } catch (Exception e) {
            logger.error("Failed to logon", e);
            throw new UserDatabaseException("Failed to logon", e);
        }
        // this needs to be outside the try/catch, otherwise the specific
        // exception is turned into a general one
        assertValidCredentials(user, password);
        return user;
    }

    private void assertValidCredentials(ActiveDirectoryUser user, String password) throws InvalidLoginCredentialsException {
        if (!areCredentialsValid(user, password)) {
            throw new InvalidLoginCredentialsException("Invalid username or password.");
        }
    }

    @SuppressWarnings("unchecked")
    public Iterable<User> allUsers() {
        Iterator<? extends User> retrievePrincipals = userContainer.retrievePrincipals();
        return (Iterable<User>) toIterable(retrievePrincipals);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.UserDatabase#getAccount(java.lang.String)
     */
    public ActiveDirectoryUser getAccount(String username) throws UserNotFoundException, Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Getting account " + username);
        }

        if (!userContainer.containsPrincipal(username)) {
            loadUsers(username, false);
        }
        if (!userContainer.containsPrincipal(username)) {
            throw new UserNotFoundException(username + " is not a valid user!");
        }
        return userContainer.retrievePrincipal(username);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.UserDatabase#getRole(java.lang.String)
     */
    public Role getRole(String groupName) throws UserDatabaseException, RoleNotFoundException {
        if (logger.isDebugEnabled()) {
            logger.debug("Getting group " + groupName);
        }

        if (!groupContainer.containsPrincipal(groupName)) {
            loadRoles(groupName, false);
        }
        if (!groupContainer.containsPrincipal(groupName)) {
            throw new RoleNotFoundException(groupName + " is not a valid group!");
        }

        ActiveDirectoryGroup group = groupContainer.retrievePrincipal(groupName);
        groupContainer.buildHierarchy(group.getOriginalDn());
        return group;
    }

    @SuppressWarnings("unchecked")
    public Iterable<Role> allRoles() {
        Iterator<? extends Role> retrievePrincipals = groupContainer.retrievePrincipals();
        return (Iterable<Role>) toIterable(retrievePrincipals);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.UserDatabase#checkPassword(java.lang.String,java.lang.String)
     */
    public boolean checkPassword(String username, String password) throws UserDatabaseException, InvalidLoginCredentialsException {
        try {
            ActiveDirectoryUser user = (ActiveDirectoryUser) getAccount(username);
            return areCredentialsValid(user, password);
        } catch (UserNotFoundException e) {
            throw new UserDatabaseException("Failed to check password", e);
        } catch (Exception e) {
            throw new UserDatabaseException("Failed to check password", e);
        }
    }

    private void loadUsers(final String filter, final boolean removeMissingEntries) throws UserDatabaseException {
        configuration.doAs(new RetryPrivilegedAction() {
            protected Object doIt(InitialLdapContext context) throws Exception {
                loadUsers(filter, context, removeMissingEntries);
                return null;
            }
        });
    }
    
    private void loadUsers(final String filter, InitialLdapContext context, final boolean removeMissingEntries)
                    throws NamingException {
        final Collection<String> usernames = userContainer.retrievePrincipalNames();
        PagedResultMapper mapper = new AbstractPagedResultMapper() {
            public void mapSearchResult(SearchResult searchResult) throws NamingException, UserDatabaseException {
                String dn = searchResult.getNameInNamespace();
                ActiveDirectoryUser user = populateActiveDirectoryUser(dn, searchResult.getAttributes());
                String key = userContainer.storePrincipal(user);
                usernames.remove(key);
                if (logger.isDebugEnabled()) {
                    logger.debug("Found user " + user);
                }
            }
        };

        try {
            String replacedFilter = buildUserFilter(filter);
            PagedResultTemplate pagedResultTemplate = configuration.getPagedResultTemplate();
            pagedResultTemplate.search(context, replacedFilter, USER_ATTRS, mapper);
        } finally {
            if (removeMissingEntries) {
                userContainer.updateRemovedPrincipals(usernames);
            }
        }
    }

    private String buildUserFilter(String filter) {
        String escapedFilter = ActiveDirectoryUserDatabaseConfiguration.getEscapedDn(filter, true);
        String usernameReplacedFilter = USER_FILTER.replaceAll(USERNAME_FILTER_PARAMETER, escapedFilter);
        String principalFilter = escapedFilter.equals("*") ? "*" : escapedFilter + "*";
        return usernameReplacedFilter.replaceAll(USER_PRINCIPAL_NAME_PARAMETER, principalFilter);
    }

    private void loadRoles(final String filter, final boolean removeMissingEntries) throws UserDatabaseException {
        configuration.doAs(new RetryPrivilegedAction() {
            @Override
            protected Object doIt(InitialLdapContext context) throws Exception {
                loadRoles(filter, context, removeMissingEntries);
                return null;
            }

            @Override
            protected InitialLdapContext getContext(String url) throws NamingException {
                Map<String, String> extraProperties = Collections.<String, String> singletonMap(
                    "java.naming.ldap.attributes.binary", OBJECT_SID_ATTRIBUTE);
                return configuration.getAuthenticatedContext(url, extraProperties);
            }
        });
        groupContainer.buildHierarchy();
    }

    private void loadRoles(String filter, InitialLdapContext context, boolean removeMissingEntries) throws Exception {
        final Collection<String> groupNames = groupContainer.retrievePrincipalNames();
        PagedResultMapper mapper = new AbstractPagedResultMapper() {
            public void mapSearchResult(SearchResult searchResult) throws NamingException {
                String dn = searchResult.getNameInNamespace();
                Attributes attributes = searchResult.getAttributes();
                String commonName = getAttributeValue(attributes, COMMON_NAME_ATTRIBUTE);
                if (commonName.length() != 0) {
                    Long rid = ActiveDirectoryGroup.getRIDFromSID((byte[]) attributes.get(OBJECT_SID_ATTRIBUTE).get());
                    ActiveDirectoryGroup group = new ActiveDirectoryGroup(commonName, dn, getEscapedDn(dn), rid, getRealm());
                    String[] parents = getParents(attributes);
                    String key = groupContainer.storeGroup(group, parents);
                    groupNames.remove(key);
                }
            }
        };

        try {
            String replacedFilter = buildGroupFilter(filter);
            PagedResultTemplate pagedResultTemplate = configuration.getPagedResultTemplate();
            pagedResultTemplate.search(context, replacedFilter, GROUP_ATTRS, mapper);
        } finally {
            if (removeMissingEntries) {
                groupContainer.updateRemovedGroups(groupNames);
            }
        }
    }

    private String[] getParents(Attributes attributes) throws NamingException {
        List<String> parents = new ArrayList<String>();
        Attribute memberOfAttribute = attributes.get(MEMBER_OF_ATTIBUTE);
        if (memberOfAttribute != null) {
            final PagedResultTemplate pagedResultTemplate = configuration.getPagedResultTemplate();
            for (int index = 0; index < memberOfAttribute.size(); index++) {
                String parentDn = (String) memberOfAttribute.get(index);
                if (pagedResultTemplate.isDnValid(parentDn)) {
                    parents.add(parentDn); // valid parent so record
                }
            }
        }
        return parents.toArray(new String[parents.size()]);
    }
    
    private String buildGroupFilter(String filter) {
        String escapedFilter = ActiveDirectoryUserDatabaseConfiguration.getEscapedDn(filter, true);
        String replacedFilter = GROUP_FILTER.replaceAll(GROUPNAME_FILTER_ATTRIBUTE, escapedFilter);

        StringBuilder filterBuilder = new StringBuilder("(");
        filterBuilder.append(replacedFilter);
        if (!configuration.isIncludeDistributionGroups()) {
            /**
             * this seems a little random, basically there is no way of saying a
             * group IS a distribution group. you can OR settings together and
             * work out what they are but the resulting query would be huge.
             */
            int minimumSecurityId = ActiveDirectoryGroupTypes.SECURITY_GROUP | ActiveDirectoryGroupTypes.APP_QUERY_GROUP;
            filterBuilder.append("(" + GROUP_TYPE_ATTRIBUTE + "<=" + String.valueOf(minimumSecurityId) + ")");
        }
        filterBuilder.append(")");
        return filterBuilder.toString();
    }

    protected final boolean areCredentialsValid(ActiveDirectoryUser user, String password) {
        try {
            assertCredentials(user, password);
            return true;
        } catch (Throwable e) {
            logger.error("Failure to authenticate user", e);
            return false;
        }
    }
    
    protected void assertCredentials(ActiveDirectoryUser user, String password) throws Throwable {
        if (configuration.isUserAuthenticationGssApi()) {
            ActiveDirectoryUser activeDirectoryUser = (ActiveDirectoryUser) user;
            LoginContext context = configuration.createLoginContext(activeDirectoryUser.getUserPrincipalName(), password);
            ActiveDirectoryUserDatabaseConfiguration.logoutContext(context);
        } else {
            assertSimpleCredentialsValid(user.getPrincipalName(), password);
        }
    }

    private void assertSimpleCredentialsValid(final String username, final String password) throws Throwable {
        RetryPrivilegedAction action = new RetryPrivilegedAction() {
            protected Object doIt(InitialLdapContext context) {
                return null;
            }

            protected InitialLdapContext getContext(String url) throws Exception {
                String userDn = ((ActiveDirectoryUser) getAccount(username)).getOriginalDn();
                Map<String, String> variables = new HashMap<String, String>(3);
                variables.put(Context.SECURITY_AUTHENTICATION, configuration.getUserAuthenticationType());
                variables.put(Context.SECURITY_PRINCIPAL, userDn);
                variables.put(Context.SECURITY_CREDENTIALS, password);
                return configuration.getInitialContext(url, variables);
            }
        };
        Object result = action.run();
        if (result instanceof Throwable) {
            throw (Throwable) result;
        }
    }

    /**
     * Get a user account given a DN. <code>null</code> will be returned if no
     * such account can be found.
     * 
     * @param dn dn
     * @return user account
     * @throws UserDatabaseException on any error
     */
    public User getAccountFromDN(final String dn) throws UserDatabaseException {
        if (dn.indexOf("CN") > -1 && dn.indexOf("DC") > -1) {
            // This looks like a DN so do a lookup
            if (logger.isDebugEnabled()) {
                logger.debug("Looking up account using DN: " + dn);
            }

            return (User) configuration.doAs(new RetryPrivilegedAction(false) {
                protected Object doIt(InitialLdapContext context) throws NamingException {
                    return getAccountFromDN(dn, context);
                }
            });
        }

        throw new UserDatabaseException("Certificate requires subject to be DN of Active Directory user");
    }

    private User getAccountFromDN(String dn, InitialLdapContext context) throws NamingException {
        String actualDN = null;
        for (StringTokenizer tokens = new StringTokenizer(dn, ","); tokens.hasMoreTokens();) {
            String elm = tokens.nextToken().trim();
            if (elm.toUpperCase().startsWith("CN") || elm.toUpperCase().startsWith("OU") || elm.toUpperCase().startsWith("DC")) {
                actualDN = (actualDN == null ? "" : actualDN + ",") + elm;
            }
        }

        try {
            Attributes attributes = context.getAttributes(actualDN, USER_ATTRS);
            return populateActiveDirectoryUser(dn, attributes);
        } catch (Exception e) {
            logger.error("Cannot locate user for DN " + dn, e);
            throw new NamingException("User not found for DN " + dn);
        }
    }

    private ActiveDirectoryUser populateActiveDirectoryUser(String dn, Attributes attributes) throws NamingException, UserDatabaseException {
        if (attributes == null) {
            throw new NamingException("No attributes for " + dn);
        }

        String username = getAttributeValue(attributes, SAM_ACCOUNT_NAME_ATTRIBUTE);
        String userPrincipalName = getAttributeValue(attributes, USER_PRINCIPAL_NAME_ATTRIBUTE);
        String defaultDomain = getConfiguration().getDomain();
        String email = getAttributeValue(attributes, MAIL_ATTRIBUTE);
        String fullName = getAttributeValue(attributes, DISPLAY_NAME_ATTRIBUTE);
        Date lastPasswordChange = isPasswordChangeAllowed(attributes) ? getPasswordLastSetDate(attributes) : new Date();
        ActiveDirectoryUser user = new ActiveDirectoryUser(username, userPrincipalName, defaultDomain, email, fullName, dn, getEscapedDn(dn), lastPasswordChange, getRealm());

        String homeDirectory = getAttributeValue(attributes, User.USER_ATTR_HOME_DIRECTORY);
        if (homeDirectory.length() != 0) {
            Property.setProperty(new UserAttributeKey(user, User.USER_ATTR_HOME_DIRECTORY), homeDirectory, null);
        }

        String homeDrive = getAttributeValue(attributes, User.USER_ATTR_HOME_DRIVE);
        if (homeDrive.length() != 0) {
            Property.setProperty(new UserAttributeKey(user, User.USER_ATTR_HOME_DRIVE), homeDrive, null);
        }

        ActiveDirectoryGroup[] groups = getGroups(user, attributes);
        if (logger.isDebugEnabled()) {
            logger.debug("User belongs to " + groups.length + " groups");
        }
        user.setRoles(groups);
        return user;
    }

    public static Date getPasswordLastSetDate(Attributes attributes) throws NamingException {
        try {
            String value = getAttributeValue(attributes, PWD_LAST_SET_ATTRIBUTE);
            return ActiveDirectoryUserDatabaseConfiguration.adTimeToJavaDate(Long.parseLong(value));
        } catch (NumberFormatException e) {
            return new Date();
        }
    }

    /**
     * @param attributes
     * @return true if the last password change date should be required
     * @throws NamingException
     */
    protected boolean isPasswordChangeAllowed(Attributes attributes) throws NamingException {
        return false;
    }

    private ActiveDirectoryGroup[] getGroups(ActiveDirectoryUser user, Attributes attributes) throws NamingException, UserDatabaseException {
        Collection<ActiveDirectoryGroup> groups = new ArrayList<ActiveDirectoryGroup>();
        String primaryGroupId = getAttributeValue(attributes, PRIMARY_GROUP_ID_ATTRIBUTE);
        if (primaryGroupId.length() != 0) {
            Long rid = new Long(Long.parseLong(primaryGroupId));

            if (logger.isDebugEnabled()) {
                logger.debug("Users primaryGroupId is " + rid.toString());
            }

            ActiveDirectoryGroup group = groupContainer.getByRid(rid);
            if (group != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Users primary group is " + group.getOriginalDn());
                }
                groups.add(group);
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not find primary group " + rid.toString());
                }
            }
        }

        if (configuration.isMemberOfSupported()) {
            groups.addAll(getUsersGroups(attributes));
        } else {
            groups.addAll(getGroupsForUser(user));
        }
        return groups.toArray(new ActiveDirectoryGroup[groups.size()]);
    }

    private Collection<ActiveDirectoryGroup> getUsersGroups(Attributes attributes) throws NamingException {
        Attribute memberOfAttribute = attributes.get(MEMBER_OF_ATTIBUTE);
        if (memberOfAttribute == null) {
            return Collections.<ActiveDirectoryGroup>emptyList();
        }
        
        Collection<ActiveDirectoryGroup> groups = new ArrayList<ActiveDirectoryGroup>();
        for (int index = 0; index < memberOfAttribute.size(); index++) {
            String groupDn = (String) memberOfAttribute.get(index);
            groups.addAll(getGroupsByDn(groupDn));
        }
        return groups;
    }

    private Collection<ActiveDirectoryGroup> getGroupsByDn(String groupDn) {
        if (logger.isDebugEnabled()) {
            logger.debug("Checking if user is a member of " + groupDn + " a valid group");
        }
        
        Collection<ActiveDirectoryGroup> groups = new ArrayList<ActiveDirectoryGroup>();
        if (groupContainer.containsDn(groupDn)) {
            ActiveDirectoryGroup group = (ActiveDirectoryGroup) groupContainer.getGroupByDn(groupDn);
            if (group != null && !groups.contains(group)) {
                groups.add(group);

                if (logger.isDebugEnabled()) {
                    logger.debug("Member of " + groupDn + " [" + group.getPrincipalName() + "]");
                }

                /**
                 * Add the parent groups for each group since the user
                 * effectively belongs to those groups too.
                 */
                if (group.getParents() != null) {
                    for (int parentIndex = 0; parentIndex < group.getParents().length; parentIndex++) {
                        ActiveDirectoryGroup parentGroup = group.getParents()[parentIndex];
                        if (parentGroup == null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Found NULL parent group");
                            }
                        } else if (!groups.contains(parentGroup)) {
                            groups.add(parentGroup);
                        }
                    }
                }
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("Could not find group " + groupDn);
                }
            }
        }
        return groups;
    }

    private Collection<ActiveDirectoryGroup> getGroupsForUser(final ActiveDirectoryUser user) throws UserDatabaseException {
        final Collection<String> groupDns = new HashSet<String>();
        configuration.doAs(new RetryPrivilegedAction() {
            @Override
            protected Object doIt(InitialLdapContext context) throws Exception {
                PagedResultMapper mapper = new AbstractPagedResultMapper() {
                    public void mapSearchResult(SearchResult searchResult) throws NamingException {
                        groupDns.add(searchResult.getNameInNamespace());
                    }
                };

                String replacedFilter = USER_GROUPS_FILTER.replaceAll(GROUPNAME_FILTER_ATTRIBUTE, user.getDn());
                PagedResultTemplate pagedResultTemplate = configuration.getPagedResultTemplate();
                pagedResultTemplate.search(context, replacedFilter, GROUP_ATTRS, mapper);
                return null;
            }
        });

        Collection<ActiveDirectoryGroup> groups = new ArrayList<ActiveDirectoryGroup>();
        for (String groupDn : groupDns) {
            groups.addAll(getGroupsByDn(groupDn));
        }
        return groups;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.CoreListener#coreEvent(com.adito.core.CoreEvent)
     */
    public void coreEvent(CoreEvent event) {
        // When in install mode, the wizard looks after re-initialising the user
        // database
        if (!ContextHolder.getContext().isSetupMode() && event instanceof PropertyChangeEvent) {
            PropertyChangeEvent changeEvent = (PropertyChangeEvent) event;
            if (changeEvent.getDefinition().getName().startsWith("activeDirectory.")) {
                if (logger.isInfoEnabled()) {
                    logger.info("Active Directory configuration changed. Re-initialising");
                }
                try {
                    configuration.refresh();
                    initialise();
                } catch (Exception e) {
                    logger.error("Failed to re-initialise Active Directory.", e);
                }
            }
        }
    }

    public void open(CoreServlet controllingServlet, Realm realm) throws Exception {
        try {
            super.open(controllingServlet, realm);
            initConfiguration();
            initialise();
            CoreServlet.getServlet().addCoreListener(this);
            threadRunner = new ThreadRunner("CacheUpdater", getCacheUpdaterJob(), configuration.getTimeToLive());
            threadRunner.start();
        } catch (Exception e) {
            close();
            throw e;
        }
    }

    private void initConfiguration() throws Exception {
        configuration = buildConfiguration();
        configuration.postInitialize();
        userContainer = configuration.createUserContainer();
        groupContainer = configuration.createRoleContainer();

        if (logger.isInfoEnabled()) {
            logger.info("Running with configuration = " + configuration.toString());
        }
    }

    protected ActiveDirectoryUserDatabaseConfiguration buildConfiguration() throws IllegalArgumentException, Exception {
        ActiveDirectoryUserDatabaseConfiguration configuration = new ActiveDirectoryUserDatabaseConfiguration(getRealm(), new Properties());
        configuration.setProtocolType(ActiveDirectoryUserDatabaseConfiguration.PLAIN_PROTOCOL);
        return configuration;
    }

    private Runnable getCacheUpdaterJob() {
        return new Runnable() {
            public void run() {
                if (logger.isInfoEnabled()) {
                    logger.info("Caching items");
                    logger.info("Caching roles");
                }

                try {
                    loadRoles(WILDCARD_SEARCH, true);
                } catch (UserDatabaseException e) {
                    logger.error("Error updating cached roles", e);
                }

                if (logger.isInfoEnabled()) {
                    logger.info("Finished caching roles");
                    logger.info("Caching users");
                }

                // once we have the roles, we'll be able to find the users role
                // assignments

                try {
                    loadUsers(WILDCARD_SEARCH, true);
                } catch (UserDatabaseException e) {
                    logger.error("Error updating cached users", e);
                }

                if (logger.isInfoEnabled()) {
                    logger.info("Finished caching users");
                    logger.info("Finished caching items");
                }
            }
        };
    }

    /**
     * I have changed this to obtain the service account user account details.
     * The previous code was not fully contacting the LDAP server, it actually
     * only performed a kerberos login so some errors were not being detected.
     * 
     * @throws NamingException
     */
    private void initialise() throws Exception {
        try {
            String serviceAccountName = configuration.getServiceAccountName();
            if (configuration.isServiceAuthenticationGssApi()) {
                assertServiceAccountValid(serviceAccountName);
            } else {
                getAttributeValue(serviceAccountName, SAM_ACCOUNT_NAME_ATTRIBUTE);
            }
        } catch (Exception e) {
            logger.error("Could not get the service account login context. All Active Directory features will be unavailable. You should check your Service Account Username and Password settings.", e);
            close(); // if we can't talk to the server we shouldn't be classed as open
            throw e;
        }
    }

    private void assertServiceAccountValid(final String serviceAccountName) throws UserNotFoundException, UserDatabaseException {
        Boolean isAccountFound = (Boolean) configuration.doAs(new RetryPrivilegedAction() {
            protected Object doIt(InitialLdapContext context) throws NamingException {
                String replacedFilter = buildUserFilter(serviceAccountName);
                PagedResultTemplate pagedResultTemplate = configuration.getPagedResultTemplate();
                return pagedResultTemplate.searchForResult(context, configuration.getBaseDn(), replacedFilter);
            }
        });
        
        if (!isAccountFound) {
            throw new UserNotFoundException(serviceAccountName + " is not a valid user!");
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.adito.boot.Database#close()
     */
    public void close() throws Exception {
        super.close();
        CoreServlet.getServlet().removeCoreListener(this);
        if (threadRunner != null) {
            threadRunner.stop();
        }
        userContainer.close();
        groupContainer.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.UserDatabase#logout(com.adito.security.User)
     */
    public void logout(User user) {
    }

    /**
     * The application must supply a PrivilegedAction that is to be run inside a
     * Subject.doAs() or Subject.doAsPrivileged().
     */
    protected abstract class RetryPrivilegedAction implements PrivilegedAction<Object> {
        private final boolean returnExceptionNotNull;
        private final String hosts;

        protected RetryPrivilegedAction() {
            this(true);
        }

        protected RetryPrivilegedAction(boolean returnExceptionNotNull) {
            this.returnExceptionNotNull = returnExceptionNotNull;
            hosts = configuration.getContactableActiveDirectories();
        }

        public final Object run() {
            long startTime = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("Starting Timed Operation");
            }

            InitialLdapContext context = null;
            try {
                context = getContext(hosts);
                Object doIt = doIt(context);
                return doIt;
            } catch (Exception e) {
                logger.error("Failed to run action.", e);
                return returnExceptionNotNull ? e : null;
            } finally {
                close(context);

                long finishTime = System.currentTimeMillis();
                if (logger.isDebugEnabled()) {
                    logger.debug("Finished Timed Operation in " + ((finishTime - startTime) / 1000) + " seconds");
                }
            }
        }

        protected InitialLdapContext getContext(String url) throws Exception {
            return configuration.getAuthenticatedContext(url);
        }

        protected abstract Object doIt(InitialLdapContext context) throws Exception;
    }

    protected String getAttributeValue(String dn, String attributeName) throws NamingException, UserDatabaseException {
        Attributes attributes = getAttributes(dn, new String[] { attributeName });
        return getAttributeValue(attributes, attributeName);
    }

    protected Attributes getAttributes(final String dn, final String... attributes) throws UserDatabaseException {
        return (Attributes) configuration.doAs(new RetryPrivilegedAction() {
            protected Object doIt(InitialLdapContext context) throws NamingException {
                return context.getAttributes(dn, attributes);
            }
        });
    }

    protected static String getAttributeValue(Attributes attributes, String attributeName) throws NamingException {
        if (attributes == null) {
            return null;
        }
        Attribute attribute = attributes.get(attributeName);
        return attribute == null ? "" : (String) attribute.get();
    }

    /**
     * As some characters are escaped via a backslash, we need to add some more
     * slashes to get this to work.
     * 
     * @param dn
     * @return String
     */
    private static String getEscapedDn(String dn) {
        String escapeDn = dn.replaceAll("\\\\", "\\\\\\\\");
        String escapeForwardSlash = escapeDn.replaceAll("/", "\\\\/");
        return escapeForwardSlash;
    }

    protected static void close(InitialLdapContext context) {
        if (context != null) {
            try {
                context.close();
            } catch (NamingException e) {
                // ignore
            }
        }
    }
}