package com.ovpnals.vfs;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.NameScope;

import com.ovpnals.core.CoreServlet;
import com.ovpnals.policyframework.LaunchSession;
import com.ovpnals.security.PasswordCredentials;
import com.ovpnals.vfs.webdav.DAVAuthenticationRequiredException;
import com.ovpnals.vfs.webdav.DAVException;
import com.ovpnals.vfs.webdav.DAVListener;
import com.ovpnals.vfs.webdav.DAVMultiStatus;
import com.ovpnals.vfs.webdav.DAVStatus;
import com.ovpnals.vfs.webdav.DAVUtilities;
import com.ovpnals.vfs.webdav.methods.GET;

/**
 * <p>
 * This class implements {@link com.ovpnals.vfs.VFSResource} and and
 * provides axcess to the file system.
 */
public class FileObjectVFSResource implements VFSResource {
	final static Log log = LogFactory.getLog(VFSResource.class);

	//	Private instance variables
	
	private VFSMount mount = null;
	private FileObject file = null;
	
	//	Protected instance variables
	protected LaunchSession launchSession;
	protected String relativePath;
	protected VFSResource parent;
	protected VFSRepository repository;
	protected PasswordCredentials requestCredentials;

	/**
	 * @param launchSession launch session
	 * @param mount The Mount acociated with this resource.
	 * @param parent parent
	 * @param relativePath path relative to root of mount
	 * @param repository repository
	 * @param requestCredentials request credentials
	 * @throws IOException on any error
	 */
	public FileObjectVFSResource(LaunchSession launchSession, VFSMount mount, VFSResource parent, String relativePath,
									VFSRepository repository, PasswordCredentials requestCredentials)
		throws IOException {
		if (mount == null)
			throw new NullPointerException("Null mount");
		this.launchSession = launchSession;
		this.mount = mount;
		this.parent = parent;
		this.relativePath = relativePath;
		this.repository = repository;
		this.requestCredentials = requestCredentials;

		// get the parent now if possible
		if (this.parent == null) {
			if (this.relativePath != null) {
				String parentPath = DAVUtilities.getParentPath(relativePath);
				if (parentPath == null) {
				} else {
					this.parent = mount.getResource(parentPath, requestCredentials /*, transaction */);
				}
			}
		}
	}
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		try {
			return getFile().hashCode();
		} catch (IOException e) {
			return hashCode();
		}
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#isMount()
	 */
	public boolean isMount() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getWebFolderPath()
	 */
	public String getWebFolderPath() {
		return "/fs/" + mount.getMountString()
			+ (mount.getMountString().endsWith("/") || relativePath.startsWith("/") ? "" : "/")
			+ relativePath;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object == null)
			return (false);
		if (object instanceof FileObjectVFSResource) {
			FileObjectVFSResource resource = (FileObjectVFSResource) object;
			try {
				boolean u = getFile().equals(resource.getFile());
				boolean r = this.getMount() == resource.mount;
				return (u && r);
			} catch (IOException ioe) {
				return false;
			}
		} else {
			return (false);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object object) {
		FileObjectVFSResource resource = (FileObjectVFSResource) object;
		try {
			return (getFile().getURL().toExternalForm().compareTo(resource.getFile().getURL().toExternalForm()));
		} catch (IOException ioe) {
			log.warn("Failed to compare two files.", ioe);
			return -999;
		}
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#verifyAccess()
	 */
	public void verifyAccess() throws Exception, DAVAuthenticationRequiredException {
		this.getFile().exists();
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#isNull()
	 */
	public boolean isNull() throws IOException {
		return !getFile().exists();
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#isCollection()
	 */
	public boolean isCollection() throws IOException {
		if (this.isNull())
			return false;
		try {
			FileObject temp = getFile();
			FileType type = temp.getType();
			if (type == null) {
				type = temp.getName().getType();
			}
			return (type.equals(FileType.FOLDER));
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.warn("Failed to test if resource is a collection.", e);
			} else {
				log.warn("Failed to test if resource is a collection : " + e.getMessage());
			}
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#isResource()
	 */
	public boolean isResource() throws IOException {
		if (this.isNull()) {
			return false;
		} else {
			return (!this.isCollection());
		}
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getFile()
	 */
	public FileObject getFile() throws IOException {
		if (file == null) {
            FileObject root = getFileObject("");
            String stripLeadingSlash = DAVUtilities.stripLeadingSlash(getRelativePath());
			file = root.resolveFile(stripLeadingSlash, NameScope.DESCENDENT_OR_SELF);
			if (file == null) {
				throw new IOException("Could not create file object.");
			} 
		}
		return file;
	}
    
    private FileObject getFileObject(String relativePath) throws DAVAuthenticationRequiredException, IOException {
        FileObject fileObject = mount.createAuthenticatedVFSFileObject(relativePath, requestCredentials);
        return fileObject;
    }

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getMount()
	 */
	public VFSMount getMount() {
		return this.mount;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getDisplayName()
	 */
	public String getDisplayName() {
		try {
			String name = getFile().getName().getBaseName();
			if (this.isCollection())
				return (name + "/");
			return name;
		} catch (IOException ioe) {
			return getBasename();
		}
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getBasename()
	 */
	public String getBasename() {
		String p = relativePath;
		int idx = p.length() < 2 ? -1 : (p.lastIndexOf('/', p.endsWith("/") ? p.length() - 2 : p.length() - 1));
		if (idx != 1) {
			p = p.substring(idx + 1);
		}
		return p;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getRelativePath()
	 */
	public String getRelativePath() {
		return relativePath;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getRelativeURI()
	 */
	public URI getRelativeURI() {
		try {
			return new URI(DAVUtilities.encodePath(getRelativePath()));
		} catch (Exception e) {
			log.warn("Failed to get the relativeURI for the resource", e);
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getParent()
	 */
	public VFSResource getParent() {
		try {
			if (parent == null) {
				String parentPath = DAVUtilities.stripLeadingSlash(DAVUtilities.getParentPath(getFullPath()));
				if (parentPath == null || parentPath.equals("/")) {
					return null;
				} else {
					return repository.getResource(getLaunchSession(), parentPath, requestCredentials/*
																									 * ,
																									 * transaction
																									 */);
				}
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return parent;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getChildren()
	 */
	public Iterator<VFSResource> getChildren() throws IOException {
		if (!this.isCollection())
			return null;

		// this forces VFS to re cashe.
		this.getFile().close();

		FileObject children[] = this.getFile().getChildren();
		List<VFSResource> resources = new ArrayList<VFSResource>(children.length);

		for (int x = 0; x < children.length; x++) {
			String fileName = children[x].getName().getBaseName();
			if (fileName.startsWith(PREFIX) && fileName.endsWith(SUFFIX))
				continue;
			if (!isCollection() && !isResource())
				continue;
			try {
				/*
				 * TODO BPS - I think we have a problem here.
				 * 
				 * When getting children that require further authentication we
				 * get a {@link DAVAuthenticationRequiredException} exception.
				 * We do not want to throw an exception at this point but we do
				 * want add the child. Its only when children of the child are
				 * accessed that we want to throw the exception. Because
				 * DAVMount.getResource() is the one that throws this, a
				 * resource object can never be created.
				 * 
				 * This will happen for example when listomg /fs/[store] and a
				 * network place that requires auth. is hit.
				 */

				VFSResource r = getMount().getResource(DAVUtilities.concatenatePaths(relativePath, fileName), requestCredentials/*
																																 * ,
																																 * transaction
																																 */);
				resources.add(r);
			} catch (Exception e) {
				/*
				 * NOTE - BPS - We cannot log this exception as it may have user
				 * information in the URI.
				 */
				// log.warn("Failed to get resource.", e);
			}
		}
		return resources.iterator();
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getContentType()
	 */
	public String getContentType() throws IOException {
		if (this.isNull())
			return null;
		if (this.isCollection())
			return GET.COLLECTION_MIME_TYPE;
		String mime = CoreServlet.getServlet().getServletContext().getMimeType(this.getDisplayName());
		return mime == null ? "application/octet-stream" : mime;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getContentLength()
	 */
	public Long getContentLength() throws IOException {
		if (this.isNull() || this.isCollection())
			return null;
		try {
			return new Long(getFile().getContent().getSize());
		} catch (IOException e) {
			log.error("Failed to get content length.", e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getLastModified()
	 */
	public Date getLastModified() throws IOException {
		if (this.isNull())
			return null;
		try {
			return new Date(getFile().getContent().getLastModifiedTime());
		} catch (IOException e) {
			log.error("Failed to get last modified date of resource.", e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getEntityTag()
	 */
	public String getEntityTag() throws IOException {
		if (this.isNull())
			return null;

		String path = this.getRelativePath();
		return DAVUtilities.getETAG(path, this.getLastModified());
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#delete()
	 */
	public void delete() throws DAVMultiStatus, IOException {
		if (this.isNull())
			throw new DAVException(404, "Not found", this);

		if (getMount().isReadOnly()) {
			throw new DAVException(DAVStatus.SC_FORBIDDEN, "You cannot delete this file because the the mount is readonly!");
		}

		if (this.isResource()) {
			try {
				if (!getFile().delete()) {
					throw new DAVException(403, "Can't delete resource '" + getRelativePath() + "'", this);
				} else {
					this.getMount().getStore().getRepository().notify(this, DAVListener.RESOURCE_REMOVED);
				}
			} catch (IOException e) {
				throw new DAVException(403, "Can't delete resource. " + VfsUtils.maskSensitiveArguments(e.getMessage()), this);
			}
		} else if (this.isMount()) {
			throw new DAVException(403, "Can't delete resource '" + getRelativePath()
				+ "' as it is the root for the mount point "
				+ this.getMount().getMountString(), this);
		} else if (this.isCollection()) {

			DAVMultiStatus multistatus = new DAVMultiStatus();

			Iterator children = this.getChildren();
			while (children.hasNext())
				try {
					((VFSResource) children.next()).delete();
				} catch (DAVException exception) {
					multistatus.merge(exception);
				}

			if (multistatus.size() > 0)
				throw multistatus;
			try {
				if (!getFile().delete()) {
					throw new DAVException(403, "Can't delete collection", this);
				} else {
					this.getMount().getStore().getRepository().notify(this, DAVListener.COLLECTION_REMOVED);
				}
			} catch (IOException e) {
				log.error("Failed to delete resource.", e);
				throw new DAVException(403, "Can't delete collection " + getRelativePath() + ". " + e.getMessage(), this);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#copy(com.ovpnals.vfs.VFSResource, boolean, boolean)
	 */
	public void copy(VFSResource dest, boolean overwrite, boolean recursive) throws DAVMultiStatus, IOException {

		/*
		 * NOTE: Since the COPY operation relies on other operation defined in
		 * this class (and in DAVOutputStream for resources) rather than on
		 * files temselves, notifications are sent elsewhere, not here.
		 */

		if (this.isNull())
			throw new DAVException(404, "Not found", this);

		/* Check if the destination exists and delete if possible */
		if (!dest.isNull()) {
			if (!overwrite) {
				String msg = "Not overwriting existing destination";
				throw new DAVException(412, msg, dest);
			}
			dest.delete();
		}

		/* Copy a single resource (destination is null as we deleted it) */
		if (this.isResource()) {
			VFSInputStream in = this.getInputStream();
			VFSOutputStream out = dest.getOutputStream();
			byte buffer[] = new byte[4096];
			int k = -1;
			while ((k = in.read(buffer)) != -1)
				out.write(buffer, 0, k);
			out.close();
		}

		/* Copy the collection and all nested members */
		if (this.isCollection()) {
			dest.makeCollection();
			if (!recursive)
				return;

			DAVMultiStatus multistatus = new DAVMultiStatus();
			Iterator children = this.getChildren();
			while (children.hasNext())
				try {
					FileObjectVFSResource childResource = (FileObjectVFSResource) children.next();
					try {
						FileObject child = ((FileObjectVFSResource) dest).getFile().resolveFile(childResource.getFile()
										.getName()
										.getBaseName());
						FileObjectVFSResource target = new FileObjectVFSResource(getLaunchSession(),
										this.getMount(),
										this, /* transaction, */
										this.getMount().getMountString(),
										repository,
										requestCredentials);
						childResource.copy(target, overwrite, recursive);
					} catch (IOException e) {
						throw new DAVException(403, "Could not resolve child.", e);
					}
				} catch (DAVException exception) {
					multistatus.merge(exception);
				}
			if (multistatus.size() > 0)
				throw multistatus;
		}
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#move(com.ovpnals.vfs.VFSResource, boolean)
	 */
	public void move(VFSResource dest, boolean overwrite) throws DAVMultiStatus, IOException {

		/*
		 * NOTE: Since the COPY operation relies on other operation defined in
		 * this class (and in DAVOutputStream for resources) rather than on
		 * files temselves, notifications are sent elsewhere, not here.
		 */

		if (this.isNull())
			throw new DAVException(404, "Not found", this);

		/* Check read only */
		if (getMount().isReadOnly()) {
			throw new DAVException(DAVStatus.SC_FORBIDDEN, "You cannot move this file because the the mount is readonly!");
		}

		/* Check if the destination exists and delete if possible */
		if (!dest.isNull()) {
			if (!overwrite) {
				String msg = "Not overwriting existing destination";
				throw new DAVException(412, msg, dest);
			}
			dest.delete();
		}

		/* If the file system supports then move then just do it */
		if (getFile().canRenameTo(dest.getFile())) {
			getFile().moveTo(dest.getFile());
			return;
		}

		/* Otherwise copy */
		copy(dest, overwrite, true);
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#makeCollection()
	 */
	public void makeCollection() throws IOException {
		VFSResource parent = this.getParent();
		if (!this.isNull())
			throw new DAVException(405, "Resource exists", this);
		if (parent.isNull())
			throw new DAVException(409, "Parent does not not exist", this);
		if (!parent.isCollection())
			throw new DAVException(403, "Parent not a collection", this);

		/* Check read only */
		if (getMount().isReadOnly()) {
			throw new DAVException(DAVStatus.SC_FORBIDDEN, "You cannot create a folder here because the the mount is readonly!");
		}

		try {
			getFile().createFolder();
			this.getMount().getStore().getRepository().notify(this, DAVListener.COLLECTION_CREATED);
		} catch (IOException e) {
			throw new DAVException(507, "Can't create collection", this);
		}
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getInputStream()
	 */
	public VFSInputStream getInputStream() throws IOException {
		if (this.isNull())
			throw new DAVException(404, "Not found", this);
		if (this.isCollection())
			throw new DAVException(403, "Resource is collection", this);
		return new VFSInputStream(this);
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSResource#getOutputStream()
	 */
	public VFSOutputStream getOutputStream() throws IOException {
		if (this.isCollection())
			throw new DAVException(409, "Can't write a collection", this);

		if (getMount().isReadOnly()) {
			throw new DAVException(DAVStatus.SC_FORBIDDEN, "You cannot create a write here because the the mount is readonly!");
		}
		return new VFSOutputStream(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSResource#getFullURI()
	 */
	public URI getFullURI() {
		VFSMount mount = getMount();
		URI uri = URI.create(mount == null ? ("/" + getRelativeURI())
			: ("/" + DAVUtilities.stripTrailingSlash(DAVUtilities.encodePath(mount.getMountString(), true)) + "/" + DAVUtilities.stripLeadingSlash(DAVUtilities.encodePath(getRelativePath(),
				true))));
		return uri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSResource#getFullPath()
	 */
	public String getFullPath() {
		VFSMount mount = getMount();
		return mount == null ? ("/" + getRelativeURI()) : ("/" + mount.getMountString()
			+ (mount.getMountString().endsWith("/") || getRelativePath().startsWith("/") ? "" : "/") + getRelativePath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSResource#getLaunchSession()
	 */
	public LaunchSession getLaunchSession() {
		return launchSession;
	}

	public boolean isBrowsable() throws IOException {
		return true;
	}
}
