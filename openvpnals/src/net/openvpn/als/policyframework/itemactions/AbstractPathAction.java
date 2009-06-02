
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.policyframework.itemactions;

import java.text.MessageFormat;

import org.apache.struts.action.ActionMapping;

import net.openvpn.als.boot.Util;
import net.openvpn.als.policyframework.ResourceItem;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.table.AvailableTableItemAction;
import net.openvpn.als.table.TableItemAction;

/**
 */
public class AbstractPathAction extends TableItemAction {

    protected final String requiredPath;


    
    /**
     * Constructor for all navigation contexts.
     * 
     * @param id table model ID to 
     * @param messageResourcesKey
     * @param weight weight
     * @param important important 
     * @param requiredPath 
     */
    public AbstractPathAction(String id, String messageResourcesKey, int weight, boolean important, String requiredPath) {
        this(id, messageResourcesKey, weight, null, important, requiredPath);
    }
	
	/**
	 * Constructor for all navigation contexts.
	 * 
	 * @param id table model ID to 
	 * @param messageResourcesKey
	 * @param weight weight
     * @param target target frame
	 * @param important important 
	 * @param requiredPath 
	 */
	public AbstractPathAction(String id, String messageResourcesKey, int weight, String target, boolean important, String requiredPath) {
		this(id, messageResourcesKey, weight, target, important, SessionInfo.ALL_CONTEXTS, requiredPath);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param id table model ID to 
	 * @param messageResourcesKey
	 * @param weight weight
	 * @param important important
	 * @param navigationContext navigation context mask 
	 * @param requiredPath 
	 */
	public AbstractPathAction(String id, String messageResourcesKey, int weight, boolean important, int navigationContext, String requiredPath) {
		this(id, messageResourcesKey, weight, null, important, navigationContext, requiredPath);
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
     * @param requiredPath 
     */
    public AbstractPathAction(String id, String messageResourcesKey, int weight, String target, boolean important, int navigationContext, String requiredPath) {
        super(id, messageResourcesKey, weight, target, important, navigationContext, null, null);
        this.requiredPath = requiredPath;
    }

    /*
     * (non-Javadoc)
     * @see net.openvpn.als.table.TableItemAction#getPath(net.openvpn.als.table.AvailableTableItemAction)
     */
    public String getPath(AvailableTableItemAction availableItem) {
        ResourceItem item = (ResourceItem) availableItem.getRowItem();
        return getPath(String.valueOf(item.getResource().getResourceId()), availableItem);
    }
    
    /**
     * @param parameterId
     * @param availableItem
     * @return String
     */
    public String getPath(String parameterId, AvailableTableItemAction availableItem) {
        ActionMapping attribute = (ActionMapping)availableItem.getRequest().getAttribute(Constants.REQ_ATTR_ACTION_MAPPING);
        String formattedPath = MessageFormat.format(requiredPath, new Object[] {parameterId, attribute.getName(), attribute.getPath()});
        return formattedPath;        
    }
}