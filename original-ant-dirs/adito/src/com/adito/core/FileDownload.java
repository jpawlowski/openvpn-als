
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
			
package com.adito.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;

import com.adito.boot.Util;
import com.adito.security.SessionInfo;


/**
 * Implementation of a {@link com.adito.core.AbstractDownloadContent}
 * that allows a locat file to be downloaded to the user.
 */
public class FileDownload extends AbstractDownloadContent {
    
    // Private instance variables
    private File file;
    private String filename;
    private int downloadCount;

    /**
     * Constructor.
     *
     * @param file file to download
     * @param filename filename to present file as
     * @param mimeType mime type
     * @param forward forward to direct to when complete
     * @param messageKey message key for message
     * @param messageResourcesKey bundle id 
     */
    public FileDownload(File file, String filename, String mimeType, ActionForward forward, String messageKey,
                        String messageResourcesKey) {
        this(file, filename, mimeType, forward, messageKey, messageResourcesKey, null);
    }

    /**
     * Constructor.
     *
     * @param file file to download
     * @param filename filename to present file as
     * @param mimeType mime type
     * @param forward forward to direct to when complete
     * @param messageKey message key for message
     * @param messageResourcesKey bundle id 
     * @param messageArg0 first message argument
     */
    public FileDownload(File file, String filename, String mimeType, ActionForward forward, String messageKey,
                    String messageResourcesKey, String messageArg0) {
        this(file, filename, mimeType, forward, messageKey, messageResourcesKey, messageArg0, null, null, null, null);
    }


    /**
     * Constructor.
     *
     * @param file file to download
     * @param filename filename to present file as
     * @param mimeType mime type
     * @param forward forward to direct to when complete
     * @param messageKey message key for message
     * @param messageResourcesKey bundle id 
     * @param messageArg0 first message argument 
     * @param messageArg1 second message argument 
     * @param messageArg2 third message argument 
     * @param messageArg3 fourth message argument 
     * @param messageArg4 fifth message argument
     */
    public FileDownload(File file, String filename, String mimeType, ActionForward forward, String messageKey,
                        String messageResourcesKey, String messageArg0, 
                        String messageArg1, String messageArg2, String messageArg3, String messageArg4) {
    	super(mimeType, forward, messageKey, messageResourcesKey, messageArg0, messageArg1, messageArg2, messageArg3, messageArg4);
        this.file = file;
        this.filename = filename;
    }

    /* (non-Javadoc)
     * @see com.adito.core.DownloadContent#getFilename()
     */
    public String getFileName() {
        return filename;
    }

    /**
     * Set the filename to download as
     * 
     * @param filename filename to download as
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Get the local file to make available for download
     * 
     * @return local file
     */
    public File getFile() {
        return file;
    }

    /**
     * Set the local file to make available for download
     * 
     * @param file local file
     */
    public void setFile(File file) {
        this.file = file;
    }
    
    /* (non-Javadoc)
     * @see com.adito.core.DownloadContent#sendDownload(javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpServletRequest)
     */
    public void sendDownload(HttpServletResponse response, HttpServletRequest request) throws Exception{
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            response.setContentLength((int) file.length());
            Util.copy(in, response.getOutputStream());
            response.getOutputStream().flush();
        } finally {
            downloadCount++;
            Util.closeStream(in);
        }
    }
    
	/* (non-Javadoc)
	 * @see com.adito.core.AbstractDownloadContent#completeDownload(com.adito.security.SessionInfo)
	 */
    public void completeDownload(SessionInfo session) {
	}

    /* (non-Javadoc)
     * @see com.adito.core.DownloadContent#getDownloadCount()
     */
    public int getDownloadCount() {
        return downloadCount;
    }
}