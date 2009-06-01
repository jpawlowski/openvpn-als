
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
			
package net.openvpn.als.tunnels.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.util.LabelValueBean;

import net.openvpn.als.boot.Util;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.navigation.AbstractFavoriteItem;
import net.openvpn.als.policyframework.LaunchSession;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.tunnels.TransportType;
import net.openvpn.als.tunnels.Tunnel;

/**
 * Implementation of an {@link net.openvpn.als.navigation.AbstractFavoriteItem}
 * that wraps {@link net.openvpn.als.tunnels.Tunnel} resources for display.
 */
public class TunnelItem extends AbstractFavoriteItem {

    private LaunchSession launchSession;

    /**
     * Constructor
     * 
     * @param tunnel tunnel
     * @param policies policies item is attached to
     * @param launchSession the launch session if open or <code>null</code> if
     *        not
     */
    public TunnelItem(Tunnel tunnel, List policies, LaunchSession launchSession) {
        super(tunnel, policies);
        this.launchSession = launchSession;
    }

    /**
     * Get the transport type for this resource.
     * 
     * @return transport type
     * @see Tunnel#getTransport()
     */
    public String getTransport() {
        return ((Tunnel) this.getResource()).getTransport();
    }

    /**
     * Get the source port for this tunnel
     * 
     * @return source port
     * @see Tunnel#getSourcePort()
     */
    public String getSourcePort() {
        return String.valueOf(((Tunnel) this.getResource()).getSourcePort());
    }

    /**
     * Get the destination host for this tunnel
     * 
     * @return destination host
     * @see Tunnel#getDestination()
     */
    public String getDestinationHost() {
        return ((Tunnel) this.getResource()).getDestination().getHost();
    }

    /**
     * Get the type of tunnel.
     * 
     * @return type of tunnel
     * @see Tunnel#getType()
     */
    public String getTunnelType() {
        return ((LabelValueBean) TransportType.TYPES.get(((Tunnel) this.getResource()).getType())).getLabel();
    }

    /**
     * Get the destination port for this tunnel
     * 
     * @return destination port
     * @see Tunnel#getDestination()
     */
    public String getDestinationPort() {
        return String.valueOf(((Tunnel) this.getResource()).getDestination().getPort());
    }

    /**
     * Get the source interface
     * 
     * @return source interface
     */
    public String getSourceInterface() {
        return ((Tunnel) this.getResource()).getSourceInterface();
    }

    /**
     * Get if this tunnel should autostart.
     * 
     * @return autostart
     * @see Tunnel#isAutoStart()
     */
    public String getAutoStart() {
        return ((Tunnel) this.getResource()).isAutoStart() ? "Yes" : "No";
    }

    /**
     * Get if this tunnel is currently open and active.
     * 
     * @return tunnel open
     */
    public String getOpen() {
        return launchSession != null ? "true" : "false";
    }

    /**
     * Get the tunnel object this item wraps. Provided more for convenience as
     * {@link net.openvpn.als.policyframework.ResourceItem#getResource} would
     * normally be used.
     * 
     * @return tunnel
     */
    public Tunnel getTunnel() {
        return ((Tunnel) this.getResource());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.FavoriteItem#getOnClick()
     */
    public String getOnClick(int policy, HttpServletRequest request) {
        return "return true";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.FavoriteItem#getLink()
     */
    public String getLink(int policy, String referer, HttpServletRequest request) {
        if (new Boolean(getOpen()).booleanValue()) {
            return getCloseLink(referer, request);
        } else {
            return getOpenLink(policy, referer, request);
        }
    }

    /**
     * Get the link used to close the tunnel.
     * 
     * @param referer referer
     * @return close link
     */
    public String getCloseLink(String referer, HttpServletRequest request) {
        String encodedReferer = Util.urlEncode(referer == null ? "" : referer);
        SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(request);
        if(null != info) {
            if(info.getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT) {
                return "/showTunnels.do?actionTarget=stop&" + LaunchSession.LAUNCH_ID + "=" + launchSession.getId()
                    + (encodedReferer.equals("") ? "" : "&returnTo=" + encodedReferer);
            } else if (info.getNavigationContext() == SessionInfo.USER_CONSOLE_CONTEXT) {
                return "/showUserTunnels.do?actionTarget=stop&" + LaunchSession.LAUNCH_ID + "=" + launchSession.getId()
                + (encodedReferer.equals("") ? "" : "&returnTo=" + encodedReferer);  
            }
        }
        return "";
    }

    /**
     * Get the link to use to open this tunnel
     * 
     * @param policy policy
     * @param referer referer
     * @param request request
     * @return open link
     */
    public String getOpenLink(int policy, String referer, HttpServletRequest request) {
        return "launchTunnel.do?policy=" + policy + "&resourceId=" + getResource().getResourceId() + "&returnTo="
                        + Util.urlEncode(Util.isNullOrTrimmedBlank(referer) ? CoreUtil.getRealRequestURI(request) : referer);

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.FavoriteItem#getName()
     */
    public String getFavoriteName() {
        return getSourcePort() + ":" + getDestinationHost() + ":" + getDestinationPort();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.FavoriteItem#getTarget()
     */
    public String getTarget() {
        return "_self";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.AbstractFavoriteItem#getFavoriteSubType()
     */
    public String getFavoriteSubType() {
        return getTunnel().getType() == TransportType.LOCAL_TUNNEL_ID ? "Local" : "Remote";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.AbstractFavoriteItem#getSmallIconPath(javax.servlet.http.HttpServletRequest)
     */
    public String getSmallIconPath(HttpServletRequest request) {
        return CoreUtil.getThemePath(request.getSession()) + "/images/actions/runTunnel.gif";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.AbstractFavoriteItem#getLargeIconPath(javax.servlet.http.HttpServletRequest)
     */
    public String getLargeIconPath(HttpServletRequest request) {
        return CoreUtil.getThemePath(request.getSession()) + "/images/actions/runTunnelLarge.gif";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.policyframework.ResourceItem#getLargeIconAdditionalIcon(javax.servlet.http.HttpServletRequest)
     */
    public String getLargeIconAdditionalIcon(HttpServletRequest request) {
        return CoreUtil.getThemePath(request.getSession())
                + "/images/actions/" + (launchSession != null ? "switchOn.gif" : "switchOff.gif")/* : ""*/;
    }
}
