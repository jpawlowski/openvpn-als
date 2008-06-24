
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
			
package com.adito.core.forms;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.SystemProperties;
import com.adito.security.Constants;

/**
 * Extension to the standard struts {@link org.apache.struts.action.ActionForm}
 * that adds some additional attributes used by pretty much all of
 * Adito's forms.
 * <p>
 * One such attribute is the <i>Referer</i> attribute. This should be set when
 * entering a form for the first in some kind of flow (edit screen for example).
 * When the form is finished with (e.g. when the user commits the entered
 * details or cancels the form) the browser is forwarded to the stored referer.
 * This allows us to return to the page the flow was called from.
 * <p>
 * Another command attribute is the <i>Action Target</i>. This is used by all
 * <i>Dispatch Actions</i> to store action withing the dispatch action to
 * invoke on the next execution.
 * <p>
 * <i>Editing</i> is another common attribute to be used to denote the form is
 * editing an entity as opposed to creating it.
 * <p>
 */
public class CoreForm extends ActionForm {

    // Private instance variables

    protected String referer;
    private String actionTarget;
    protected boolean editing;

    /**
     * Constructor
     */
    public CoreForm() {
        super();
    }

    /**
     * Get if the resource is being edited
     * 
     * @return resource being edited
     */
    public boolean getEditing() {
        return editing;
    }

    /**
     * Set the form as creating an entity. This method has no signature to
     * prevent the value from being able to be changed by posting a parameter
     */
    public void setCreating() {
        this.editing = false;
    }

    /**
     * Set the form as creating an entity. This method has no signature to
     * prevent the value from being able to be changed by posting a parameter
     */
    public void setEditing() {
        this.editing = true;
        this.actionTarget = "edit";
    }

    /**
     * Helps with a work around for character encoding on IE. All JSP for forms
     * should include the hidden <i>_charset_</i> attribute using the struts
     * <stronghtml:hidden</strong> tag. This will be expanded to 'utf-8'
     * forcing all form submissions to be in UTF-8 encoding.
     * 
     * @return character set for form submission encoding
     */
    public String get_charset_() {
        return SystemProperties.get("adito.encoding", "UTF-8");
    }

    /**
     * Set action target, used when form is handled by a <i>Dispatch Action</i>
     * to store the dispatch method to invoke the next time the action is
     * executed.
     * 
     * @param actionTarget action target for dispatch action
     */
    public void setActionTarget(String actionTarget) {
        this.actionTarget = actionTarget;
    }

    /**
     * Get action target, used when form is handled by a <i>Dispatch Action</i>
     * to store the dispatch method to invoke the next time the action is
     * executed.
     * 
     * @return action target for dispatch action
     */
    public String getActionTarget() {
        return actionTarget;
    }

    /**
     * Set the referer. This should be set when entering a form for the first in
     * some kind of flow (edit screen for example). When the form is finished
     * with (e.g. when the user commits the entered details or cancels the form)
     * the browser is forwarded to the stored referer. This allows us to return
     * to the page the flow was called from.
     * 
     * @param referer referer
     */
    public void setReferer(String referer) {
        this.referer = referer;
    }


    /**
     * Get the referer. This should be set when entering a form for the first in
     * some kind of flow (edit screen for example). When the form is finished
     * with (e.g. when the user commits the entered details or cancels the form)
     * the browser is forwarded to the stored referer. This allows us to return
     * to the page the flow was called from.
     * 
     * @return referer
     */
    public String getReferer() {
        return referer;
    }

    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        referer = null;
        actionTarget = null;
        request.setAttribute(Constants.REQ_ATTR_ACTION_MAPPING, mapping);
        request.setAttribute(Constants.REQ_ATTR_FORM, this);
    }

    /**
     * Get if the user is canceling this form. This may be used in the
     * validate() method to prevent validation upon cancel.
     * 
     * @return cancelling
     */
    public boolean isCancelling() {
        return getActionTarget().equals("cancel");
    }
    
    /**
     * @param value
     * @return true if the value is either null or zero length when trimmed
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * Get if the user is commiting this form. This may be used in the
     * validate() method to prevent validation upon cancel.
     * 
     * @return commit
     */
    public boolean isCommiting() {
        return "commit".equals(getActionTarget());
    }
}