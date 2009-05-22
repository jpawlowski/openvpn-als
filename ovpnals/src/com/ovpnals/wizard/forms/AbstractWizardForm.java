
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
			
package com.ovpnals.wizard.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.ovpnals.core.forms.CoreForm;
import com.ovpnals.security.Constants;
import com.ovpnals.wizard.AbstractWizardSequence;

/**
 * Abstract superclass class for all <i>Wizard Pages</i>. Every page within
 * the wizard framework must provide a concrete implementation of this class.
 */
public abstract class AbstractWizardForm extends CoreForm {
    
    private int gotoStep;
    
    public AbstractWizardForm() {
        super();
    }
    
    /**
     * Initialise the wizard form whenever it is visited
     * 
     * @param sequence sequence
     * @param request TODO
     * @throws Exception on any error
     */
    public abstract void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception;

    /* (non-Javadoc)
     * @see com.ovpnals.core.forms.CoreForm#isCommiting()
     */
    public boolean isCommiting() {
        return super.isCommiting() || "next".equals(getActionTarget()) || "gotoStep".equals(getActionTarget()) ||
            "finish".equals(getActionTarget());
    }

    /**
     * Apply the current state to the sequence object whenever the user
     * changes pages.
     * 
     * @param sequence sequence
     * @throws Exception on any error
     */
    public abstract void apply(AbstractWizardSequence sequence) throws Exception;
    
    /* (non-Javadoc)
     * @see com.ovpnals.core.forms.CoreForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        setActionTarget("unspecified");
    }

    /**
     * Return if the user may move onto the next page
     * 
     * @return next available
     */
    public abstract boolean getNextAvailable();

    /**
     * Return if the user may move back to the previous page
     * 
     * @return previous available
     */
    public abstract boolean getPreviousAvailable();

    /**
     * Return if the user may move back to the finish page
     * 
     * @return previous available
     */
    public abstract boolean getFinishAvailable();
    
    /**
     * Return the path of the JSP fragment that provides the
     * body of the page. This should be a fully qualified path starting at
     * <strong>/WEB-INF</strong>. For example 
     * <strong>/WEB-INF/jsp/content/install/selectCertificateSource.jspf</strong>
     * 
     * @return path of JSP fragement providing wizard page body
     */
    public abstract String getPage();
    
    /**
     * Return the resource prefix used for localisation of 
     * common elements of a wizard page. Currently this only includes
     * the <i>description</i>. For example, <i>installation.selectCertificateSource</i>.
     * 
     * @return resource prefix
     */
    public abstract String getResourcePrefix();

    /**
     * Return the name of the resource bundle  used for localisation of 
     * common elements of a wizard page. Currently this only includes
     * the <i>description</i>. For example, <i>install</i>.
     *  
     * @return resource bundle
     */
    public abstract String getResourceBundle();

    /**
     * Get the step index for this action. 
     *  
     * @return resource bundle
     */
    public abstract int getStepIndex();
    
    /**
     * Return the page name of this wizard page. This is used for building
     * the struts action page amongst other things. For example, <i>selectCertificateSource</i>
     * 
     * @return page name.
     */
    public abstract String getPageName();
    
    /**
     * Return the initially focused field name
     * 
     * @return initially focused field name
     */
    public abstract String getFocussedField();
    
    /**
     * Get if this form should support autocomplete
     * 
     * @return autocomplete
     */
    public abstract boolean getAutocomplete();
    
    /**
     * Return the value the should be placed in the onclick attribute
     * of the <b>Next</b> button. Subclasses should overide this
     * method if they wish to target a different action.
     * 
     *  @return javascript
     */
    public String getNextOnClick() {
        return "setActionTarget('next'); document.forms[0].submit(); return false";
    }
    
    /**
     * Return the value the should be placed in the onclick attribute
     * of the <b>Previous</b> button. Subclasses should overide this
     * method if they wish to target a different action.
     * 
     *  @return javascript
     */
    public String getPreviousOnClick() {
        return "setActionTarget('previous'); document.forms[0].submit(); return false";
    }
    
    /**
     * Return the value the should be placed in the onclick attribute
     * of the <b>Finish</b> button. Subclasses should overide this
     * method if they wish to target a different action.
     * 
     *  @return javascript
     */
    public String getFinishOnClick() {
        return "setActionTarget('finish'); document.forms[0].submit()";
    }
    
    /**
     * Return the encoding to use for the &lt;form/&gt; tag for this
     * form.
     * 
     * @return encoding
     */
    public String getFormEncoding() {
        return "application/x-www-form-urlencoded";
    }
    
    /**
     * Return the action to use for the &lt;form/&gt; tag for this
     * form.
     * 
     * @return action
     */
    public String getFormAction() {
        return "/" + getPageName() + ".do";
    }
    
    /**
     * Convenience method to get the current {@link com.ovpnals.wizard.AbstractWizardSequence}
     * for the session
     * 
     * @param request request from which to get session
     * @return current wizard sequence object or null if none
     */
    public AbstractWizardSequence getWizardSequence(HttpServletRequest request) {
        return (AbstractWizardSequence)request.getSession().getAttribute(Constants.WIZARD_SEQUENCE);
    }

    public int getGotoStep() {
        return gotoStep;
    }

    public void setGotoStep(int gotoStep) {
        this.gotoStep = gotoStep;
    }
}
