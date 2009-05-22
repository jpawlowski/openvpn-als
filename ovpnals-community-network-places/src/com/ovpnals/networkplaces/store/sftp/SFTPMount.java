package com.ovpnals.networkplaces.store.sftp;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.apache.commons.vfs.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;


import com.ovpnals.networkplaces.AbstractNetworkPlaceMount;
import com.ovpnals.policyframework.LaunchSession;
import com.ovpnals.security.PasswordCredentials;
import com.ovpnals.vfs.VFSStore;
import com.ovpnals.vfs.utils.URI;
import com.ovpnals.vfs.webdav.DAVAuthenticationRequiredException;
import com.ovpnals.vfs.webdav.DAVUtilities;

public class SFTPMount extends AbstractNetworkPlaceMount{
	final static Log log = LogFactory.getLog(SFTPMount.class);

	public SFTPMount(LaunchSession launchSession, VFSStore store) {
		super(launchSession, store);
	}

	public FileSystemOptions getOptions(URI uri) throws FileSystemException {
//		FileSystemOptions options = new FileSystemOptions();    
	//	SftpFileSystemConfigBuilder c = SftpFileSystemConfigBuilder.getInstance();
		//c.setStrictHostKeyChecking(options, "no");
		//String mode = Property.getProperty(new ResourceKey("ftp.mode", this.getNetworkPlace().getResourceType(), this.getNetworkPlace().getResourceId()));
		//c.setPassiveMode(options, mode.equals("passive"));
		
		//int idleTimeout = Property.getPropertyInt(new ResourceKey("ftp.idleTimeout", getNetworkPlace().getResourceType(), getNetworkPlace().getResourceId()));
//		c.setTimeout(options, 1000);
		// TODO: Add resource attribute for all these settings.
	//	c.setUserDirIsRoot(options, true);
		//String hostType = Property.getProperty(new ResourceKey("ftp.hostType", this.getNetworkPlace().getResourceType(), this.getNetworkPlace().getResourceId()));
		//if (!"automatic".equals(hostType)) {
		//  c.setEntryParser(options, hostType);
		//}
		FileSystemOptions options = new FileSystemOptions();
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(options, "no");
		return options;
	}

	public FileObject createVFSFileObject(String path, PasswordCredentials credentials) throws IOException {
		try {
			URI uri = getRootVFSURI();
			if (credentials != null) {
				uri.setUserinfo(DAVUtilities.encodeURIUserInfo(credentials.getUsername() + (credentials.getPassword() != null ? ":" + new String(credentials.getPassword()) : "")));
			}
			log.info("Sftp Path here"+uri.toString());
			uri.setPath(uri.getPath() + (uri.getPath().endsWith("/") ? "" : "/") + DAVUtilities.encodePath(path));
			//((StandardFileSystemManager)this.getStore().getRepository().getFileSystemManager()).init();
			FileObject fileObject = this.getStore().getRepository().getFileSystemManager().resolveFile(uri.toString(), getOptions(uri));
			return fileObject;
		} catch (FileSystemException fse) {
			if (fse.getCode().equals("vfs.provider.ftp/connect.error")) {
				throw new DAVAuthenticationRequiredException(getMountString());
			}
			throw fse;
		}
	}
}
