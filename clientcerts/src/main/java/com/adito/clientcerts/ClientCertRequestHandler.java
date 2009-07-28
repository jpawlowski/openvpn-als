
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
			
package com.adito.clientcerts;

import com.adito.boot.RequestHandler;
import com.adito.boot.RequestHandlerRequest;
import com.adito.boot.RequestHandlerResponse;
import com.adito.boot.RequestHandlerException;
import com.adito.boot.ContextHolder;
import com.adito.boot.Context;
import com.adito.core.ServletRequestAdapter;
// import com.adito.server.jetty.RequestAdapter;
import com.adito.security.LogonController;
import com.adito.security.LogonControllerFactory;
import com.adito.security.AuthenticationScheme;
import com.adito.security.PasswordAuthenticationModule;
import com.adito.security.DefaultAuthenticationScheme;
import com.adito.security.UserDatabase;
import com.adito.security.User;
import com.adito.security.SessionInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.adito.core.UserDatabaseManager;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.cert.X509Certificate;

import java.lang.reflect.Field;

public class ClientCertRequestHandler implements RequestHandler {
	private static final Log LOG = LogFactory.getLog(ClientCertRequestHandler.class);

	public boolean handle(String pathInContext, String pathParams, RequestHandlerRequest request, RequestHandlerResponse response) throws IOException, RequestHandlerException {

		/* if (request instanceof ServletRequestAdapter) {
		// if (request instanceof RequestAdapter) {
			LOG.info("Found instance of RequestAdapter");
		}
		LOG.info("request is class: "+request.getClass());

		try {
			Class cl = request.getClass();
			Field[] flds = cl.getDeclaredFields();
			Field.setAccessible(flds,true);
			for (Field f:flds) {
				if (f.getName().equals("request")) {
					Object tmp = f.get(request);
					LOG.info("tmp is class: "+tmp.getClass());
					if (tmp instanceof HttpServletRequest) {
						HttpServletRequest r = (HttpServletRequest) tmp;
						LOG.info("Found private request! Session: "+r.getSession().getId());
					}
				}
			}
		} catch (Exception e) {
			LOG.info("Error here: "+e.getMessage());
			e.printStackTrace(System.err);
		} */
		
		/* Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			// LOG.info("Found no cookies, redirecting to start session");
			//httpresponse.sendRedirect("/");
			//return true;
		} else {
			for (int i=0;i<cookies.length; i++) {
				LOG.info("Cookie "+cookies[i].getName()+" -> "+cookies[i].getValue());
				if (cookies[i].getName().equals("logonTicket")) {
					// return false;
				}
			}
		} */
		
		if (pathInContext.equals("/checkClientCert.do")) return false;

		X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
		if (certs != null && pathInContext.equals("/showLogon.do")) {
			response.sendRedirect("/checkClientCert.do");
			return true;
		}
		
		return false;
						
		// check for a cert and login if nobody is logged in...hopefully
		/* LOG.info("pathInContext: "+pathInContext);
		Context main = ContextHolder.getContext();
		
		HttpServletRequest httprequest = main.createServletRequest(request);
		HttpServletResponse httpresponse = main.createServletResponse(response, httprequest); */

		/* Cookie[] cookies = httprequest.getCookies();
		if (cookies == null || cookies.length == 0) {
			// LOG.info("Found no cookies, redirecting to start session");
			//httpresponse.sendRedirect("/");
			//return true;
		} else {
			for (int i=0;i<cookies.length; i++) {
				LOG.info("Cookie "+cookies[i].getName()+" -> "+cookies[i].getValue());
			}
		} */
		
		/* LogonController lc = LogonControllerFactory.getInstance();

		LOG.info("SessionID: "+httprequest.getSession().getId()+" "+(httprequest.getSession().isNew() ? "new" : ""));

		try {

			if (lc.hasClientLoggedOn(httprequest,httpresponse) == LogonController.NOT_LOGGED_ON) {
				if (pathInContext.equals("/showLogon.do")) {
					try {
						// X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
						if (certs != null) {
							X509Certificate cert = certs[0];
							LOG.info("Found client cert: "+cert.getSubjectDN().getName());

							UserDatabaseManager udm = UserDatabaseManager.getInstance();
							UserDatabase ud = udm.getDefaultUserDatabase();
							User user = ud.getAccount("admin");
                
							AuthenticationScheme scheme = null;
							try {
								// SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(httprequest);
								Calendar now = new GregorianCalendar();
								// scheme = new DefaultAuthenticationScheme(info.getRealmId(), Integer.MAX_VALUE, "Fake sheme", "Fake scheme",
								scheme = new DefaultAuthenticationScheme(-1, Integer.MAX_VALUE, "Fake sheme", "Fake scheme",
										now, now, true, 0);
                						scheme.addModule(PasswordAuthenticationModule.MODULE_NAME);
							} catch (Exception e) {
								LOG.warn("Problem with authentication scheme: "+e.getMessage());
							} */

							/* Calendar now = Calendar.getInstance();
							AuthenticationScheme scheme = new DefaultAuthenticationScheme(-1, -1, "", "", now, now, true, 0); */
							/* scheme.setUser(user);
							scheme.init(httprequest.getSession());
							lc.logon(httprequest, httpresponse, scheme);
							httpresponse.sendRedirect("/");
							return true;
							// return false;
						} else {
							LOG.info("Didn't find any client cert");
						}
					} catch (Exception e) {
						LOG.warn("couldn't get request: "+e.getMessage());
					}
				}
			}
		} catch (Exception e) { }
		return false; */
	}
}
