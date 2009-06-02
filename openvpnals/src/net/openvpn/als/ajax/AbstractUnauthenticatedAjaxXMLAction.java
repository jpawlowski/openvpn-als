package net.openvpn.als.ajax;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.boot.Util;
import net.openvpn.als.core.actions.DefaultAction;

/**
 * Abstract unauthenticated action for use by action implementations that returns XML documents 
 * for use by Ajax scripts.
 * <p>
 * Implementations will probably require some attributes to be passed. 
 */

public abstract class AbstractUnauthenticatedAjaxXMLAction extends DefaultAction {
    
    final static Log log = LogFactory.getLog(AbstractUnauthenticatedAjaxXMLAction.class);

    /**
     * Handle the request
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @param builder xml builder
     * @throws Exception on any array
     */
    protected abstract void onAjaxRequest(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response, AjaxXmlBuilder builder) throws Exception;

    /* (non-Javadoc)
     * @see net.openvpn.als.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        AjaxXmlBuilder builder = new AjaxXmlBuilder();      
        try {
            onAjaxRequest(mapping, form, request, response, builder);
            String content = builder.toString();
            
            // Set content to xml
            response.setContentType("text/xml; charset=UTF-8");
            Util.noCache(response);
            PrintWriter pw = response.getWriter();
            pw.write(content);
            pw.close();
        }
        catch(Exception e) {
            log.error("Error processing Ajax request.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing ajax request. " + e.getMessage());
        }
        return null;
    }
}
