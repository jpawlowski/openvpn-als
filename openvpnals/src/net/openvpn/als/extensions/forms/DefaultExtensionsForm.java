
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
			

package net.openvpn.als.extensions.forms;

import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.extensions.ExtensionBundle;
import net.openvpn.als.extensions.ExtensionBundleItem;
import net.openvpn.als.extensions.ExtensionBundleItemModel;
import net.openvpn.als.table.forms.AbstractPagerForm;

public class DefaultExtensionsForm extends AbstractPagerForm {
    static final long serialVersionUID = 4283488241230531541L;
    private static final Log logger = LogFactory.getLog(DefaultExtensionsForm.class);
    
    private String submitAction = null;
    private String subFormName = null;
    

    public String getSubFormName() {
        return subFormName;
    }

    public void setSubFormName(String subFormName) {
        this.subFormName = subFormName;
    }

    public DefaultExtensionsForm(String submitAction, String subFormName) {
        super(new ExtensionBundleItemModel());
        this.submitAction = submitAction;
        this.subFormName = subFormName;
    }

    public String getSubmitAction() {
        return submitAction;
    }

    public void setSubmitAction(String submitAction) {
        this.submitAction = submitAction;
    }

    /**
     * <p>
     * Initialise the pager and the items and the ability to sort.
     * 
     * @param session The session information.
     * @param extensions Collection of ExtensionBundle
     */
    public void initialise(HttpSession session, Collection<ExtensionBundle> extensions) {
        super.initialize(session, "status");

        try {
            for (ExtensionBundle bundle : extensions) {
                this.getModel().addItem(new ExtensionBundleItem(bundle, false, subFormName));
            }

            checkSort();
            getPager().rebuild(getFilterText());
        } catch (Throwable t) {
            logger.error("Failed to initialise resources form.", t);
        }
    }
}