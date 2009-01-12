package com.adito.policyframework.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import com.adito.core.BundleActionMessage;
import com.adito.navigation.Favorite;
import com.adito.navigation.FavoriteResourceType;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.PolicyUtil;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceType;
import com.adito.policyframework.ResourceUtil;
import com.adito.policyframework.forms.AbstractResourcesForm;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.SystemDatabase;
import com.adito.security.SystemDatabaseFactory;
import com.adito.security.User;

/**
 * <p>
 * Abstract class for favorites.
 */
public abstract class AbstractFavoriteResourcesDispatchAction extends AbstractResourcesDispatchAction {


    /**
     * Construtor
     */
    public AbstractFavoriteResourcesDispatchAction() {
        super();
    }

    /**
     * Constructor for normal resource types that have the standard,
     * Create / Edit / Assign, Edit / Assign, Delete and Assign permissions
     * 
     * @param resourceType resource type
     * @param requiresResources requires actual resources of type
     */
    public AbstractFavoriteResourcesDispatchAction(ResourceType resourceType, ResourceType requiresResources) {
        this(resourceType, new Permission[] { PolicyConstants.PERM_EDIT_AND_ASSIGN, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE, PolicyConstants.PERM_ASSIGN, PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE  },
                        PolicyConstants.PERM_EDIT_AND_ASSIGN, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE,
                        PolicyConstants.PERM_ASSIGN, requiresResources);
    }

    /**
     * @param resourceType The resource type.
     * @param requiredPermissions The required permissions
     * @param editPermission The edit permission
     * @param createPermission The create permission
     * @param removePermission The remove permission
     * @param assignPermission The assign permission
     */
    public AbstractFavoriteResourcesDispatchAction(ResourceType resourceType, Permission[] requiredPermissions,
                                                   Permission editPermission, Permission createPermission,
                                                   Permission removePermission, Permission assignPermission) {
        super(resourceType, requiredPermissions, editPermission, createPermission, removePermission, assignPermission);
    }

    /**
     * @param resourceType The resource type.
     * @param requiredPermissions The required permissions.
     * @param editPermission The edit permission.
     * @param createPermission The create permission.
     * @param removePermission The remove permission.
     * @param assignPermission The assign permission.
     * @param requiresResources The resource type required.
     */
    public AbstractFavoriteResourcesDispatchAction(ResourceType resourceType, Permission[] requiredPermissions,
                                                   Permission editPermission, Permission createPermission,
                                                   Permission removePermission, 
                                                   Permission assignPermission, ResourceType requiresResources) {
        super(resourceType, requiredPermissions, editPermission, createPermission, removePermission, assignPermission, requiresResources);
    }

    /**
     * Add the item as a <i>User Favorite</i>.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception
     */
    public ActionForward favorite(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        AbstractResourcesForm f = (AbstractResourcesForm) form;
        ActionMessages msgs = new ActionMessages();
        User user = LogonControllerFactory.getInstance().getUser(request);
        SystemDatabase sdb = SystemDatabaseFactory.getInstance();
        if (getSessionInfo(request).getNavigationContext() != SessionInfo.USER_CONSOLE_CONTEXT) {
            throw new Exception("Favorites may only be set in the user console.");
        }        
        Resource resource = getResourceById(f.getSelectedResource());
        if(ResourceUtil.filterResourceIdsForGlobalFavorites(PolicyDatabaseFactory.getInstance().getGrantedResourcesOfType(getSessionInfo(request).getUser(),
            resource.getResourceType()), resource.getResourceType()).contains(new Integer(resource.getResourceId()))) {
            throw new Exception("Cannot set a user favorite for items that have a policy favorite.");
        }
        if (sdb.getFavorite(getResourceType().getResourceTypeId(), user, f.getSelectedResource()) != null) {
            msgs.add(Globals.ERROR_KEY, new BundleActionMessage("navigation", "addToFavorites.error.alreadyFavorite", resource
                .getResourceName()));
            saveErrors(request, msgs);
            return mapping.findForward("refresh");
        }
        sdb.addFavorite(getResourceType().getResourceTypeId(), f.getSelectedResource(), user.getPrincipalName());
        msgs.add(Globals.MESSAGES_KEY, new BundleActionMessage("navigation", "addToFavorites.message.favoriteAdded", resource
            .getResourceName()));
        saveMessages(request, msgs);
        return mapping.findForward("refresh");
    }

    /**
     * Remove the item from the users favorites.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception
     */
    public ActionForward removeFavorite(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        AbstractResourcesForm f = (AbstractResourcesForm) form;
        ActionMessages msgs = new ActionMessages();
        User user = LogonControllerFactory.getInstance().getUser(request);
        SystemDatabase sdb = SystemDatabaseFactory.getInstance();
        if (getSessionInfo(request).getNavigationContext() != SessionInfo.USER_CONSOLE_CONTEXT) {
            throw new Exception("Favorites may only be set in the user console.");
        }        
        Resource resource = getResourceById(f.getSelectedResource());
        if(ResourceUtil.filterResourceIdsForGlobalFavorites(PolicyDatabaseFactory.getInstance().getGrantedResourcesOfType(getSessionInfo(request).getUser(),
            resource.getResourceType()), resource.getResourceType()).contains(new Integer(resource.getResourceId()))) {
            throw new Exception("Cannot remove a user favorite from items that have a policy favorite.");
        }        
        if (sdb.getFavorite(getResourceType().getResourceTypeId(), user, f.getSelectedResource()) == null) {
            msgs.add(Globals.ERROR_KEY, new BundleActionMessage("navigation", "removeFromFavorites.error.notFavorite", resource
                .getResourceName()));
            saveErrors(request, msgs);
            return mapping.findForward("refresh");
        }
        sdb.removeFavorite(getResourceType().getResourceTypeId(), f.getSelectedResource(), user.getPrincipalName());
        msgs.add(Globals.MESSAGES_KEY, new BundleActionMessage("navigation", "removeFromFavorites.message.favoriteAdded", resource
            .getResourceName()));
        saveMessages(request, msgs);
        return mapping.findForward("refresh");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        // Get the favorites for this resource type and store the list in the
        // form, this may then be used to determine if an item is a favorite
        List<Integer> l = new ArrayList<Integer>();
        List<Favorite> favorites = SystemDatabaseFactory.getInstance().getFavorites(getResourceType().getResourceTypeId(),
            getSessionInfo(request).getUser());
        for (Iterator i = favorites.iterator(); i.hasNext();) {
            Favorite f = (Favorite) i.next();
            l.add(new Integer(f.getFavoriteKey()));
        }
        ((AbstractResourcesForm) form).setUserFavorites(l);
        ((AbstractResourcesForm) form).setGlobalFavorites(ResourceUtil.filterResourceIdsForGlobalFavorites(PolicyDatabaseFactory.getInstance()
            .getGrantedResourcesOfType(getSessionInfo(request).getUser(), getResourceType()), getResourceType()));
        return super.unspecified(mapping, form, request, response);
    }
    
    /* (non-Javadoc)
     * @see com.adito.policyframework.actions.AbstractResourcesDispatchAction#getResourceById(int)
     */
    public Resource getResourceById(int id) throws Exception {
        return ((FavoriteResourceType)getResourceType()).getResourceById(id);
    }

}
