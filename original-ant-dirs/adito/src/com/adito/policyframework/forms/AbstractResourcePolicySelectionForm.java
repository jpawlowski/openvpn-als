
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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.adito.input.MultiSelectDataSource;
import com.adito.policyframework.ResourcePermissionDelegatedPoliciesDatasource;

/**
 * Abstract implementation of a {@link AbstractWizardPolicySelectionForm}.
 * abstact implementation of the policy selection form.
 */
public class AbstractResourcePolicySelectionForm extends AbstractWizardPolicySelectionForm {

    /**
     * @param nextAvailable
     * @param previousAvailable
     * @param page
     * @param focussedField
     * @param autoComplete
     * @param finishAvailable
     * @param pageName
     * @param resourceBundle
     * @param resourcePrefix
     * @param stepIndex
     */
    public AbstractResourcePolicySelectionForm(boolean nextAvailable, boolean previousAvailable, String page, String focussedField,
                    boolean autoComplete, boolean finishAvailable, String pageName, String resourceBundle, String resourcePrefix,
                    int stepIndex) {
        super(nextAvailable, previousAvailable, page, focussedField, autoComplete, finishAvailable, pageName, resourceBundle,
                        resourcePrefix, stepIndex);
    }

    public MultiSelectDataSource createDatasource(ActionMapping mapping, HttpServletRequest request) {
        try {
            return new ResourcePermissionDelegatedPoliciesDatasource(null);
        } catch (Exception e) {
            throw new Error("Failed to create datasource for list of available policies.", e);
        }
    }

    public MultiSelectDataSource createDatasourceExcludePersonal(ActionMapping mapping, HttpServletRequest request) {
        try {
            return new ResourcePermissionDelegatedPoliciesDatasource(null);
        } catch (Exception e) {
            throw new Error("Failed to create datasource for list of available policies.", e);
        }
    }

}
