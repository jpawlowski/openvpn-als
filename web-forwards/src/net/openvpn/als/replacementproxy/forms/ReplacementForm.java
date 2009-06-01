
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
			
package net.openvpn.als.replacementproxy.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import net.openvpn.als.core.forms.CoreForm;
import net.openvpn.als.replacementproxy.Replacement;

/**
 *  
 */
public class ReplacementForm extends CoreForm {

    final static List REPLACE_TYPES = new ArrayList();
    Replacement replacement;

    boolean editing = false;

    static Log log = LogFactory.getLog(ReplacementForm.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if ("commit".equals(request.getParameter("action"))) {
            ActionErrors msgs = new ActionErrors();
            if ((getReplacement().getReplaceType() == Replacement.REPLACEMENT_TYPE_RECEIVED_CONTENT || getReplacement()
                            .getReplaceType() == Replacement.REPLACEMENT_TYPE_SENT_CONTENT)
                            && getReplacement().getMimeType().trim().equals("")) {
                msgs.add(Globals.MESSAGE_KEY, new ActionMessage("createReplacement.error.mimeTypeRequired"));
            }
            if (getReplacement().getMatchPattern().trim().equals("")) {
                msgs.add(Globals.MESSAGE_KEY, new ActionMessage("createReplacement.error.noMatchPattern"));
            }
            try {
                Pattern.compile(getReplacement().getMatchPattern().trim());
            } catch (Exception e) {
                msgs.add(Globals.MESSAGE_KEY, new ActionMessage("createReplacement.error.invalidMatchPattern", e.getMessage()));
            }
            if (!replacement.getSitePattern().trim().equals("")) {
                try {
                    Pattern.compile(getReplacement().getSitePattern().trim());
                } catch (Exception e) {
                    msgs.add(Globals.MESSAGE_KEY, new ActionMessage("createReplacement.error.invalidSitePattern", e.getMessage()));
                }
            }
            return msgs;
        } else {
            return null;
        }
    }

    public Replacement getReplacement() {
        return replacement;
    }

    public void setReplacement(Replacement replacement) {
        this.replacement = replacement;
    }

    public String getUpdateAction() {
        return editing ? "/editReplacement.do" : "/createReplacement.do";
    }

    public void initialize(Replacement replacement, boolean editing) {
        this.replacement = replacement;
        this.editing = editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setReplaceType(String replaceType) {
        try {
            replacement.setReplaceType(Integer.parseInt(replaceType));
        } catch (NumberFormatException nfe) {
            log.error(nfe);
        }
    }

    public String getReplaceType() {
        return String.valueOf(replacement.getReplaceType());
    }
}