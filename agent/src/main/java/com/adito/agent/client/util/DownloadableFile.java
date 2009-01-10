
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
			
package com.adito.agent.client.util;

import java.io.File;
import java.util.Vector;


/**
 * Represents a file that may be downloaded.
 */
public class DownloadableFile {
	
	//	Private instance variables
	
    private String applicationName;
    private boolean executable;
    private boolean readOnly;
    private Vector aliases;
    private File target;
    private String name;
    private long checksum;
    
	/**
	 * Constructor.
	 *
	 * @param name 
	 * @param applicationName
	 * @param executable
	 * @param readOnly
	 * @param writable
	 * @param target
	 */
	public DownloadableFile(String name, String applicationName, boolean executable, boolean readOnly, File target, long checksum) {
		super();
		this.name = name;
		this.applicationName = applicationName;
		this.executable = executable;
		this.readOnly = readOnly;
		this.target = target;
		this.checksum = checksum;
		aliases = new Vector();
	}
	
	public void addAlias(String alias) {
		aliases.addElement(alias);
	}

	/**
	 * @param applicationName the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * @param executable the executable to set
	 */
	public void setExecutable(boolean executable) {
		this.executable = executable;
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * @return the executable
	 */
	public boolean isExecutable() {
		return executable;
	}

	/**
	 * @return
	 */
	public Vector getAliases() {
		return aliases;
	}

	/**
	 * @return the target
	 */
	public File getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(File target) {
		this.target = target;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the checksum
	 */
	public long getChecksum() {
		return checksum;
	}

	/**
	 * @param checksum the checksum to set
	 */
	public void setChecksum(long checksum) {
		this.checksum = checksum;
	}
    
    
}