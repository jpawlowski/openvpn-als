
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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.core.CoreEvent;
import com.adito.core.CoreServlet;
import com.adito.navigation.FavoriteResourceType;
import com.adito.navigation.WrappedFavoriteItem;
import com.adito.policyframework.DefaultResourceType;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.Policy;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceChangeEvent;
import com.adito.policyframework.ResourceDeleteEvent;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.vfs.VFSStore;
import com.adito.vfs.webdav.DAVProcessor;
import com.adito.vfs.webdav.DAVServlet;

/**
 * Implementation of a {@link com.adito.policyframework.ResourceType} for
 * <i>Network Place</i> resources.
 */
public class NetworkPlaceResourceType extends DefaultResourceType implements FavoriteResourceType {

    final static Log log = LogFactory.getLog(NetworkPlaceResourceType.class);

    /**
     * Constructor
     */
    public NetworkPlaceResourceType() {
        super(NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE_ID, "policyframework", PolicyConstants.DELEGATION_CLASS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.FavoriteResourceType#createWrappedFavoriteItem(int,
     *      javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    public WrappedFavoriteItem createWrappedFavoriteItem(int resourceId, HttpServletRequest request, String type) throws Exception {
        NetworkPlace np = NetworkPlaceDatabaseFactory.getInstance().getNetworkPlace(resourceId);
        NetworkPlaceItem npi;
        if (np != null) {
            DAVProcessor processor = DAVServlet.getDAVProcessor(request);
            SessionInfo sessionInfo = LogonControllerFactory.getInstance().getSessionInfo(request);
            VFSStore store = processor.getRepository().getStore(np.getScheme());
            if (store == null) {
                log.warn("Store that handles '" + np.getScheme() + "' cannot be found.");
            } else {
                npi = new NetworkPlaceItem(np, store.getMountPath(np.getResourceName()), PolicyDatabaseFactory.getInstance()
                                .getPoliciesAttachedToResource(np, sessionInfo.getUser().getRealm()), np
                                .sessionPasswordRequired(sessionInfo));
                return new WrappedFavoriteItem(npi, type);
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.FavoriteResourceType#getResourceById(int)
     */
    public Resource getResourceById(int resourceId) throws Exception {
        return NetworkPlaceDatabaseFactory.getInstance().getNetworkPlace(resourceId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.DefaultResourceType#getResourceByName(java.lang.String,
     *      com.adito.security.SessionInfo)
     */
    public Resource getResourceByName(String resourceName, SessionInfo session) throws Exception {
        return NetworkPlaceDatabaseFactory.getInstance().getNetworkPlace(resourceName, session.getUser().getRealm().getRealmID());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.boot.policyframework.ResourceType#removeResource(int,
     *      com.adito.security.SessionInfo)
     */
    public Resource removeResource(int resourceId, SessionInfo session) throws Exception {
        try {
            NetworkPlace np = NetworkPlaceDatabaseFactory.getInstance().deleteNetworkPlace(resourceId);
            CoreServlet.getServlet().fireCoreEvent(
                            new ResourceDeleteEvent(this, NetworkPlacesEventConstants.DELETE_NETWORK_PLACE, np, session,
                                            CoreEvent.STATE_SUCCESSFUL).addAttribute(NetworkPlacesEventConstants.EVENT_ATTR_VFS_URI,
                                            String.valueOf(np.getPath())));
            return np;
        } catch (Exception e) {
            CoreServlet.getServlet().fireCoreEvent(
                            new ResourceDeleteEvent(this, NetworkPlacesEventConstants.DELETE_NETWORK_PLACE, session, e));
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.boot.policyframework.ResourceType#updateResource(com.adito.boot.policyframework.Resource,
     *      com.adito.security.SessionInfo)
     */
    public void updateResource(Resource resource, SessionInfo session) throws Exception {
        try {
            NetworkPlace np = (NetworkPlace) resource;
            NetworkPlaceDatabaseFactory.getInstance().updateNetworkPlace(np.getResourceId(), np.getScheme(), np.getResourceName(),
                            np.getResourceDescription(), np.getHost(), np.getPath(), np.getPort(), np.getUsername(),
                            np.getPassword(), np.isReadOnly(), np.isAllowRecursive(), np.isNoDelete(), np.isShowHidden(), np.isAutoStart());
            CoreServlet.getServlet().fireCoreEvent(
                            NetworkPlaceResourceType.addNetworkPlaceAttributes(
                                            new ResourceChangeEvent(this, NetworkPlacesEventConstants.UPDATE_NETWORK_PLACE, resource,
                                                            session, CoreEvent.STATE_SUCCESSFUL), np));
        } catch (Exception e) {
            CoreServlet.getServlet().fireCoreEvent(
                            new ResourceChangeEvent(this, NetworkPlacesEventConstants.UPDATE_NETWORK_PLACE, session, e));
            throw e;
        }
    }

    /**
     * Add the common network place attributes to an event. These include <i>URI</i>,
     * <i>Allow Recursive</i>, <i>No Delete</i>, <i>Read Only</i> and <i>Show
     * Hidden</i>
     * 
     * @param evt event to add attributes to
     * @param networkPlace network place object to get event attributes from
     * @return event (same as evt)
     */
    public static CoreEvent addNetworkPlaceAttributes(CoreEvent evt, NetworkPlace networkPlace) {
        evt.addAttribute(NetworkPlacesEventConstants.EVENT_ATTR_VFS_URI, String.valueOf(networkPlace.getPath()));
        evt.addAttribute(NetworkPlacesEventConstants.EVENT_ATTR_VFS_ALLOW_RECURSIVE, String.valueOf(networkPlace.isAllowRecursive()));
        evt.addAttribute(NetworkPlacesEventConstants.EVENT_ATTR_VFS_NO_DELETE, String.valueOf(networkPlace.isNoDelete()));
        evt.addAttribute(NetworkPlacesEventConstants.EVENT_ATTR_VFS_READ_ONLY, String.valueOf(networkPlace.isReadOnly()));
        evt.addAttribute(NetworkPlacesEventConstants.EVENT_ATTR_VFS_SHOW_HIDDEN, String.valueOf(networkPlace.isShowHidden()));
        return evt;
    }

    /**
     * Create an event object for when network place resources are accessed.
     * 
     * @param src source of event
     * @param launchSession launch session
     * @param request request
     * @param path path in which network place exists
     * @param uri uri of network place
     * @param exception exception if failed or <code>null</code> if ok
     * @return event
     */
    public static CoreEvent getResourceAccessListEvent(Object src, LaunchSession launchSession, HttpServletRequest request,
                    String path, String uri, Throwable exception) {
        if (exception != null) {
            return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_DIRECTORY_LISTED, getResource(launchSession),
                            getPolicy(launchSession), getSessionInfo(request), exception, request, path, uri);
        } else {
            return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_DIRECTORY_LISTED, getResource(launchSession),
                            getPolicy(launchSession), getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL, request, path, uri);
        }
    }

    /**
     * Create an event object for when network place resources are accessed.
     * 
     * @param src source of event
     * @param launchSession resource session
     * @param request request
     * @param path path in which deleted resource exists
     * @param uri uri of network place
     * @param name filename deleted
     * @param exception exception if failed or <code>null</code> if ok
     * @return event
     */
    public static CoreEvent getResourceAccessDeleteEvent(Object src, LaunchSession launchSession, HttpServletRequest request,
                    String path, String uri, String name, Throwable exception) {
        if (exception != null) {
            return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_REMOVE, getResource(launchSession), getPolicy(launchSession),
                            getSessionInfo(request), exception, request, path, uri).addAttribute(
                                            NetworkPlacesEventConstants.EVENT_ATTR_VFS_FILE_NAME, name);
        } else {
            return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_REMOVE, getResource(launchSession), getPolicy(launchSession),
                            getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL, request, path, uri).addAttribute(
                                            NetworkPlacesEventConstants.EVENT_ATTR_VFS_FILE_NAME, name);
        }
    }

    /**
     * Create an event object for when a file is uploaded to a network place
     * 
     * @param src source of event
     * @param launchSession resource session
     * @param request request
     * @param path path to which file was uploaded
     * @param uri uri of network place
     * @param name filename uploaded
     * @param exception exception if failed or <code>null</code> if ok
     * @return event
     */
    public static CoreEvent getResourceAccessUploadEvent(Object src, LaunchSession launchSession, HttpServletRequest request,
                    String path, String uri, String name, Throwable exception) {
        if (exception != null) {
            return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_UPLOAD_FILE, getResource(launchSession),
                            getPolicy(launchSession), getSessionInfo(request), exception, request, null, null).addAttribute(
                            NetworkPlacesEventConstants.EVENT_ATTR_VFS_UPLOAD_DESTINATION_URI, uri).addAttribute(
                            NetworkPlacesEventConstants.EVENT_ATTR_VFS_UPLOAD_DESTINATION_PATH, path).addAttribute(
                            NetworkPlacesEventConstants.EVENT_ATTR_VFS_FILE_NAME, name);

        } else {
            return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_UPLOAD_FILE, getResource(launchSession),
                            getPolicy(launchSession), getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL, request).addAttribute(
                            NetworkPlacesEventConstants.EVENT_ATTR_VFS_UPLOAD_DESTINATION_URI, uri).addAttribute(
                            NetworkPlacesEventConstants.EVENT_ATTR_VFS_UPLOAD_DESTINATION_PATH, path).addAttribute(
                            NetworkPlacesEventConstants.EVENT_ATTR_VFS_FILE_NAME, name);
        }
    }

    /**
     * Create an event object for when a new directory is created in a network
     * place
     * 
     * @param src source of event
     * @param launchSession resource session
     * @param request request
     * @param path path to where directory was created
     * @param uri uri of network place
     * @param exception exception if failed or <code>null</code> if ok
     * @return event
     */
    public static CoreEvent getResourceAccessMkDirEvent(Object src, LaunchSession launchSession, HttpServletRequest request,
                    String path, String uri, Throwable exception) {
        if (exception != null) {
            return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_DIRECTORY_CREATED, getResource(launchSession),
                            getPolicy(launchSession), getSessionInfo(request), exception, request, path, uri);
        } else {
            return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_DIRECTORY_CREATED, getResource(launchSession),
                            getPolicy(launchSession), getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL, request, path, uri);
        }
    }

    /**
     * Create an event object for when a file is pasted to a network place
     * 
     * @param src source of event
     * @param launchSession resource session
     * @param request request
     * @param path path to which file was uploaded
     * @param uri uri of network place
     * @param exception exception if failed or <code>null</code> if ok
     * @return event
     */
    /**
     * @param src
     * @param launchSession
     * @param request
     * @param path
     * @param uri
     * @param exception
     * @return
     */
    public static CoreEvent getResourceAccessPasteEvent(Object src, LaunchSession launchSession, HttpServletRequest request,
                    String path, String uri, Throwable exception) {
        if (exception != null) {
            return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_PASTE_OPERATION, getResource(launchSession),
                            getPolicy(launchSession), getSessionInfo(request), exception, request, path, uri);
        } else {
            return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_PASTE_OPERATION, getResource(launchSession),
                            getPolicy(launchSession), getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL, request, path, uri);
        }
    }

    /**
     * Create an event object for when a selection of files are zipped and
     * downloaded
     * 
     * @param src source of event
     * @param launchSession resource session
     * @param request request
     * @param path path of selection
     * @param uri uri of network place
     * @param exception exception if failed or <code>null</code> if ok
     * @return event
     */
    public static CoreEvent getResourceAccessZipEvent(Object src, LaunchSession launchSession, HttpServletRequest request,
                    String path, String uri, Throwable exception) {
        return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_ZIP_DOWNLOAD, getResource(launchSession), getPolicy(launchSession),
                        getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL, request, path, uri);
    }

    /**
     * Add a directory name attribute to an event
     * 
     * @param evt event to add to
     * @param name name of directory
     * @param counter index of directory in selected
     */
    public static void addDirectoryAttribute(CoreEvent evt, String name, int counter) {
        evt.addAttribute(NetworkPlacesEventConstants.EVENT_ATTR_VFS_DIRECTORY_NAME + counter, name);
    }

    /**
     * Add a file name attribute to an event
     * 
     * @param evt event to add to
     * @param name name of file
     * @param counter index of file in selected
     */
    public static void addFileAttribute(CoreEvent evt, String name, int counter) {
        evt.addAttribute(NetworkPlacesEventConstants.EVENT_ATTR_VFS_FILE_NAME + counter, name);
    }

    /**
     * Add a the operation type attribute to an event
     * 
     * @param evt event to add to
     * @param isCut when true <i>Cut</i> will be the event attribute value,
     *        otherwise <i>Copy</i>
     */
    public static void addOperationType(CoreEvent evt, boolean isCut) {
        evt.addAttribute(NetworkPlacesEventConstants.EVENT_ATTR_VFS_OPERATION, isCut ? "Cut" : "Copy");
    }

    /**
     * Create an event object for when a file is renamed
     * 
     * @param src source of event
     * @param launchSession resource session
     * @param request request
     * @param path path to location of file
     * @param uri uri of network place
     * @param oldName old file name
     * @param newName new file name
     * @param exception exception if failed or <code>null</code> if ok
     * @return event
     */
    public static CoreEvent getResourceAccessRenameEvent(Object src, LaunchSession launchSession, HttpServletRequest request,
                    String path, String uri, String oldName, String newName, Throwable exception) {
        if (exception != null) {
            return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_RENAME, getResource(launchSession),
                            getPolicy(launchSession), getSessionInfo(request), exception, request, path, uri).addAttribute(
                            NetworkPlacesEventConstants.EVENT_ATTR_VFS_OLD_NAME, oldName).addAttribute(
                            NetworkPlacesEventConstants.EVENT_ATTR_VFS_NEW_NAME, newName);
        } else {
            return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_RENAME, getResource(launchSession),
                            getPolicy(launchSession), getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL, request, path, uri)
                            .addAttribute(NetworkPlacesEventConstants.EVENT_ATTR_VFS_OLD_NAME, oldName).addAttribute(
                                            NetworkPlacesEventConstants.EVENT_ATTR_VFS_NEW_NAME, newName);
        }
    }

    /**
     * Create an event object for when a file download starts
     * 
     * @param src source of event
     * @param launchSession resource session
     * @param request request
     * @param path path to location of file
     * @param uri uri of network place
     * @return event
     */
    public static CoreEvent getResourceAccessDownloadStartedEvent(Object src, LaunchSession launchSession,
                    HttpServletRequest request, String path, String uri) {
        return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_FILE_DOWNLOAD_STARTED, getResource(launchSession),
                        getPolicy(launchSession), getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL, request, path, uri);
    }

    /**
     * Create an event object for when a file download completes
     * 
     * @param src source of event
     * @param launchSession resource session
     * @param request request
     * @param path path to location of file
     * @param uri uri of network place
     * @return event
     */
    public static CoreEvent getResourceAccessDownloadCompleteEvent(Object src, LaunchSession launchSession,
                    HttpServletRequest request, String path, String uri) {
        return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_FILE_DOWNLOAD_COMPLETE, getResource(launchSession),
                        getPolicy(launchSession), getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL, request, path, uri);
    }

    /**
     * Create an event object for when a file download fails
     * 
     * @param src source of event
     * @param launchSession resource session
     * @param request request
     * @param path path to location of file
     * @param uri uri of network place
     * @param exception exception
     * @return event
     */
    public static CoreEvent getResourceAccessDownloadFailedEvent(Object src, LaunchSession launchSession,
                    HttpServletRequest request, String path, String uri, Throwable exception) {
        return new NetworkPlacesAccessEvent(src, NetworkPlacesEventConstants.VFS_FILE_DOWNLOAD_STARTED, getResource(launchSession),
                        getPolicy(launchSession), getSessionInfo(request), exception, request, path, uri);
    }

    private static SessionInfo getSessionInfo(HttpServletRequest request) {
        return (SessionInfo) request.getSession().getAttribute(Constants.SESSION_INFO);
    }

    private static Policy getPolicy(LaunchSession launchSession) {
        return launchSession == null ? null : launchSession.getPolicy();
    }

    private static Resource getResource(LaunchSession launchSession) {
        return launchSession == null ? null : launchSession.getResource();
    }
}
