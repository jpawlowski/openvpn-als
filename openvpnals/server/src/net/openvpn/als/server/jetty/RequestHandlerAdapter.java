
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
			
package net.openvpn.als.server.jetty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.handler.AbstractHttpHandler;

import net.openvpn.als.boot.RequestHandler;
import net.openvpn.als.boot.RequestHandlerException;
import net.openvpn.als.boot.SystemProperties;

/**
 * Implementation of Jetty's
 * {@link org.mortbay.http.handler.AbstractHttpHandler} that adapts requests 
 * to OpenVPN-ALS's own registered {@link net.openvpn.als.boot.RequestHandler}
 * implementations.
 * @see net.openvpn.als.boot.RequestHandler
 */
public class RequestHandlerAdapter extends AbstractHttpHandler {

    // Package private statics

    static Log log = LogFactory.getLog(RequestHandlerAdapter.class);

    // Private statics

    private static final long serialVersionUID = 4682392114545977296L;

    // Private instance variables

    private List requestHandlers = new ArrayList();

    /**
     * <p>
     * Add a new {@link RequestHandler}. Every time a request is received that
     * is not serviced by the main webapp, each registered handler will be
     * invoked until one deals with the request.
     * 
     * <p>
     * This shouldn't be called directly, but through
     * {@link CustomHttpContext#registerRequestHandler(RequestHandler)}
     * 
     * @param requestHandler handler to add
     */
    public void registerRequestHandler(RequestHandler requestHandler) {
    	if (log.isInfoEnabled())
    		log.info("Registering request handler " + requestHandler.getClass().getName());
        requestHandlers.add(requestHandler);
    }

    /**
     * <p>
     * Remove a {@link RequestHandler} so that is no longer received unhandled
     * requests. See {@link #registerRequestHandler(RequestHandler)}.
     * 
     * <p>
     * This shouldn't be called directly, but through
     * {@link CustomHttpContext#deregisterRequestHandler(RequestHandler)}
     * 
     * @param requestHandler handler to remove
     */
    public void deregisterRequestHandler(RequestHandler requestHandler) {
    	if (log.isInfoEnabled())
    		log.info("De-registering request handler " + requestHandler.getClass().getName());
        requestHandlers.remove(requestHandler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mortbay.http.HttpHandler#handle(java.lang.String,
     *      java.lang.String, org.mortbay.http.HttpRequest,
     *      org.mortbay.http.HttpResponse)
     */
    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException,
                    IOException {
    	if (log.isDebugEnabled())
    		log.debug("Request for " + pathInContext);
        for (Iterator i = requestHandlers.iterator(); i.hasNext();) {
            try {
                request.setCharacterEncoding(SystemProperties.get("openvpnals.encoding", "UTF-8"), false);
                RequestHandler handler = (RequestHandler) i.next();
                ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(handler.getClass().getClassLoader());
                if (handler.handle(pathInContext, pathParams, new RequestAdapter(request), new ResponseAdapter(response))) {
                    request.setHandled(true);
                    break;
                }
                Thread.currentThread().setContextClassLoader(oldLoader);
            } catch (RequestHandlerException e) {
                log.error("Failed to handle request. Status code " + e.getCode());
                throw new HttpException(e.getCode(), e.getMessage());
            }
        }
    }
}
