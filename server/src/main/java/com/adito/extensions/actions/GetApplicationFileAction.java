
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
			
package com.adito.extensions.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.ContextKey;
import com.adito.boot.KeyStoreManager;
import com.adito.boot.Util;
import com.adito.core.CoreException;
import com.adito.core.actions.XMLOutputAction;
import com.adito.core.filters.GZIPResponseWrapper;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.store.ExtensionStore;
import com.adito.properties.Property;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;

public class GetApplicationFileAction extends XMLOutputAction {
	final static Log log = LogFactory.getLog(GetApplicationFileAction.class);

	public GetApplicationFileAction() {
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
					throws Exception {
		if(response instanceof GZIPResponseWrapper) {
			((GZIPResponseWrapper)response).setCompress(false);
		}

		request.setAttribute(Constants.REQ_ATTR_COMPRESS, Boolean.FALSE);
		String application = request.getParameter("name");
		String file = request.getParameter("file").replace('\\', '/');

		String ticket = request.getParameter("ticket");

		if (file.equalsIgnoreCase("adito.cert")) {
			processApplicationCertRequest(application, ticket, response);
		} else {
			processApplicationFileRequest(application, file, ticket, request, response);
		}
		return null;
	}

	protected void processApplicationCertRequest(String application, String ticket, HttpServletResponse response)

	throws Exception {

		byte[] cert = KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE)
						.getCertificate(Property.getProperty(new ContextKey("webServer.alias")))
						.getEncoded();

		/**
		 * If the ticket is a pending VPN session ticket (Agent) then process
		 */
		SessionInfo sessionInfo = LogonControllerFactory.getInstance().getAuthorizationTicket(ticket);

		if (sessionInfo == null)
			throw new CoreException(0, "");

		sendFile(new ByteArrayInputStream(cert), cert.length, response);
	}

	protected void processApplicationFileRequest(String application, String file, String ticket, HttpServletRequest request,
													HttpServletResponse response)

	throws Exception {

		/*
		 * We may already have session info
		 */
		SessionInfo sessionInfo = LogonControllerFactory.getInstance().getSessionInfo(request);
		if (sessionInfo == null) {
			/**
			 * If the ticket is a pending VPN session ticket (Agent) then
			 * process
			 */
			sessionInfo = LogonControllerFactory.getInstance().getAuthorizationTicket(ticket);
		}

		if (sessionInfo == null)
			throw new CoreException(0, "");

		ExtensionDescriptor app = ExtensionStore.getInstance().getExtensionDescriptor(application);

		if (app == null)
			app = ExtensionStore.getInstance().getAgentApplication();

		if (!app.containsFile(file)) {
			log.error("Agent requested a file that does not exist (" + file + ").");
			sendError(file + " not found", response);
		} else {
			sendFile(app.getFile(file), response);
		}
	}

	private void sendFile(File file, HttpServletResponse response) throws IOException {

		sendFile(new FileInputStream(file), file.length(), response);

	}

	private void sendFile(InputStream in, long length, HttpServletResponse response) throws IOException {
		Util.noCache(response);

		response.setHeader("Content-type", "application/octet-stream");
		response.setContentLength((int) length);
		try {

			Util.copy(in, response.getOutputStream());

		} catch (IOException ex) {
		} finally {
			Util.closeStream(in);
			Util.closeStream(response.getOutputStream());
		}

	}

}