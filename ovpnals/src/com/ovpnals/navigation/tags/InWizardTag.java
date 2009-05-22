
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
			
package com.ovpnals.navigation.tags;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.security.Constants;
import com.ovpnals.wizard.AbstractWizardSequence;

public class InWizardTag extends TagSupport {
    final static Log log = LogFactory.getLog(InWizardTag.class);
    
    private String value;
    private String finish = "true";

    /**
     * @return Returns the finish.
     */
    public String getFinish() {
        return finish;
    }

    /**
     * @param finish The finish to set.
     */
    public void setFinish(String finish) {
        this.finish = finish;
    }

    public InWizardTag() {
        value = "true";
    }

    public int doStartTag() {
        AbstractWizardSequence seq = (AbstractWizardSequence)pageContext.getSession().getAttribute(Constants.WIZARD_SEQUENCE); 
        if (seq != null && ( finish.equals("true") || seq.getCurrentPageForm().getStepIndex() != 0 ) ) {
            return value.equalsIgnoreCase("false") ? SKIP_BODY : EVAL_BODY_INCLUDE;
        }
        return value.equalsIgnoreCase("false") ? EVAL_BODY_INCLUDE : SKIP_BODY;
    }

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }
}