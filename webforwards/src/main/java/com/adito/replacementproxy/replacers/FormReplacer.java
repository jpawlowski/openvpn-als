
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
			
package com.adito.replacementproxy.replacers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.CaseInsensitiveMap;
import com.adito.boot.Replacer;
import com.adito.boot.Util;
import com.adito.util.Utils;

public class FormReplacer implements Replacer {

    private URL context;
    private String ticket;
    
    static Log log = LogFactory.getLog(FormReplacer.class);
    
    public FormReplacer(URL context, String ticket) {
        this.context = context;
        this.ticket = ticket;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.services.Replacer#getReplacement(java.util.regex.Pattern,
     *      java.util.regex.Matcher, java.lang.String)
     */
    public String getReplacement(Pattern pattern, Matcher matcher, String replacementPattern) {
        String attrs = matcher.group(2);
        return doRepl(attrs);
    }

    private String doRepl(String attrs) {
        StringBuffer attrName = new StringBuffer();
        char quote = '\0';
        StringBuffer attrVal = new StringBuffer();
        boolean doName = true;
        boolean doVal = false;
        CaseInsensitiveMap a = new CaseInsensitiveMap();
        for (int i = 0; i < attrs.length(); i++) {
            char ch = attrs.charAt(i);
            if (ch == '\'' && quote == '\0') {
                quote = '\'';
            } else if (ch == '"' && quote == '\0') {
                quote = '"';
//            } else if (((doName && (ch == '\r' || ch == '\n')) || (doVal && ( i == attrs.length() - 1 ) ) || (doVal && ch == ' ' && quote == ' ' && attrVal.length() > 0)
//                || (doVal && ch == '\'' && quote == '\'') || (doVal && ch == '\"' && quote == '\"'))) {
              } else if (doVal && ( ch == quote || ( quote == '\0' && ch == ' ' ) || i == ( attrs.length() - 1 ) ) ) {
            	if(quote == '\0') {
            		attrVal.append(ch);
            	}
                quote = '\0';
                String an = attrName.toString();
                if (!an.equals("")) {
                    a.put(attrName.toString(), attrVal.toString());
                }
                attrName.setLength(0);
                attrVal.setLength(0);
                doVal = false;
                doName = true;
            } else if (ch == '=' && doName) {
                doName = false;
                doVal = true;
            } else {
                if (doName) {
                    if ((ch != ' ' && ch != '\r' && ch != '\n') || attrName.length() > 0) {
                        attrName.append(ch);
                    }
                } else if (doVal) {
                    attrVal.append(ch);
                }
            }
        }
        StringBuffer buf = new StringBuffer("<form");
        String sslexUrl = context.toExternalForm();
        if (a.containsKey("action")) {
            try {
                String contextPath = context.toExternalForm();
                if (contextPath.endsWith("/")) {
                    contextPath = contextPath.substring(0, contextPath.length() - 1);
                }                
                String originalAction = a.get("action").toString();
                try {
                	sslexUrl = new URL(originalAction).toExternalForm();
                }
                catch(MalformedURLException murle) {
                	/**
                	 * LDP - Bug fix: the commented out code causes problems with relative
                	 * URLs used in the action parameter of a form
                	 * 
                	 * Example:
                	 * 
                	 * Context URL:
                	 * http://foobar/OA_HTML/AppsLocalLogin.jsp
                	 * 
                	 * Action:
                	 * fndvald.jsp
                	 * 
                	 * Results In:
                	 * 
                	 * http://foobar/OA_HTML/AppsLocalLogin.jsp/fndvald.jsp
                	 * 
                	 * Fixed code results in:
                	 * 
                	 * http://foobar/OA_HTML/fndvald.jsp
                	 * 
                	 * Slash should not be a problem because URL is intelligent enough to work out that
                	 * the slash means from the root of the URL. I have tested this case and it does indeed
                	 * work the same way regardless of the value of originalAction, in fact originalAction
                	 * can be a completely different URL for example http://localhost/fndvald.jsp
                	 * 
                	 * Here are the test cases:
                	 * 
                	 * new URL("http://foobar/OA_HTML/AppsLocalLogin.jsp/fndvald.jsp", "fndvald.jsp").toExternalForm();
                	 * == http://foobar/OA_HTML/AppsLocalLogin.jsp/fndvald.jsp
                	 * 
                	 * new URL("http://foobar/OA_HTML/AppsLocalLogin.jsp/fndvald.jsp", "/fndvald.jsp").toExternalForm();
                	 * == http://foobar/fndvald.jsp
                	 * 
                	 * new URL("http://foobar/OA_HTML/AppsLocalLogin.jsp/fndvald.jsp", "http://localhost/fndvald.jsp").toExternalForm();
                	 * == http://localhost/fndvald.jsp
                	 */
                	//if(originalAction.startsWith("/")) {
                		sslexUrl = new URL(context, originalAction).toExternalForm();
                	//}
                	//else {
                	//	sslexUrl = new URL(DAVUtilities.concatenatePaths(contextPath, originalAction)).toExternalForm();
                	//}
                }
            } catch (MalformedURLException e) {
            	log.error("Failed to process FORM action", e);
            }
        }
        
        a.put("action", "/replacementProxyEngine/" + ticket + "/" + Util.urlEncode(Utils.htmlunescape(sslexUrl)) );

        for (Iterator i = a.entrySet().iterator(); i.hasNext();) {
            buf.append(" ");
            Map.Entry entry = (Map.Entry) i.next();
            buf.append(entry.getKey().toString());
            buf.append("=\"");
            buf.append(entry.getValue());
            buf.append("\"");
        }
        buf.append(">");

        return buf.toString();
    }
}
