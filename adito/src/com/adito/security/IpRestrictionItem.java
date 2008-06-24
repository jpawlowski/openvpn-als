package com.adito.security;

import javax.servlet.http.HttpServletRequest;

import com.adito.core.CoreUtil;
import com.adito.table.TableItem;


/**
 * Wrapper object for {@link IpRestriction} represented as items in tables in 
 * the user interface.
 */
public class IpRestrictionItem implements TableItem {
    
    //  Private instance variables
	
    private IpRestriction ipRestriction;
    private boolean canMoveUp, canMoveDown, canDelete;


    /**
     * Constructor
     * 
     * @param ipRestriction restriction to wrapo
     * @param canMoveUp restriction can be moved up
     * @param canMoveDown  restriction can be moved dowb
     * @param canDelete restriction can be deleted
     */
    public IpRestrictionItem(IpRestriction ipRestriction, boolean canMoveUp, boolean canMoveDown, boolean canDelete) {
        this.ipRestriction = ipRestriction;
        this.canMoveUp = canMoveUp;
        this.canMoveDown = canMoveDown;
        this.canDelete = canDelete;
    }
    
    /**
     * Get if this item may be deleted. As a rule, all restrictions may be
     * deleted except for the default one.
     * 
     * @return item can be deleted
     */
    public boolean isCanDelete() {
        return canDelete;
    }
    
    /**
     * Get if this item may be moved up one row. This will be <code>false</code>
     * if the item is the first in the list.
     * 
     * @return item can move up
     */
    public boolean isCanMoveUp() {
        return canMoveUp;
    }
    
    /**
     * Get if this item may be moved up down row. This will be <code>false</code>
     * if the item is the last in the list.
     * 
     * @return item can move down
     */
    public boolean isCanMoveDown() {
        return canMoveDown;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.adito.table.TableItem#getColumnValue(int)
     */
    public Object getColumnValue(int col) {
        switch (col) {
            case 0:
                return ipRestriction.getAddress();
            default:
                return new Boolean("true");
        }
    }
    
    /**
     * Get the {@link IpRestriction} this object wraps.
     * 
     * @return ip restriction
     */
    public IpRestriction getIpRestriction() {
    	    return ipRestriction;
    }
    
    public String getSmallIconPath(HttpServletRequest request) {
        if (ipRestriction.getAllowed()){
            return CoreUtil.getThemePath(request.getSession()) + "/images/actions/allowed.gif";
        }
        else {
            return CoreUtil.getThemePath(request.getSession()) + "/images/actions/denied.gif";
        }
    }

}
