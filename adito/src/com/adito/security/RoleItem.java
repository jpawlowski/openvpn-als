/*
 */
package com.adito.security;

import javax.servlet.http.HttpServletRequest;

import com.adito.core.CoreUtil;
import com.adito.table.TableItem;

/**
 * 
 * Implementation of {@link TableItem} suitable for display configured
 * <i>Groups</i> (previously known as <i>Roles</i>.
 */
public class RoleItem implements TableItem {
    private Role role;

    /**
     * Constructor.
     *
     * @param role role
     * @param accounts accounts
     */
    public RoleItem(Role role) {
        this.role = role;
    }
    
    /**
     * Get the role this item wraps
     * 
     * @return role
     */
    public Role getRole() {
        return role;
    }

    public Object getColumnValue(int col) {
        return role.getPrincipalName();
    }
    
    /**
     * @return String
     */
    public String getLink() {
        return "#";
    }
    
    /**
     * @return String
     */
    public String getOnClick() {
        return "";
    }

    public String getSmallIconPath(HttpServletRequest request) {
        return CoreUtil.getThemePath(request.getSession()) + "/images/actions/group.gif";
    }
}