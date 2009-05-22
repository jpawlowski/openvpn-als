
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
			
package com.ovpnals.security;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.ovpnals.boot.ReplacementEngine;
import com.ovpnals.core.BundleActionMessage;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.GlobalWarning;
import com.ovpnals.core.GlobalWarningManager;
import com.ovpnals.core.PageInterceptException;
import com.ovpnals.core.PageInterceptListener;
import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.realms.RealmKey;
import com.ovpnals.security.actions.ChangePasswordAction;
import com.ovpnals.security.actions.ShowChangePasswordAction;

/**
 * Implementation of {@link com.ovpnals.security.AbstractPasswordAuthenticationModule}
 * that is suitable for logging on via the web interface.
 */
public class PasswordAuthenticationModule extends AbstractPasswordAuthenticationModule {

    /**
     * The name of this authentication module
     */
    public static final String MODULE_NAME = "Password";

    /**
     * Constructor
     */
    public PasswordAuthenticationModule() {
        super(MODULE_NAME, true);
    }

    /* (non-Javadoc)
     * @see com.ovpnals.security.AuthenticationModule#authenticationComplete()
     */
    public void authenticationComplete() throws SecurityErrorException {
        UserDatabase udb;
        try {
            udb = UserDatabaseManager.getInstance().getUserDatabase(scheme.getUser().getRealm());
        } catch (Exception e1) {
            throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, e1, "Failed to initialise user database.");
        } 

        if (udb.supportsPasswordChange()) {
            /* Check that the password matches the current policy, if not then
            request a new one */
            Pattern p = null;
            try {
                String pattern = Property.getProperty(new RealmKey("security.password.pattern", scheme.getUser().getRealm()
                                .getResourceId()));
                p = ReplacementEngine.getPatternPool().getPattern(pattern, false, false);
                if (!p.matcher(new String(credentials.getPassword())).matches()) {
                    scheme.getServletSession().setAttribute(Constants.PASSWORD_CHANGE_REASON_MESSAGE, new ActionMessage("passwordChange.noLongerMatchesPattern"));
                }
            } catch (Exception e) {
                throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, e, "Could not check password against current policy.");
            } finally {
                if (p != null) {
                    ReplacementEngine.getPatternPool().releasePattern(p);
                }
            }

            // Check if the password has expired (or is
            try {
                if (scheme.getUser().getLastPasswordChange() != null) {
                    GregorianCalendar lastChange = new GregorianCalendar();
                    lastChange.setTimeInMillis(scheme.getUser().getLastPasswordChange().getTime());

                    GregorianCalendar warningOn = new GregorianCalendar();

                    int warningInDays = Property.getPropertyInt(new RealmKey("security.password.daysBeforeExpiryWarning", scheme.getUser().getRealm()
                                    .getResourceId()));
                    warningOn.setTimeInMillis(scheme.getUser().getLastPasswordChange().getTime());
                    warningOn.add(Calendar.DATE, warningInDays);

                    GregorianCalendar expiresOn = new GregorianCalendar();
                    expiresOn.setTimeInMillis(scheme.getUser().getLastPasswordChange().getTime());

                    int expiryInDays = Property.getPropertyInt(new RealmKey("security.password.daysBeforeExpiry", scheme.getUser().getRealm()
                                    .getResourceId()));
                    expiresOn.add(Calendar.DATE, expiryInDays);

                    GregorianCalendar now = new GregorianCalendar();

                    if (expiresOn.before(now) && expiryInDays > 0) {
                        scheme.getServletSession().setAttribute(Constants.PASSWORD_CHANGE_REASON_MESSAGE,
                                        new ActionMessage("passwordChange.expired"));
                    } else if (warningOn.before(now) && warningInDays > 0) {
                        long daysToExpiry = ((expiresOn.getTimeInMillis() - now.getTimeInMillis()) + 86399999l) / 86400000l;
                        GlobalWarningManager.getInstance().addToSession(new GlobalWarning(scheme.getServletSession(), new BundleActionMessage("navigation",
                            "globalWarning.passwordNearExpiry", new Long(daysToExpiry))));

                    }
                } else if (scheme.getUser().requiresPasswordChange()) {
                    scheme.getServletSession().setAttribute(Constants.PASSWORD_CHANGE_REASON_MESSAGE,
                                    new ActionMessage("passwordChange.newPassword"));
                }
                if (scheme.getServletSession().getAttribute(Constants.PASSWORD_CHANGE_REASON_MESSAGE) != null) {

                    CoreUtil.addPageInterceptListener(scheme.getServletSession(), new PageInterceptListener() {

                        public String getId() {
                            return "changePassword";
                        }

                        public ActionForward checkForForward(Action action, ActionMapping mapping, HttpServletRequest request,
                                        HttpServletResponse response) throws PageInterceptException {
                            if (!(action instanceof ShowChangePasswordAction) && !(action instanceof ChangePasswordAction)) {
                                return new ActionForward("/showChangePassword.do?referer=/logoff.do", true);
                            }
                            return null;
                        }

                        public boolean isRedirect() {
                            return false;
                        }
                    });
                }
            } catch (Exception e) {
                throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, e, "Could not check password against current policy.");
            }
        }

    }
    
    /* (non-Javadoc)
     * @see com.ovpnals.security.AuthenticationModule#getInclude()
     */
    public String getInclude() {
        return "/WEB-INF/jsp/auth/userPasswordAuth.jspf";
    }
}
