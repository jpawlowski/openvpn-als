
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

import java.awt.Rectangle;

import com.adito.boot.Util;


/**
 * Constructs a fragment of JavaScript to open a link in a new Window with
 * parameters.
 */
public class WindowOpenJavascriptLink implements JavascriptLink {
    
    // Private instance variables
    private String uri;
    private String windowId;
    private Rectangle bounds;
    private boolean resizable;
    private boolean menuBar;
    private boolean toolBar;
    private boolean scrollBars;
    private boolean location;

    /**
     * Construct a new link that does nothing.
     *
     */
    public WindowOpenJavascriptLink() {
        this(null, null, null, false, false, false, false, false);
    }
    
    /**
     * Constructor.
     * 
     * @param uri uri to open (must be encoded)
     * @param windowId ID to give window
     * @param bounds window bounds
     * @param resizable resizable
     * @param menuBar show menu bar
     * @param toolBar show tool bar
     * @param scrollBars show scroll bars
     * @param location show location bar
     *
     */
    public WindowOpenJavascriptLink(String uri, String windowId, Rectangle bounds, 
                          boolean resizable, boolean menuBar, boolean toolBar, boolean scrollBars, boolean location) {
        this.uri = uri;
        this.windowId = windowId;
        this.bounds = bounds;
        this.resizable = resizable;
        this.menuBar = menuBar;
        this.toolBar = toolBar;
        this.scrollBars = scrollBars;
        this.location = location;
    }
    
    /**
     * Get the URI
     * 
     * @return uri
     */
    public String getURI() {
        return uri;
    }
    
    /**
     * Generate the Javascript fragment.
     * 
     * @return javascript fragement to open the window
     */
    public String toJavascript() {
        if(uri == null) {
            return "void();";
        }
        
        StringBuffer openBuf = new StringBuffer();
        openBuf.append("windowRef = window.open('");
        openBuf.append(Util.escapeForJavascriptString(uri));
        openBuf.append("','");
        openBuf.append(windowId);
        openBuf.append("','");
        if(bounds != null) {
        	openBuf.append("top=");
        	openBuf.append(bounds.y);
        	openBuf.append(",left=");
        	openBuf.append(bounds.x);
            openBuf.append(",width=");
            openBuf.append(bounds.width);
            openBuf.append(",height=");
            openBuf.append(bounds.height);
            openBuf.append(",");
        }
        openBuf.append("location=");
        openBuf.append(location ? 1 : 0);
        openBuf.append(",resizable=");
        openBuf.append(resizable ? 1 : 0);
        openBuf.append(",toolbar=");
        openBuf.append(toolBar ? 1 : 0);
        openBuf.append(",menubar=");
        openBuf.append(menuBar ? 1 : 0);
        openBuf.append(",scrollbars=");
        openBuf.append(scrollBars ? 1 : 0);
        openBuf.append("'); ");
        
        StringBuffer buf = new StringBuffer();
        buf.append("this.blur(); ");
        buf.append(openBuf.toString());
        buf.append("if(windowRef==null || typeof(windowRef)=='undefined') { ");
        buf.append("  if(setPopupBlocked) { setPopupBlocked(); }");
        buf.append("} else { windowRef.focus(); }");
        return buf.toString();
    }
}
