
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
			
package com.ovpnals.extensions.actions;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jdom.JDOMException;

import com.ovpnals.core.CoreException;
import com.ovpnals.core.actions.XMLOutputAction;
import com.ovpnals.extensions.ExtensionDescriptor;
import com.ovpnals.extensions.ExtensionException;
import com.ovpnals.extensions.ExtensionParser;
import com.ovpnals.extensions.store.ExtensionStore;
import com.ovpnals.security.Constants;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;

/**
 * Retrieve an application extensions descriptor.
 */
public class GetExtensionDescriptorAction extends XMLOutputAction {

	final static Log log = LogFactory.getLog(GetExtensionDescriptorAction.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
					throws Exception {

		request.setAttribute(Constants.REQ_ATTR_COMPRESS, Boolean.FALSE);
		String ticket = request.getParameter("ticket");
		String id = request.getParameter("id");
		if(id == null) {
			throw new Exception("No id");
		}
		ExtensionDescriptor app = ExtensionStore.getInstance().getExtensionDescriptor(id);
		if(app == null) {
			throw new Exception("No extension with id of " + id);
		}
		Properties properties = new Properties();
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String name = (String) e.nextElement();
			String val = request.getParameter(name);
			if (name.equals("ticket") || name.equals("id") || name.equals("name")) {
				continue;
			} else {
				properties.put(name, val);
			}
		}
		processApplicationRequest(app, ticket, request, response, properties);
		return null;
	}

	protected void processApplicationRequest(ExtensionDescriptor app, String ticket, HttpServletRequest request,
												HttpServletResponse response, Properties properties)
					throws JDOMException, Exception {
		try {
			SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
			if(session == null) {
				throw new ExtensionException(ExtensionException.INTERNAL_ERROR, "No session.");
			}
			sendApplicationDescriptor(app, request, response, session, properties);

		} catch (CoreException coreException) {
			String message = coreException.getLocalizedMessage(request.getSession());
			log.error(message, coreException);
			sendError(message, response);
		} catch (Throwable t) {
			log.error("Failed to process request for application.", t);
			sendError(t.getLocalizedMessage() == null ? "<null>" : t.getLocalizedMessage(), response);
		}
	}

	private void sendApplicationDescriptor(ExtensionDescriptor app, HttpServletRequest request, HttpServletResponse response,
											SessionInfo session, Properties properties)
					throws JDOMException, IOException, UndefinedParameterException, CoreException {
		response.setHeader("Content-type", "text/xml"); 
		byte[] xml = ExtensionParser.processAgentParameters(app, request, session, properties).getBytes();
//		response.setContentLength(xml.length);
		response.getOutputStream().write(xml);
		response.getOutputStream().close();
	}

}
