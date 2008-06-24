package com.adito.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.Util;
import com.adito.security.SessionInfo;


/**
 * Implementation of {@link com.adito.ajax.AbstractAjaxXMLAction}
 *  that returns an XML document containing all currently configured 
 *  <i>Validation Strings</i>.
 * <p>
 * A single request parameter is supported, <i>validationString</i> that will narrow
 * the results returned to those that begin with the supplied value.  
 * 
 * @revision $Revision: 1.2.8.2 $
 */
public class ListValidationStringsAction extends AbstractAjaxXMLAction {
    
    /**
     * Default validation strings available for use. 
     * 
     * TODO we need some kind of registry for this so plugins can add new validation strings
     */
    public static String[] VALIDATION_STRINGS = new String[] { 
        "com.adito.input.validators.IntegerValidator", 
        "com.adito.input.validators.IntegerValidator(minValue=0,maxValue=50)",  
        "com.adito.input.validators.PortValidator",
        "com.adito.input.validators.StringValidator(minLength=0,maxLength=30,trim=true,regExp=,pattern=)", 
        "com.adito.input.validators.NonBlankStringValidator"} ;

    /* (non-Javadoc)
     * @see com.adito.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void onAjaxRequest(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response, AjaxXmlBuilder builder) throws Exception {
        String validationString = Util.urlDecode(Util.trimmedOrBlank(request.getParameter("validationString")));
        for(int i = 0 ; i < VALIDATION_STRINGS.length; i++) {
            if(validationString.equals("") || VALIDATION_STRINGS[i].startsWith(validationString)) {
                builder.addItem(VALIDATION_STRINGS[i], VALIDATION_STRINGS[i]);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

}
