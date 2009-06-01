
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
			
package com.maverick.http;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class Cookie {

    String name;
    String value;
    String path = ""; //$NON-NLS-1$
    Date expires = null;
    String domain = ""; //$NON-NLS-1$
    boolean secure = false;

    static SimpleDateFormat format = new SimpleDateFormat("EEE, DD-MMM-yyyy HH:mm:ss z"); //$NON-NLS-1$

    public Cookie(String setCookieHeaderValue) {

        StringTokenizer tokens = new StringTokenizer(setCookieHeaderValue, ";"); //$NON-NLS-1$
        while (tokens.hasMoreTokens()) {
            String pair = tokens.nextToken();
            int idx = pair.indexOf('=');
            if (idx > -1) {
                String name = pair.substring(0, idx).trim();
                String value = pair.substring(idx + 1).trim();

                if (name.equalsIgnoreCase("expires")) { //$NON-NLS-1$
                    try {
                        Integer.parseInt(value);
                    } catch (NumberFormatException ex) {
                        try {
                            expires = format.parse(value);
                        } catch (ParseException ex2) {

                        }
                    }
                } else if (name.equalsIgnoreCase("path")) { //$NON-NLS-1$
                    this.path = value;
                } else if (name.equalsIgnoreCase("domain")) { //$NON-NLS-1$
                    this.domain = path;
                } else {
                    this.name = name;
                    this.value = value;
                }
            } else if (pair.trim().equalsIgnoreCase("secure")) //$NON-NLS-1$
                secure = true;
        }

    }

    public String getPath() {
        return path;
    }

    public String getDomain() {
        return domain;
    }

    public boolean isSecure() {
        return secure;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Date getExpires() {
        return expires;
    }

    public String toString() {
        return name + "=" + value; //$NON-NLS-1$

        /*
         * + (expires!=null ? format.format(expires) + "; " : "") + (path!=null ?
         * "path=" + path + "; " : "") + (domain!=null ? "domain=" + domain + "; " :
         * "") + (secure ? "secure" : "");
         */
    }
}
