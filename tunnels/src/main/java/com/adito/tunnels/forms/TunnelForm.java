
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
			
package com.adito.tunnels.forms;

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
import org.apache.struts.util.LabelValueBean;

import com.adito.boot.HostService;
import com.adito.boot.PropertyList;
import com.adito.boot.Util;
import com.adito.input.MultiSelectSelectionModel;
import com.adito.input.validators.HostnameOrIPAddressWithReplacementsValidator;
import com.adito.input.validators.IPV4AddressValidator;
import com.adito.policyframework.Resource;
import com.adito.policyframework.forms.AbstractFavoriteResourceForm;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.tunnels.TransportType;
import com.adito.tunnels.Tunnel;

/**
 * Implementation of {@link AbstractFavoriteResourceForm} suitable for editing
 * an <i>SSL Tunnel</i>.
 */
public class TunnelForm extends AbstractFavoriteResourceForm {
    static Log log = LogFactory.getLog(TunnelForm.class);

    // Private instance variables

    private String selectedTab = "details";

    private String sourceInterface;
    private String sourcePort;
    private String destinationHost;
    private String destinationPort;
    private int tunnelType;
    private String transport;
    private boolean autoStart;

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.forms.AbstractResourceForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errs = super.validate(mapping, request);
        if (isCommiting()) {

            if (!Util.isNullOrTrimmedBlank(sourceInterface)) {
                /**
                 * For remote tunnels, the listening interface must be a valid
                 * IP address of a network interface on this server
                 */
                if (getTunnelType() == TransportType.REMOTE_TUNNEL_ID) {
                    if (!sourceInterface.trim().equals("0.0.0.0") && !sourceInterface.trim().equals("127.0.0.2"))
                        try {
                            InetAddress addr = InetAddress.getByName(sourceInterface);
                            NetworkInterface nif = NetworkInterface.getByInetAddress(addr);
                            if (nif == null) {
                                throw new Exception();
                            }
                        } catch (Exception e) {
                            errs.add(Globals.ERROR_KEY, new ActionMessage(
                                            "tunnelWizard.tunnelDetails.error.invalidRemoteSourceInterface"));
                        }
                } else {
                    /**
                     * For local tunnels, we do not know what will be a valid IP
                     * address until the client is running so all we can do is
                     * validate that it looks like an IP address
                     */
                    if (!IPV4AddressValidator.isIpAddressExpressionValid(sourceInterface)) {
                        errs.add(Globals.ERROR_KEY, new ActionMessage(
                                        "tunnelWizard.tunnelDetails.error.invalidLocalSourceInterface"));
                    }
                }
            }

            try {
                int port = Integer.valueOf(sourcePort).intValue();
                if (port < 0 || port > 65535) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("tunnelWizard.tunnelDetails.error.sourcePortNotInteger"));
            }

            try {
                int port = Integer.valueOf(destinationPort).intValue();
                if (port < 1 || port > 65535) {
                    throw new IllegalArgumentException();
                }
                Integer.valueOf(destinationPort).intValue();
            } catch (Exception e) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("tunnelWizard.tunnelDetails.error.destinationPortNotInteger"));
            }

            if (Util.isNullOrTrimmedBlank(destinationHost)) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("tunnelWizard.tunnelDetails.error.noDestinationHost"));
            } else {
                if (!HostnameOrIPAddressWithReplacementsValidator.isValidAsHostOrIp(destinationHost)) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage("tunnelWizard.tunnelDetails.error.invalidHost"));
                }
            }

            if (getResourceName().equalsIgnoreCase("default")
                            && (!getEditing() || (getEditing() && !getResource().getResourceName().equalsIgnoreCase("default")))) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("error.createNetworkPlace.cantUseNameDefault"));
                setResourceName("");
            }
        }
        return errs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabCount()
     */
    public int getTabCount() {
        return getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT ? 3 : 2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabName(int)
     */
    public String getTabName(int idx) {
        switch (idx) {
            case 0:
                return "details";
            case 1:
                return "other";
            default:
                return "policies";
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabTitle(int)
     */
    public String getTabTitle(int idx) {

        // Get from resources
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.forms.AbstractFavoriteResourceForm#initialise(javax.servlet.http.HttpServletRequest,
     *      com.adito.policyframework.Resource, boolean,
     *      com.adito.input.MultiSelectSelectionModel,
     *      com.adito.boot.PropertyList, com.adito.security.User,
     *      boolean)
     */
    public void initialise(HttpServletRequest request, Resource resource, boolean editing, MultiSelectSelectionModel policyModel,
                           PropertyList selectedPolicies, User owner, boolean assignOnly) throws Exception {
        super.initialise(request, resource, editing, policyModel, selectedPolicies, owner, assignOnly);
        Tunnel tunnel = (Tunnel) resource;
        sourcePort = String.valueOf(tunnel.getSourcePort());
        destinationHost = tunnel.getDestination().getHost();
        destinationPort = String.valueOf(tunnel.getDestination().getPort());
        tunnelType = tunnel.getType();
        transport = tunnel.getTransport();
        autoStart = tunnel.isAutoStart();
        sourceInterface = tunnel.getSourceInterface();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getSelectedTab()
     */
    public String getSelectedTab() {
        return selectedTab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#setSelectedTab(java.lang.String)
     */
    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.forms.AbstractResourceForm#applyToResource()
     */
    public void applyToResource() throws Exception {
        Tunnel tunnel = (Tunnel) getResource();
        tunnel.setType(getTunnelType());
        tunnel.setAutoStart(isAutoStart());
        tunnel.setTransport(getTransport());
        tunnel.setSourcePort(Integer.parseInt(getSourcePort()));
        tunnel.setDestination(new HostService(getDestinationHost(), Integer.parseInt(getDestinationPort())));
        tunnel.setSourceInterface(getSourceInterface());
    }

    /**
     * Get the source port
     * 
     * @return source port
     */
    public String getSourcePort() {
        return sourcePort;
    }

    /**
     * Set the source port
     * 
     * @param sourcePort source port
     */
    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    /**
     * Get the source interface
     * 
     * @return source interface
     */
    public String getSourceInterface() {
        return sourceInterface;
    }

    /**
     * Set the source interface
     * 
     * @param sourceInterface source interface
     */
    public void setSourceInterface(String sourceInterface) {
        this.sourceInterface = sourceInterface;
    }

    /**
     * Get whether the tunnel should auto-start
     * 
     * @return auto start
     */
    public boolean isAutoStart() {
        return autoStart;
    }

    /**
     * Set whether the tunnel shhould auto-start
     * 
     * @param autoStart auto start
     */
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    /**
     * Get the destination hostname or IP address
     * 
     * @return destination hostname or IP address
     */
    public String getDestinationHost() {
        return destinationHost;
    }

    /**
     * Set the destination hostname or IP address
     * 
     * @param destinationHost destination hostname or IP address
     */
    public void setDestinationHost(String destinationHost) {
        this.destinationHost = destinationHost;
    }

    /**
     * Get the destination port
     * 
     * @return destination port
     */
    public String getDestinationPort() {
        return destinationPort;
    }

    /**
     * Set the destination port.
     * 
     * @param destinationPort destination port
     */
    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    /**
     * Get the tunnel type. This may be one of
     * {@link TransportType#REMOTE_TUNNEL_ID} or
     * {@link TransportType#LOCAL_TUNNEL_ID}.
     * 
     * @return tunnel type
     */
    public int getTunnelType() {
        return tunnelType;
    }

    /**
     * Set the tunnel type. This may be one of
     * {@link TransportType#REMOTE_TUNNEL_ID} or
     * {@link TransportType#LOCAL_TUNNEL_ID}.
     * 
     * @param tunnelType tunnel type
     */
    public void setTunnelType(int tunnelType) {
        this.tunnelType = tunnelType;
    }

    /**
     * Set the tunnel trasport. This may be one of
     * {@link TransportType#TCP_TUNNEL} or {@link TransportType#UDP_TUNNEL}.
     * 
     * @param transport transport type
     */
    public void setTransport(String transport) {
        this.transport = transport;
    }

    /**
     * Get the tunnel trasport. This may be one of
     * {@link TransportType#TCP_TUNNEL} or {@link TransportType#UDP_TUNNEL}.
     * 
     * @return transport type
     */
    public String getTransport() {
        return transport;
    }

    /**
     * Get a list of available tunnel types as {@link LabelValueBean} objects.
     * 
     * @return tunnel types
     */
    public List getTunnelTypeList() {
        return TransportType.TYPES;
    }

    /**
     * Get a list of available tunnel transports as {@link LabelValueBean}
     * objects.
     * 
     * @return tunnel types
     */
    public List getTransportList() {
        return TransportType.TRANSPORTS;
    }

    /**
     * Get the tunnel type as a string
     * 
     * @return tunnel type string
     */
    public String getTunnelTypeString() {
        return ((LabelValueBean) getTunnelTypeList().get(getTunnelType())).getLabel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.forms.AbstractFavoriteResourceForm#reset(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        this.sourceInterface = "127.0.0.1";
        this.autoStart = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabBundle(int)
     */
    public String getTabBundle(int idx) {
        return null;
    }
}