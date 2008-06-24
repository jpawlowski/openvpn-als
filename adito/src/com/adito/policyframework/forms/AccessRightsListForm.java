
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
			
package com.adito.policyframework.forms;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.policyframework.AccessRights;
import com.adito.policyframework.AccessRightsItem;
import com.adito.policyframework.ResourceItemModel;

/**
 * Implementation of a
 * {@link com.adito.policyframework.forms.AbstractResourcesForm} which
 * contains a model of {@link com.adito.policyframework.AccessRightsItem}
 * instances.
 */
public class AccessRightsListForm extends AbstractResourcesForm<AccessRightsItem> {

    final static Log log = LogFactory.getLog(AccessRightsListForm.class);

    /**
     * Constructor
     */
    public AccessRightsListForm() {
        super(new AccessRightsModel());
    }

    /**
     * @param resources
     * @param session
     */
    public void initialize(List resources, HttpSession session) {
        super.initialize(resources, AccessRights.class, AccessRightsItem.class, session, "name");
    }

    /**
     * Implementation of a
     * {@link com.adito.policyframework.ResourceItemModel} which is a
     * model of {@link com.adito.policyframework.AccessRightsItem}
     * instances.
     */
    static class AccessRightsModel extends ResourceItemModel<AccessRightsItem> {

        /**
         * Constructor
         */
        public AccessRightsModel() {
            super("accessRights");
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.adito.policyframework.ResourceItemModel#getColumnCount()
         */
        public int getColumnCount() {
            return 3;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.adito.policyframework.ResourceItemModel#getColumnName(int)
         */
        public String getColumnName(int col) {
            if (col == 2) {
                return "permissionClass";
            }
            return super.getColumnName(col);
        }

        public int getColumnWidth(int col) {
            return 0;
        }
    }
}