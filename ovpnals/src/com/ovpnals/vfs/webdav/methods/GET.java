/* ========================================================================== *
 * Copyright (C) 2004-2005 Pier Fumagalli <http://www.betaversion.org/~pier/> *
 *                            All rights reserved.                            *
 * ========================================================================== *
 *                                                                            *
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may *
 * not use this file except in compliance with the License.  You may obtain a *
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.       *
 *                                                                            *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software *
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT *
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the *
 * License for the  specific language  governing permissions  and limitations *
 * under the License.                                                         *
 *                                                                            *
 * ========================================================================== */
package com.ovpnals.vfs.webdav.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.RandomAccessContent;
import org.apache.commons.vfs.util.RandomAccessMode;

import com.ovpnals.boot.SystemProperties;
import com.ovpnals.boot.Util;
import com.ovpnals.vfs.VFSLockManager;
import com.ovpnals.vfs.VFSResource;
import com.ovpnals.vfs.webdav.DAVTransaction;
import com.ovpnals.vfs.webdav.LockedException;

/**
 * <p>
 * <a href="http://www.rfc-editor.org/rfc/rfc2616.txt">HTTP</a>
 * <code>GET</code> metohd implementation.
 * </p>
 * 
 * @author <a href="/">Pier Fumagalli</a>
 */
public class GET extends HEAD {

	static Log log = LogFactory.getLog(GET.class);
	
    /**
     * <p>
     * The mime type this method will return for collections.
     * </p>
     */
    public static final String COLLECTION_MIME_TYPE = "text/html";

    /**
     * <p>
     * Create a new {@link GET} instance.
     * </p>
     */
    public GET() {
        super();
    }

    /**
     * <p>
     * Process the <code>GET</code> method.
     * </p>
     */
    public void process(DAVTransaction transaction, VFSResource resource) throws LockedException, IOException {
        
    	
    	String handle = VFSLockManager.getNewHandle();
    	VFSLockManager.getInstance().lock(resource, transaction.getSessionInfo(), false, true, handle);
    	
    	try {
	    	doHead(transaction, resource);
	
	        try {
	            if (resource.isCollection() || resource.isMount()) {
	            	
	            	if(SystemProperties.get("ovpnals.disableFolderBrowsing", "true").equals("true") && !resource.isBrowsable()) {
	            		transaction.getResponse().sendError(404);
	            		return;
	            	}
	            	
	                String mime = COLLECTION_MIME_TYPE + "; charset=\"utf-8\"";
	                transaction.setContentType(mime);
	
	                Util.noCache(transaction.getResponse());
	                
	                PrintWriter out = transaction.write("utf-8");
	                String path = resource.getFullPath();
	                out.println("<html>");
	                out.println("<head>");
	                out.println("<title>Collection: " + path + "</title>");
	                out.println("</head>");
	                out.println("<body>");
	                out.println("<h2>Collection: " + path + "</h2>");
	                out.println("<table>");
	                out.println("<thead>");
	                out.println("<tr>");
	                out.println("<td>Name</td>");
	                out.println("<td>Type</td>");
	                out.println("<td>Size</td>");
	                out.println("<td>Date</td>");
	                out.println("</tr>");
	                out.println("</thead>");
	                out.println("<tbody>");
	
	                /* Process the parent */
	                VFSResource parent = resource.getParent();
	                if (parent != null && (parent.isBrowsable() || !SystemProperties.get("ovpnals.disableFolderBrowsing", "true").equals("true"))) {
		                out.println("<tr>");
	                    out.print("<td><li><a href=\"..\">../</a></td>");
		                out.println("<td>Dir</td><td>");
		                try {
		                	out.println(parent.getFile().getContent().getSize());
		                }
		                catch(Exception e) {		                	
		                }
		                out.println("</td><td>");
		                try {
		                	out.println(SimpleDateFormat.getDateTimeInstance().format(new Date(parent.getFile().getContent().getLastModifiedTime())));
		                }
		                catch(Exception e) {		                	
		                }
		                out.println("</td></tr>");
	                }
	
	                /* Process the children */
	                Iterator iterator = resource.getChildren();
	                if (iterator != null) {
	                    while (iterator.hasNext()) {
	                        VFSResource child = (VFSResource) iterator.next();
	                        String childPath = child.getDisplayName();

			                out.println("<tr>");
		                    out.print("<td><li><a href=\"" + child.getWebFolderPath() + "\">"  + childPath +  "</a></td>");
		                    if(child.isCollection())
		                    	out.println("<td>Dir</td><td>");
		                    else if(child.isResource())
		                    	out.println("<td>Resource</td><td>");
		                    else
		                    	out.println("<td>Unknown</td><td>");
			                try {
			                	out.println(child.getFile().getContent().getSize());
			                }
			                catch(Exception e) {		                	
			                }
			                out.println("</td><td>");
			                try {
			                	out.println(SimpleDateFormat.getDateTimeInstance().format(new Date(child.getFile().getContent().getLastModifiedTime())));
			                }
			                catch(Exception e) {		                	
			                }
			                out.println("</td></tr>");
			                out.println("</tr>");
	                    }
	                }	
	                out.println("</tbody>");
	                out.println("</table>");
	                out.println("</html>");
	                out.flush();
		        	if(resource.getMount() != null) {
		        		resource.getMount().resourceAccessList(resource, transaction, null);
		        	}
	                return;
	            }
	        } catch (Exception e) {
	        	if(resource.getMount() != null) {
	        		resource.getMount().resourceAccessList(resource, transaction, e);
	        	}
	            IOException ioe = new IOException(e.getMessage());
	            ioe.initCause(e);
	            throw ioe;
	        }
	
	        int total = 0;
	        try {
	
	        	Range[] ranges = null;
	        	
	        	try {
	        		ranges = processRangeHeader(transaction, resource);
	        	} catch(IOException ex) { 
	        		// Invalid range means send full entity with 200 OK.
	        		ranges = null;
	        	}
	        	
	            transaction.setHeader("Content-Type", resource.getContentType());

                resource.getMount().resourceAccessDownloading(resource, transaction);
	
	            OutputStream out = transaction.getOutputStream();
	            byte buffer[] = new byte[32768];

            	InputStream in = null;
            	
            	try {
	            
		            if(ranges == null || ranges.length > 1 /* We dont support multiple ranges yet */) {
			            /* Processing a normal resource request */
			            in = resource.getInputStream();
			            int k;
			            while ((k = in.read(buffer)) != -1) {
			                out.write(buffer, 0, k);
			                total += k;
			            }
		            } else {
		            	/* Process a single range */
		            	
		            	RandomAccessContent content = resource.getFile().getContent().getRandomAccessContent(RandomAccessMode.READ);
		
		        	    content.seek(ranges[0].startPosition);
		        	    in = content.getInputStream();
		        		
		        	    long count = ranges[0].count;
		        	    int k;
		        	    while(count > 0) {
		    	            while ((k = in.read(buffer, 0, (int) (count < buffer.length ? count : buffer.length))) > 0) {
		    	                out.write(buffer, 0, k);
		    	                total += k;
		    	                count -= k;
		    	            }
		        	    }
		            		
		            	transaction.setHeader("Content-Range", "bytes " + ranges[0].startPosition 
		            			+ "-" + ranges[0].startPosition 
		            			+ ranges[0].count + "/" + resource.getFile().getContent().getSize());
		            	
		            	transaction.setStatus(206 /* Partial */);
		            	
		            }
            	}
            	finally {
            		Util.closeStream(in);
            	}
                resource.getMount().resourceAccessDownloadComplete(resource, transaction, null);
	        } catch (IOException ioe) {
                resource.getMount().resourceAccessDownloadComplete(resource, transaction, ioe);
	            throw ioe;
	        }
        } finally {
        	VFSLockManager.getInstance().unlock(transaction.getSessionInfo(), handle);
        }

    }
    
    private Range[] processRangeHeader(DAVTransaction transaction, VFSResource resource) throws IOException {
    	
    	try {
			if(transaction.getRequest().getHeader("Range")!=null) {
				
				String header = transaction.getRequest().getHeader("Range").toLowerCase();
				
				if(header.startsWith("bytes=")) {
					
					Vector v = new Vector();
					
					StringTokenizer tokens = new StringTokenizer(header.substring(6), ",");
					while(tokens.hasMoreTokens()) {
						String r = tokens.nextToken();

						if(log.isDebugEnabled())
							log.debug("Processing byte range " + r);
						
						int idx = r.indexOf('-');
						
						String startPoint = r.substring(0, idx);
						String endPoint = r.substring(idx+1);
						
						Range newRange = new Range();
						
						if("".equals(startPoint) && !"".equals(endPoint)) {
							
							newRange.count = Long.parseLong(endPoint);
							newRange.startPosition = resource.getFile().getContent().getSize() - newRange.count;
							v.add(newRange);
							
						} else if(!"".equals(startPoint) && "".equals(endPoint)) {
							
							newRange.startPosition = Long.parseLong(startPoint);
							newRange.count = resource.getFile().getContent().getSize() - newRange.startPosition;
                            v.add(newRange);
							
						} else if(!"".equals(startPoint) && !"".equals(endPoint)) {
							
							newRange.startPosition = Long.parseLong(startPoint);
							newRange.count = Long.parseLong(endPoint) - newRange.startPosition;
                            v.add(newRange);
							
						} else {
							log.error("Unsupported byte range element: " + r);
						}
					}
					
					if(v.size() > 0) {
						return (Range[]) v.toArray(new Range[0]);
					}
				}
				
			}
		} catch (Throwable t) {
			log.error("Failed to process byte range header " + transaction.getRequest().getHeader("Range"), t);
			throw new IOException("Invalid range");
		}
    	
    	return null;
    }
    
    
    class Range {
    	
    	long startPosition;
    	long count;
    }
}
