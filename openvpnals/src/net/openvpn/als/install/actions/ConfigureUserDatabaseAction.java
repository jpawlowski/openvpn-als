package net.openvpn.als.install.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import net.openvpn.als.boot.AbstractPropertyKey;
import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.properties.forms.PropertiesForm;
import net.openvpn.als.properties.impl.realms.RealmKey;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.wizard.actions.AbstractWizardPropertiesAction;
import net.openvpn.als.wizard.forms.AbstractWizardPropertiesForm;


/**
 * Implementatation of a {@link AbstractInstallWizardAction}
 * that allows the selected user database to be configured.
 *  
 * @see net.openvpn.als.install.forms.ConfigureUserDatabaseForm
 */
public class ConfigureUserDatabaseAction extends AbstractWizardPropertiesAction {
	
	final static Log log = LogFactory.getLog(ConfigureUserDatabaseAction.class);
    
    /**
     * 
     */
    protected void configureProperties() {

    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.SETUP_CONSOLE_CONTEXT;
    }    

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#finish(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward finish(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            return super.finish(mapping, form, request, response);
        }
        catch(Exception e) {
        	log.error("Failed to configure user database.", e);
            ActionMessages errs = new ActionMessages();
            errs.add(Globals.ERROR_KEY, new ActionMessage("installation.configureUserDatabase.error.failedToOpenUserDatabase", e.getMessage()));
            saveErrors(request, errs);
            return mapping.getInputForward();
        }
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#next(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward next(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            return super.next(mapping, form, request, response);
        }
        catch(Exception e) {
        	log.error("Failed to open user database.", e);
            ActionMessages errs = new ActionMessages();
            errs.add(Globals.ERROR_KEY, new ActionMessage("installation.configureUserDatabase.error.failedToOpenUserDatabase", e.getMessage()));
            saveErrors(request, errs);
            return mapping.getInputForward();
        }
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#previous(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            return super.previous(mapping, form, request, response);
        }
        catch(Exception e) {
            ActionMessages errs = new ActionMessages();
            errs.add(Globals.ERROR_KEY, new ActionMessage("installation.configureUserDatabase.error.failedToOpenUserDatabase", e.getMessage()));
            saveErrors(request, errs);
            return mapping.getInputForward();
        }
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.actions.AbstractWizardPropertiesAction#createKey(net.openvpn.als.boot.PropertyDefinition, net.openvpn.als.properties.forms.PropertiesForm)
     */
    public AbstractPropertyKey createKey(PropertyDefinition definition, PropertiesForm propertiesForm) throws Exception {
        return new RealmKey(definition.getName(), UserDatabaseManager.getInstance().getDefaultRealm());
    }

	/* (non-Javadoc)
	 * @see net.openvpn.als.wizard.actions.AbstractWizardPropertiesAction#unspecified(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		((AbstractWizardPropertiesForm)form).setSelectedCategory(-1);
        ((AbstractWizardPropertiesForm)form).setSelectedTab("");
		return super.unspecified(mapping, form, request, response);
	}

}
