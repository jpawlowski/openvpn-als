
				/*
 *  OpenVPN-ALS
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
			
package net.openvpn.als.jdbc;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.SystemProperties;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.realms.Realm;
import net.openvpn.als.security.AccountLockedException;
import net.openvpn.als.security.DefaultUser;
import net.openvpn.als.security.DefaultUserDatabase;
import net.openvpn.als.security.InvalidLoginCredentialsException;
import net.openvpn.als.security.Role;
import net.openvpn.als.security.User;
import net.openvpn.als.security.UserDatabaseException;
import net.openvpn.als.security.UserNotFoundException;

/**
 * @author lee
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class JDBCUserDatabase extends DefaultUserDatabase {

	static Log log = LogFactory.getLog(JDBCUserDatabase.class);

	// JDBCConnection con;
	JDBCDatabaseEngine db;

	HashMap permissionModules = new HashMap();

    /**
     * Constant for the database type.
     */
    public static final String DATABASE_TYPE = "builtIn"; 

	/**
	 * Constructor
	 */
	public JDBCUserDatabase() {
		super("JDBC", true, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.core.Database#open(net.openvpn.als.core.CoreServlet)
	 */
    public void open(CoreServlet controllingServlet, Realm realm) throws Exception {
        super.open(controllingServlet, realm);
        String dbName = SystemProperties.get("openvpnals.userDatabase.jdbc.dbName", "explorer_configuration");
        controllingServlet.addDatabase(dbName, ContextHolder.getContext().getDBDirectory());
        File upgradeDir = new File("install/upgrade");
        String jdbcUser = SystemProperties.get("openvpnals.jdbc.username", "sa");
        String jdbcPassword = SystemProperties.get("openvpnals.jdbc.password", "");
        String vendorDB = SystemProperties.get("openvpnals.jdbc.vendorClass", "net.openvpn.als.jdbc.hsqldb.HSQLDBDatabaseEngine");
        if (log.isInfoEnabled()) {
            log.info("User database is being opened...");
            log.info("JDBC vendor class implementation is " + vendorDB);
        }
        db = (JDBCDatabaseEngine) Class.forName(vendorDB).newInstance();
        db.init("userDatabase", dbName, jdbcUser, jdbcPassword, null);
        DBUpgrader upgrader = new DBUpgrader(ContextHolder.getContext().getVersion(), db, ContextHolder.getContext()
                        .getDBDirectory(), upgradeDir);
        upgrader.upgrade();
        open = true;
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.security.UserDatabase#logon(java.lang.String, java.lang.String)
	 */
	public User logon(String username, String password)
			throws UserDatabaseException, InvalidLoginCredentialsException,
			AccountLockedException {
		JDBCPreparedStatement ps = null;
		try {
			ps = db.getStatement("logon.user");
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setInt(3, realm.getResourceId());
			ResultSet results = ps.executeQuery();
			try {
				while (results.next()) {
                    try {
                        DefaultUser user = new DefaultUser(results.getString("username"), results.getString("email"), results
                                        .getString("fullname"), results.getDate("last_password_change"),
                                        UserDatabaseManager.getInstance().getRealm(results.getInt("realm_ID")));
                        addRoles(user);
                        return user;
                    } catch (Exception e) {
                        throw new UserDatabaseException("Failed to get user realm.");
                    }
				}
			} finally {
				results.close();
			}
		} catch (UserDatabaseException ude) {
			throw ude;
		} catch (SQLException ex) {
			throw new UserDatabaseException("Failed to execute SQL query", ex);
		} catch (ClassNotFoundException ex) {
			throw new UserDatabaseException("Failed to execute SQL query", ex);
		} finally {
			try {
				if (ps != null) {
					ps.releasePreparedStatement();
				}
			} catch (SQLException e) {
			}
		}
		throw new InvalidLoginCredentialsException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.enterprise.admin.UserDatabase#logout(com.sshtools.enterprise.admin.User)
	 */
	public void logout(User user) {
		// Nothing needed to logout of the db.
	}

	
	
	public Iterable<User> allUsers() throws UserDatabaseException {
		try {
            // Get the allowed roles from the database
            JDBCPreparedStatement ps = db.getStatement("select.users");
            try {
            	ResultSet results = ps.executeQuery();
            	try {
            		Vector<User> tmp = new Vector<User>();
            		while (results.next()) {
            			String username = results.getString("username");
                        Realm usersRealm = UserDatabaseManager.getInstance().getRealm(results.getInt("realm_ID"));
            			if (usersRealm.equals(realm)) {
            				DefaultUser u = new DefaultUser(results
            						.getString("username"), results
            						.getString("email"), results
            						.getString("fullname"), results
            						.getDate("last_password_change"), usersRealm);
            				addRoles(u);
            				tmp.add(u);
            			}
            		}
            		return tmp;
            	} finally {
            		results.close();
            	}
            } finally {
            	ps.releasePreparedStatement();
            }
        } catch (Exception e) {
            throw new UserDatabaseException("failed to get all users", e);
        } 
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.security.UserDatabase#getAccount(java.lang.String)
	 */
	public User getAccount(String username) throws UserNotFoundException, Exception {
		// Get the allowed roles from the database
		JDBCPreparedStatement ps = db.getStatement("select.user");
		try {
			ps.setString(1, username);
			ResultSet results = ps.executeQuery();
			try {
				while (results.next()) {
                    Realm usersRealm = UserDatabaseManager.getInstance().getRealm(results.getInt("realm_ID"));
                    if (usersRealm.equals(realm)){
						DefaultUser u = new DefaultUser(results
								.getString("username"), results.getString("email"),
								results.getString("fullname"), results
									.getDate("last_password_change"), usersRealm);
						addRoles(u);
						return u;
					}
                }
                throw new UserNotFoundException("No user exists with username " + username + " in realm " + realm.getResourceDisplayName());
			} finally {
				results.close();
			}
		} finally {
			ps.releasePreparedStatement();
		}
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.security.DefaultUserDatabase#createAccount(java.lang.String, java.lang.String, java.lang.String, java.lang.String, net.openvpn.als.security.Role[])
	 */
	public User createAccount(String username, String password, String email,
			String fullname, Role[] roles)
			throws Exception {
		JDBCPreparedStatement ps = db.getStatement("create.account");
		try {
			ps.setString(1, username);
			ps.setString(2, email);
			ps.setString(3, password);
			ps.setString(4, fullname);
            ps.setInt(5, realm.getResourceId());
			ps.execute();
			for (int i = 0; roles != null && i < roles.length; i++) {
				JDBCPreparedStatement ps2 = db.getStatement("assign.role");
				try {
					ps2.setString(1, username);
					ps2.setString(2, roles[i].getPrincipalName());
                    ps2.setInt(3, realm.getResourceId());
					ps2.execute();
				} finally {
					ps2.releasePreparedStatement();
				}
			}
		} finally {
			ps.releasePreparedStatement();
		}
		return getAccount(username);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.enterprise.admin.UserDatabase#updateAccount(com.sshtools.enterprise.admin.User,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public void updateAccount(User user, String email, String fullname,
			Role[] roles) throws Exception {
		JDBCPreparedStatement ps = db.getStatement("update.account");
		try {
			ps.setString(1, email);
			ps.setString(2, fullname);
			ps.setString(3, user.getPrincipalName());
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}
		ps = db.getStatement("delete.roles");
		try {
			ps.setString(1, user.getPrincipalName());
            ps.setInt(2, user.getRealm().getResourceId());
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}
		ps = db.getStatement("assign.role");
		try {
			for (int i = 0; roles != null && i < roles.length; i++) {
				ps.setString(1, user.getPrincipalName());
				ps.setString(2, roles[i].getPrincipalName());
                ps.setInt(3, user.getRealm().getResourceId());
				ps.execute();
				ps.reset();
			}
		} finally {
			ps.releasePreparedStatement();
		}
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.security.DefaultUserDatabase#deleteAccount(net.openvpn.als.security.User)
	 */
	public void performDeleteAccount(User user) throws Exception, UserNotFoundException {
        this.getAccount(user.getPrincipalName());
		JDBCPreparedStatement ps = db.getStatement("delete.account");
		try {
			ps.setString(1, user.getPrincipalName());
            ps.setInt(2, user.getRealm().getResourceId());
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}
		ps = db.getStatement("delete.account.roles");
		try {
			ps.setString(1, user.getPrincipalName());
            ps.setInt(2, user.getRealm().getResourceId());
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.security.UserDatabase#checkPassword(java.lang.String, java.lang.String)
	 */
	public boolean checkPassword(String username, String password)
			throws UserDatabaseException, InvalidLoginCredentialsException {
		try {
			JDBCPreparedStatement ps = db.getStatement("logon.user");
			try {
				ps.setString(1, username);
				ps.setString(2, password);
				ps.setInt(3, realm.getResourceId());
				ResultSet results = ps.executeQuery();
				try {
					return results.next();
				} finally {
					results.close();
				}
			} finally {
				ps.releasePreparedStatement();
			}
		} catch (Exception ex) {
			throw new UserDatabaseException("Failed to execute SQL query", ex);
		}
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.security.DefaultUserDatabase#changePassword(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void changePassword(String username, String oldPassword,
			String password, boolean forcePasswordChangeAtLogon)
			throws UserDatabaseException, InvalidLoginCredentialsException {
		JDBCPreparedStatement ps = null;
		try {
			if (forcePasswordChangeAtLogon) {
				ps = db.getStatement("change.password.force");
				ps.setString(1, password);
				ps.setString(2, username);
				ps.setInt(3, realm.getResourceId());
			} else {
				ps = db.getStatement("change.password");
				ps.setString(1, password);
				ps.setString(2, username);
				ps.setInt(3, realm.getResourceId());
			}
			ps.execute();
		} catch (Exception e) {
			throw new UserDatabaseException(
					"Failed to change password for user " + username + " in realm " + realm + ".");
		} finally {
			if (ps != null) {
				try {
					ps.releasePreparedStatement();
				} catch (SQLException e) {
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.security.DefaultUserDatabase#setPassword(java.lang.String, java.lang.String, boolean, net.openvpn.als.security.User, java.lang.String)
	 */
	public void setPassword(String username, String password,
			boolean forcePasswordChangeAtLogon, User adminUser,
			String adminPassword) throws UserDatabaseException,
			InvalidLoginCredentialsException {
		changePassword(username, "", password, forcePasswordChangeAtLogon);
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.security.UserDatabase#getRole(java.lang.String)
	 */
	public Role getRole(String rolename) throws Exception {
		JDBCPreparedStatement ps = db.getStatement("select.role");
		try {
			ps.setString(1, rolename);
            ps.setInt(2, realm.getResourceId());
			ResultSet results = ps.executeQuery();
			try {
				if (results.next()) {
                    JDBCRole r = new JDBCRole(results.getString("rolename"), realm);
					return r;
				}
			} finally {
				results.close();
			}
		} finally {
			ps.releasePreparedStatement();
		}
		throw new Exception("No role exists with rolename " + rolename);
	}

	public Iterable<Role> allRoles() throws UserDatabaseException {
		try {
            JDBCPreparedStatement ps = db.getStatement("select.roles");
            try {
                ResultSet results = ps.executeQuery();
                try {
                    Vector<Role> tmp = new Vector<Role>();
                    while (results.next()) {
                        String rolename = results.getString("rolename");
                        Realm usersRealm = UserDatabaseManager.getInstance().getRealm(results.getInt("realm_ID"));
                        if (usersRealm.equals(realm)) {
                            JDBCRole r = new JDBCRole(rolename, realm);
                            tmp.add(r);
                        }
                    }
                    return tmp;
                } finally {
                    results.close();
                }
            } finally {
                ps.releasePreparedStatement();
            }
        } catch (Exception e) {
            throw new UserDatabaseException("failed to get all roles", e);
        }
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.security.DefaultUserDatabase#createRole(java.lang.String)
	 */
	public Role createRole(String rolename) throws Exception {
		JDBCPreparedStatement ps = db.getStatement("create.role");
		try {
			ps.setString(1, rolename);
            ps.setInt(2, realm.getResourceId());
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}
		return getRole(rolename);
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.security.DefaultUserDatabase#deleteRole(java.lang.String)
	 */
	public void deleteRole(String rolename) throws Exception {
		JDBCPreparedStatement ps = db.getStatement("delete.role.1");
		try {
			ps.setString(1, rolename);
            ps.setInt(2, realm.getResourceId());
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}
		ps = db.getStatement("delete.role.2");
		try {
			ps.setString(1, rolename);
            ps.setInt(2, realm.getResourceId());
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}
	}

	/**
	 * @param u
	 * @throws UserDatabaseException
	 */
	private void addRoles(DefaultUser u) throws UserDatabaseException {
		JDBCPreparedStatement ps = null;
		try {
			ps = db.getStatement("select.user.roles");
			ps.setString(1, u.getPrincipalName());
            ps.setInt(2, u.getRealm().getResourceId());
			ResultSet r2 = ps.executeQuery();
			List<Role> roles = new ArrayList<Role>();
			try {
				while (r2.next()) {
					Role r = getRole(r2.getString("rolename"));
					roles.add(r);
				}
			} finally {
				r2.close();
			}
			Role[] r = new Role[roles.size()];
			roles.toArray(r);
			u.setRoles(r);
		} catch (Exception e) {
			throw new UserDatabaseException("Failed to add roles to user.", e);
		} finally {
			if (ps != null) {
				try {
					ps.releasePreparedStatement();
				} catch (SQLException e) {
				}
			}
		}
	}
}
