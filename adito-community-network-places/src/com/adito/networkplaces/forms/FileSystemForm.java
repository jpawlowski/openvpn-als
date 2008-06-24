
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
			
package com.adito.networkplaces.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.adito.networkplaces.NetworkPlace;
import com.adito.networkplaces.model.FileSystemItem;
import com.adito.networkplaces.model.FileSystemItemModel;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.forms.AbstractResourcesForm;
import com.adito.security.SessionInfo;
import com.adito.vfs.VFSPath;
import com.adito.vfs.VFSResource;

/**
 * The form that provides the data for file system view.
 */
public class FileSystemForm extends AbstractResourcesForm {

    private static final long serialVersionUID = -8944288890287463553L;
    static Log log = LogFactory.getLog(FileSystemForm.class);

    private String path;
    private boolean confirmDeletion;
    private String newFolder;
    private String newName;
    private String fileName;
    private List paths;
    private boolean viewOnly;
    private LaunchSession launchSession;
    private String launchId;
    private VFSResource vfsResource;

    /**
     * Constructor that sets up the form.
     */
    public FileSystemForm() {
        super(new FileSystemItemModel("fileSystem"));
        this.path = null;
        this.confirmDeletion = false;
        this.newFolder = null;
        this.newName = null;
        this.fileName = null;
        this.launchSession = null;
        this.paths = new ArrayList();
    }
    
    /**
     * Set the launch ID. This is used to retrieve the resource session after
     * the network place has been launched
     * 
     * @param launchId launch ID
     */
    public void setLaunchId(String launchId) {
        this.launchId = launchId;
    }

	/**
	 * Set the {@link VFSResource} for the current path.
	 * 
	 * @param vfsResource vfs resource
	 */
	public void setVFSResource(VFSResource vfsResource) {
		this.vfsResource = vfsResource;
		
	}

	/**
	 * Get the {@link VFSResource} for the current path.
	 * 
	 * @return vfs resource
	 */
	public VFSResource getVFSResource() {
		return vfsResource;
		
	}
    
    /**
     * Get the launch ID. This is used to retrieve the resource session after
     * the network place has been launched
     * 
     * @return launch ID
     */
    public String getLaunchId() {
        return launchId;
    }
    
    /**
     * Set the launch session.
     * 
     * @param launchSession launch session
     */
    public void setLaunchSession(LaunchSession launchSession) {
        this.launchSession = launchSession;
        launchId = launchSession.getId();
    }
    
    /**
     * Get the launch session
     * 
     * @return launch session
     */
    public LaunchSession getLaunchSession() {
        return launchSession;
    }

    /**
     * @return Object[] of the selected resources.
     */
    public String[] getSelectedFileNames() {
        List selected = new ArrayList();
        for (Iterator i = getModel().getItems().iterator(); i.hasNext();) {
            FileSystemItem ti = (FileSystemItem) i.next();
            if (ti.getChecked())
                selected.add(ti.getFileName());
        }
        return (String[]) selected.toArray(new String[selected.size()]);
    }
    
    /**
     * Set the policy object this network place was launched under. Access
     * to this resource should be 
     * @return weather the source resource is deleted after an operation, used
     *         on cut.
     */
    public boolean isConfirmDeletion() {
        return confirmDeletion;
    }

    /**
     * @param confirmDeletion weather the resource should be deleted.
     */
    public void setConfirmDeletion(boolean confirmDeletion) {
        this.confirmDeletion = confirmDeletion;
    }

    /**
     * @return The path to the current location.
     */
    public String getPath() {
        return path;
    }

    /**
     * @param String path Sets the path to the current location.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return String the path to the home of the network place.
     */
    public String getHome() {
        VFSPath tmp = (VFSPath) this.paths.get(0);
        this.paths.clear();
        this.paths.add(0, tmp);
        return tmp.getPath();
    }

    /**
     * @param String home sets the path to the home for this network place.
     */
    public void setHome(String home) {
        this.paths.add(new VFSPath(0, home));
    }

    public List getPaths() {
        return this.paths;
    }

    /**
     * @return int the id of the resource.
     */
    public int getResourceId() {
        return getNetworkPlace() == null ? -1 : getNetworkPlace().getResourceId();
    }

    /**
     * @return String the name of the new folder.
     */
    public String getNewFolder() {
        return newFolder;
    }

    /**
     * @param newFolderString String to set the name of the new folder.
     */
    public void setNewFolder(String newFolderString) {
        this.newFolder = newFolderString;
    }

    /**
     * @return String the name of the new name.
     */
    public String getNewName() {
        return newName;
    }

    /**
     * @param newName String to set the name of the file.
     */
    public void setNewName(String newName) {
        this.newName = newName;
    }

    /**
     * @return String of the file name selected.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName String of the file selected.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * <p>
     * Initialise the form.
     * 
     * @param request The operation request.
     * @param allFileSystemItems List of files to be viewed.
     * @throws Exception
     */
    public void initialize(HttpServletRequest request, List allFileSystemItems, SessionInfo session) throws Exception {
        super.initialize(session.getHttpSession(), "name");

        try {
            Iterator i = allFileSystemItems.iterator();

            while (i.hasNext()) {
                FileSystemItem it = (FileSystemItem) i.next();
                it.setSortFoldersFirst(true);
                getModel().addItem(it);
            }
            getPager().setSortReverse(getSortReverse());
            getPager().rebuild(getFilterText());
        } catch (Throwable t) {
            log.error("Failed to initialise resources form.", t);
        }
    }

    /**
     * Set the folder to be read / write. By default a form is view only, this
     * method must be called before any write functions are allowed.
     * 
     */
    public void setReadWrite() {
        viewOnly = false;
    }

	/**
	 * Set this folder to be a read only
	 */
	public void setViewOnly() {
		viewOnly = true;		
	}
	
    /**
     * Get if this should be <i>view only</i>. For a folder to not be view
     * only, {@link #setReadWrite()} must have been called
     * 
     * @return view only
     */
    public boolean isViewOnly() {
        return viewOnly;
    }

    /**
     * Get the network place that points to the mount currently being
     * viewed. <code>null</code> will be returned if the mount does
     * not point to a network place.
     * 
     * @return The network place acossiated with the file system view.
     */
    public NetworkPlace getNetworkPlace() {
        return launchSession == null ? null : (NetworkPlace)launchSession.getResource();
    }

    /**
     * @param path The path to add or go to.
     */
    public void addPath(String path) {
        VFSPath newPath = new VFSPath(this.paths.size(), path);
        VFSPath oldPath = null;
        boolean add = true;
        List newPaths = new ArrayList();
        Iterator iter = this.paths.iterator();
        while (iter.hasNext()) {
            VFSPath element = (VFSPath) iter.next();
            if (element.getPath().equals(newPath.getPath())) {
                add = false;
                newPaths.add(element);
                break;
            } else {
                newPaths.add(element);
            }
        }
        this.paths = newPaths;
        if (add)
            this.paths.add(newPath);
    }

    /**
     * @param _id The position to move to...
     */
    public void clearPathsTo(String _id) {
        int id = Integer.parseInt(_id);
        Iterator iter = this.paths.iterator();
        List newPaths = new ArrayList();
        while (iter.hasNext()) {
            VFSPath element = (VFSPath) iter.next();
            if (element.getPosition() <= id)
                newPaths.add(element);
        }
        this.paths = newPaths;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        /*
         * NOTE This done to avoid double slashes between folder names.
         */
        if ("list".equals(getActionTarget())) {
            if (this.getPath()!=null && this.getPath().endsWith("/")) {
                this.setPath(this.getPath().substring(0, this.getPath().length() - 1));
            }
        }
        return super.validate(mapping, request);
    }

    public String getFullURI() {
        if (vfsResource == null ) {
            return null;
        } else {
        	return vfsResource.getFullPath();
        }
    }

	/* (non-Javadoc)
	 * @see com.adito.table.forms.AbstractPagerForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		viewOnly = true;
	}
}
