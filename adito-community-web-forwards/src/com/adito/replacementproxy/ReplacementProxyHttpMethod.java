package com.adito.replacementproxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.http.HttpConnection;
import com.maverick.http.HttpMethod;
import com.maverick.http.HttpRequest;
import com.maverick.http.HttpResponse;
import com.maverick.util.IOUtil;
import com.adito.boot.HttpConstants;
import com.adito.core.MultiMap;
import com.adito.core.RequestParameterMap;

/*
 * NOT YET COMPLETE. UNUSED
 */

public class ReplacementProxyHttpMethod extends HttpMethod {

    final static Log log = LogFactory.getLog(ReplacementProxyHttpMethod.class);

    private RequestParameterMap requestParameterMap;
    private InputStream requestInputStream;
    private String contentType;
    private long contentLength;
    private InputStream content;

    public ReplacementProxyHttpMethod(String name, RequestProcessor requestProcessor) throws Exception {
        this(name, requestProcessor, requestProcessor.getRequestParameters());
    }

    public ReplacementProxyHttpMethod(String name, RequestProcessor requestProcessor, MultiMap parameters) throws Exception {
        super(name, requestProcessor.getRequestParameters().getProxiedURIDetails().getProcessedRequestURI(requestProcessor.getSessionInfo()));
        this.requestParameterMap = requestProcessor.getRequestParameters();
        this.requestInputStream = requestProcessor.getRequest().getInputStream();
        determineContent();
    }

    public HttpResponse execute(HttpRequest request, HttpConnection connection) throws IOException {
        configureContent(request);

        // Execute the request
        if (log.isDebugEnabled())
            log.debug("Connecting to " + connection.getHost() + ":" + connection.getPort() + " (Secure = " + connection.isSecure() + ")");

        request.performRequest(this, connection);
        
        // If the request is multipart/form-data then copy the streams now
        if (content != null) {
            
            sendContent(connection, contentLength, content);
            
            if(log.isDebugEnabled())
                log.debug("Completed sending request content");
        }

        return new HttpResponse(connection);
    }
    
    void configureContent(HttpRequest request) {
        if(contentLength != -1) {
            request.setHeaderField(HttpConstants.HDR_CONTENT_LENGTH, String.valueOf(contentLength));
        }
        if(contentType != null) {
            request.setHeaderField(HttpConstants.HDR_CONTENT_TYPE, contentType);
        }
    }

    void determineContent() throws IOException, FileNotFoundException, UnsupportedEncodingException {
        contentLength = requestParameterMap.getOriginalContentLength();
        contentType = requestParameterMap.getOriginalContentType();
        if(requestParameterMap.isMultipart() && requestParameterMap.getMultipartDataLength()  > 0) {
            contentLength = requestParameterMap.getMultipartDataLength(); 
            content = getDebugStream(requestParameterMap.getMultipartData());
        }
        else if(requestParameterMap.isWwwFormURLEncoded()) {
            String encoded = requestParameterMap.getParametersAsEncodedString();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(baos);
            pw.print(encoded);
            pw.flush();
            content = getDebugStream(new ByteArrayInputStream(baos.toByteArray()));
            contentLength = baos.toByteArray().length;
            contentType = "application/x-www-form-urlencoded";
            
        }
        else if(contentLength > 0) {
            content = getDebugStream(requestInputStream);
        }
    }

    protected void sendContent(HttpConnection connection, long contentLength, InputStream content) throws IOException {
        if (log.isDebugEnabled())
            log.debug("Sending " + contentLength + " bytes of content");

        int read;
        byte[] buf = new byte[4096];
        long total = 0;

        do {
            read = content.read(buf, 0, (int) Math.min(buf.length, contentLength - total));

            if (log.isDebugEnabled())
                log.debug("Sent " + read + " bytes of content");
            if (read > -1) {
                total += read;
                connection.getOutputStream().write(buf, 0, read);
                connection.getOutputStream().flush();
            }
        } while (read > -1 && (contentLength - total) > 0);
    }

    InputStream getDebugStream(InputStream in) throws IOException {
        if (log.isDebugEnabled()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtil.copy(in, baos);
            byte[] buf = baos.toByteArray();
            log.debug("Sending content :-\n" + new String(buf));
            return new ByteArrayInputStream(buf);
        }
        return in;
    }

}
