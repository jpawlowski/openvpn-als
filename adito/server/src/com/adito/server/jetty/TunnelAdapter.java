
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
			
package com.adito.server.jetty;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.http.HttpTunnel;

import com.adito.boot.RequestHandlerTunnel;

class TunnelAdapter extends HttpTunnel {

	static Log log = LogFactory.getLog(TunnelAdapter.class);
	
    private RequestHandlerTunnel tunnel;
    private int timeoutMs;
    private OutputStream originalOutputStream;
    
    public TunnelAdapter(RequestHandlerTunnel tunnel) {
        this(tunnel, 0);
    }

    public TunnelAdapter(RequestHandlerTunnel tunnel, int timeoutMs) {
        this.tunnel = tunnel;
        this.timeoutMs = timeoutMs;
    }
    
    /* (non-Javadoc)
     * @see org.mortbay.http.HttpTunnel#handle(java.io.InputStream, java.io.OutputStream)
     */
    public void handle(InputStream in, OutputStream out) {
    	
    	this.originalOutputStream = out;
    	
    	if(timeoutMs > 0) {
    		setSocketTimeout(out, timeoutMs);
    	}
    	
        tunnel.tunnel(in, out);
    }
    
	private void setSocketTimeout(Object obj, int timeoutMs) {
		
		if(log.isDebugEnabled())
			log.debug("Looking for com.sun.net.ssl.internal.ssl.SSLSocketImpl to set timeout");
		
		Object ssl = getPrivateMember(obj, "com.sun.net.ssl.internal.ssl.SSLSocketImpl");
		
		if(ssl!=null) {
			try {
				Method m = ssl.getClass().getMethod("setSoTimeout", new Class[] { int.class});
				if(m!=null) {
					m.setAccessible(true);
					m.invoke(ssl, new Object[] { timeoutMs});
					
					if(log.isDebugEnabled())
						log.debug("Configured socket timeout on tunnel of " + timeoutMs + "ms");
				} else {
					if(log.isInfoEnabled())
						log.warn("Could not configure a timeout on socket! no setSoTimeout available");
				}
			} catch(Throwable t) {
				log.error("Could not access setSoTimeout method", t);
			}
		} else {
			if(log.isInfoEnabled())
				log.warn("Could not configure a timeout on socket! no SSLSocketImpl available");
		}
	}

	private Object getPrivateMember(Object obj, String type) {

		Class secretClass = obj.getClass();

		// Print all the field names & values
		Field fields[] = secretClass.getDeclaredFields();
		
		if(log.isDebugEnabled())
			log.debug("Access all the fields on " 
					+ obj.getClass().getName() 
					+ " looking for type " 
					+ type);
		
		for (int i = 0; i < fields.length; i++) {
			
			if(log.isDebugEnabled())
				log.debug("Field Name: " + fields[i].getName());
			
			fields[i].setAccessible(true);

			try {
				Object obj2 = fields[i].get(obj);
				if (obj2!=null && obj2.getClass().getName().equals(type)) {
					return obj2;
				}
			} catch (Throwable e) {
				log.error("Failed to set timeout on SSLSocketImpl", e);
			}

		}

		log.error("Could not find " + type + " on object " + obj.getClass().getName());
		
		return null;
	}
}