package com.adito.clientcerts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import java.security.cert.X509Certificate;

public class CheckClientCertAction extends Action {

	private static Log LOG = LogFactory.getLog(CheckClientCertAction.class);

	public CheckClientCertAction() {
		LOG.info("Constructor for checkClienCert");
	}

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, ServletRequest request, ServletResponse response) {
		LOG.info("Starting action for checkClienCert the other method");
		return null;
	}

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOG.info("Starting action for checkClienCert");
		X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
		if (certs == null) {
			LOG.info("Found no certs, going to start");
			response.sendRedirect("/");
			return null;
		}
		
		LogonController lc = LogonControllerFactory.getInstance();

		LOG.info("SessionID: "+request.getSession().getId()+" "+(request.getSession().isNew() ? "new" : ""));

		try {

			if (lc.hasClientLoggedOn(request,response) == LogonController.NOT_LOGGED_ON) {
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
						}

						/* Calendar now = Calendar.getInstance();
						AuthenticationScheme scheme = new DefaultAuthenticationScheme(-1, -1, "", "", now, now, true, 0); */
						scheme.setUser(user);
						scheme.init(request.getSession());
						lc.logon(request, response, scheme);
						response.sendRedirect("/");
						return null;
						// return false;
					} else {
						LOG.info("Didn't find any client cert");
					}
				} catch (Exception e) {
					LOG.warn("couldn't get request: "+e.getMessage());
				}
			}
		} catch (Exception e) { }
		return null;
						
	}
}
