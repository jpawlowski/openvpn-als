/*
 *  Adito-PAM
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
package com.adito.pam;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import net.sf.jpam.Pam;
import net.sf.jpam.PamReturnValue;

import com.adito.core.CoreJAASConfiguration;
import com.adito.core.CoreServlet;
import com.adito.policyframework.Principal;
import com.adito.properties.Property;
import com.adito.properties.impl.realms.RealmKey;
import com.adito.realms.Realm;
import com.adito.security.AccountLockedException;
import com.adito.security.DefaultUserDatabase;
import com.adito.security.InvalidLoginCredentialsException;
import com.adito.security.Role;
import com.adito.security.User;
import com.adito.security.UserDatabaseException;
import com.adito.security.UserNotFoundException;

/**
 * This is the PAM Extension Core.
 *
 *
 */
public class PAMUserDatabase extends DefaultUserDatabase
{

	private final static String DEFAULT_SERVICE_NAME = "adito";
	private final static String DESCRIPTION = "PAM";
	private final static boolean SUPPORTS_ACCOUNT_CREATION = false;
	private final static boolean SUPPORTS_PASSWORD_CHANGE = false;
	private Pam pam;
	private String serviceName;
	private Map<String, PAMUser> users;
	private Map<String, PAMGroup> groups;
	
	/**
	 * Default Constructor
	 * 
	 */
	public PAMUserDatabase() {
		super(DESCRIPTION, SUPPORTS_ACCOUNT_CREATION, SUPPORTS_PASSWORD_CHANGE);
		users = new HashMap<String, PAMUser>();
		groups = new HashMap<String, PAMGroup>();
	}
	
	/* (non-Javadoc)
	 * @see com.adito.security.DefaultUserDatabase#open(com.adito.core.CoreServlet, com.adito.realms.Realm)
	 */
	@Override
	public void open(CoreServlet controllingServlet, Realm realm) throws Exception {
		super.open(controllingServlet, realm);
		init();
	}

	/**
	 * Initialisation, called by open method.
	 * <ul>
	 * 		<li>Set members from Property</li>
	 * 		<li>Generate JAAS Configuration</li>
	 * 		<li>Add generated JAAS Configuration</li>
	 * 		<li>Instantiate members with property dependencies</li>
	 * 		<li>Add defaults groups</li>
	 * </ul>
	 */
	private void init() {
		// set configuration
		setServiceName(Property.getProperty(new RealmKey("pam.serviceName",this.realm))); 

		// Generate JAAS Configuration
		Map<String, String> params = new HashMap<String, String>();
		params.put("serviceName", serviceName);
		AppConfigurationEntry entry = new AppConfigurationEntry("net.sf.jpam.jaas.JpamLoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, params);
		
		// Add generated JAAS Configuration
		CoreJAASConfiguration config = (CoreJAASConfiguration) Configuration.getConfiguration();
		config.addAppConfigurationEntry(PAMUserDatabase.class.getName(), entry);

		// Instantiate members with Property dependencies
		pam = new Pam(serviceName);

		// Add defaults groups
		addGroup("Users");
		
	}

	/**
	 * @param groupName
	 * @return created group
	 * Add group to PAM Manager
	 */
	private PAMGroup addGroup(String groupName) {
		PAMGroup group = new PAMGroup(groupName,this.realm);
		groups.put(groupName, group);
		return group;
	}

	
	/* (non-Javadoc)
	 * @see com.adito.security.UserDatabase#checkPassword(java.lang.String, java.lang.String)
	 */
	public boolean checkPassword(String username, String password) throws UserDatabaseException, InvalidLoginCredentialsException {
		return pam.authenticateSuccessful(username, password);
	}

	/* (non-Javadoc)
	 * @see com.adito.security.UserDatabase#getAccount(java.lang.String)
	 */
	public User getAccount(String username) throws UserNotFoundException, Exception {
		// TODO Check if username exists, need to checks the dependencie to PAM modules.
		PAMUser user = users.get(username);
		if (user == null) {
			PamReturnValue r = pam.authenticate(username, "");
			if (r != PamReturnValue.PAM_USER_UNKNOWN) {
				user = new PAMUser(username, this.realm);
				user.addGroup(groups.get("Users"));
				users.put(username, user);
			}
		}
		return user; 
	}

	/* (non-Javadoc)
	 * @see com.adito.security.UserDatabase#getRole(java.lang.String)
	 */
	public Role getRole(String rolename) throws Exception {
		PAMGroup group = groups.get(rolename);
		if (group == null) {
			throw new Exception("Invalid rolename");
		}
		return group;
	}

	/* (non-Javadoc)
	 * @see com.adito.security.UserDatabase#listAllRoles(java.lang.String)
	 */
	public Role[] listAllRoles(String filter) throws Exception {
		Collection<PAMGroup> values = groups.values();
		return groups.values().toArray(new PAMGroup[values.size()]);
	}

	/* (non-Javadoc)
	 * @see com.adito.security.UserDatabase#listAllUsers(java.lang.String)
	 */
	public User[] listAllUsers(String filter) throws Exception {
		Collection<PAMUser> values = users.values();
		User[] users = values.toArray(new User[values.size()]);
		return users;
	}

	/* (non-Javadoc)
	 * @see com.adito.security.UserDatabase#listAvailablePrincipals()
	 */
	public Principal[] listAvailablePrincipals() throws Exception {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.adito.security.UserDatabase#logon(java.lang.String, java.lang.String)
	 */
	public User logon(String username, String password) throws UserDatabaseException, InvalidLoginCredentialsException,	AccountLockedException {
		PAMCallbackHandler callback = new PAMCallbackHandler();
		callback.setUserId(username);
		callback.setPassword(password);

		try {
			LoginContext context = new LoginContext(PAMUserDatabase.class.getName(), callback);
			context.login();
		}
		catch (LoginException e) {
			throw new InvalidLoginCredentialsException(e.getMessage());
		}
		return users.get(username);
	}
	
	/* (non-Javadoc)
	 * @see com.adito.security.UserDatabase#logout(com.adito.security.User)
	 */
	public void logout(User user) {
	}
	
	/**
	 * This method sets the service name to be used. 
	 * @param newServiceName The new service name. If null, DEFAULT_SERVICE_NAME will be used.
	 */
	private void setServiceName(String newServiceName) {
		if ((newServiceName == null) || (newServiceName.length() == 0)) {
			serviceName = DEFAULT_SERVICE_NAME;
		}
		else {
			serviceName = newServiceName;
		}
	}
	


}
