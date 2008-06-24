
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
			
package com.adito.applications.types;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.maverick.multiplex.Request;
import com.adito.agent.AgentTunnel;
import com.adito.agent.DefaultAgentManager;
import com.adito.applications.ApplicationLauncherType;
import com.adito.applications.ApplicationService;
import com.adito.applications.ApplicationShortcut;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionException;
import com.adito.extensions.types.AbstractJavaType;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.Resource.LaunchRequirement;
import com.adito.security.SessionInfo;

/**
 * Implementation of an
 * {@link com.adito.applications.ApplicationLauncherType} that allows
 * execution of Java applications using a running Agent.
 */
public class JavaType extends AbstractJavaType implements ApplicationLauncherType {


    static Log log = LogFactory.getLog(JavaType.class);
    
    /**
     * Type name
     */
    public final static String TYPE = "java";

    /**
     * Constructor
     */
    public JavaType() {
        super(TYPE, true);
    }
    
    /* (non-Javadoc)
     * @see com.adito.extensions.ExtensionType#isHidden()
     */
    public boolean isHidden() {
        return false;
    }

	/* (non-Javadoc)
	 * @see com.adito.extensions.ApplicationLauncherType#isAgentRequired(com.adito.extensions.ApplicationShortcut, com.adito.extensions.ExtensionDescriptor)
	 */
	public boolean isAgentRequired(ApplicationShortcut shortcut, ExtensionDescriptor descriptor) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.adito.applications.ApplicationLauncherType#launch(java.util.Map, com.adito.extensions.ExtensionDescriptor, com.adito.applications.ApplicationShortcut, org.apache.struts.action.ActionMapping, com.adito.policyframework.LaunchSession, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public ActionForward launch(Map<String, String> parameters, ExtensionDescriptor descriptor, ApplicationShortcut shortcut, ActionMapping mapping, LaunchSession launchSession, String returnTo, HttpServletRequest request) throws ExtensionException {
		if (log.isInfoEnabled())
			log.info("Launching client application " + shortcut.getResourceName());

		SessionInfo session = launchSession.getSession();
		if (DefaultAgentManager.getInstance().hasActiveAgent(session)) {
			try {
				Request agentRequest = ((ApplicationService) DefaultAgentManager.getInstance().getService(ApplicationService.class)).launchApplication(launchSession);
				AgentTunnel agent = DefaultAgentManager.getInstance().getAgentBySession(launchSession.getSession());
				if (!agent.sendRequest(agentRequest, true, 60000)) {
					throw new ExtensionException(ExtensionException.AGENT_REFUSED_LAUNCH);
				}
			} catch (ExtensionException ee) {
				throw ee;
			} catch (Exception e) {
				throw new ExtensionException(ExtensionException.INTERNAL_ERROR, e);
			}
		} 
		else {
			throw new ExtensionException(ExtensionException.NO_AGENT);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.adito.extensions.ExtensionType#getTypeBundle()
	 */
	public String getTypeBundle() {
		return "applications";
	}

	/* (non-Javadoc)
	 * @see com.adito.applications.ApplicationLauncherType#isServiceSide()
	 */
	public boolean isServerSide() {
		return false;
	}

    /* (non-Javadoc)
     * @see com.adito.extensions.ExtensionType#getLaunchRequirement()
     */
    public LaunchRequirement getLaunchRequirement() {
        return LaunchRequirement.LAUNCHABLE;
    }
}