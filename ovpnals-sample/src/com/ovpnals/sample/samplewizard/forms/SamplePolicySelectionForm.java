
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

import com.ovpnals.policyframework.forms.AbstractWizardResourcePolicySelectionForm;
import com.ovpnals.sample.Sample;

/**
 * <p>
 * The form for selecting policies to attach to this resource.
 * 
 * @author James D Robinson <a href="mailto:james@localhost">&lt;james@localhost&gt;</a>
 * 
 */
public class SamplePolicySelectionForm extends AbstractWizardResourcePolicySelectionForm {

    /**
     * Constructor
     */
    public SamplePolicySelectionForm() {
        super(true, true, "/WEB-INF/jsp/tiles/wizardPolicySelection.jspf", "samplePolicySelection", "sample",
                        "samplewizard.samplePolicySelection", 3, Sample.SAMPLE_RESOURCE_TYPE);
    }
}
