
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
			
package com.adito.navigation.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import com.adito.boot.Util;
import com.adito.core.CoreException;
import com.adito.core.CoreUtil;
import com.adito.core.actions.DefaultAction;
import com.adito.core.stringreplacement.VariableReplacement;
import com.adito.navigation.Option;
import com.adito.navigation.forms.ConfirmForm;
import com.adito.security.Constants;
import com.adito.security.SessionInfo;

public class ConfirmAction extends DefaultAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		// Get all of the options from the resource bundle
		List options = new ArrayList();
		MessageResources resources = null;

		String bundle = request.getParameter("bundle");
		String propertyPrefix = request.getParameter("propertyPrefix");
		String type = request.getParameter("type");
		if (type == null) {
			type = "message";
		}
		String arg0 = request.getParameter("arg0");
		boolean decorated = true;
		String align = "center";
        
        Locale locale = (Locale)request.getSession().getAttribute(Globals.LOCALE_KEY); 

		// parameter
		// ="administration,deletePropertyProfile,yes=/updatePropertyProfile.do?action=delete,no="
		if (mapping.getParameter() != null
				&& !mapping.getParameter().equals("")) {
			StringTokenizer t = new StringTokenizer(mapping.getParameter(), ",");
			if (t.hasMoreTokens()) {
				type = t.nextToken();
			}

			// TODO this is a hack to get the extension store agreement aligning
			// left, sort it out properly!
			if (type.startsWith("align=")) {
				int idx = type.indexOf('=');
				align = type.substring(idx + 1);
				type = t.nextToken();
			}

			if (t.hasMoreTokens()) {
				decorated = t.nextToken().equalsIgnoreCase("true");
			}
			if (t.hasMoreTokens()) {
				bundle = t.nextToken();
			}
			if (t.hasMoreTokens()) {
				propertyPrefix = t.nextToken();
			}
			resources = getResources(request, bundle);
			if (resources == null) {
				throw new Exception("Could not find resource bundle " + bundle);
			}
			while (t.hasMoreTokens()) {
				String option = t.nextToken();
				options.add(getOption(locale, option, propertyPrefix, request,
						resources));
			}
		} else {
			resources = getResources(request, bundle);
			if (resources == null) {
				throw new Exception("Could not find resource bundle " + bundle);
			}
			String[] optionValues = request.getParameterValues("option");
			if (options != null) {
				for (int i = 0; i < optionValues.length; i++) {
					options.add(getOption(locale, optionValues[i], propertyPrefix,
							request, resources));
				}

			}
		}

		// Get the title text and the description
		String title = resources.getMessage(locale, propertyPrefix + ".title");
		String subtitle = resources.getMessage(locale, propertyPrefix + ".subtitle");
		String message = resources
				.getMessage(locale, propertyPrefix + ".message", arg0);

		// Initialise the form
		ConfirmForm confirmForm = (ConfirmForm) form;
		confirmForm.initialize(type, title, subtitle, message, options,
				decorated, align, arg0);

		// If this confirmation is the result on an exception then build up the
		// exception text
		Throwable exception = type.equals(ConfirmForm.TYPE_EXCEPTION) ? (Throwable) request
				.getSession().getAttribute(Constants.EXCEPTION)
				: null;
		request.getSession().removeAttribute(Constants.EXCEPTION);
		if (exception != null) {
			StringBuffer mesgBuf = new StringBuffer();
			StringBuffer traceBuf = new StringBuffer();
			Throwable ex = exception;
			while (ex != null) {
				String mesg = ex.getMessage();
				if (mesg != null) {
					mesg = mesg.trim();
					if (!mesg.endsWith(".")) {
						mesg += ".";
					}
					if (mesgBuf.length() == 0) {
						mesgBuf.append('\n');
					}
					mesgBuf.append(mesg);
				}
				StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				if (traceBuf.length() == 0) {
					traceBuf.append("\n");
				}
				traceBuf.append(sw.toString());
				ex = ex.getCause();
			}

			// If this is a code exception we can get the localised messages
			if (exception instanceof CoreException) {
				CoreException ce = (CoreException) exception;
				MessageResources mr = CoreUtil.getMessageResources(request
						.getSession(), ce.getBundle());
				if (mr != null) {
					mesgBuf.append(" ");
					mesgBuf.append(mr.getMessage((Locale) request
							.getSession().getAttribute(Globals.LOCALE_KEY), ce
							.getBundleActionMessage().getKey(), ce
							.getBundleActionMessage().getArg0(), ce
							.getBundleActionMessage().getArg1(), ce
							.getBundleActionMessage().getArg2(), ce
							.getBundleActionMessage().getArg3()));

				}
			}

			confirmForm.setExceptionMessage(mesgBuf.toString());
			confirmForm.setTraceMessage(traceBuf.toString());
		}

		return mapping.findForward("success");
	}

	public Option getOption(Locale locale, String optionText, String propertyPrefix,
			HttpServletRequest request, MessageResources resources) {

		/*
		 * If the path for the forward for this option ends with a !, then dont
		 * append parameters. This is used in places like the logon screen where
		 * you would not want parameters such as password, PIN etc to be passed
		 * back after an exception
		 */
		boolean includeParameters = true;
		if (optionText.endsWith("!")) {
			optionText = optionText.substring(0, optionText.length() - 1);
			includeParameters = false;
		}

		int idx = optionText.indexOf('=');
		String name = optionText;
		String forward = null;
		if (idx != -1) {
			forward = name.substring(idx + 1);
			if (forward.equals("@")) {
				forward = CoreUtil.getReferer(request);
				if (forward == null) {
					forward = "/showHome.do";
				}
			}
			name = name.substring(0, idx);
            
            VariableReplacement r = new VariableReplacement();
            r.setServletRequest(request);
            forward = r.replace(forward);            
		}

		StringBuffer buf = new StringBuffer(forward);
		if (includeParameters && !"".equals(forward)) {
			// Build up the URL to forard to
			for (Enumeration e = request.getParameterNames(); e
					.hasMoreElements();) {
				String paramName = (String) e.nextElement();
				String[] paramVals = request.getParameterValues(paramName);
				for (int i = 0; i < paramVals.length; i++) {
					if (buf.length() == forward.length()
							&& forward.indexOf('?') == -1) {
						buf.append("?");
					} else {
						buf.append("&");
					}
					buf.append(Util.urlEncode(paramName));
					buf.append("=");
					buf.append(Util.urlEncode(paramVals[i]));
				}
			}
		}

		String label = resources.getMessage(locale, propertyPrefix + ".option." + name);
		return new Option(buf.toString(), label, name);

	}

	public int getNavigationContext(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		return SessionInfo.ALL_CONTEXTS;
	}

}