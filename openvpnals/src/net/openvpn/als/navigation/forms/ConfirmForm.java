
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
			
package net.openvpn.als.navigation.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.forms.CoreForm;

/**
 * 
 */
public class ConfirmForm extends CoreForm {

    static Log log = LogFactory.getLog(ConfirmForm.class);

    public final static String TYPE_QUESTION = "question";
    public final static String TYPE_INFO = "info";
    public final static String TYPE_EXCEPTION = "exception";
    public final static String TYPE_ERROR = "error";
    public final static String TYPE_OK = "OK";

    private List options;
    private String title, subtitle, message;
    private boolean decorated;
    private String traceMessage, exceptionMessage;
    private List messages = new ArrayList();
    private String align;
    private String type;
    private String arg0;

    public void initialize(String type, String title, String subtitle, String message, List options, boolean decorated,
                           String align, String arg0) {
        setTitle(title);
        setSubtitle(subtitle);
        setMessage(message);
        setOptions(options);
        setDecorated(decorated);
        setAlign(align);
        setType(type);
        setArg0(arg0);
    }

    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        messages.clear();
        StringTokenizer t = new StringTokenizer(message == null ? "<null>" : message, "\n");
        while (t.hasMoreTokens()) {
            messages.add(t.nextToken());
        }
    }

    public String getAlign() {
        return align;
    }

    public List getOptions() {
        return options;
    }

    public void setOptions(List options) {
        this.options = options;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isDecorated() {
        return decorated;
    }

    public void setDecorated(boolean decorated) {
        this.decorated = decorated;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public List getMessages() {
        return messages;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setTraceMessage(String traceMessage) {
        this.traceMessage = traceMessage;
    }

    public String getTraceMessage() {
        return traceMessage;
    }

    public String getArg0() {
        return arg0;
    }

    public void setArg0(String arg0) {
        this.arg0 = arg0;
    }
}