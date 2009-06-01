
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
			
package net.openvpn.als.tunnels.wizards.forms;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import net.openvpn.als.boot.Util;
import net.openvpn.als.core.BundleActionMessage;
import net.openvpn.als.core.forms.AbstractResourceDetailsWizardForm;
import net.openvpn.als.input.validators.HostnameOrIPAddressWithReplacementsValidator;
import net.openvpn.als.input.validators.IPV4AddressValidator;
import net.openvpn.als.tunnels.TransportType;
import net.openvpn.als.tunnels.TunnelPlugin;
import net.openvpn.als.wizard.AbstractWizardSequence;

public class TunnelDetailsForm extends AbstractResourceDetailsWizardForm {

    public final static String ATTR_SOURCE_PORT = "sourcePort";
    public final static String ATTR_DESTINATION_HOST = "destinationHost";
    public final static String ATTR_DESTINATION_PORT = "destinationPort";
    public final static String ATTR_TYPE = "tunnelType";
    public final static String ATTR_TRANSPORT = "transport";
    public final static String ATTR_AUTO_START = "autoStart";
    public final static String ATTR_SOURCE_INTERFACE = "sourceInterface";

    private String sourcePort;
    private String destinationHost;
    private String destinationPort;
    private int tunnelType;
    private String transport;
    private boolean autoStart;
    private String sourceInterface;

    final static Log log = LogFactory.getLog(TunnelDetailsForm.class);

    public TunnelDetailsForm() {
        super(true, true, "/WEB-INF/jsp/content/tunnels/tunnelWizard/tunnelDetails.jspf", "resourceName", true, false,
                        "tunnelDetails", "tunnels", "tunnelWizard.tunnelDetails", 2, TunnelPlugin.SSL_TUNNEL_RESOURCE_TYPE);
    }

    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        super.init(sequence, request);
        sourcePort = ((Integer) sequence.getAttribute(ATTR_SOURCE_PORT, new Integer(0))).toString();
        destinationHost = (String) sequence.getAttribute(ATTR_DESTINATION_HOST, "");
        destinationPort = ((Integer) sequence.getAttribute(ATTR_DESTINATION_PORT, new Integer(0))).toString();
        tunnelType = ((Integer) sequence.getAttribute(ATTR_TYPE, new Integer(TransportType.LOCAL_TUNNEL_ID))).intValue();
        transport = (String) sequence.getAttribute(ATTR_TRANSPORT, String.valueOf(TransportType.TCP_TUNNEL));
        autoStart = ((Boolean) sequence.getAttribute(ATTR_AUTO_START, Boolean.FALSE)).booleanValue();
        sourceInterface = ((String) sequence.getAttribute(ATTR_SOURCE_INTERFACE, "127.0.0.1"));
    }

    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(ATTR_SOURCE_PORT, new Integer(sourcePort));
        sequence.putAttribute(ATTR_DESTINATION_HOST, destinationHost);
        sequence.putAttribute(ATTR_DESTINATION_PORT, new Integer(destinationPort));
        sequence.putAttribute(ATTR_TYPE, new Integer(tunnelType));
        sequence.putAttribute(ATTR_TRANSPORT, transport);
        sequence.putAttribute(ATTR_AUTO_START, new Boolean(autoStart));
        sequence.putAttribute(ATTR_SOURCE_INTERFACE, sourceInterface);
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (getResourceName() != null && isCommiting()) {
            ActionErrors errs = super.validate(mapping, request);
            AbstractWizardSequence seq = getWizardSequence(request);


            if (!Util.isNullOrTrimmedBlank(sourceInterface)) {
            	/**
            	 * For remote tunnels, the listening interface must be a valid
            	 * IP address of a network interface on this server
            	 */
            	if(getTunnelType() == TransportType.REMOTE_TUNNEL_ID) {
            		if(!sourceInterface.trim().equals("0.0.0.0") && 
            			!sourceInterface.trim().equals("127.0.0.2"))
                	try {
                		InetAddress addr = InetAddress.getByName(sourceInterface);
                		NetworkInterface nif = NetworkInterface.getByInetAddress(addr);
                		if(nif == null) {
                			throw new Exception();
                		}
                	}
                	catch(Exception e) {            	
            			errs.add(Globals.ERROR_KEY, new ActionMessage("tunnelWizard.tunnelDetails.error.invalidRemoteSourceInterface"));
                	}
            	}
            	else {
            		/**
            		 * For local tunnels, we do not know what will be a valid IP
            		 * address until the client is running so all we can do
            		 * is validate that it looks like an IP address 
            		 */
            		if(!IPV4AddressValidator.isIpAddressExpressionValid(sourceInterface)) {            	
            			errs.add(Globals.ERROR_KEY, new ActionMessage("tunnelWizard.tunnelDetails.error.invalidLocalSourceInterface"));
            		}
            	}
            }
            
            try {
                int port = Integer.valueOf(sourcePort).intValue();
                if(port < 0 || port > 65535) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {                 
                errs.add(Globals.ERROR_KEY, new BundleActionMessage(seq.getCurrentPageForm().getResourceBundle(), seq
                                .getCurrentPageForm().getResourcePrefix()
                                + ".error.sourcePortNotInteger"));
            }

            try {
                int port = Integer.valueOf(destinationPort).intValue();
                if(port < 1 || port > 65535) {
                    throw new IllegalArgumentException();
                }                
            } catch (Exception e) {
                errs.add(Globals.ERROR_KEY, new BundleActionMessage(seq.getCurrentPageForm().getResourceBundle(), seq
                                .getCurrentPageForm().getResourcePrefix()
                                + ".error.destinationPortNotInteger"));
            }

            if (destinationHost == null || destinationHost.equals("")) {
                errs.add(Globals.ERROR_KEY, new BundleActionMessage(seq.getCurrentPageForm().getResourceBundle(), seq
                                .getCurrentPageForm().getResourcePrefix()
                                + ".error.noDestinationHost"));
            }
            else{
                if(!HostnameOrIPAddressWithReplacementsValidator.isValidAsHostOrIp(destinationHost)) {                      
                    errs.add(Globals.ERROR_KEY, new ActionMessage("tunnelWizard.tunnelDetails.error.invalidHost"));                         
                }
            }

            if (transport.equals(TransportType.UDP_TUNNEL) && tunnelType == TransportType.REMOTE_TUNNEL_ID) {
                errs.add(Globals.ERROR_KEY, new BundleActionMessage(seq.getCurrentPageForm().getResourceBundle(), seq
                                .getCurrentPageForm().getResourcePrefix()
                                + ".error.remote.udp"));
            }

            return errs;
        }
        return null;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public void setSourceInterface(String sourceInterface) {
        this.sourceInterface = sourceInterface;
    }

    public String getSourceInterface() {
        return sourceInterface;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public String getDestinationHost() {
        return destinationHost;
    }

    public void setDestinationHost(String destinationHost) {
        this.destinationHost = destinationHost;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    public int getTunnelType() {
        return tunnelType;
    }

    public void setTunnelType(int tunnelType) {
        this.tunnelType = tunnelType;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getTransport() {
        return transport;
    }

    public List getTunnelTypeList() {
        return TransportType.TYPES;
    }

    public List getTransportList() {
        return TransportType.TRANSPORTS;
    }
}