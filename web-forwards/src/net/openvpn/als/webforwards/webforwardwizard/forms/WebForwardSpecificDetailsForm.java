
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.webforwards.webforwardwizard.forms;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import net.openvpn.als.boot.PropertyList;
import net.openvpn.als.core.BundleActionMessage;
import net.openvpn.als.core.forms.AbstractResourceDetailsWizardForm;
import net.openvpn.als.vfs.webdav.DAVUtilities;
import net.openvpn.als.webforwards.WebForward;
import net.openvpn.als.webforwards.WebForwardPlugin;
import net.openvpn.als.webforwards.WebForwardTypes;
import net.openvpn.als.wizard.AbstractWizardSequence;

/**
 * The form for all other attributes associated with the TunneledSite resource.
 */
public class WebForwardSpecificDetailsForm extends AbstractResourceDetailsWizardForm {

    final static Log log = LogFactory.getLog(WebForwardSpecificDetailsForm.class);

    private int type = -1;
    // for all web forwards
    public final static String ATTR_DESTINATION_URL = "destinationURL";
    public final static String ATTR_CATEGORY = "category";

    private String destinationURL;
    private String category;

    // Replacement proxy attributes
    public final static String ATTR_RESTRICT_TO_HOSTS = "restrictToHosts";
    public final static String ATTR_ENCODEING = "encoding";

    private String encoding;
    private PropertyList restrictToHosts;

    // reverse proxy attribute
    public final static String ATTR_PATHS = "paths";
    public final static String ATTR_ACTIVE_DNS = "activeDNS";
    public final static String ATTR_CUSTOM_HEADERS = "customHeaders";

    public static final String ATTR_HOST_HEADER = "hostHeader";

    private String hostHeader;
    private String paths;
    private boolean activeDNS;
    private Map customHeaders;

    /**
     * Construtor
     */
    public WebForwardSpecificDetailsForm() {
        super(true, true, "/WEB-INF/jsp/content/webforward/webforwardwizard/webForwardSpecificDetails.jspf", "destinationURL", true, false,
                        "webForwardSpecificDetails", "webForwards", "webForwardWizard.webForwardSpecificDetails", 3, WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#init(net.openvpn.als.wizard.AbstractWizardSequence,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        super.init(sequence, request);
        this.restrictToHosts = (PropertyList)sequence.getAttribute(ATTR_RESTRICT_TO_HOSTS, new PropertyList());
        this.destinationURL = (String) sequence.getAttribute(ATTR_DESTINATION_URL, "");
        this.category = (String) sequence.getAttribute(ATTR_CATEGORY, "General");
        type = ((Integer) sequence.getAttribute(WebForwardTypeSelectionForm.ATTR_TYPE, new Integer(0))).intValue();
        this.encoding = (String) sequence.getAttribute(ATTR_ENCODEING, WebForwardTypes.DEFAULT_ENCODING);

        this.paths = (String) sequence.getAttribute(ATTR_PATHS, "");
        this.hostHeader = (String) sequence.getAttribute(ATTR_HOST_HEADER, "");
        this.activeDNS = ((Boolean) sequence.getAttribute(ATTR_ACTIVE_DNS, Boolean.FALSE)).booleanValue();
        this.customHeaders = (Map) sequence.getAttribute(ATTR_CUSTOM_HEADERS, new HashMap());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#apply(net.openvpn.als.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(ATTR_DESTINATION_URL, this.destinationURL);
        sequence.putAttribute(ATTR_CATEGORY, this.category);
        sequence.putAttribute(ATTR_ENCODEING, this.encoding);
        sequence.putAttribute(ATTR_PATHS, this.paths);
        sequence.putAttribute(ATTR_ACTIVE_DNS, new Boolean(this.activeDNS));
        sequence.putAttribute(ATTR_HOST_HEADER, activeDNS ? "" : this.hostHeader);
        sequence.putAttribute(ATTR_CUSTOM_HEADERS, this.customHeaders);
        sequence.putAttribute(ATTR_RESTRICT_TO_HOSTS, restrictToHosts);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (getResourceName() != null && isCommiting()) {
            ActionErrors errs = super.validate(mapping, request);
            AbstractWizardSequence seq = getWizardSequence(request);
            
            try {
                if (this.getDestinationURL().indexOf("${") == -1){
                    // only chek the format if there is no hash on the front.
                    new URL(this.getDestinationURL());
                }
            } catch (MalformedURLException e) {
                errs.add(Globals.ERROR_KEY, new BundleActionMessage(seq.getCurrentPageForm().getResourceBundle(), seq
                                .getCurrentPageForm().getResourcePrefix()
                                + ".error.malformedURLException"));
            }
            

            if(getCategory().trim().equals("")) {
                errs.add(Globals.ERROR_KEY, new BundleActionMessage(seq.getCurrentPageForm().getResourceBundle(), seq
                                .getCurrentPageForm().getResourcePrefix()
                                + ".error.noCategory"));                
            }
            
            if (type == WebForward.TYPE_PATH_BASED_REVERSE_PROXY){
                if (this.paths != null && this.paths.length() == 0){
                    errs.add(Globals.ERROR_KEY, new ActionMessage("webForwardWizard.webForwardSpecificDetails.error.needs.path"));
                }
            	String validatedPaths = "";
            	
            	if (this.paths != null && this.paths.length() > 0){
                    StringTokenizer t = new StringTokenizer(this.paths, "\n\r");
                    while (t.hasMoreTokens()) {
                        String path = t.nextToken();
                        path = path.trim();
                        if(!path.startsWith("/"))
                        	path = "/" + path;
                        if(path.endsWith("/"))
                        	path = DAVUtilities.stripTrailingSlash(path);

                        validatedPaths += path + "\n";
                    }
                    if(errs.size() == 0)
                    	this.paths = validatedPaths;
            	}
            } else if (type == WebForward.TYPE_HOST_BASED_REVERSE_PROXY){
                if (this.activeDNS && !this.hostHeader.equals("")){
                    errs.add(Globals.ERROR_KEY, new ActionMessage("webForwardWizard.webForwardSpecificDetails.error.hostBased.bothSelected"));
                }
                if (!this.activeDNS && this.hostHeader.equals("")){
                    errs.add(Globals.ERROR_KEY, new ActionMessage("webForwardWizard.webForwardSpecificDetails.error.hostBased.nonSelected"));
                }
            }
            
            return errs;
        }
        return null;
    }

    /**
     * @return The Web Forward Category.
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category The Web Forward Category.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return The destination URL.
     */
    public String getDestinationURL() {
        return destinationURL;
    }

    /**
     * @param destinationURL The destination URL.
     */
    public void setDestinationURL(String destinationURL) {
        this.destinationURL = destinationURL;
    }

    public boolean isPathBased() {
        return WebForward.TYPE_PATH_BASED_REVERSE_PROXY == type;
    }
    
    public boolean isHostBased() {
        return WebForward.TYPE_HOST_BASED_REVERSE_PROXY == type;
    }
    
    public PropertyList getRestrictToHostsList() {
        return restrictToHosts;
    }

    public String getRestrictToHosts() {
        return restrictToHosts.getAsTextFieldText();
    }

    public void setRestrictToHosts(String restrictToHosts) {
        this.restrictToHosts.setAsTextFieldText(restrictToHosts);
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        this.activeDNS = false;
    }

    public boolean isActiveDNS() {
        return activeDNS;
    }

    public void setActiveDNS(boolean activeDNS) {
        this.activeDNS = activeDNS;
    }

    public Map getCustomHeaders() {
        return customHeaders;
    }

    public void setCustomHeaders(Map customHeaders) {
        this.customHeaders = customHeaders;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getHostHeader() {
        return hostHeader;
    }

    public void setHostHeader(String hostHeader) {
        this.hostHeader = hostHeader;
    }

    public List getEncodeingTypeList() {
        return WebForwardTypes.ENCODING_TYPES;
    }
}