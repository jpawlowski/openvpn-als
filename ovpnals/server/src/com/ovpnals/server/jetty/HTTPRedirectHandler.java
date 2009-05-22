package com.ovpnals.server.jetty;

import java.io.IOException;
import java.util.ArrayList;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpHandler;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;

import com.ovpnals.boot.ContextHolder;
import com.ovpnals.boot.RequestHandler;
import com.ovpnals.boot.RequestHandlerException;
import com.ovpnals.boot.SystemProperties;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 */
public class HTTPRedirectHandler implements HttpHandler {

    HttpContext context;
    
    static ArrayList<RequestHandler> handlers = new ArrayList<RequestHandler>();
    
    
    public static void registerHandler(RequestHandler handler) {
    	handlers.add(handler);
    }
    
    public static void unregisterHandler(RequestHandler handler) {
    	handlers.remove(handler);
    }
    /**
     * Constructor
     */
    public HTTPRedirectHandler() {
    }

    /* (non-Javadoc)
     * @see org.mortbay.http.HttpHandler#initialize(org.mortbay.http.HttpContext)
     */
    public void initialize(HttpContext context) {
        this.context = context;
    }

    /* (non-Javadoc)
     * @see org.mortbay.util.LifeCycle#isStarted()
     */
    public boolean isStarted() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.mortbay.util.LifeCycle#stop()
     */
    public void stop() {

    }

    /* (non-Javadoc)
     * @see org.mortbay.util.LifeCycle#start()
     */
    public void start() {

    }
    
    /* (non-Javadoc)
     * @see org.mortbay.http.HttpHandler#getName()
     */
    public String getName() {
        return "SECURE";
    }
    
    /* (non-Javadoc)
     * @see org.mortbay.http.HttpHandler#getHttpContext()
     */
    public HttpContext getHttpContext() {
        return context;
    }
    
    /* (non-Javadoc)
     * @see org.mortbay.http.HttpHandler#handle(java.lang.String, java.lang.String, org.mortbay.http.HttpRequest, org.mortbay.http.HttpResponse)
     */
    public void handle(String pathInContext,
                       String str,
                           HttpRequest request,
                           HttpResponse response) throws IOException {
        handle(pathInContext, request, response);
    }
    /**
     *
     * @param pathInContext String
     * @param request RequestHandlerRequest
     * @param response RequestHandlerResponse
     * @throws IOException
     * @todo Implement this com.ovpnals.boot.RequestHandler method
     */
    public void handle(String pathInContext,
                       HttpRequest request,
                       HttpResponse response) throws IOException {

    	for(RequestHandler handler : handlers) {
    		try {
                request.setCharacterEncoding(SystemProperties.get("ovpnals.encoding", "UTF-8"), false);
                 if (handler.handle(pathInContext, "", new RequestAdapter(request), new ResponseAdapter(response))) {
                    request.setHandled(true);
                    return;
                }
            } catch (RequestHandlerException e) {
                throw new HttpException(e.getCode(), e.getMessage());
            }
            
    	}
    	
        if(!request.isConfidential()) {
            int sslPort = ContextHolder.getContext().getPort();
            response.sendRedirect("https://" + request.getHost() + (sslPort > 0 && sslPort!=443 ? ":" + sslPort : "") + request.getEncodedPath());
            request.setHandled(true);
        }
    }
}
