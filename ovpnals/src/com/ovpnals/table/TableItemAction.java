package com.ovpnals.table;

import com.ovpnals.core.MenuItem;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.security.SessionInfo;


/**
 * Extension of {@link MenuItem} to be used for <i>Table Item Actions</i>.
 * These are actions that may be linked to any {@link TableItemModel}
 * via its ID and will be available for each item the model.
 * <p> 
 * Item actions have a number of additional attributes that determine 
 * amongst other thing how they are presented to the user.
 * <p>
 * <ul>
 *    <li><b>Important</b>. In item action is important, it may
 *    be displayed more prominantly. For example, the default
 *    theme would render important actions as always visible
 *    icons, where unimportant actions get relegated to the 
 *    &quot;More&quot; menu.</li> 
 * </ul>
 * 
 * @author brett
 */
public class TableItemAction extends MenuItem {
	
	// Private instance variables;

	private boolean important;
    
    /**
     * Constructor for all navigation contexts.
     * 
     * @param id table model ID to 
     * @param messageResourcesKey
     * @param weight weight
     * @param important important 
     */
    public TableItemAction(String id, String messageResourcesKey, int weight, boolean important) {
        this(id, messageResourcesKey, weight, null, important);
    }
	
	/**
	 * Constructor for all navigation contexts.
	 * 
	 * @param id table model ID to 
	 * @param messageResourcesKey
	 * @param weight weight
     * @param target target frame
	 * @param important important 
	 */
	public TableItemAction(String id, String messageResourcesKey, int weight, String target, boolean important) {
		this(id, messageResourcesKey, weight, target, important, SessionInfo.ALL_CONTEXTS);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param id table model ID to 
	 * @param messageResourcesKey
	 * @param weight weight
	 * @param important important
	 * @param navigationContext navigation context mask 
	 */
	public TableItemAction(String id, String messageResourcesKey, int weight, boolean important, int navigationContext) {
		this(id, messageResourcesKey, weight, null, important, navigationContext);
	}
    
    /**
     * Constructor.
     * 
     * @param id table model ID to 
     * @param messageResourcesKey
     * @param weight weight
     * @param target target frame
     * @param important important
     * @param navigationContext navigation context mask 
     */
    public TableItemAction(String id, String messageResourcesKey, int weight, String target, boolean important, int navigationContext) {
        super(id, messageResourcesKey, "", weight, true, target, navigationContext);
        init(important);
    }
	
    /**
     * Constructor.
     * 
     * @param id table model ID to 
     * @param messageResourcesKey
     * @param weight weight
     * @param target target frame
     * @param important important
     * @param navigationContext navigation context mask 
     * @param resourceTypeOfPermissionsRequired 
     * @param permissionsRequired 
     */
    public TableItemAction(String id, String messageResourcesKey, int weight, String target, boolean important,
                    int navigationContext, ResourceType resourceTypeOfPermissionsRequired, Permission[] permissionsRequired) {
        super(id, messageResourcesKey, "", weight, true, target, navigationContext, resourceTypeOfPermissionsRequired, permissionsRequired);
        init(important);
    }

    /**
	 * Get if the action is <i>Important</i>. Important actions
	 * should be renderered more prominently by the theme in
	 * use.
	 * 
	 * @return action is important
	 */
	public boolean isImportant() {
		return important;
	}
	
	void init(boolean important) {
		this.important = important;		 
	}
	
	/**
	 * Table Item Actions should overide this method to provide specific behaviour
	 * for the onclick event.
	 * 
	 * @param availableItem available item
	 * @return onclick link
	 */
	public String getOnClick(AvailableTableItemAction availableItem) {
		return "";
	}
	
	/**
	 * Table Item Actions should overide this method to provide specific behaviour
	 * for testing if the action is enabled.
	 * 
	 * @param availableItem available item
	 * @return enabled
	 */
	public boolean isEnabled(AvailableTableItemAction availableItem) {
		return true;
	}
	
	/**
	 * Table Item Actions should overide this method to provide specific behaviour
	 * for the href
	 * 
	 * @param availableItem available item
	 * @return href
	 */
	public String getPath(AvailableTableItemAction availableItem) {
		return "#";
	}
	
	/**
	 * If an additional attribute should be rendered. This method should 
	 * return its name, otherwise an empty string should be returned.
	 * 
	 * @return additional attribute name
	 */
	public String getAdditionalAttributeName() {
		return "";
	}
	
	/**
	 * If an additional attribute should be rendered. This method should 
	 * return its value, otherwise an empty string should be returned.
	 * 
	 * @return additional attribute value
	 */
	public String getAdditionalAttributeValue(AvailableTableItemAction availableItem) {
		return "";
	}
	
	/**
	 * Get this actions tooltip content location. If an empty string is returned
	 * the tooltip should is retrieved using message resources.
	 * 
	 * @return tooltip content location
	 */
	public String getToolTipContentLocation(AvailableTableItemAction availableItem) {
		return "";
	}
	
	/**
	 * Get this actions tooltip width
	 * 
	 * @return tooltip width
	 */
	public int getToolTipWidth(AvailableTableItemAction availableItem) {
		/* TODO must be able to use css for this somehow. Will probably require
		 * changing the tooltip Javascript
		 */		
		return 140;
	}

}
