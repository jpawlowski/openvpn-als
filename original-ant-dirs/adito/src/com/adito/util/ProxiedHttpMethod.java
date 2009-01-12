
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
			
package com.adito.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.http.HttpConnection;
import com.maverick.http.HttpMethod;
import com.maverick.http.HttpRequest;
import com.maverick.http.HttpResponse;
import com.maverick.util.URLUTF8Encoder;
import com.adito.boot.SystemProperties;
import com.adito.boot.Util;
import com.adito.core.MultiMap;
import com.adito.policyframework.LaunchSession;
import com.adito.security.SessionInfo;

public class ProxiedHttpMethod extends HttpMethod {

    private BufferedInputStream content;
    private long contentLength = 0;
    private HttpRequest proxiedHeaders = new HttpRequest();
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private String charsetEncoding = null;
    private boolean wwwURLEncodedParameters;
    private int bufferSize = 0;
    private MultiMap uriParameters;
    private boolean sendOriginalURI = false;
    
    static Log log = LogFactory.getLog(ProxiedHttpMethod.class);
    /**
     * @param name
     * @param uri
     * @param parameters
     * @param session
     * @param i
     * @param string
     * @param multiPartForm
     */
    public ProxiedHttpMethod(String name, String path, MultiMap parameters, SessionInfo session, boolean wwwURLEncodedParameters) { // ,

        super(name, path);
        
        this.wwwURLEncodedParameters = wwwURLEncodedParameters;
        String key;
        Object val;
        
        uriParameters = getURIParameters();
        
//		Make sure the launch ID parameter is not passed on
        uriParameters.remove(LaunchSession.LONG_LAUNCH_ID);
        parameters.remove(LaunchSession.LONG_LAUNCH_ID);
        
        for(Iterator it = parameters.keySet().iterator();it.hasNext();) {

            key = (String) it.next();

            List values = parameters.getValues(key);
            List uriValues = uriParameters.getValues(key);
            
        	for(Iterator it2 = values.iterator(); it2.hasNext();) {
        		
	            val = it2.next();
	
	            if(val instanceof String) {
	                val = (String) val;
		            if(uriValues!=null && uriValues.contains(val))
		            	continue;
	            	addParameter(key, (String)val);
	            } else if(val instanceof List) {
	                // Multiple parameter values.
	                List l = (List)val;
	                for(Iterator it3 = l.iterator(); it3.hasNext();) {
	                	String tmp = (String) it3.next();
	    	            if(uriValues!=null && uriValues.contains(tmp))
	    	            	continue;
	                    addParameter(key, tmp);
	                }
	            }
	            else if(uriValues!=null && val instanceof String[]) {
	                String[] tmp = (String[]) val;
	                for(int i=0;i<tmp.length;i++) {
	    	            if(uriValues.contains(tmp[i]))
	    	            	continue;
	                    addParameter(key, tmp[i]);
	                }
	            }
        	}
        }
    }
    
    public void setCharsetEncoding(String charsetEncoding) {
        this.charsetEncoding = charsetEncoding;
    }

    public OutputStream getOutputStream() {
        return out;
    }

    public void setContent(InputStream content, long contentLength, String contentType) {
    	this.content = new BufferedInputStream(content, bufferSize == 0 ? (int)contentLength : bufferSize);
    	this.contentLength = contentLength;
        if(contentType!=null)
        	proxiedHeaders.setHeaderField("Content-Type", contentType);
        proxiedHeaders.setHeaderField("Content-Length", String.valueOf(contentLength));
    }
    
    public void setContentType(String contentType) {
    	if(contentType!=null)
    		proxiedHeaders.setHeaderField("Content-Type", contentType);
    }

    public void setContent(InputStream content, long contentLength) {
        setContent(content, contentLength, null);
    }

    public HttpRequest getProxiedRequest() {
        return proxiedHeaders;
    }

    public HttpResponse execute(HttpRequest request, HttpConnection connection) throws IOException {

        String encodedContent = "";
        /**
         * Encode parameters into content if application/x-www-form-urlencoded
         */
        if (wwwURLEncodedParameters) {
            String key;
            String value;
            Vector v;
            for (Enumeration e = getParameterNames(); e.hasMoreElements(); ) {

                key = (String) e.nextElement();
                v = getParameterValueList(key);

                for (Iterator it2 = v.iterator(); it2.hasNext();) {
                    value = (String) it2.next();
                    encodedContent += (encodedContent.length() > 0 ? "&" : "") + Util.urlEncode(key, (charsetEncoding==null ? SystemProperties.get("adito.urlencoding", "UTF-8") : charsetEncoding)) + "="
                    + Util.urlEncode(value, (charsetEncoding==null ? SystemProperties.get("adito.urlencoding", "UTF-8") : charsetEncoding));
                }
            }

            if(encodedContent.length() > 0) {
	            if(charsetEncoding==null) {
	            	ByteArrayInputStream in = new ByteArrayInputStream(encodedContent.getBytes());
	            	setContent(in, in.available(), "application/x-www-form-urlencoded");
	            } else {
	            	ByteArrayInputStream in = new ByteArrayInputStream(encodedContent.getBytes(charsetEncoding));
	            	setContent(in, in.available(), "application/x-www-form-urlencoded");
	            }
            }
        }

        // Setup all the proxied headers
        for (Enumeration e = proxiedHeaders.getHeaderFieldNames(); e.hasMoreElements();) {
            String header = (String) e.nextElement();
            if (header.equalsIgnoreCase("Authorization"))
                if (request.getHeaderField("Authorization") != null)
                    continue;
            String[] values = proxiedHeaders.getHeaderFields(header);
            for (int i = 0; i < values.length; i++) {
                request.addHeaderField(header, values[i]);
            }
        }

        request.performRequest(this, connection);
        
        // If the request is multipart/form-data then copy the streams now
        if (content != null) {
        	
        	if(log.isDebugEnabled())
        		log.debug("Sending " + contentLength + " bytes of content");
        	
            content.mark(bufferSize);
            
            try {
	        	int read;
	        	byte[] buf = new byte[4096];
	        	long total = 0;
	        	
	        	do {
	        		read = content.read(buf, 0, (int) Math.min(buf.length, contentLength - total));
	        		
	        		if(log.isDebugEnabled())
	        			log.debug("Sent " + read + " bytes of content");
	        		if(read > -1) {
		        		total += read;
		        		connection.getOutputStream().write(buf, 0, read);
		        		connection.getOutputStream().flush();
	        		}
	        	} while(read > -1 && (contentLength - total) > 0);
	        
            } finally {
            	content.reset();
            }
        	
        	if(log.isDebugEnabled())
        		log.debug("Completed sending request content");
        }

        return new HttpResponse(connection);
    }
    
    public String getDecodedURI() {
    	return URLUTF8Encoder.decode(super.getURI());
    }
    
    public String getOriginalURIParameters() {
    	int idx = super.getURI().indexOf('?');
		if(idx > -1) {
			return super.getURI().substring(idx+1);
		} else {
			return "";	
		}
    }
    
    
    public MultiMap getURIParameters() {
    	MultiMap output = new MultiMap();
    	StringTokenizer tokens = new StringTokenizer(getOriginalURIParameters(), "&");
    	int idx;
    	String name;
    	while(tokens.hasMoreTokens()) {
    		name = tokens.nextToken();
    		idx = name.indexOf('=');
    		if(idx > -1) {
   				output.add(URLUTF8Encoder.decode(name.substring(0, idx)), URLUTF8Encoder.decode(name.substring(idx+1)));
    		} else {
   				output.add(URLUTF8Encoder.decode(name), "");
    		}
    	}
    	return output;
    }
    
    public String getEncodedURIParameters() {
    	String encodedParams = "";
    	String key;
    	String val;
        for(Iterator it = uriParameters.keySet().iterator(); it.hasNext();) {
            key = (String) it.next();
	        	for(Iterator it2 = uriParameters.getValues(key).iterator(); it2.hasNext();) {
	        		val = (String) it2.next();
	        		if(val.length()==0)
	        			encodedParams += (encodedParams.equals("") ? "" : "&") + URLUTF8Encoder.encode(key, true);
	        		else
	        			encodedParams += (encodedParams.equals("") ? "" : "&") + URLUTF8Encoder.encode(key, true) + "=" + URLUTF8Encoder.encode(val, true);
	        	}
        }
    	return encodedParams;
    }
    
    public String getDecodedURIPath() {
    	int idx = super.getURI().indexOf('?');
		if(idx > -1) {
			return URLUTF8Encoder.decode(super.getURI().substring(0, idx));
		} else {
			return URLUTF8Encoder.decode(super.getURI());	
		}
    }
    
    public String getEncodedURIPath() {
    	return URLUTF8Encoder.encode(getDecodedURIPath(), false);
    }    
    
    public String getURI() {

    	/**
    	 * LDP - I've added this option because sometimes clients/servers send
    	 * bad URIs that do not conform to the specification (Outlook RPC/IIS for example). This
    	 * allows us to bypass the encoding and simply send the URI as it was received.
    	 */
    	if(sendOriginalURI)
    		return super.getURI();
    	
    	/**
    	 * LDP - Jetty decodes POST application/x-www-form-urlencoded parameters so we don't add them 
    	 * onto the URI. If we have parameters and the method IS NOT POST and content type IS NOT
    	 * application/x-www-form-urlencoded then add them to the URI as its the only place they 
    	 * could have come from. 
    	 */
    	String uri = getEncodedURIPath();
    	String params = getEncodedURIParameters();
    	
    	if(!params.equals(""))
    		uri += "?" + params;

    	return uri;
    }

	public void setContentBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setSendOriginalURI(boolean sendOriginalURI) {
		this.sendOriginalURI = sendOriginalURI;
	}    
}
