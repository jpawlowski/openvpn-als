
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
			
package com.adito.sample.samplewizard.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.adito.core.forms.AbstractFavoriteResourceDetailsWizardForm;
import com.adito.navigation.FavoriteResourceType;
import com.adito.sample.Sample;
import com.adito.wizard.AbstractWizardSequence;

/**
 * <p>
 * The form for the default resource attributes.
 * 
 * @author James D Robinson <a href="mailto:james@localhost">&lt;james@localhost&gt;</a>
 * 
 */
public class SampleDefaultDetailsForm extends AbstractFavoriteResourceDetailsWizardForm {

    final static Log log = LogFactory.getLog(SampleDefaultDetailsForm.class);

    /**
     * Constructor
     */
    public SampleDefaultDetailsForm() {
        super(true, false, "/WEB-INF/jsp/content/sample/samplewizard/sampleDefaultDetails.jspf", "resourceName", true, false,
                        "sampleDefaultDetails", "sample", "samplewizard.sampleDefaultDetails", 1, (FavoriteResourceType)Sample.SAMPLE_RESOURCE_TYPE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (getResourceName() != null && !isCancelling()) {
            ActionErrors errs = super.validate(mapping, request);
            // TODO validate the extra attributes.
            return errs;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.forms.AbstractResourceDetailsWizardForm#parentResourcePermissionChanged(com.adito.wizard.AbstractWizardSequence)
     */
    public void parentResourcePermissionChanged(AbstractWizardSequence sequence) {
    }
}
