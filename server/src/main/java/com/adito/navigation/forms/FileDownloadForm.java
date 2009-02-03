
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
			
package com.adito.navigation.forms;

import java.awt.Rectangle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.Util;
import com.adito.core.DownloadContent;
import com.adito.core.WindowOpenJavascriptLink;
import com.adito.core.forms.CoreForm;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;

/**
 * Form for performing file downloads.
 */
public class FileDownloadForm extends CoreForm {

    static Log log = LogFactory.getLog(FileDownloadForm.class);

    /**
     * Default window width for popup
     */
    public final static int WINDOW_WIDTH = 790;

    /**
     * Default window width for popup
     */
    public final static int WINDOW_HEIGHT = 480;

    private DownloadContent download;

    /**
     * @param download
     */
    public void init(DownloadContent download) {
        this.download = download;
    }

    /**
     * @return Returns the download.
     */
    public DownloadContent getDownload() {
        return download;
    }
    
    /**
     * Get the link show the temporary download directory for the current session/ 
     * 
     * @param request request
     * @return String
     */
    public String getTempDownloadLink(HttpServletRequest request){
    	SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
        WindowOpenJavascriptLink windowOpenJavascriptLink = new WindowOpenJavascriptLink("fileSystem.do?actionTarget=list&path=" + Util.urlEncode("temp/" + session.getUser().getPrincipalName() + "."
            + session.getHttpSession().getId())+ "&resourceId=0", 
                        "vfs_" + "0" + "_" + System.currentTimeMillis(),
                        new Rectangle(20, 20, WINDOW_WIDTH, WINDOW_HEIGHT), true, false, false, true, false);
        return "javascript: " + windowOpenJavascriptLink.toJavascript();
    }
    
    /* (non-Javadoc)
     * @see com.adito.core.forms.CoreForm#getReferer()
     */
    public String getReferer() {
        String tmpReferer = super.getReferer();
        // if there is no referer then just go back to home.
        if (tmpReferer == null){
            return "window.location = '/showHome.do'";
        }
        return tmpReferer;
    }

    /**
     * @return String
     */
    public String getLink() {
        return "/fileDownload.do?id=" + download.getId();
    }
}