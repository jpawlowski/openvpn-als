
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
			
package com.adito.wizard.forms;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.wizard.WizardActionStatus;

public class AbstractWizardFinishForm extends DefaultWizardForm {
    
    final static Log log = LogFactory.getLog(AbstractWizardFinishForm.class);

    // Private statics for sequence attributes
    
    // Private instance variables
    private List actionStatus;
    private int errors, warnings;

    public AbstractWizardFinishForm(String pageName, String resourceBundle, String resourcePrefix) {
        super(false, false, "/WEB-INF/jsp/tiles/wizardFinish.jspf",
                "", true, false,  
                pageName, resourceBundle, resourcePrefix, 0);
    }


    public void setActionStatus(List actionStatus) {
        this.actionStatus = actionStatus; 
        
        errors = 0 ;
        warnings = 0;
        for(Iterator i = actionStatus.iterator(); i.hasNext(); ) {
            WizardActionStatus status = (WizardActionStatus)i.next();
            if(status.getStatus() == WizardActionStatus.COMPLETED_WITH_ERRORS) {
                errors++;
            }
            else if(status.getStatus() == WizardActionStatus.COMPLETED_WITH_WARNINGS) {
                warnings++;
            }
        }   
    }

    public String getFocussedField() {
        return errors > 0|| warnings > 0 ? "rerun" : "finish";
    }
    
    public List getActionStatus() {
        return actionStatus;
    }
    
    public boolean getCompletedWithErrors() {
        return errors > 0;
    }
    
    public boolean getCompletedWithWarnings() {
        return warnings > 0;
    }
    
    public boolean getCompletedOk() {
        return errors == 0 && warnings == 0;
    }
}
