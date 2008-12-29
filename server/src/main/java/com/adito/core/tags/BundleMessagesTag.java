
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
			

package com.adito.core.tags;

import javax.servlet.jsp.JspException;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.MessagesTag;

import com.adito.core.BundleActionMessage;

/**
 * Extension of the standard &lt;bean:message&gt; tag that understands {@link com.adito.core.BundleActionMessage}
 * objects as well as the usual {@link org.apache.struts.action.ActionMessage} objects.
 * <p>
 * <i>Bundle Action Messages</i> differe from the usual objects in that the key of the
 * message bundle that contains the message text may be specified by the contructor of
 * the object instead of in the JSP tags.
 */
public class BundleMessagesTag extends MessagesTag {

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    public int doStartTag() throws JspException {
        processed = false;
        ActionMessages messages = null;
        String name = this.name;
        if (message != null && "true".equalsIgnoreCase(message)) {
            name = Globals.MESSAGE_KEY;
        }
        try {
            messages = TagUtils.getInstance().getActionMessages(pageContext, name);
        } catch (JspException e) {
            TagUtils.getInstance().saveException(pageContext, e);
            throw e;
        }
        this.iterator = (property == null) ? messages.get() : messages.get(property);
        if (!this.iterator.hasNext()) {
            return SKIP_BODY;
        }
        ActionMessage report = (ActionMessage) this.iterator.next();
        String actualBundle = report instanceof BundleActionMessage ? ((BundleActionMessage)report).getBundle() :bundle;
        String msg =
            TagUtils.getInstance().message(
                pageContext,
                actualBundle,
                locale,
                report.getKey(),
                report.getValues());
        if (msg == null) {
            pageContext.setAttribute(id, "Could not locate resource with key " + report.getKey() + " in bundle " + actualBundle );
        } else {
            pageContext.setAttribute(id, msg);
        }
        if (header != null && header.length() > 0) {
            String headerMessage =
                TagUtils.getInstance().message(pageContext, bundle, locale, header);

            if (headerMessage != null) {
                TagUtils.getInstance().write(pageContext, headerMessage);
            }
        }
        processed = true;
        return (EVAL_BODY_BUFFERED);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
     */
    public int doAfterBody() throws JspException {
        if (bodyContent != null) {
            TagUtils.getInstance().writePrevious(pageContext, bodyContent.getString());
            bodyContent.clearBody();
        }
        if (iterator.hasNext()) {
            ActionMessage report = (ActionMessage) iterator.next();
            String msg =
                TagUtils.getInstance().message(
                    pageContext,
                    report instanceof BundleActionMessage ? ((BundleActionMessage)report).getBundle() :bundle,
                    locale,
                    report.getKey(),
                    report.getValues());

           if (msg == null) {
               pageContext.removeAttribute(id);
           } else {
               pageContext.setAttribute(id, msg);
           }

           return (EVAL_BODY_BUFFERED);
        } else {
           return (SKIP_BODY);
        }

    }
}
