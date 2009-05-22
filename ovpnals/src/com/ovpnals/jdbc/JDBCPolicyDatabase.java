
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
			
package com.ovpnals.jdbc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cache.Cache;
import org.apache.commons.cache.CacheStat;
import org.apache.commons.cache.MemoryStash;
import org.apache.commons.cache.SimpleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.boot.ContextHolder;
import com.ovpnals.boot.PropertyList;
import com.ovpnals.boot.SystemProperties;
import com.ovpnals.core.CoreEvent;
import com.ovpnals.core.CoreEventConstants;
import com.ovpnals.core.CoreListener;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.policyframework.AbstractPolicyDatabase;
import com.ovpnals.policyframework.AccessRight;
import com.ovpnals.policyframework.AccessRights;
import com.ovpnals.policyframework.DefaultAccessRights;
import com.ovpnals.policyframework.DefaultPolicy;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.Policy;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.PolicyUtil;
import com.ovpnals.policyframework.Principal;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.ResourceAttachedToPolicyEvent;
import com.ovpnals.policyframework.ResourceDetachedFromPolicyEvent;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.policyframework.forms.AbstractWizardPersonalResourcePolicyForm;
import com.ovpnals.realms.Realm;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.Role;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;
import com.ovpnals.security.UserDatabase;
import com.ovpnals.security.UserNotFoundException;

/**
 * Concrete implementation of a
 * {@link com.ovpnals.policyframework.PolicyDatabase} that stores policy
 * information in a JDBC compliant database.
 */
public class JDBCPolicyDatabase extends AbstractPolicyDatabase {
	final static Log log = LogFactory.getLog(JDBCPolicyDatabase.class);

	private JDBCDatabaseEngine db;

	final static Long CACHE_TTL = new Long(SystemProperties.get(
			"ovpnals.jdbcPolicyDatabase.cacheTTL", "180000"));

	final static Integer CACHE_MAXOBJS = new Integer(SystemProperties.get(
			"ovpnals.jdbcPolicyDatabase.cacheMaxObjs", "2000"));

	final static Long CACHE_COST = new Long(0);

	// Caches
	private Cache policyCache;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#getPolicy(int)
	 */
	public Policy getPolicy(int id) throws Exception {
		String cacheKey = "policy-" + id;
		Policy pol = (Policy) policyCache.retrieve(cacheKey);
		if (pol == null) {
			// Get the top level policy
			JDBCPreparedStatement ps = db.getStatement("getPolicy.selectById");
			ps.setInt(1, id);
			try {
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					pol = buildPolicy(rs);
				}
			} finally {
				ps.releasePreparedStatement();
			}
			if (pol != null) {
				storeToCache(cacheKey, (Serializable) pol);
			}
		}
		return pol;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.policyframework.PolicyDatabase#getPolicies(com.ovpnals.realms.Realm)
	 */
	public List<Policy> getPolicies(Realm realm) throws Exception {
        String cacheKey = "policyByRealm-" + realm.getResourceId();
		List<Policy> l = (List<Policy>) policyCache.retrieve(cacheKey);
		if (l == null) {
			// Get the top level policy
            JDBCPreparedStatement ps = db.getStatement("getPolicy.selectByRealmId");
            ps.setInt(1, realm.getResourceId());
			l = new ArrayList<Policy>();
			try {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					l.add(buildPolicy(rs));
				}
			} finally {
				ps.releasePreparedStatement();
			}
			storeToCache(cacheKey, (Serializable) l);
		}
		return l;
	}

    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.PolicyDatabase#getPoliciesExcludePersonal(com.ovpnals.realms.Realm)
     */
    public List<Policy> getPoliciesExcludePersonal(Realm realm) throws Exception {
        String cacheKey = "policyByRealm-" + realm.getRealmID() + "-excludeType" + Policy.TYPE_PERSONAL;
        List<Policy> l = (List<Policy>) policyCache.retrieve(cacheKey);
        if (l == null) {
            // Get the top level policy
            JDBCPreparedStatement ps = db.getStatement("getPolicy.excludeType.selectByRealmId");
            ps.setInt(1, realm.getRealmID());
            ps.setInt(2, Policy.TYPE_PERSONAL);
            l = new ArrayList<Policy>();
            try {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    l.add(buildPolicy(rs));
                }
            } finally {
                ps.releasePreparedStatement();
            }
            storeToCache(cacheKey, (Serializable) l);
        }
        return l;
    }
    
    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.PolicyDatabase#getPolicies()
     */
    public List<Policy> getPolicies() throws Exception {
	    String cacheKey = "policies";
	    List<Policy> l = (List<Policy>) policyCache.retrieve(cacheKey);
	    if (l == null) {
	        // Get the top level policy
	        JDBCPreparedStatement ps = db.getStatement("getPolicies.select");
	        l = new ArrayList<Policy>();
	        try {
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) {
	                l.add(buildPolicy(rs));
	            }
	        } finally {
	            ps.releasePreparedStatement();
	        }
	        storeToCache(cacheKey, (Serializable) l);
	    }
	    return l;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.policyframework.PolicyDatabase#createPolicy(java.lang.String, java.lang.String, int, int)
	 */
	public Policy createPolicy(String name, String description, int type, int realmID) throws Exception {
		policyCache.clear();
		JDBCPreparedStatement ps = db.getStatement("createPolicy.insert");
		ps.startTransaction();
		ps.setInt(1, type);
		ps.setString(2, name);
		ps.setString(3, description);
		Calendar c = Calendar.getInstance();
		ps.setString(4, db.formatTimestamp(c));
		ps.setString(5, db.formatTimestamp(c));
        ps.setInt(6, realmID);
		try {
			try {
				ps.execute();
				int id = db.getLastInsertId(ps, "createPolicy.lastInsertId");
				ps.commit();
				return new DefaultPolicy(id, name, description, type, c, c, realmID);
			} finally {
				ps.releasePreparedStatement();
			}
		} catch (Exception e) {
			ps.rollback();
			throw e;
		} finally {
			ps.endTransaction();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#updatePolicy(com.ovpnals.policyframework.Policy)
	 */
	public void updatePolicy(Policy policy) throws Exception {
		Policy oldPolicy = getPolicy(policy.getResourceId());
		if (oldPolicy == null) {
			throw new Exception("Cannot update a policy that doesnt exist");
		}
		policyCache.clear();
		JDBCPreparedStatement ps = db.getStatement("updatePolicy.update");
		ps.setInt(1, policy.getType());
		ps.setString(2, policy.getResourceName());
		ps.setString(3, policy.getResourceDescription());
		Calendar c = Calendar.getInstance();
		ps.setString(4, db.formatTimestamp(c));
		ps.setInt(5, policy.getResourceId());

		try {
			ps.execute();
			policy.setDateAmended(c);
		} finally {
			ps.releasePreparedStatement();
		}
	}

	public Policy deletePolicy(int id) throws Exception {
		Policy oldPolicy = getPolicy(id);
		if (oldPolicy == null) {
			throw new Exception("Cannot delete a policy that doesnt exist");
		}
		policyCache.clear();
		// Now delete this policy
		JDBCPreparedStatement ps = db.getStatement("deletePolicy.delete");
		ps.setInt(1, id);
		try {
			ps.execute();
			ps = db.getStatement("deletePolicy.relationships1");
			ps.setInt(1, id);
			ps.execute();
			ps = db.getStatement("deletePolicy.relationships2");
			ps.setInt(1, id);
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}
		return oldPolicy;
	}

    /**
     * Get if a principal has been graded a policy.
     * 
     * @param policy policy
     * @param principal principal
     * @return granted
     * @throws Exception on any error
     */
	private boolean isPolicyGrantedToPrincipal(Policy policy, Principal principal)
			throws Exception {
		if(principal==null) {
			if(log.isInfoEnabled())
				log.info("NULL principal found!");
			return false;
		}
		if (policy.getResourceId() == getEveryonePolicyIDForRealm(principal.getRealm())) {
			return true;
		}
		String cacheKey = "policyGrantedToPrincipal-" + policy.getResourceId()
				+ "-" + principal.getPrincipalName() + "-" + principal.getRealm().getResourceId();
		Boolean val = (Boolean) policyCache.retrieve(cacheKey);
		if (val == null) {
			JDBCPreparedStatement ps = db
					.getStatement("isPolicyGrantedToPrincipal.select");
			ps.setInt(1, policy.getResourceId());
			ps.setString(2, principal.getPrincipalName());
			boolean found = false;
			try {
				ResultSet rs = ps.executeQuery();
				try {
					found = rs.next();
				} finally {
					rs.close();
				}
			} finally {
				ps.releasePreparedStatement();
			}
			storeToCache(cacheKey, Boolean.valueOf(found));
			val = Boolean.valueOf(found);
		}
		return val.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#grantPolicyToPrincipal(com.ovpnals.policyframework.Policy,
	 *      com.ovpnals.permissions.Principal)
	 */
	public void grantPolicyToPrincipal(Policy policy, Principal principal)
			throws Exception {
		if (policy.getResourceId() == getEveryonePolicyIDForRealm(principal.getRealm())) {
			throw new Exception(
					"Cannot grant special Everyone policy to any principal, it is granted by default.");
		}
		policyCache.clear();
		JDBCPreparedStatement ps = db
				.getStatement("grantPolicyToPrincipal.insert");
		ps.setInt(1, policy.getResourceId());
		ps.setString(2, principal.getPrincipalName());
		ps.setInt(3, (principal instanceof User) ? Policy.PRINCIPAL_USER
				: Policy.PRINCIPAL_GROUP);
		try {
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#revokePolicyFromPrincipal(com.ovpnals.policyframework.Policy,
	 *      com.ovpnals.permissions.Principal)
	 */
	public void revokePolicyFromPrincipal(Policy policy, Principal principal)
			throws Exception {
		if (policy.getResourceId() == getEveryonePolicyIDForRealm(principal.getRealm())) {
			throw new Exception(
					"Cannot revoke special Everyone policy from any principal.");
		}
		policyCache.clear();
		JDBCPreparedStatement ps = db
				.getStatement("revokePolicyFromPrincipal.delete");
		ps.setInt(1, policy.getResourceId());
		ps.setString(2, principal.getPrincipalName());
		ps.setInt(3, (principal instanceof User) ? Policy.PRINCIPAL_USER
				: Policy.PRINCIPAL_GROUP);
		try {
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#revokeAllPoliciesFromPrincipal(com.ovpnals.permissions.Principal)
	 */
	public void revokeAllPoliciesFromPrincipal(Principal principal)
			throws Exception {
		policyCache.clear();
		JDBCPreparedStatement ps = db
				.getStatement("revokeAllPoliciesFromPrincipal.delete");
		ps.setString(1, principal.getPrincipalName());
		try {
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}

	}
	
    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.PolicyDatabase#revokeAllPoliciesFromPrincipals(com.ovpnals.realms.Realm)
     */
    public void revokeAllPoliciesFromPrincipals(Realm realm) throws Exception {
        policyCache.clear();
        JDBCPreparedStatement ps = db.getStatement("revokeAllPoliciesFromPrincipals.delete");
        try {
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
    }

	/* (non-Javadoc)
	 * @see com.ovpnals.policyframework.PolicyDatabase#attachResourceToPolicy(com.ovpnals.policyframework.Resource, com.ovpnals.policyframework.Policy, int)
	 */
	public void attachResourceToPolicy(Resource resource, Policy policy, int sequence, Realm realm)
			throws Exception {
		policyCache.clear();
		JDBCPreparedStatement ps = db
				.getStatement("attachResourceToPolicy.insert");
		ps.setInt(1, resource.getResourceId());
		ps.setInt(2, resource.getResourceType().getResourceTypeId());
		ps.setInt(3, policy.getResourceId());
        ps.setInt(4, sequence);
        ps.setInt(5, realm.getResourceId());
		try {
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#detachResourceFromPolicy(com.ovpnals.policyframework.Resource,
	 *      com.ovpnals.policyframework.Policy)
	 */
	public void detachResourceFromPolicy(Resource resource, Policy policy, Realm realm)
			throws Exception {
		policyCache.clear();
		JDBCPreparedStatement ps = db
				.getStatement("detachResourceFromPolicy.delete");
		ps.setInt(1, resource.getResourceId());
		ps.setInt(2, resource.getResourceType().getResourceTypeId());
        ps.setInt(3, policy.getResourceId());
        ps.setInt(4, realm.getResourceId());
		try {
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#isResourceAttachedToPolicy(com.ovpnals.policyframework.Resource,
	 *      com.ovpnals.policyframework.Policy)
	 */
	public boolean isResourceAttachedToPolicy(Resource resource, Policy policy, Realm realm)
			throws Exception {
		String cacheKey = "resourcePolicy-" + resource.getResourceId() + "-"
				+ resource.getResourceType().getResourceTypeId() + "-"
				+ policy.getResourceId();
		Boolean val = (Boolean) policyCache.retrieve(cacheKey);
		if (val == null) {
			JDBCPreparedStatement ps = db
					.getStatement("isResourceAttachedToPolicy.select");
			ps.setInt(1, resource.getResourceId());
			ps.setInt(2, resource.getResourceType().getResourceTypeId());
            ps.setInt(3, policy.getResourceId());
            ps.setInt(4, realm.getResourceId());
			try {
				ResultSet rs = ps.executeQuery();
				try {
					val = new Boolean(rs.next());
				} finally {
					rs.close();
				}
			} finally {
				ps.releasePreparedStatement();
			}
			storeToCache(cacheKey, val);
		}
		return val.booleanValue();
	}


	/* (non-Javadoc)
	 * @see com.ovpnals.policyframework.PolicyDatabase#isPrincipalAllowed(com.ovpnals.policyframework.Principal, com.ovpnals.policyframework.Resource, boolean)
	 */
	public boolean isPrincipalAllowed(Principal principal, Resource resource,
			boolean includeSuperUser) throws Exception {

		String cacheKey = "principalAllowed-" + principal.getPrincipalName() + "-realmID-" + principal.getRealm().getResourceId()
				+ "-" + resource.getResourceId() + "-"
				+ resource.getResourceType().getResourceTypeId() + "-"
				+ includeSuperUser;
		Boolean val = (Boolean) policyCache.retrieve(cacheKey);
		if (val == null) {

			if (principal instanceof User && includeSuperUser) {
				if (LogonControllerFactory.getInstance()
						.isAdministrator((User) principal)) {
					val = Boolean.TRUE;
					storeToCache(cacheKey, val);
					return val.booleanValue();
				}
			}
            
            Policy p = getGrantingPolicy(principal, resource);
            val = p == null ? Boolean.FALSE : Boolean.TRUE;

			storeToCache(cacheKey, val);
		}
		return val.booleanValue();
	}
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.boot.Database#cleanup()
	 */
	public void cleanup() throws Exception {
		policyCache.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.boot.Database#open(javax.servlet.ServletContext)
	 */
	public void open(CoreServlet controllingServlet) throws Exception {
		String dbName = SystemProperties.get(
				"ovpnals.policyDatabase.jdbc.dbName",
				"explorer_configuration");
		controllingServlet.addDatabase(dbName, ContextHolder.getContext().getDBDirectory());
		String jdbcUser = SystemProperties.get("ovpnals.jdbc.username", "sa");
		String jdbcPassword = SystemProperties.get("ovpnals.jdbc.password",
				"");
		String vendorDB = SystemProperties.get("ovpnals.jdbc.vendorClass",
				"com.ovpnals.jdbc.hsqldb.HSQLDBDatabaseEngine");
		if (log.isInfoEnabled()) {
			log.info("Policy database is being opened...");
			log.info("JDBC vendor class implementation is " + vendorDB);
		}
		File upgradeDir = new File("install/upgrade");
		db = (JDBCDatabaseEngine) Class.forName(vendorDB).newInstance();
		db.init("policyDatabase", dbName, jdbcUser, jdbcPassword, null);
		DBUpgrader upgrader = new DBUpgrader(ContextHolder.getContext()
				.getVersion(), db, ContextHolder.getContext().getDBDirectory(),
				upgradeDir);
		upgrader.upgrade();
		policyCache = new SimpleCache(new MemoryStash(CACHE_MAXOBJS.intValue()));
		CoreServlet.getServlet().addCoreListener(new CoreListener() {
			public void coreEvent(CoreEvent evt) {
				if (evt.getId() == CoreEventConstants.USER_CREATED
						|| evt.getId() == CoreEventConstants.USER_EDITED
						|| evt.getId() == CoreEventConstants.USER_REMOVED
						|| evt.getId() == CoreEventConstants.GROUP_CREATED
						|| evt.getId() == CoreEventConstants.GROUP_REMOVED) {
					policyCache.clear();
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.boot.Database#close()
	 */
	public void close() throws Exception {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#getPoliciesAttachedToResource(com.ovpnals.boot.policyframework.Resource)
	 */
	public List<Policy> getPoliciesAttachedToResource(Resource resource, Realm realm)
			throws Exception {
		String cacheKey = "resourcePolicies-" + resource.getResourceId() + "-"
				+ resource.getResourceType().getResourceTypeId() + "-realmID-" + realm.getResourceId();
		List<Policy> l = (List<Policy>) policyCache.retrieve(cacheKey);
		if (l == null) {

			// Get the top level policy
			JDBCPreparedStatement ps = db
					.getStatement("getPoliciesAttachedToResource.select");
			ps.setInt(1, resource.getResourceId());
            ps.setInt(2, resource.getResourceType().getResourceTypeId());
            ps.setInt(3, realm.getResourceId());
			l = new ArrayList<Policy>();
			try {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					l.add(buildPolicy(rs));
				}
				storeToCache(cacheKey, (Serializable) l);
			} finally {
				ps.releasePreparedStatement();
			}
		}
		return l;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.policyframework.PolicyDatabase#getPrincipalsGrantedPolicy(com.ovpnals.policyframework.Policy, com.ovpnals.realms.Realm)
	 */
	public List<Principal> getPrincipalsGrantedPolicy(Policy policy, Realm realm) throws Exception {
		String cacheKey = "policyPrincipals-" + policy.getResourceId();
		List<Principal> l = (List<Principal>) policyCache.retrieve(cacheKey);
		if (l == null) {
			l = new ArrayList<Principal>();
			UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(realm);
			if (policy.getResourceId() == getEveryonePolicyIDForRealm(realm)) {
                return Collections.<Principal>emptyList();
			} else {
				JDBCPreparedStatement ps = db
						.getStatement("getPrincipalsGrantedPolicy.select");
				ps.setInt(1, policy.getResourceId());
				try {
					ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						String principalId = rs.getString("principal_id");
						int princpalType = rs.getInt("principal_type");
						Principal p = null;
						if (princpalType == Policy.PRINCIPAL_USER) {
                            try {
                                p = udb.getAccount(principalId);
                            }
                            catch(UserNotFoundException unfe) {
                                // User no longer exists, just place a warning in the logs
                            }
						} else {
							try {
                                p = udb.getRole(principalId);
                            } catch (Exception expt) {
                                // Role no longer exists, just place a warning in the logs
                            }
						}
						if (p == null) {
							log
									.warn("An invalid principal is attached to policy "
											+ policy.getResourceId()
											+ ". This may happen if you switch user databases or remove users from an external userdatabase. Ignoring.");
						} else {
							l.add(p);
						}
					}
				} finally {
					ps.releasePreparedStatement();
				}
			}
			storeToCache(cacheKey, (Serializable) l);
		}
		return l;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.policyframework.PolicyDatabase#revokePolicyFromAllPrincipals(com.ovpnals.policyframework.Policy, com.ovpnals.realms.Realm)
	 */
	public void revokePolicyFromAllPrincipals(Policy policy, Realm realm) throws Exception {
		if (policy.getResourceId() == getEveryonePolicyIDForRealm(realm)) {
			throw new Exception(
					"Cannot revoke special Everyone policy from all principals.");
		}
		policyCache.clear();
		JDBCPreparedStatement ps2 = db
				.getStatement("revokePolicyFromAllPrincipals.delete");
		ps2.setInt(1, policy.getResourceId());
		try {
			ps2.execute();
		} finally {
			ps2.releasePreparedStatement();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#createResourcePermission(com.ovpnals.policyframework.ResourcePermission)
	 */
	public AccessRights createAccessRights(
			AccessRights resourcePermission) throws Exception {
		policyCache.clear();
		JDBCPreparedStatement ps = db
				.getStatement("createResourcePermission.insert");
		ps.startTransaction();
		ps.setString(1, resourcePermission.getResourceName());
		ps.setString(2, resourcePermission.getAccessRightsClass());
		ps.setString(3, resourcePermission.getResourceDescription());
        ps.setTimestamp(4, resourcePermission.getDateCreated());
        ps.setTimestamp(5, resourcePermission.getDateAmended());
		ps.setInt(6, resourcePermission.getRealmID());
		try {
			try {
				ps.execute();
				int id = db.getLastInsertId(ps,
						"createResourcePermission.lastInsertId");
				resourcePermission.setResourceId(id);
				updateResourcePermissionRelationships(ps, resourcePermission);
				ps.commit();
				return resourcePermission;
			} finally {
				ps.releasePreparedStatement();
			}
		} catch (Exception e) {
			ps.rollback();
			throw e;
		} finally {
			ps.endTransaction();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#getResourcePermissions()
	 */
	public List<AccessRights> getAccessRights() throws Exception {
		String cacheKey = "resourcePermissions";
		List<AccessRights> val = (List<AccessRights>) policyCache.retrieve(cacheKey);
		if (val == null) {
			JDBCPreparedStatement ps = db
					.getStatement("getResourcePermissions.select");
			try {
				ResultSet rs = ps.executeQuery();
				val = buildResourcePermission(rs);
			} finally {
				ps.releasePreparedStatement();
			}
		}
        
		return val;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.policyframework.PolicyDatabase#getPolicyByName(java.lang.String, int)
	 */
	public Policy getPolicyByName(String name, int realmID) throws Exception {
		String cacheKey = "policyByName-" + name;
		Policy pol = (Policy) policyCache.retrieve(cacheKey);
		if (pol == null) {
			JDBCPreparedStatement ps = db
					.getStatement("getPolicyByName.selectByName");
			ps.setString(1, name);
			ps.setInt(2, realmID);
			try {
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					pol = buildPolicy(rs);
				}
			} finally {
				ps.releasePreparedStatement();
			}
			if (pol != null) {
				storeToCache(cacheKey, pol);
			}
		}
		return pol;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.policyframework.PolicyDatabase#getAccessRightsByName(java.lang.String, int)
	 */
	public AccessRights getAccessRightsByName(String name, int realmID)
			throws Exception {
		String cacheKey = "resourcePermissionByName-" + name + "-realm id-" + realmID;
		AccessRights resourcePermission = (AccessRights) policyCache
				.retrieve(cacheKey);
		if (resourcePermission == null) {
			JDBCPreparedStatement ps = db
					.getStatement("getResourcePermissionByName.select");
			ps.setString(1, name);
			ps.setInt(2, realmID);
			try {
				ResultSet rs = ps.executeQuery();
				List l = buildResourcePermission(rs);
				if (l.size() > 0) {
					resourcePermission = (AccessRights) l.get(0);
				}
			} finally {
				ps.releasePreparedStatement();
			}
			if (resourcePermission != null) {
				storeToCache(cacheKey, resourcePermission);
			}
		}
		return resourcePermission;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#getResourcePermission(int)
	 */
	public AccessRights getAccessRight(int id) throws Exception {
		String cacheKey = "resourcePermission-" + id;
		AccessRights resourcePermission = (AccessRights) policyCache
				.retrieve(cacheKey);
		if (resourcePermission == null) {
			JDBCPreparedStatement ps = db
					.getStatement("getResourcePermission.select");
			ps.setInt(1, id);
			try {
				ResultSet rs = ps.executeQuery();
				List l = buildResourcePermission(rs);
				if (l.size() > 0) {
					resourcePermission = (AccessRights) l.get(0);
				}
			} finally {
				ps.releasePreparedStatement();
			}
			if (resourcePermission != null) {
				storeToCache(cacheKey, resourcePermission);
			}
		}
		return resourcePermission;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.policyframework.PolicyDatabase#isAnyAccessRightAllowed(com.ovpnals.security.User, boolean, boolean, boolean)
	 */
	public boolean isAnyAccessRightAllowed(User user,
			boolean delegation, boolean system, boolean personal)
			throws Exception {
		String cacheKey = "anyResourcePermissionAllowed-"
				+ (user == null ? "" : user.getPrincipalName()) + "-"
				+ delegation + "-" + system + "-" + personal;
		Boolean val = (Boolean) policyCache.retrieve(cacheKey);
		if (val == null) {
			if (LogonControllerFactory.getInstance().isAdministrator(
					user)) {
				val = Boolean.TRUE;
			} else {
				List resourcePermissions = getAccessRights();
				AccessRights resourcePermission = null;
				for (Iterator i = resourcePermissions.iterator(); val == null
						&& i.hasNext();) {
					resourcePermission = (AccessRights) i.next();
					if (system
							&& resourcePermission.getAccessRightsClass().equals(
									PolicyConstants.SYSTEM_CLASS)
							|| delegation
							&& resourcePermission.getAccessRightsClass().equals(
									PolicyConstants.DELEGATION_CLASS)
							|| personal
							&& resourcePermission.getAccessRightsClass().equals(
									PolicyConstants.PERSONAL_CLASS))
						if (isPrincipalAllowed(user, resourcePermission, true)) {
							val = Boolean.TRUE;
						}
				}
				if (val == null) {
					val = Boolean.FALSE;
				}
			}
			storeToCache(cacheKey, val);
		}
		return val.booleanValue();
	}

    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.PolicyDatabase#getAnyAccessRightAllowed(com.ovpnals.security.User, boolean, boolean, boolean)
     */
    public List<AccessRight> getAnyAccessRightAllowed(User user,
            boolean delegation, boolean system, boolean personal)
            throws Exception {
        String cacheKey = "listResourcePermissionAllowed-"
                + (user == null ? "" : user.getPrincipalName()) + "-"
                + delegation + "-" + system + "-" + personal;
        List<AccessRight> listAccessRight;
        listAccessRight = (List) policyCache.retrieve(cacheKey);
        if (listAccessRight == null) {
            listAccessRight = new ArrayList<AccessRight>();
            List resourcePermissions = getAccessRights(user.getRealm().getRealmID());
            AccessRights resourcePermission = null;
            for (Iterator i = resourcePermissions.iterator(); i.hasNext();) {
                resourcePermission = (AccessRights) i.next();
                if (system && resourcePermission.getAccessRightsClass().equals(PolicyConstants.SYSTEM_CLASS) || delegation
                                && resourcePermission.getAccessRightsClass().equals(PolicyConstants.DELEGATION_CLASS) || personal
                                && resourcePermission.getAccessRightsClass().equals(PolicyConstants.PERSONAL_CLASS)) {
                    if (isPrincipalAllowed(user, resourcePermission, true)
                                    || LogonControllerFactory.getInstance().isAdministrator(user)) {
                        listAccessRight.addAll(resourcePermission.getAccessRights());
                    }
                }
            }
            storeToCache(cacheKey, (Serializable) listAccessRight);
        }
        return listAccessRight;
    }
    
	/*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.PolicyDatabase#isPermitted(com.ovpnals.policyframework.ResourceType,
     *      com.ovpnals.policyframework.Permission[],
     *      com.ovpnals.security.User, boolean)
     */
	public boolean isPermitted(ResourceType resourceType,
			Permission[] requiredPermissions, User user, boolean all)
			throws Exception {
		StringBuffer buf = new StringBuffer("resourcePermissionAllowed-");
		buf.append(resourceType.getResourceTypeId());
		buf.append("-");
		if(requiredPermissions != null) {
			for (int i = 0; i < requiredPermissions.length; i++) {
				buf.append(requiredPermissions[i].getId());
				buf.append("-");
			}
		}
		buf.append(user == null ? "" : user.getPrincipalName());
		buf.append("-");
		buf.append(String.valueOf(all));
		String cacheKey = buf.toString();
		Boolean val = (Boolean) policyCache.retrieve(cacheKey);
		if (val == null) {
			if (LogonControllerFactory.getInstance().isAdministrator(
					user)) {
				val = Boolean.TRUE;
			} else {
				List resourcePermissions = getAccessRights();
				AccessRights resourcePermission = null;
				AccessRight permission = null;
				// Iterator through all resource permissions
				Map<String,Boolean> matched = new HashMap<String,Boolean>();
				for (Iterator i = resourcePermissions.iterator(); val == null
						&& i.hasNext();) {
					resourcePermission = (AccessRights) i.next();
					// Iterator through all permissions in the resource
					for (Iterator j = resourcePermission.getAccessRights()
							.iterator(); val == null && j.hasNext();) {
						permission = (AccessRight) j.next();
						// Until the resource type matches
						if (resourceType.equals(permission.getResourceType())) {
							// Check the mask, at least one must match
							for (int x = 0; requiredPermissions!=null && x < requiredPermissions.length; x++) {
								if (permission.getPermission().getId() == requiredPermissions[x]
										.getId()) {
									// Check if the user in a policy
									if (isPrincipalAllowed(user,
											resourcePermission, true)) {
										String key = String
												.valueOf(requiredPermissions[x]
														.getId());
										matched.put(key, Boolean.TRUE);
										if (!all
												|| matched.size() == requiredPermissions.length) {
											break;
										}
									}
								}
							}
						}
					}
				}
				if (all && requiredPermissions!=null && matched.size() == requiredPermissions.length) {
					val = Boolean.TRUE;
				} else if (!all && matched.size() > 0) {
					val = Boolean.TRUE;
				} else {
					val = Boolean.FALSE;
				}
			}
			storeToCache(cacheKey, val);
		}
		return val.booleanValue();
	}

    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.PolicyDatabase#isPersonalPermitted(com.ovpnals.policyframework.ResourceType, com.ovpnals.policyframework.Permission[], com.ovpnals.security.User, boolean)
     */
    public boolean isPersonalPermitted(Resource resource, Permission[] requiredPermissions,
                                       User user) throws Exception {
        StringBuffer buf = new StringBuffer("personalResourcePermissionAllowed-");
        buf.append(resource.getResourceType().getResourceTypeId());
        buf.append("-");
        buf.append(resource.getResourceId());
        buf.append("-");
        if(requiredPermissions != null) {
            for (int i = 0; i < requiredPermissions.length; i++) {
                buf.append(requiredPermissions[i].getId());
                buf.append("-");
            }
        }
        buf.append(user == null ? "" : user.getPrincipalName());
        String cacheKey = buf.toString();
        Boolean val = (Boolean) policyCache.retrieve(cacheKey);
        if (val == null) {
            if (LogonControllerFactory.getInstance().isAdministrator(
                user)) {
                val = Boolean.TRUE;
            } else {
                val = Boolean.FALSE;
                List resourcePermissions = getAccessRights();
                AccessRights resourcePermission = null;
                AccessRight permission = null;
                // Iterator through all resource permissions
                
                for (Iterator i = resourcePermissions.iterator();  !val && i.hasNext();) {
                    resourcePermission = (AccessRights) i.next();
                    // Iterator through all permissions in the resource
                    for (Iterator j = resourcePermission.getAccessRights().iterator();  !val && j.hasNext();) {
                        permission = (AccessRight) j.next();
                        // Until the resource type matches
                        if (resource.getResourceType().equals(permission.getResourceType())) {
                
                
                            for (int x = 0; x < requiredPermissions.length; x++) {
                                Permission requiredPermission = requiredPermissions[x];
                                if ((permission.getPermission().getId() == requiredPermission.getId())
                                 && (requiredPermission.getId() == PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE_ID)) {
                                    List policiesResource = getPoliciesAttachedToResource(resource, user.getRealm());
                                    if (policiesResource.size() != 1) {
                                        val = Boolean.FALSE;
                                    } else {
                                        Policy policy = (Policy) policiesResource.get(0);
                                        Policy policyUser = getGrantingPolicyForUser(user, resource);
                                        if (!policy.equals(policyUser)) {
                                            val = Boolean.FALSE;
                                        } else {
                                            if ((policy.getType() != Policy.TYPE_PERSONAL) || (!policy.getResourceName().equals(PolicyUtil.getPersonalPolicyName(user.getPrincipalName())))) {
                                                val = Boolean.FALSE;
                                            } else {
                                                val = Boolean.TRUE;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    val = Boolean.FALSE;
                                }
                            }
                            
                
                        }
                    }
                }
                
                
            }
            storeToCache(cacheKey, val);
        }
        return val.booleanValue();
    }
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#deleteResourcePermission(int)
	 */
	public AccessRights deleteAccessRights(int id) throws Exception {
		policyCache.clear();
		AccessRights dr = getAccessRight(id);
		if (dr == null) {
			throw new Exception(
					"Cannot delete a resource permission that doesnt exist");
		}
		JDBCPreparedStatement ps = db
				.getStatement("deleteResourcePermission.delete");
		ps.startTransaction();
		ps.setInt(1, id);
		try {
			try {
				ps.execute();
				deleteResourcePermissionRelationships(ps, id);
				ps = db.getStatement(ps,
						"deleteResourcePermission.policyRelationship");
				ps.setInt(1, id);
				ps.setInt(2, dr.getResourceType().getResourceTypeId());
				ps.execute();
				ps.commit();
			} finally {
				ps.releasePreparedStatement();
			}
		} catch (Exception e) {
			ps.rollback();
			throw e;
		} finally {
			ps.endTransaction();
		}
		return dr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#updateResourcePermission(com.ovpnals.policyframework.ResourcePermission)
	 */
	public void updateAccessRights(AccessRights resourcePermission)
			throws Exception {
		policyCache.clear();
		JDBCPreparedStatement ps = db
				.getStatement("updateResourcePermission.update");
		ps.startTransaction();
		ps.setString(1, resourcePermission.getResourceName());
		ps.setString(2, resourcePermission.getResourceDescription());
		Calendar c = Calendar.getInstance();
		ps.setString(3, db.formatTimestamp(c));
		ps.setInt(4, resourcePermission.getResourceId());

		try {
			try {
				ps.execute();
				updateResourcePermissionRelationships(ps, resourcePermission);
			} finally {
				ps.releasePreparedStatement();
			}
			ps.commit();
			resourcePermission.setDateAmended(c);
		} catch (Exception e) {
			ps.rollback();
			throw e;
		} finally {
			ps.endTransaction();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#initResourcePermissions()
	 */
	public void initAccessRights() throws Exception {
		
        // Has no permission
		registerResourceType(PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE);
        PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN);
        PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_EDIT_AND_ASSIGN);
        PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_ASSIGN);
        PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_DELETE);

        // -- Resource Permissions


		// Policy
		registerResourceType(PolicyConstants.POLICY_RESOURCE_TYPE);
		PolicyConstants.POLICY_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN);
		PolicyConstants.POLICY_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_EDIT_AND_ASSIGN);
		PolicyConstants.POLICY_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_DELETE);
		PolicyConstants.POLICY_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_ASSIGN);

		// Profile
		registerResourceType(PolicyConstants.PROFILE_RESOURCE_TYPE);
		PolicyConstants.PROFILE_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN);
		PolicyConstants.PROFILE_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_EDIT_AND_ASSIGN);
		PolicyConstants.PROFILE_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_DELETE);
		PolicyConstants.PROFILE_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_ASSIGN);

		// -- System Resource Permissions

		// Shutdown
		registerResourceType(PolicyConstants.SERVICE_CONTROL_RESOURCE_TYPE);
		PolicyConstants.SERVICE_CONTROL_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_SHUTDOWN);
		PolicyConstants.SERVICE_CONTROL_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_RESTART);

		// System Configuration
		registerResourceType(PolicyConstants.SYSTEM_CONFIGURATION_RESOURCE_TYPE);
		PolicyConstants.SYSTEM_CONFIGURATION_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_CHANGE);

		// Keystore
		registerResourceType(PolicyConstants.KEYSTORE_RESOURCE_TYPE);
		PolicyConstants.KEYSTORE_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_CHANGE);

		// Authentication Schemes
		registerResourceType(PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE);
		PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN);
		PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_EDIT_AND_ASSIGN);
		PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_ASSIGN);
		PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_DELETE);

		// Accounts
		registerResourceType(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE);
		PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN);
		PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_DELETE);

		// IP Restrictions
		registerResourceType(PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE);
		PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_CREATE);
		PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_DELETE);
        PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE
                .addPermission(PolicyConstants.PERM_EDIT);

		// Extensions
		registerResourceType(PolicyConstants.EXTENSIONS_RESOURCE_TYPE);
		PolicyConstants.EXTENSIONS_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_CHANGE);

		// Message Queue
		registerResourceType(PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE);
		PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_VIEW);
		PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_CLEAR);
		PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_CONTROL);
		PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_SEND);

		// Status
		registerResourceType(PolicyConstants.STATUS_TYPE_RESOURCE_TYPE);
		PolicyConstants.STATUS_TYPE_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_VIEW);

		// Replacement
		registerResourceType(PolicyConstants.REPLACEMENTS_RESOURCE_TYPE);
		PolicyConstants.REPLACEMENTS_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_CHANGE);

		// Attributes
		registerResourceType(PolicyConstants.ATTRIBUTE_DEFINITIONS_RESOURCE_TYPE);
		PolicyConstants.ATTRIBUTE_DEFINITIONS_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_MAINTAIN);
		registerResourceType(PolicyConstants.ATTRIBUTES_RESOURCE_TYPE);
		PolicyConstants.ATTRIBUTES_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_MAINTAIN);

		// -- Personal Resource Permissions

		// Profile
		registerResourceType(PolicyConstants.PERSONAL_PROFILE_RESOURCE_TYPE);
		PolicyConstants.PERSONAL_PROFILE_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_MAINTAIN);

		// Password
		registerResourceType(PolicyConstants.PASSWORD_RESOURCE_TYPE);
			PolicyConstants.PASSWORD_RESOURCE_TYPE
					.addPermission(PolicyConstants.PERM_CHANGE);

		// VPN Client
		registerResourceType(PolicyConstants.AGENT_RESOURCE_TYPE);
		PolicyConstants.AGENT_RESOURCE_TYPE
				.addPermission(PolicyConstants.PERM_USE);

        // VPN Client
        registerResourceType(PolicyConstants.LANGUAGE_RESOURCE_TYPE);
        PolicyConstants.LANGUAGE_RESOURCE_TYPE
                .addPermission(PolicyConstants.PERM_CHANGE);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#getGrantedResourcesOfType(com.ovpnals.permissions.Principal,
	 *      com.ovpnals.boot.policyframework.ResourceType)
	 */
	public List<Integer> getGrantedResourcesOfType(Principal principal, ResourceType type)
			throws Exception {
		String cacheKey = "grantedResourcesOfType-"
				+ principal.getPrincipalName() + "-" + principal.getRealm().getResourceId() + "-" + type.getResourceTypeId();
		Set<Integer> resourceIds = (Set<Integer>) policyCache.retrieve(cacheKey);

		if (resourceIds == null) {
			JDBCPreparedStatement ps = null;
			resourceIds = new HashSet<Integer>();
			try {
				ps = db.getStatement("getGrantedResourcesOfType.select");
				ps.setInt(1, type.getResourceTypeId());
				ps.setString(2, principal.getPrincipalName());
				ps.setInt(3, principal instanceof User ? Policy.PRINCIPAL_USER
						: Policy.PRINCIPAL_GROUP);
                ps.setInt(4, type.getResourceTypeId());
                ps.setInt(5, principal.getRealm().getResourceId());
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					resourceIds.add(new Integer(rs.getInt("resource_id")));
				}

				if (principal instanceof User) {
					// Now try roles
					Role[] r = ((User) principal).getRoles();
					if (r != null) {
						for (int i = 0; i < r.length; i++) {

							if (r[i] == null) {
								log.warn("NULL role in principal "
										+ principal.getPrincipalName());
								continue;
							}

							ps.reset();
							ps = db
									.getStatement("getGrantedResourcesOfType.select");
							ps.setInt(1, type.getResourceTypeId());
							ps.setString(2, r[i].getPrincipalName());
							ps.setInt(3, Policy.PRINCIPAL_GROUP);
							ps.setInt(4, type.getResourceTypeId());
                            ps.setInt(5, principal.getRealm().getResourceId());
							try {
								rs = ps.executeQuery();
								while (rs.next()) {
									resourceIds.add(new Integer(rs
											.getInt("resource_id")));
								}
							} finally {
								ps.releasePreparedStatement();
							}
						}
					}
				}
			} finally {
				if (ps != null) {
					ps.releasePreparedStatement();
				}
			}
			storeToCache(cacheKey, (Serializable) resourceIds);
		}
		return new ArrayList<Integer>(resourceIds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#isPrincipalGrantedResourcesOfType(com.ovpnals.permissions.Principal,
	 *      com.ovpnals.boot.policyframework.ResourceType, java.util.List)
	 */
	public boolean isPrincipalGrantedResourcesOfType(Principal principal,
			ResourceType resourceRequired, List resourceTypesToExclude)
			throws Exception {

		String cacheKey = "isGrantedResourceOfType-"
				+ principal.getPrincipalName()
				+ "-"
				+ (resourceRequired == null ? "" : String
						.valueOf(resourceRequired.getResourceTypeId()));
		Boolean val = (Boolean) policyCache.retrieve(cacheKey);
		if (val == null) {
			JDBCPreparedStatement ps = null;
			if (resourceRequired == null) {
				// Is the user granted ANY resources of ANY type
				ps = db.getStatement("isPrincipalGranted.selectAny");
				ps.setString(1, principal.getPrincipalName());
                ps.setInt(2, principal instanceof User ? Policy.PRINCIPAL_USER
                                : Policy.PRINCIPAL_GROUP);
                ps.setInt(3, principal.getRealm().getResourceId());
			} else {
				// Is the user granted ANY resources of a particular type
				ps = db.getStatement("isPrincipalGranted.selectType");
				ps.setInt(1, resourceRequired.getResourceTypeId());
				ps.setString(2, principal.getPrincipalName());
				ps.setInt(3, principal instanceof User ? Policy.PRINCIPAL_USER
						: Policy.PRINCIPAL_GROUP);
				ps.setInt(4, resourceRequired.getResourceTypeId());
                ps.setInt(5, principal.getRealm().getResourceId());
			}
			try {
				ResultSet rs = ps.executeQuery();

				// First check the if provided principal has access

				while (true) {
					if (rs.next()) {
						if (resourceTypesToExclude == null
								|| resourceTypesToExclude.size() == 0) {
							val = Boolean.TRUE;
							break;
						}
						int rtn = rs.getInt("resource_type");
						ResourceType rt = getResourceType(rtn);
						if (rt == null) {
							log
									.warn("Failed to locate resource type with ID of "
											+ rtn
											+ ". Its possible this was created by a plugin which is no longer available.");
						} else {
							if (!resourceTypesToExclude.contains(rt)) {
								val = Boolean.TRUE;
								break;
							}
						}
					} else {
						break;
					}
				}

				// If the principal is a user, the get their roles and check
				// those as well
				if (val == null && principal instanceof User) {
					// Now try roles
					Role[] r = ((User) principal).getRoles();
					if (r != null) {
						for (int i = 0; val == null && i < r.length; i++) {

							if (r[i] == null) {
								log.warn("NULL role in principal "
										+ principal.getPrincipalName());
								continue;
							}

							ps.reset();
							if (resourceRequired == null) {
								ps = db
										.getStatement("isPrincipalGranted.selectAny");
								ps.setString(1, r[i].getPrincipalName());
								ps.setInt(2, Policy.PRINCIPAL_GROUP);
                                ps.setInt(3, principal.getRealm().getResourceId());
							} else {
								ps = db
										.getStatement("isPrincipalGranted.selectType");
								ps.setInt(1, resourceRequired
										.getResourceTypeId());
								ps.setString(2, r[i].getPrincipalName());
								ps.setInt(3, Policy.PRINCIPAL_GROUP);
								ps.setInt(4, resourceRequired
										.getResourceTypeId());
                                ps.setInt(5, principal.getRealm().getResourceId());
							}

							try {
								rs = ps.executeQuery();
								while (true) {
									if (rs.next()) {
										if (resourceTypesToExclude == null
												|| resourceTypesToExclude
														.size() == 0) {
											val = Boolean.TRUE;
											break;
										}
										int rtn = rs.getInt("resource_type");
										ResourceType rt = getResourceType(rtn);
										if (rt == null) {
											log
													.warn("Failed to locate resource type with ID of "
															+ rtn
															+ ". Its possible this was created by a plugin which is no longer available.");
										} else {
											if (!resourceTypesToExclude
													.contains(rt)) {
												val = Boolean.TRUE;
												break;
											}
										}
									} else {
										break;
									}
								}
							} finally {
								ps.releasePreparedStatement();
							}
						}
					}
				}

			} finally {
				ps.releasePreparedStatement();
			}
			if (val == null) {
				val = Boolean.FALSE;
			}
            
			storeToCache(cacheKey, val);
		}
		return val.booleanValue();
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.policyframework.PolicyDatabase#getPoliciesOfDelegatedAccessRights(com.ovpnals.policyframework.ResourceType, java.lang.String, com.ovpnals.security.User, boolean)
	 */
	public List<Policy> getPoliciesOfDelegatedAccessRights(
			ResourceType resourceType, String permissionClass, User user) throws Exception {
		StringBuffer buf = new StringBuffer(
				"policiesOfDelegatedResourcePermissions");
		if (resourceType != null) {
			buf.append("-");
			buf.append(resourceType.getResourceTypeId());
		}
		if (permissionClass != null) {
			buf.append("-");
			buf.append(permissionClass);
		}
		buf.append("-");
		buf.append(user.getPrincipalName());
		String cacheKey = buf.toString();
		List<Policy> l = (List<Policy>) policyCache.retrieve(cacheKey);
		if (l == null) {
			l = new ArrayList<Policy>();
			List resourcePermissions = getAccessRights();
			AccessRights resourcePermission = null;
			AccessRight accessRight = null;
			for (Iterator i = resourcePermissions.iterator(); i.hasNext();) {
				resourcePermission = (AccessRights) i.next();
				if (isPrincipalAllowed(user, resourcePermission, true)) {
					if (permissionClass == null
							|| permissionClass.equals(resourcePermission
									.getAccessRightsClass())) {
						for (Iterator j = resourcePermission.getAccessRights()
								.iterator(); j.hasNext();) {

							accessRight = (AccessRight) j
									.next();
							if (resourceType == null
									|| resourceType
											.equals(accessRight
													.getResourceType())) {
								// LDP - Add the policies attached to a resource
								// as well
								List del = getPoliciesAttachedToResource(resourcePermission, user.getRealm());

								for (Iterator k = del.iterator(); k.hasNext();) {
									Policy p = (Policy) k.next();
									if (!l.contains(p)) {
										l.add(p);
									}
								}
								break;
							}
						}
					}

				}
			}
			storeToCache(cacheKey, (Serializable) l);
		}
		return l;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.policyframework.PolicyDatabase#getPermittingAccessRights(com.ovpnals.policyframework.ResourceType, com.ovpnals.policyframework.Permission, java.lang.String, com.ovpnals.security.User, boolean, boolean, boolean, com.ovpnals.realms.Realm)
	 */
	public List<AccessRights> getPermittingAccessRights(ResourceType resourceType,
			Permission permission, String permissionClass, User user) throws Exception {
		String cacheKey = "permittingResourcePermissions-"
				+ (resourceType == null ? "" : String.valueOf(resourceType
						.getResourceTypeId()))
				+ "-"
				+ (permission == null ? "" : String.valueOf(permission.getId()))
				+ "-" + (permissionClass == null ? "" : permissionClass) + "-"
				+ user.getPrincipalName();
		List<AccessRights> l = (List<AccessRights>) policyCache.retrieve(cacheKey);
		if (l == null) {
			l = new ArrayList<AccessRights>();
			List resourcePermissions = getAccessRights();
			AccessRights resourcePermission = null;
			AccessRight accessRight = null;

			/*
             * First iterate through all of the resource permissions looking for
             * what is visible at the top level.
             */

			for (Iterator i = resourcePermissions.iterator(); i.hasNext();) {
				resourcePermission = (AccessRights) i.next();
				if (permissionClass == null
						|| permissionClass.equals(resourcePermission
								.getAccessRightsClass())) {
					// Check the user is allowed
				if (isPrincipalAllowed(
								user, resourcePermission, true)) {
						// Iterator through all permissions in the resource
						for (Iterator j = resourcePermission
								.getAccessRights().iterator(); j.hasNext();) {
							accessRight = (AccessRight) j
									.next();
							// Until the resource type matches
							if (resourceType == null
									|| resourceType
											.equals(accessRight
													.getResourceType())) {
								// Until at least one permission matches
								if (permission == null
										|| permission.getId() == accessRight
												.getPermission()
												.getId()) {
									l.add(resourcePermission);
									break;
								}
							}
						}
					}
				}
			}

			// Sort and cache
			Collections.sort(resourcePermissions);
			storeToCache(cacheKey, (Serializable) l);
		}
		return l;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.policyframework.PolicyDatabase#getResourcePermissions(com.ovpnals.boot.policyframework.ResourceType,
	 *      com.ovpnals.boot.policyframework.Permission, java.lang.String,
	 *      com.ovpnals.security.User)
	 */
	public List<AccessRights> getAccessRights(ResourceType resourceType,
			Permission permission, String permissionClass, User user)
			throws Exception {
		StringBuffer buf = new StringBuffer("permission");
		if (resourceType != null) {
			buf.append("-");
			buf.append(resourceType.getResourceTypeId());
		}
		if (permission != null) {
			buf.append("-");
			buf.append(permission.getId());
		}
		if (permissionClass != null) {
			buf.append("-");
			buf.append(permissionClass);
		}
		buf.append("-");
		buf.append(user.getPrincipalName());
		String cacheKey = buf.toString();
		List<AccessRights> n = (List<AccessRights>) policyCache.retrieve(cacheKey);
		if (n == null) {
			ArrayList<AccessRights> l = new ArrayList<AccessRights>();
			boolean superUser = LogonControllerFactory.getInstance()
					.isAdministrator(user);
			List allAccessRights = getAccessRights();
			AccessRights accessRights = null;
			AccessRight accessRight = null;

			/*
			 * First iterate through all of the resource permissions looking for
			 * what is visible at the top level.
			 */

			for (Iterator i = allAccessRights.iterator(); i.hasNext();) {
                accessRights = (AccessRights) i.next();
                if (permissionClass == null || permissionClass.equals(accessRights.getAccessRightsClass())) {
                    // Check the user is allowed
                    if (isPrincipalAllowed(user, accessRights, true)) {

                        // Iterator through all permissions in the resource
                        for (Iterator j = accessRights.getAccessRights().iterator(); j.hasNext();) {
                            accessRight = (AccessRight) j.next();
                            // Until the resource type matches
                            if (resourceType == null || resourceType.equals(accessRight.getResourceType())) {
                                // Until at least one permission matches
                                if (permission == null || permission.getId() == accessRight.getPermission().getId()) {
                                    l.add(accessRights);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            /*
             * Now iterate again, also adding resource permissions that have one
             * of the top level resources permissions as parent.
             */
            if (!superUser) { // Super user should already have all resource
                // permissions anyway() + ")");
                n = new ArrayList<AccessRights>();
                for (Iterator i = allAccessRights.iterator(); i.hasNext();) {
                    accessRights = (AccessRights) i.next();
                    if (!l.contains(accessRights)) {
                        n.add(accessRights);
                    }
                }
            } else {
                n = l;
            }

			// Sort and cache
			Collections.sort(n);
			storeToCache(cacheKey, (Serializable) n);
		}
		return n;
	}

	void deleteResourcePermissionRelationships(JDBCPreparedStatement ps, int id)
			throws Exception {
		try {
			ps = db
					.getStatement("deleteResourcePermissionRelationships.delete");
			ps.setInt(1, id);
			ps.execute();
		} finally {
			ps.releasePreparedStatement();
		}
	}

	boolean checkPolicy(Policy policy, Resource resource, Principal principal)
			throws Exception {
		List principals = getPrincipalsGrantedPolicy(policy, principal.getRealm());
		for (Iterator i = principals.iterator(); i.hasNext();) {
			Principal p = (Principal) i.next();
			if (p.equals(principal)) {
				return true;
			}
		}
		return false;
	}

	void storeToCache(String key, Serializable object) {
		if (log.isDebugEnabled()) {
			log.debug("Caching under " + key + ", ttl=" + CACHE_TTL + ", cost="
					+ CACHE_COST);
		}

        // NOTE Temporary code to make sure policy objects are serializable, in development and testing
        if ("true".equals(SystemProperties.get("ovpnals.useDevConfig")) | "true".equals(SystemProperties.get("ovpnals.testing"))) {
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

		policyCache.store(key, object, new Long(CACHE_TTL.longValue()
				+ System.currentTimeMillis()), CACHE_COST);
		if (log.isDebugEnabled()) {
			log.debug("NUM_RETRIEVE_REQUESTED "
					+ policyCache.getStat(CacheStat.NUM_RETRIEVE_REQUESTED));
			log.debug("NUM_RETRIEVE_FOUND "
					+ policyCache.getStat(CacheStat.NUM_RETRIEVE_FOUND));
			log.debug("NUM_RETRIEVE_NOT_FOUND "
					+ policyCache.getStat(CacheStat.NUM_RETRIEVE_NOT_FOUND));
			log.debug("NUM_STORE_REQUESTED "
					+ policyCache.getStat(CacheStat.NUM_STORE_REQUESTED));
			log.debug("NUM_STORE_STORED "
					+ policyCache.getStat(CacheStat.NUM_STORE_STORED));
			log.debug("NUM_STORE_NOT_STORED "
					+ policyCache.getStat(CacheStat.NUM_STORE_NOT_STORED));
			log.debug("CUR_CAPACITY "
					+ policyCache.getStat(CacheStat.CUR_CAPACITY));
		}
	}

	Policy buildPolicy(ResultSet rs) throws Exception {
		Timestamp cd = rs.getTimestamp("date_created");
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(cd == null ? System.currentTimeMillis() : cd
				.getTime());
		Timestamp ad = rs.getTimestamp("date_amended");
		Calendar a = Calendar.getInstance();
		a.setTimeInMillis(ad == null ? System.currentTimeMillis() : ad
				.getTime());
		return new DefaultPolicy(rs.getInt("id"), rs.getString("policy_name"),
				rs.getString("policy_description"),
				rs.getInt("policy_type_id"), c, a, rs.getInt("realm_id"));
	}
	
	List<AccessRights> buildResourcePermission(ResultSet resultSet) throws Exception {
        List<AccessRight> permissions = null;
        AccessRights accessRights = null;
        List<AccessRights> accessRightsList = new ArrayList<AccessRights>();
        int lastId = -1;
        while (resultSet.next()) {
            int resourceId = resultSet.getInt("resource_id");
            int realmID = resultSet.getInt("realm_id");
            if (resourceId != lastId) {
                permissions = new ArrayList<AccessRight>();
                Calendar dateCreated = JDBCUtil.getCalendar(resultSet, "date_created");
                Calendar dateAmended = JDBCUtil.getCalendar(resultSet, "date_amended");
                String resourceName = resultSet.getString("resource_name");
                String resourceDescription = resultSet.getString("resource_description");
                String resourceClass = resultSet.getString("resource_class");
                accessRights = new DefaultAccessRights(realmID, resourceId, resourceName, resourceDescription, permissions,
                                resourceClass, dateCreated, dateAmended);
                accessRightsList.add(accessRights);
                lastId = resourceId;
            }
            // check to see if the access right has any permissions
            if (!JDBCUtil.isNull(resultSet, "resource_type_id")) {
                int resourceTypeId = resultSet.getInt("resource_type_id");
                ResourceType resourceType = getResourceType(resourceTypeId);
                if (resourceType == null) {
                    log.warn("No resource type with Id of " + resourceTypeId + " for resource permission " + resourceId
                                    + ", ignoring");
                } else {
                    int permissionId = resultSet.getInt("permission_id");
                    Permission permission = resourceType.getPermission(permissionId);
                    if (permission == null) {
                        log.warn("No permission with Id of " + permissionId + " for resource type " + resourceTypeId
                                        + " and resource permission " + resourceId + ", ignoring");
                    } else {
                        AccessRight accessRight = new AccessRight(resourceType, permission);
                        permissions.add(accessRight);
                    }
                }
            } else {
                log.debug("Access Rights with name " + accessRights.getResourceName() + " has no permissions.");
            }
        }
        return accessRightsList;
    }

	void updateResourcePermissionRelationships(JDBCPreparedStatement ps,
			AccessRights dr) throws Exception {
		deleteResourcePermissionRelationships(ps, dr.getResourceId());
		for (Iterator i = dr.getAccessRights().iterator(); i.hasNext();) {
			AccessRight perm = (AccessRight) i
					.next();
			JDBCPreparedStatement ps2 = db
					.getStatement("updateResourcePermission.insertPermissions");
			try {
				ps2.setInt(1, dr.getResourceId());
				ps2.setInt(2, perm.getResourceType().getResourceTypeId());
				ps2.setInt(3, perm.getPermission().getId());
				ps2.execute();
			} finally {
				ps2.releasePreparedStatement();
			}
		}
	}

    public int getEveryonePolicyIDForRealm(Realm realm) throws Exception {
        int id = 0;
        JDBCPreparedStatement ps = db.getStatement("select.everyone.policy.id");
        ps.setString(1, PolicyConstants.EVERYONE_POLICY_NAME);
        ps.setInt(2, realm.getResourceId());
        try {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("ID");
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return id;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.PolicyDatabase#isResourceInRealm(com.ovpnals.policyframework.Resource, com.ovpnals.realms.Realm)
     */
    public boolean isResourceInRealm(Resource resource, Realm realm) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("is.resource.in.realm");
        ps.setInt(1, resource.getResourceId());
        ps.setInt(2, resource.getResourceType().getResourceTypeId());
        ps.setInt(3, realm.getResourceId());
        try {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.PolicyDatabase#getGrantingPolicyForUser(com.ovpnals.security.User, com.ovpnals.policyframework.Resource)
     */
    public Policy getGrantingPolicyForUser(User user, Resource resource) throws Exception {
        Policy policy = null;
        if((policy = getGrantingPolicy(user, resource))==null) {
            Role[] roles = user.getRoles();
            for(int i=0;i<roles.length;i++) {
                
                if((policy = getGrantingPolicy(roles[i], resource))!=null) {
                    break;
                }
            }
        }
        return policy;
            
    }

    /**
     * Get the policy thats grants the specified principal access to the 
     * specified resource. 
     * 
     * @param principal principal
     * @param resource resource
     * @return policy that grants access or <code>null</code> if no policy grants access
     * @throws Exception on any error
     */
    private Policy getGrantingPolicy(Principal principal, Resource resource) throws Exception {
        String cacheKey = "grantingPolicy-" + principal.getPrincipalName() + "-realmID-" + principal.getRealm().getResourceId()
                + "-" + resource.getResourceId() + "-"
                + resource.getResourceType().getResourceTypeId();
        Policy val = (Policy) policyCache.retrieve(cacheKey);
        if (val == null) {
            List policies = getPoliciesAttachedToResource(resource, principal.getRealm());
            for (Iterator i = policies.iterator(); val == null && i.hasNext();) {
                Policy p = (Policy) i.next();
                if (isPolicyGrantedToPrincipal(p, principal)) {
                    val = p;
                }
                if (principal instanceof User) {
                    Role[] r = ((User) principal).getRoles();
                    if (r != null) {
                        for (int j = 0; val == null && j < r.length; j++) {
                            if (r[j]!=null && isPolicyGrantedToPrincipal(p, r[j])) {
                                val = p;
                            }
                        }
                    }
                }
            }
            storeToCache(cacheKey, val);
        }
        return val;
        
    }

    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.PolicyDatabase#isPolicyGrantedToUser(com.ovpnals.policyframework.Policy, com.ovpnals.security.User)
     */
    public boolean isPolicyGrantedToUser(Policy policy, User user) throws Exception {
        boolean found = false;
        if(!isPolicyGrantedToPrincipal(policy, user)) {
            Role[] roles = user.getRoles();
            for(int i=0;i<roles.length;i++) {
                if(isPolicyGrantedToPrincipal(policy, roles[i])) {
                    found = true;
                    break;
                }
            }   
        } else
            found = true;
        return found;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.PolicyDatabase#detachResourceFromPolicyList(com.ovpnals.policyframework.Resource, com.ovpnals.security.SessionInfo)
     */
    public void detachResourceFromPolicyList(Resource resource, SessionInfo session) throws Exception {
        List policies = getPolicies(session.getUser().getRealm());
        for (Iterator i = policies.iterator(); i.hasNext();) {
            Policy p = (Policy) i.next();
            if(isResourceAttachedToPolicy(resource, p, session.getUser().getRealm())) {
                if (log.isDebugEnabled())
                    log.debug("Detaching policy " + p.getResourceName() + " (" + p.getResourceId() + ") to resource "
                                + resource.getResourceName() + "(id=" + resource.getResourceId() + ", type="
                                + resource.getResourceType() + ")");
                try {
                    detachResourceFromPolicy(resource, p, session.getUser().getRealm());
                    CoreServlet.getServlet().fireCoreEvent(new ResourceDetachedFromPolicyEvent(this, resource, p, session, CoreEvent.STATE_SUCCESSFUL));
                }
                catch(Exception e) {
                    CoreServlet.getServlet().fireCoreEvent(new ResourceDetachedFromPolicyEvent(this, resource, p, session, CoreEvent.STATE_UNSUCCESSFUL));
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.PolicyDatabase#attachResourceToPolicyList(com.ovpnals.policyframework.Resource, com.ovpnals.boot.PropertyList, com.ovpnals.security.SessionInfo)
     */
    public void attachResourceToPolicyList(Resource resource, PropertyList selectedPolicies, SessionInfo session) throws Exception {
        List l = getPoliciesAttachedToResource(resource, session.getUser().getRealm());
        for (Iterator i = l.iterator(); i.hasNext();) {
            Policy p = (Policy) i.next();
            if(!selectedPolicies.contains(String.valueOf(p.getResourceId()))) {
                if (log.isDebugEnabled())
                    log.debug("Detaching policy " + p.getResourceName() + " (" + p.getResourceId() + ") to resource "
                                + resource.getResourceName() + "(id=" + resource.getResourceId() + ", type="
                                + resource.getResourceType() + ")");
                try {
                    detachResourceFromPolicy(resource, p, session.getUser().getRealm());
                    CoreServlet.getServlet().fireCoreEvent(new ResourceDetachedFromPolicyEvent(this, resource, p, session, CoreEvent.STATE_SUCCESSFUL));
                }
                catch(Exception e) {
                    CoreServlet.getServlet().fireCoreEvent(new ResourceDetachedFromPolicyEvent(this, resource, p, session, CoreEvent.STATE_UNSUCCESSFUL));
                    throw e;
                }                
            }
        }
        int idx = 0;
        for (Iterator i = selectedPolicies.iterator(); i.hasNext(); ) {
            String pn = (String)i.next();
            Policy p = getPolicy(Integer.parseInt(pn));
            if (!l.contains(p)) {
                if (log.isDebugEnabled())
                    log.debug("Attaching policy " + p.getResourceName() + " (" + p.getResourceId() + ") to resource "
                                + resource.getResourceName() + "(id=" + resource.getResourceId() + ", type="
                                + resource.getResourceType() + ")");
                try {
                    attachResourceToPolicy(resource, p, idx++, session.getUser().getRealm());
                    CoreServlet.getServlet().fireCoreEvent(new ResourceAttachedToPolicyEvent(this, resource, p, session, CoreEvent.STATE_SUCCESSFUL));
                }
                catch(Exception e) {
                    CoreServlet.getServlet().fireCoreEvent(new ResourceAttachedToPolicyEvent(this, resource, p, session, CoreEvent.STATE_UNSUCCESSFUL));
                    throw e;                    
                }
            } 
        }
    }

    public List<AccessRights> getAccessRights(int realmID) throws Exception {
        String cacheKey = "resourcePermissions-realmID=" + realmID;
        List<AccessRights> val = (List<AccessRights>) policyCache.retrieve(cacheKey);
        if (val == null) {
            JDBCPreparedStatement ps = db
                    .getStatement("getResourcePermissions.realm.select");
            try {
                ps.setInt(1, realmID);
                ResultSet rs = ps.executeQuery();
                val = buildResourcePermission(rs);
            } finally {
                ps.releasePreparedStatement();
            }
        }
        
        return val;
    }

}