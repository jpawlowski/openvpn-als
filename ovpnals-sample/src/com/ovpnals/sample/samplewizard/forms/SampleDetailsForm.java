
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.sample.samplewizard.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.policyframework.Resource;
import com.ovpnals.wizard.AbstractWizardSequence;
import com.ovpnals.wizard.forms.DefaultWizardForm;

/**
 * <p>
 * The form for all other attributes associated with the TunneledSite resource.
 * 
 * @author James D Robinson <a href="mailto:james@localhost">&lt;james@localhost&gt;</a>
 * 
 */
public class SampleDetailsForm extends DefaultWizardForm {

    final static Log log = LogFactory.getLog(SampleDetailsForm.class);

    // TODO sampleAttributes attributes should be defined here.

    /**
     * Constructor
     */
    public SampleDetailsForm() {
        super(true, true, "/WEB-INF/jsp/content/sample/samplewizard/sampleDetails.jspf", "resourceName", true, false,
                        "sampleDetails", "sample", "samplewizard.sampleDetails", 2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#init(com.ovpnals.wizard.AbstractWizardSequence,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        super.init(sequence, request);
        // TODO sampleAttribute = ((Integer) sequence.getAttribute(ATTR_SAMPLE,
        // ""));

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#apply(com.ovpnals.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        // TODO sequence.putAttribute(ATTR_SAMPLE, sampleAttribute);
    }

    public void parentResourcePermissionChanged(AbstractWizardSequence sequence) {
        // TODO Auto-generated method stub
        
    }

    public Resource getResourceByName(String name) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
