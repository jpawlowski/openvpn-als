package com.adito.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.HttpConstants;
import com.adito.boot.Util;
import com.adito.vfs.webdav.DAVUtilities;

public class StaticResourceServlet extends HttpServlet {
	private static final String JAVAX_SERVLET_INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";

	private static Log log = LogFactory.getLog(StaticResourceServlet.class);

	/* ------------------------------------------------------------ */
	public void init() throws UnavailableException {
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        if(response instanceof GZIPResponseWrapper) {
//            ((GZIPResponseWrapper)response).setCompress(false);
//        }
        
		// Find the resource
		String servletPath = (String) request.getAttribute(JAVAX_SERVLET_INCLUDE_REQUEST_URI);
		String pathInfo = null;
		boolean include = false;
		if (servletPath == null) {
			servletPath = request.getServletPath();
			pathInfo = request.getPathInfo();
		} else {
			pathInfo = (String) request.getAttribute("javax.servlet.include.path_info");
			include = true;
		}

		String pathInContext = DAVUtilities.concatenatePaths(servletPath, pathInfo);
		if(pathInContext.equals("/")) {
		    response.sendRedirect(request.getContextPath() + "/showHome.do");
		    return;
		}
		boolean endsWithSlash = pathInContext.endsWith("/");

		URL res = getServletContext().getResource(pathInContext);
		if (res == null) {
			response.sendError(HttpConstants.RESP_404_NOT_FOUND);
			return;
		}

		String method = request.getMethod();
		if (!method.equalsIgnoreCase("get")) {
			response.setHeader(HttpConstants.HDR_ALLOW, "GET");
			response.sendError(HttpConstants.RESP_405_METHOD_NOT_ALLOWED);
			return;
		}

		try {
			handleGet(request, response, pathInContext, res, endsWithSlash, include);
		} catch (URISyntaxException e) {
			throw new ServletException(e);
		}

	}

	public void handleGet(HttpServletRequest request, HttpServletResponse response, String pathInContext, URL resource,
							boolean endsWithSlash, boolean include) throws ServletException, IOException, URISyntaxException {

		// check if directory
		if (resource.getProtocol().equals("file")) {
			File f = new File(resource.toURI());
			if (f.isDirectory()) {
				throw new ServletException("Directory listing not allowed");
			}
		}

		URLConnection conx = resource.openConnection();

		if (!passConditionalHeaders(request, response, conx, include))
			return;

		sendData(request, response, pathInContext, resource, conx, include);
	}

	protected boolean passConditionalHeaders(HttpServletRequest request, HttpServletResponse response, URLConnection connection,
												boolean include) throws IOException {
		if (request.getAttribute(JAVAX_SERVLET_INCLUDE_REQUEST_URI) == null) {

			long date = 0;

			if ((date = request.getDateHeader(HttpConstants.HDR_IF_UNMODIFIED_SINCE)) > 0) {
				if (connection.getLastModified() / 1000 > date / 1000) {
					response.sendError(HttpConstants.RESP_412_PRECONDITION_FAILED);
					return false;
				}
			}

			if ((date = request.getDateHeader(HttpConstants.HDR_IF_MODIFIED_SINCE)) > 0) {
				if (connection.getLastModified() / 1000 <= date / 1000) {
					response.reset();
					response.setStatus(HttpConstants.RESP_304_NOT_MODIFIED);
					response.flushBuffer();
					return false;
				}
			}
		}
		return true;
	}

	protected void sendData(HttpServletRequest request, HttpServletResponse response, String pathInContext, URL resource,
							URLConnection connection, boolean include) throws IOException {
		OutputStream out = response.getOutputStream();
		if (!include) {
			writeHeaders(response, connection, resource);
		}
		InputStream in = connection.getInputStream();
		try {
			Util.copy(in, out);
		} finally {
			in.close();
		}
		return;
	}

	protected void writeHeaders(HttpServletResponse response, URLConnection connection, URL resource) throws IOException {
		if (connection.getContentType() != null && !connection.getContentType().equals("content/unknown")) {
			response.setHeader(HttpConstants.HDR_CONTENT_TYPE, connection.getContentType());
		} else {
		    String contentType = getServletContext().getMimeType(DAVUtilities.basename(resource.getPath(), '/'));
		    response.setHeader(HttpConstants.HDR_CONTENT_TYPE, contentType == null ? "application/octet-stream" : contentType);
		}
		if (connection.getContentLength() != -1) {
			response.setHeader(HttpConstants.HDR_CONTENT_LENGTH, String.valueOf(connection.getContentLength()));
		}
		if (connection.getLastModified() != 0) {
			response.setDateHeader(HttpConstants.HDR_LAST_MODIFIED, connection.getLastModified());
		}
		response.setHeader(HttpConstants.HDR_CONNECTION, HttpConstants.HDR_KEEP_ALIVE);
	}

}
