
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
			
package com.adito.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.ResourceType;
import com.adito.policyframework.ResourceUtil;
import com.adito.properties.Property;
import com.adito.properties.impl.systemconfig.SystemConfigKey;
import com.adito.security.SessionInfo;

/**
 * Represents a single item in the tree of available menu actions. A menu item
 * may either be a submenu or a leaf menu item. Whenever a user navigates to a
 * page, the entire tree of <code>MenuItem</code>s is travered looking for
 * menu items that are valid for the current state. This is then used to build
 * up a tree of {@link com.adito.core.AvailableMenuItem} objects that is
 * passed to the view for rendering.
 * <p>
 * A <code>MenuItem</code> is deemed valid for the current state if
 * <code>true</code> is returned from {@link #isAvailable(HttpServletRequest)}
 * method. By default this checks the parmeters parameters passed to this object
 * when constructing <code>administratorOnly</code>,
 * <code>availableInSetup</code> and <code>permissionId</code>. If any
 * actions have any special requirements as to when they are visible,
 * <code>MenuItem</code> should be sub-classed and
 * {@link #isAvailable(HttpServletRequest)} should be overidden.
 * <p>
 * Every menu item must have two message resources added to a bundle. The keys
 * must be in the format <strong>menuItem.[id].name</strong> and
 * <strong>menuItem.[id].description</strong>. The bundle name must be passed
 * as the contructor parameter <code>messageResourcesKey</code>.
 * 
 * @see com.adito.core.AvailableMenuItem
 */
public class MenuItem implements Comparable {

    final static Log log = LogFactory.getLog(MenuItem.class);

    // Protected instance variables

    protected String messageResourcesKey;
    protected String path;
    protected List children;
    protected MenuItem parent;
    protected boolean leaf;
    protected boolean hasReferrer = false;
    protected int weight;
    protected int navigationContext;
    protected ResourceType resourceTypeOfPermissionsRequired;
    protected ResourceType resourcesOfTypeRequired;
    protected Permission[] permissionsRequired;

    // Private instance variables

    private String id;
    private String target = "_self";

    /**
     * Construct a new <code>MenuItem</code>.
     * 
     * @param id menu item item
     * @param messageResourcesKey the name of the resource bundle to retrieve
     *        menu name displayed to user
     * @param part the URL or relative path that will be navigated to upon
     *        selecting this menu item
     * @param weight weight of item in its parent menu used to order the items.
     * @param leaf <code>true</code> if this item is not a sub-menu.
     * @param navigationContext the navigation context this menu item should
     *        appear in. This should be a bitmask of the constants
     *        {@link com.adito.security.SessionInfo#USER_CONSOLE_CONTEXT}
     *        and
     *        {@link com.adito.security.SessionInfo#MANAGEMENT_CONSOLE_CONTEXT}.
     */
    public MenuItem(String id, String messageResourcesKey, String path, int weight, boolean leaf, int navigationContext) {
        this(id, messageResourcesKey, path, weight, leaf, null, navigationContext, null, null, null);
    }

    /**
     * Construct a new <code>MenuItem</code>.
     * 
     * @param id menu item item
     * @param messageResourcesKey the name of the resource bundle to retrieve
     *        menu name displayed to user
     * @param part the URL or relative path that will be navigated to upon
     *        selecting this menu item
     * @param weight weight of item in its parent menu used to order the items.
     * @param availableInSetup the action is only valid when in setup mode
     * @param leaf <code>true</code> if this item is not a sub-menu.
     * @param target the browser target (i.e. _self, _blank etc). A value of
     *        null means _self.
     * @param navigationContext the navigation context this menu item should
     *        appear in. This should be a bitmask of the constants
     *        {@link com.adito.security.SessionInfo#USER_CONSOLE_CONTEXT}
     *        and
     *        {@link com.adito.security.SessionInfo#MANAGEMENT_CONSOLE_CONTEXT}.
     */
    public MenuItem(String id, String messageResourcesKey, String path, int weight, boolean leaf, String target,
                    int navigationContext) {
        this(id, messageResourcesKey, path, weight, leaf, target, navigationContext, null, null, null);
    }

    /**
     * Construct a new <code>MenuItem</code>. Because resource type can be
     * supplied, this implies that the menu item is for the management console
     * 
     * @param id menu item item
     * @param messageResourcesKey the name of the resource bundle to retrieve
     *        menu name displayed to user
     * @param part the URL or relative path that will be navigated to upon
     *        selecting this menu item
     * @param weight weight of item in its parent menu used to order the items
     * @param leaf <code>true</code> if this item is not a sub-menu.
     * @param navigationContext the navigation context this menu item should
     *        appear in. This should be a bitmask of the constants
     *        {@link com.adito.security.SessionInfo#USER_CONSOLE_CONTEXT}
     *        and
     *        {@link com.adito.security.SessionInfo#MANAGEMENT_CONSOLE_CONTEXT}.
     * @param resourceTypeOfPermissionsRequired resource type of any resource
     *        permissions required. May be <code>null</code> if you do not
     *        wish to check for permissions
     * @param permissionsRequired array of required permission. Must be supplied
     *        if you have specified a
     *        <code>resourceTypeOfPermissionsRequired</code> otherwise may be
     *        null.
     */
    public MenuItem(String id, String messageResourcesKey, String path, int weight, boolean leaf,
                    ResourceType resourceTypeOfPermissionsRequired, Permission[] permissionsRequired) {
        this(id, messageResourcesKey, path, weight, leaf, null, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
                        resourceTypeOfPermissionsRequired, permissionsRequired, null);
    }

    /**
     * Construct a new <code>MenuItem</code>.
     * 
     * @param id menu item item
     * @param messageResourcesKey the name of the resource bundle to retrieve
     *        menu name displayed to user
     * @param part the URL or relative path that will be navigated to upon
     *        selecting this menu item
     * @param weight weight of item in its parent menu used to order the items.
     * @param leaf <code>true</code> if this item is not a sub-menu.
     * @param target the browser target (i.e. _self, _blank etc). A value of
     *        null means _self.
     * @param navigationContext the navigation context this menu item should
     *        appear in. This should be a bitmask of the constants
     *        {@link com.adito.security.SessionInfo#USER_CONSOLE_CONTEXT}
     *        and
     *        {@link com.adito.security.SessionInfo#MANAGEMENT_CONSOLE_CONTEXT}.
     * @param resourceTypeOfPermissionsRequired resource type of any resource
     *        permissions required. May be <code>null</code> if you do not
     *        wish to check for permissions
     * @param permissionsRequired array of required permission. Must be supplied
     *        if you have specified a
     *        <code>resourceTypeOfPermissionsRequired</code> otherwise may be
     *        null.
     */
    public MenuItem(String id, String messageResourcesKey, String path, int weight, boolean leaf, String target,
                    int navigationContext, ResourceType resourceTypeOfPermissionsRequired, Permission[] permissionsRequired) {
        this(id, messageResourcesKey, path, weight, leaf, target, navigationContext, resourceTypeOfPermissionsRequired,
                        permissionsRequired, null);
    }

    /**
     * Construct a new <code>MenuItem</code>.
     * 
     * @param id menu item item
     * @param messageResourcesKey the name of the resource bundle to retrieve
     *        menu name displayed to user
     * @param part the URL or relative path that will be navigated to upon
     *        selecting this menu item
     * @param weight weight of item in its parent menu used to order the items.
     * @param leaf <code>true</code> if this item is not a sub-menu.
     * @param target the browser target (i.e. _self, _blank etc). A value of
     *        null means _self.
     * @param navigationContext the navigation context this menu item should
     *        appear in. This should be a bitmask of the constants
     *        {@link com.adito.security.SessionInfo#USER_CONSOLE_CONTEXT}
     *        and
     *        {@link com.adito.security.SessionInfo#MANAGEMENT_CONSOLE_CONTEXT}.
     * @param resourceTypeOfPermissionsRequired resource type of any resource
     *        permissions required. May be <code>null</code> if you do not
     *        wish to check for permissions
     * @param permissionsRequired array of required permission. Must be supplied
     *        if you have specified a
     *        <code>resourceTypeOfPermissionsRequired</code> otherwise may be
     *        null.
     * @param resourcesOfTypeRequired if specified the user must have access to
     *        at least one resource of the type.
     */
    public MenuItem(String id, String messageResourcesKey, String path, int weight, boolean leaf, String target,
                    int navigationContext, ResourceType resourceTypeOfPermissionsRequired, Permission[] permissionsRequired,
                    ResourceType resourcesOfTypeRequired) {
    	this(id, messageResourcesKey, path, weight, leaf, target, navigationContext, resourceTypeOfPermissionsRequired,
                        permissionsRequired, resourcesOfTypeRequired, true);
    }

    /**
     * Construct a new <code>MenuItem</code>.
     * 
     * @param id menu item item
     * @param messageResourcesKey the name of the resource bundle to retrieve
     *        menu name displayed to user
     * @param part the URL or relative path that will be navigated to upon
     *        selecting this menu item
     * @param weight weight of item in its parent menu used to order the items.
     * @param leaf <code>true</code> if this item is not a sub-menu.
     * @param target the browser target (i.e. _self, _blank etc). A value of
     *        null means _self.
     * @param navigationContext the navigation context this menu item should
     *        appear in. This should be a bitmask of the constants
     *        {@link com.adito.security.SessionInfo#USER_CONSOLE_CONTEXT}
     *        and
     *        {@link com.adito.security.SessionInfo#MANAGEMENT_CONSOLE_CONTEXT}.
     * @param resourceTypeOfPermissionsRequired resource type of any resource
     *        permissions required. May be <code>null</code> if you do not
     *        wish to check for permissions
     * @param permissionsRequired array of required permission. Must be supplied
     *        if you have specified a
     *        <code>resourceTypeOfPermissionsRequired</code> otherwise may be
     *        null.
     * @param resourcesOfTypeRequired if specified the user must have access to
     *        at least one resource of the type.
     * @param hasReferrer allows item to specify if a referrer parameter is added to link
     */
    public MenuItem(String id, String messageResourcesKey, String path, int weight, boolean leaf, String target,
                    int navigationContext, ResourceType resourceTypeOfPermissionsRequired, Permission[] permissionsRequired,
                    ResourceType resourcesOfTypeRequired, boolean hasReferrer) {
    	
    	super();
        this.navigationContext = navigationContext;
        this.target = target == null ? "_self" : target;
        this.id = id;
        this.leaf = leaf;
        this.weight = weight;
        this.messageResourcesKey = messageResourcesKey;
        this.path = path;
        this.permissionsRequired = permissionsRequired;
        this.resourceTypeOfPermissionsRequired = resourceTypeOfPermissionsRequired;
        this.resourcesOfTypeRequired = resourcesOfTypeRequired;
        this.hasReferrer = hasReferrer;
    }

    /**
     * Add a child to this submenu.
     * 
     * @param menuItem menu item to add
     * @throws IllegalArgumentException if this menu item is a leaf
     */
    public void addChild(MenuItem menuItem) throws IllegalArgumentException {
        if (isLeaf()) {
            throw new IllegalArgumentException("Cannot add child menu items to leaf menu items.");
        }
        if (children == null) {
            children = new ArrayList();
        }
        children.add(menuItem);
    }

    /**
     * Remove a child from this menu
     * 
     * @param menuItem menu item to remove
     * @throws IllegalArgumentException if this menu item is a leaf
     */
    public void removeChild(MenuItem menuItem) throws IllegalArgumentException {
        if (isLeaf()) {
            throw new IllegalArgumentException("Cannot remove child menu items from leaf menu items.");
        }
        if (children != null) {
            children.remove(menuItem);
        }
    }

    /**
     * Get a child menu item given its name
     * 
     * @param id id
     * @return menu item
     */
    public MenuItem getChild(String id) {
        if (children != null) {
            for (Iterator i = children.iterator(); i.hasNext();) {
                MenuItem it = (MenuItem) i.next();
                if (it.getId().equals(id)) {
                    return it;
                }
            }
        }
        return null;
    }
    
   
    /**
     * Determine if referrer parameter should be added to link
     * @return
     */
    public boolean hasReferrer() {
    	return hasReferrer;
    }

    /**
     * Get if this menu item is a leaf. <code>false</code> means it is a
     * sub-menu
     * 
     * @return menu item is a leaf
     */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * Set the parent of menu item
     * 
     * @param parent
     */
    public void setParent(MenuItem parent) {
        this.parent = parent;
    }

    /**
     * Return the id of this menu item
     * 
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Get the name of the bundle to use for the message resources required for
     * this menu item.
     * 
     * @return bunlde name
     */
    public String getMessageResourcesKey() {
        return messageResourcesKey;
    }

    /**
     * Get the browser target (e.g. _self, _blank etc). If a value of
     * <code>null</code> is returned, the default <strong>_self</strong>
     * should be used.
     * 
     * @return browser targe
     */
    public String getTarget() {
        return target;
    }

    /**
     * Return the URL or relative path that should be navigated to if this menu
     * item is actioned.
     * 
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Determine if this menu item should be available based on the current
     * state. By default, this will check the current navigation context and
     * {@link #getPermissionId()}.
     * <p>
     * If the menu item has any other checks it should perform (checking if a
     * property is enabled for example), it should override this method
     * (probably calling the super implementation as well).
     * 
     * 
     * @param checkNavigationContext navigation context to check against
     * @param info user to check against permissions
     * @param request request
     * @return item is available
     */
    public boolean isAvailable(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
    	// PLUNDEN: Removing the context
		//         if ((ContextHolder.getContext().isSetupMode() && ((navigationContext & SessionInfo.SETUP_CONSOLE_CONTEXT) != 0))
        if ((false && ((navigationContext & SessionInfo.SETUP_CONSOLE_CONTEXT) != 0))
		// end change
                        || (navigationContext & checkNavigationContext) != 0 || navigationContext == 0 ) {
            
            // not available if there are no granted resources.
            try {
                if (Property.getPropertyBoolean(new SystemConfigKey("security.enforce.policy.resource.access"))) {
                    if (resourcesOfTypeRequired != null) {
                        if (ResourceUtil.getGrantedResource(info, resourcesOfTypeRequired).size() == 0) {
                            return false;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Failed to check auth schemes restrictions.", e);
                return false;
            }
            if (resourceTypeOfPermissionsRequired != null) {
                try {
                    boolean allowed = info != null && PolicyDatabaseFactory.getInstance().isPermitted(
                                    resourceTypeOfPermissionsRequired, permissionsRequired, info.getUser(), false);
                    if (!allowed) {
                        if (resourcesOfTypeRequired != null) {
                            return PolicyDatabaseFactory.getInstance().isPrincipalGrantedResourcesOfType(info.getUser(),
                                            resourcesOfTypeRequired, null);
                        }
                        return false;
                    }
                    return true;
                } catch (Exception e) {
                    log.error("Failed to check delegation rights.", e);
                    return false;
                }
            } else {
                try {
                    if (resourcesOfTypeRequired != null) {
                        return info != null &&  PolicyDatabaseFactory.getInstance().isPrincipalGrantedResourcesOfType(info.getUser(),
                                        resourcesOfTypeRequired, null);
                    }
                } catch (Exception e) {
                    log.error("Failed to check delegation rights.", e);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Return an {@link List} of all child menu items that are valid for the
     * current state (as determined by {@link #isAvailable(HttpServletRequest)}.
     * 
     * @param checkNavigationContext navigation context to check against
     * @param info session info
     * @param request request
     * 
     * @return list of available children
     */
    public List availableChildren(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
        List l = new ArrayList();
        if (children != null) {
            for (Iterator i = children.iterator(); i.hasNext();) {
                MenuItem it = (MenuItem) i.next();
                if (it.isAvailable(navigationContext, info, request)) {
                    l.add(it);
                }
            }
        }
        return l;
    }

    /**
     * Get if the menu is empty (i.e. contains no child items).
     * 
     * @return menu is empty
     */
    public boolean isEmpty() {
        return children == null || children.size() == 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        return new Integer(weight).compareTo(new Integer(((MenuItem) arg0).weight));
    }

    /**
     * Get the navigation context the menu item should appear in. This should be
     * a bitmask of the contstants
     * {@link com.adito.security.SessionInfo#USER_CONSOLE_CONTEXT} and
     * {@link com.adito.security.SessionInfo#MANAGEMENT_CONSOLE_CONTEXT}.
     * 
     * @return navigation context mask
     */
    public int getNavigationContext() {
        return navigationContext;
    }
}