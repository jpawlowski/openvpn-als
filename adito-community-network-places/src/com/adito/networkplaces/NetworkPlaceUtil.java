
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
			
package com.adito.networkplaces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.util.URLUTF8Encoder;
import com.adito.boot.ReplacementEngine;
import com.adito.boot.Replacer;
import com.adito.boot.Util;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.ResourceUtil;
import com.adito.security.SessionInfo;
import com.adito.vfs.VFSProvider;
import com.adito.vfs.VFSProviderManager;
import com.adito.vfs.VFSRepository;
import com.adito.vfs.VFSStore;
import com.adito.vfs.utils.URI;
import com.adito.vfs.utils.URI.MalformedURIException;
import com.adito.vfs.webdav.DAVBundleActionMessageException;
import com.adito.vfs.webdav.DAVUtilities;

public final class NetworkPlaceUtil {
	
	final static Log log = LogFactory.getLog(NetworkPlaceUtil.class);

	/**
	 * Create a valid VFS URI from a path. 
	 * 
	 * @param path path
	 * @return URI
	 * @throws IllegalArgumentException if URI cannot be create 
	 */
	public static URI createURIForPath(String path) { 		
		NetworkPlace np = createNetworkPlaceForPath(path);
		
		try {
			String userInfo = URLUTF8Encoder.encode(np.getUsername(), false);
			if(!Util.isNullOrTrimmedBlank(np.getPassword())) {
				userInfo = URLUTF8Encoder.encode(np.getUsername(), false);
			}
    		URI uri = new URI(np.getScheme(), userInfo, np.getHost(), np.getPort(), np.getPath(), null, null);
    		return uri;
		}
		catch(Exception e) {
			throw new IllegalArgumentException("Could not create URI.");
		}
	}
	
	/**
	 * Create a network place object given a path.
	 * 
	 * @param path
	 * @return network place
	 * @throws IllegalArgumentException if resource cannot be created
	 * 
	 */
	public static NetworkPlace createNetworkPlaceForPath(String path) {
		NetworkPlace np = null;
		

		try {		
			np = new DefaultNetworkPlace(1, 0, 
					"", "", "", "", path, 0, "", "", NetworkPlace.TYPE_HIDDEN, false, false, false, false, false, null, null);			
    		NetworkPlaceUtil.convertNetworkPlace(np);
		}
		catch(Exception e) {
			log.error(e);
			throw new IllegalArgumentException("Could not convert path to network place.");
		}

		VFSProvider provider = VFSProviderManager.getInstance().getProvider(np.getScheme());
		if(provider == null) {
			throw new IllegalArgumentException("No provider " + np.getScheme());
		}
		
		return np;
		
	}
	

    /**
     * Get a list of {@link NetworkPlaceItem} objects that may be used by the user
     * of the given session.
     * 
     * @param session
     * @return network place items
     * @throws DAVBundleActionMessageException
     * @throws Exception
     */
    public static List<NetworkPlaceItem> refreshNetworkMounts(VFSRepository repository, SessionInfo session) throws DAVBundleActionMessageException, Exception {
        List<NetworkPlaceItem> networkPlaceItems = new ArrayList<NetworkPlaceItem>();
        List granted =   ResourceUtil.getGrantedResource(session, NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE);
        for (Iterator i = granted.iterator(); i.hasNext();) {
            NetworkPlace np = (NetworkPlace) i.next();
            try {
                VFSProvider provider = VFSProviderManager.getInstance().getProvider(np.getScheme());
                if (provider == null) {
                	if(np.getScheme().equals("")) {
            			URI uri = createURIForPath(np.getPath());
            			provider = VFSProviderManager.getInstance().getProvider(uri.getScheme());
                	}
                	if(provider == null)
                		throw new Exception("No provider for network place URI " + np.getPath());
                }
                if (np.getType() != NetworkPlace.TYPE_HIDDEN) {
                	// Create a store so we can get the mount path
                	VFSStore store = repository.getStore(provider.getScheme());
                    NetworkPlaceItem npi = new NetworkPlaceItem(np, store.getMountPath(np.getResourceName()), PolicyDatabaseFactory.getInstance()
                                    .getPoliciesAttachedToResource(np, session.getUser().getRealm()), np
                                    .sessionPasswordRequired(session));
                    networkPlaceItems.add(npi);
                }
            } catch (Exception e) {
                log.warn("Failed to register " + np.getResourceName() + " with store.", e);
            }
        }
        return networkPlaceItems;
    }


	/**
	 * Convert a network place that only has a path into a network
	 * place with separate elements. Used by the install task
	 * and when creating a network place using the <i>URI</i> type 
	 * 
	 * @param networkPlace
	 * @throws Exception
	 */
	public static void convertNetworkPlace(NetworkPlace networkPlace) throws Exception {
		if(Util.isNullOrTrimmedBlank(networkPlace.getScheme())) {
			String path = networkPlace.getPath();
			
			Replacer r = new Replacer() {
				public String getReplacement(Pattern pattern, Matcher matcher, String replacementPattern) {
	
			        String match = matcher.group();
			        String key = match.substring(2, match.length() - 1);
			        try {
			            int idx = key.indexOf(":");
			            if (idx == -1) {
			                throw new Exception("String replacement pattern is in incorrect format for " + key
			                    + ". Must be <TYPE>:<key>");
			            }
			            String type = key.substring(0, idx);
			            key = key.substring(idx + 1);
			            return "_prototype_" + type + "_" + key + "_";
			        }
			        catch(Exception e) {
			        	NetworkPlaceInstall.log.error("Failed to replace.", e);
			        }
			        return "prototype";
				}
				
			};
			
			// Replace all replacement variables with prototype values
	        ReplacementEngine engine = new ReplacementEngine();
	        engine.addPattern(NetworkPlaceInstall.VARIABLE_PATTERN, r, null);
	        path = engine.replace(path);
	        
	        // 
	        URI newUri = null;
			
			// Is it a UNC path
			if(path.startsWith("\\\\")) {
				try {
					newUri = new URI("smb:" + path.replace('\\', '/'));
				}
				catch(MalformedURIException murie) {
					murie.printStackTrace();
				}
			}
			
			// Is this a supported RI?
			if(newUri == null) {
				int idx = path.indexOf(':');
				if(idx > 1) { // index of 1 would mean a windows absolute path						
					// Is it an non windows absolute file URI?
					String rpath = path.substring(idx + 1);
					String scheme = path.substring(0, idx);
					if(scheme.equals("file")) {
						if(rpath.startsWith("//") && !rpath.startsWith("///") &&
										( rpath.length() < 4 || rpath.charAt(3) != ':' ) ) {
							path = scheme + ":/" + rpath;
						}
					}
					
					newUri = new URI(path);					
				}
			}
			
			// Is it a local file? (wont work for replacements)
			boolean switchSlash = false;
			if(newUri == null) {		
	    		if(path.contains("\\")) {
	    			switchSlash = true;
	    		}
	    		try {
                    String scheme;
                    if(path.toLowerCase().endsWith(".jar")) {
                        scheme = "jar";
                    } else if(path.toLowerCase().endsWith(".zip")) {
                        scheme = "zip";
                    } else {
                        scheme = "file";
                    }
					newUri = new URI(scheme + ":///" + DAVUtilities.stripLeadingSlash(path.replace('\\', '/')));
				} catch (MalformedURIException e) {
					e.printStackTrace();
				}
			}
			
			// Convert the network place if required
			if(newUri != null) {
		        engine = new ReplacementEngine();
	
				
				r = new Replacer() {
					public String getReplacement(Pattern pattern, Matcher matcher, String replacementPattern) {
				        String match = matcher.group();
				        String key = match.substring(11, match.length() - 1);
				        try {
				            int idx = key.indexOf("_");
				            if (idx == -1) {
				                throw new Exception("String replacement pattern is in incorrect format for " + key
				                    + ". Must be <TYPE>:<key>");
				            }
				            String type = key.substring(0, idx);
				            key = key.substring(idx + 1);
				            return "${" + type + ":" + key + "}";
				        }
				        catch(Exception e) {
				        	NetworkPlaceInstall.log.error("Failed to replace.", e);
				        }
				        return "prototype";
					}
					
				};
		        engine.addPattern(NetworkPlaceInstall.PROTOTYPE_PATTERN, r, null);
		        String newScheme = newUri.getScheme();
		        String newHost = newUri.getHost();
		        if(!Util.isNullOrTrimmedBlank(newHost)) {
		        	newHost = engine.replace(newHost);
		        }
		        int newPort = Math.max(newUri.getPort(), 0);
		        String newPath = newUri.getPath();
		        if(!Util.isNullOrTrimmedBlank(newPath)) {
		        	newPath = Util.urlDecode(engine.replace(newPath));
		        }
		        if(newPath.startsWith("/") && newPath.length() > 2 && newPath.charAt(2) == ':') {
		        	newPath = newPath.substring(1);
		        }
		        if(switchSlash) {
		        	newPath = newPath.replace('/', '\\');
		        }
		        String newFragment = newUri.getFragment();
		        if(!Util.isNullOrTrimmedBlank(newFragment)) {
		        	newFragment = engine.replace(newFragment);
		        }
		        String newUserinfo = newUri.getUserinfo();
				int idx = newUserinfo == null ? -1 : newUserinfo.indexOf(':');
				String newUsername = newUserinfo;
				String newPassword = "";
				if(idx != -1) {
					newPassword = newUsername.substring(idx + 1);
					newUsername = newUsername.substring(0, idx);
				}
		        if(!Util.isNullOrTrimmedBlank(newUsername)) {
		        	newUsername = Util.urlDecode(engine.replace(newUsername));
		        }
		        if(!Util.isNullOrTrimmedBlank(newPassword)) {
		        	newPassword = Util.urlDecode(engine.replace(newPassword));
		        }
				
				networkPlace.setScheme(Util.emptyWhenNull(newScheme));
				networkPlace.setHost(Util.emptyWhenNull(newHost));
				networkPlace.setPort(newPort);
				networkPlace.setPath(Util.emptyWhenNull(newPath));
				networkPlace.setUsername(Util.emptyWhenNull(newUsername));
				networkPlace.setPassword(Util.emptyWhenNull(newPassword));
	
				// networkPlace.setFragment(newFragment);
			}
		}	
	}
}
