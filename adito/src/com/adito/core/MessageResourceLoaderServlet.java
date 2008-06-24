
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
			
package com.adito.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.ContextHolder;
import com.adito.boot.Util;
import com.adito.security.SessionInfo;

/**
 * A network class loader servlet that loads classes and returns them 
 * to the client via HTTP.
 * 
 * Note, this currently on allows ApplicationResources*.properties 
 * resources to be loaded (for use by the agent suite). 
 */
public class MessageResourceLoaderServlet extends HttpServlet {
    final static Log log = LogFactory.getLog(MessageResourceLoaderServlet.class);

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getPathInfo().substring(1);
        if (name.equals("")) {
            log.error("No name supplied to the class loader servlet.");
            response.sendError(500);
            return;
        } 
        int idx = name.lastIndexOf('/');
        String basename = name;
        if(idx != -1) {
            basename = name.substring(idx + 1);
        }
        if(!basename.startsWith("ApplicationResources") || ( !basename.endsWith(".properties") && !basename.endsWith(".class"))) {
            log.debug("Attempt to load something other that a resource bundle via the class loader servlet.");
            response.sendError(500);
            return;            
        }
        
        /* This is a hack to get around the problem where we never get
         * get a request for the default language resources when they are
         * in a properties file. This is because we use a class loader on
         * the client end to retrieve the resources.
         */
        
        if(basename.endsWith(".class")) {
        	basename = basename.substring(0, basename.length() - 6) + ".properties";
        	name = name.substring(0, name.length() - 6) + ".properties";
        }
        
        /*
         * Load into byte array so we get the content length before sending on
         * to the client
         */
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        InputStream in = ContextHolder.getContext().getContextLoader().getResourceAsStream(name);
        if (in == null) {
            response.setContentType("text/plain");
            response.sendError(404, "Class not found");
        } else {
            try {
                Util.copy(in, bout);
            }
            finally {
                bout.close();
                in.close();
            }
            response.setContentType("text/plain");
            response.setContentLength(bout.size());
            response.setStatus(200);            
            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            sendFile(bin, bout.size(), response);
        }
    }

    private void sendFile(InputStream in, long length, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Content-Length", String.valueOf(length));
        response.setContentLength((int) length);
        Util.noCache(response);
        try {
            Util.copy(in, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException ex) {
        } finally {
            Util.closeStream(in);
            Util.closeStream(response.getOutputStream());
        }

    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.ALL_CONTEXTS;
    }

}