
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.navigation.actions.FileDownloadAction;
import com.adito.navigation.actions.ShowFileDownloadAction;

/**
 * Implementation of a of {@link com.adito.core.PageInterceptListener}
 * that may be used to force a file download when it becomes available.
 */

public class FileDownloadPageInterceptListener implements PageInterceptListener {
    
    /**
     * Intercept listener id
     */
    public final static String INTERCEPT_ID = "fileDownload";

    // Protected instance variables
    protected List downloads;

    // Private statics
    static int id = 0;

    /**
     * Constructor.
     */
    public FileDownloadPageInterceptListener() {
        downloads = new ArrayList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.PageInterceptListener#getId()
     */
    public String getId() {
        return INTERCEPT_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.PageInterceptListener#checkForForward(org.apache.struts.action.Action,
     *      org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward checkForForward(Action action, ActionMapping mapping, HttpServletRequest request,
                                         HttpServletResponse response) throws PageInterceptException {
        if (downloads.size() > 0 && !(action instanceof ShowFileDownloadAction) && !(action instanceof FileDownloadAction)) {
            DownloadContent download = ((DownloadContent) downloads.get(0));
            return CoreUtil.addParameterToForward(download.getMessageForward(), "id", String.valueOf(download.getId()));
        }
        return null;
    }

    /**
     * Add a new download.
     * 
     * @param downloadContent download
     * @return assigned id
     */
    public int addDownload(DownloadContent downloadContent) {
        id++;
        downloadContent.setId(id);
        downloads.add(downloadContent);
        return id;
    }

    /**
     * Get a download given its id.
     * 
     * @param id id
     * @return download
     */
    public DownloadContent getDownload(int id) {
        for (Iterator i = downloads.iterator(); i.hasNext();) {
            DownloadContent l = (DownloadContent) i.next();
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }

    /**
     * Get the number of downloads available.
     * 
     * @return number of downloads
     */
    public int size() {
        return downloads.size();
    }

    /**
     * Remove a download given its id.
     * 
     * @param id id of download to remove
     */
    public void removeDownload(int id) {
        DownloadContent d = getDownload(id);
        if (d != null) {
            downloads.remove(d);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.PageInterceptListener#isRedirect()
     */
    public boolean isRedirect() {
        return false;
    }
}