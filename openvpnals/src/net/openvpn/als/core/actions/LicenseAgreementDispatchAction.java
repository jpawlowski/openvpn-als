
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
			
package net.openvpn.als.core.actions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.core.LicenseAgreement;
import net.openvpn.als.core.forms.LicenseAgreementForm;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.SessionInfo;

public class LicenseAgreementDispatchAction extends DefaultDispatchAction {

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception { 
        List l = (List)request.getSession().getAttribute(Constants.LICENSE_AGREEMENTS);
        if(l == null || l.size() == 0) {
            throw new Exception("No license agreements to show.");
        }
        LicenseAgreement agreement = (LicenseAgreement)l.get(0);
        ((LicenseAgreementForm)form).setAgreement(agreement);
        return mapping.findForward("display");
    }

    public ActionForward accept(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception { 
        List l = (List)request.getSession().getAttribute(Constants.LICENSE_AGREEMENTS);
        if(l == null || l.size() == 0) {
            throw new Exception("No license agreements to agree to.");
        }
        LicenseAgreement agreement = (LicenseAgreement)l.get(0);
        agreement.getCallback().licenseAccepted(request);
        removeAgreement(request.getSession(), l);
        ActionForward fwd = agreement.getReturnTo();
        return fwd;
    }

    public ActionForward reject(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception { 
        List l = (List)request.getSession().getAttribute(Constants.LICENSE_AGREEMENTS);
        if(l == null || l.size() == 0) {
            throw new Exception("No license agreements to reject.");
        }
        LicenseAgreement agreement = (LicenseAgreement)l.get(0);
        agreement.getCallback().licenseRejected(request);
        removeAgreement(request.getSession(), l);
        return agreement.getReturnTo();
    }
    
    private void removeAgreement(HttpSession session, List l) {
        l.remove(0);
        if(l.size() == 0) {
            session.removeAttribute(Constants.LICENSE_AGREEMENTS);
            CoreUtil.removePageInterceptListener(session, CoreUtil.getPageInterceptListenerById(session, "licenseAgreement"));
        }
    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.ALL_CONTEXTS;
    }

}