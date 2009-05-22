
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
			
package com.ovpnals.install.forms;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.LabelValueBean;

import com.ovpnals.boot.ContextKey;
import com.ovpnals.boot.PropertyList;
import com.ovpnals.core.InterfacesMultiSelectListDataSource;
import com.ovpnals.input.MultiSelectSelectionModel;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.systemconfig.SystemConfigKey;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.wizard.AbstractWizardSequence;
import com.ovpnals.wizard.forms.DefaultWizardForm;

/**
 * Form used during installation to enter the web server details.
 */
public class WebServerForm extends DefaultWizardForm {

    final static Log log = LogFactory.getLog(WebServerForm.class);

    // Private statics for sequence attributes

    /**
     * Web server port
     */
    public final static String ATTR_WEB_SERVER_PORT = "webServerPort";

    /**
     * Web server protocol
     */
    public final static String ATTR_WEB_SERVER_PROTOCOL = "webServerProtocol";

    /**
     * Listening interfaces
     */
    public final static String ATTR_LISTENING_INTERFACES = "bindAddresses";

    /**
     * Valid external hosts
     */
    public final static String ATTR_VALID_EXTERNAL_HOSTS = "validExternalHosts";

    /**
     * Invalid hostname action
     */
    public final static String ATTR_INVALID_HOSTNAME_ACTION = "invalidHostnameAction";

    // Private instance variables
    private String port;
    private String protocol;
    private String listeningInterfaces;
    private String invalidHostnameAction;
    private final PropertyList validExternalHostnames = new PropertyList();
    private MultiSelectSelectionModel model;

    /**
     */
    public final static List<LabelValueBean> TYPES = new ArrayList<LabelValueBean>();

    static {
        TYPES.add(new LabelValueBean("HTTPS", "https"));
        TYPES.add(new LabelValueBean("HTTP", "http"));
    }

    /**
     * Constructor
     */
    public WebServerForm() {
        super(true, true, "/WEB-INF/jsp/content/install/webServer.jspf", "port", true, false, "webServer", "install",
                        "installation.webServer", 4);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#init(com.ovpnals.wizard.AbstractWizardSequence,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        try {
            port = (String) sequence.getAttribute(ATTR_WEB_SERVER_PORT, Property.getProperty(new ContextKey("webServer.port")));
            protocol = (String) sequence.getAttribute(ATTR_WEB_SERVER_PROTOCOL, Property.getProperty(new ContextKey(
                            "webServer.protocol")));
            listeningInterfaces = (String) sequence.getAttribute(ATTR_LISTENING_INTERFACES, Property.getProperty(new ContextKey(
                            "webServer.bindAddress")));
            String validExternalHostnamesAsTextFieldText = (String) sequence.getAttribute(ATTR_VALID_EXTERNAL_HOSTS, Property
                            .getProperty(new SystemConfigKey("webServer.validExternalHostnames")));
            validExternalHostnames.setAsPropertyText(validExternalHostnamesAsTextFieldText);
            invalidHostnameAction = (String) sequence.getAttribute(ATTR_INVALID_HOSTNAME_ACTION, Property
                            .getProperty(new SystemConfigKey("webServer.invalidHostnameAction")));
            PropertyList pl = PropertyList.createFromTextFieldText(listeningInterfaces);
            SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
            model = new MultiSelectSelectionModel(session, new InterfacesMultiSelectListDataSource(), pl);
        } catch (Exception e) {
            log.error("Failed to initialise form.");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#apply(com.ovpnals.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        sequence.putAttribute(ATTR_WEB_SERVER_PORT, port);
        sequence.putAttribute(ATTR_WEB_SERVER_PROTOCOL, protocol);
        sequence.putAttribute(ATTR_LISTENING_INTERFACES, listeningInterfaces);
        sequence.putAttribute(ATTR_VALID_EXTERNAL_HOSTS, validExternalHostnames.getAsTextFieldText());
        sequence.putAttribute(ATTR_INVALID_HOSTNAME_ACTION, invalidHostnameAction);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (isCommiting()) {
            ActionErrors errors = new ActionErrors();
            validateListeningInterfaces(errors);
            validateExternalHostnames(errors);
            return errors;
        } else {
            return null;
        }
    }

    private void validateListeningInterfaces(ActionErrors errs) {
        try {
            int port = Integer.parseInt(getPort());
            if (!isPortValid(port)) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("installation.webServer.error.invalidPortNumber", getPort()));
                return;
            }

            PropertyList listeningInterfaces = PropertyList.createFromTextFieldText(getListeningInterfaces().equals("") ? "0.0.0.0"
                            : getListeningInterfaces());
            boolean containsRootAddress = false;
            for (String address : listeningInterfaces) {

                if (!isHostAndPortValid(address, port)) {
                    if (port < 1024) {
                        errs.add(Globals.ERROR_KEY, new ActionMessage("installation.webServer.error.portConflictLess1024",
                                        getPort(), address));
                    } else {
                        errs.add(Globals.ERROR_KEY, new ActionMessage("installation.webServer.error.portConflict", getPort(),
                                        address));
                    }
                }

                if (address.equals("0.0.0.0")) {
                    containsRootAddress = true;
                }
            }

            if (containsRootAddress && listeningInterfaces.size() > 1) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("installation.webServer.invalidSelectedInterfaces"));
            }
        } catch (NumberFormatException nfe) {
            errs.add(Globals.ERROR_KEY, new ActionMessage("installation.webServer.error.invalidPortNumber", getPort()));
        }
    }

    private static boolean isPortValid(int port) {
        return port >= 1 && port <= 65535;
    }

    private static boolean isHostAndPortValid(String address, int port) {
        ServerSocket socket = null;
        try {
            if (log.isInfoEnabled())
                log.info("Testing listener on " + address + ":" + port);
            socket = new ServerSocket(port, 0, InetAddress.getByName(address));
            return true;
        } catch (IOException e) {
            log.error("Failed to setup server socket.", e);
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void validateExternalHostnames(ActionErrors errs) {
        PropertyList listeningInterfaces = PropertyList.createFromTextFieldText(getValidExternalHostnames());
        for (String address : listeningInterfaces) {
            if (!isValidIpAddress(address)) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("installation.webServer.error.invalidExeternalHostname", address));
            }
        }
    }

    private static boolean isValidIpAddress(String ipAddress) {
        try {
            InetAddress.getByName(ipAddress);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /**
     * Get list list of listening interfaces as newline separated string
     * 
     * @return listening interfaces as newline separated string
     */
    public String getListeningInterfaces() {
        return listeningInterfaces;
    }

    /**
     * Set the list of listening interfaces as newline separated string
     * 
     * @param listeningInterfaces listening interfaces as newline separated
     *        string
     */
    public void setListeningInterfaces(String listeningInterfaces) {
        this.listeningInterfaces = listeningInterfaces;
    }

    /**
     * Get the port on which the server should run
     * 
     * @return port
     */
    public String getPort() {
        return port;
    }

    /**
     * Set the port on which the server should run
     * 
     * @param port port on which server should run
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Get the newline separated list of valid external hostnames
     * 
     * @return valid external hostnames
     */
    public String getValidExternalHostnames() {
        return validExternalHostnames.getAsTextFieldText();
    }

    /**
     * Set the newline separated list of valid external hostnames
     * 
     * @param validExternalHostnames valid external hostnames
     */
    public void setValidExternalHostnames(String validExternalHostnames) {
        this.validExternalHostnames.setAsTextFieldText(validExternalHostnames);
    }

    /**
     * Get the model of items that may be selected for valid listening
     * interfaces
     * 
     * @return model for valid listening interfaces
     */
    public MultiSelectSelectionModel getModel() {
        return model;
    }

    /**
     * @return String
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return List<LabelValueBean>
     */
    public List<LabelValueBean> getProtocolList() {
        return TYPES;
    }    
    
    /**
     * @return String
     */
    public String getInvalidHostnameAction() {
        return invalidHostnameAction;
    }

    /**
     * @param invalidHostnameAction
     */
    public void setInvalidHostnameAction(String invalidHostnameAction) {
        this.invalidHostnameAction = invalidHostnameAction;
    }
}
