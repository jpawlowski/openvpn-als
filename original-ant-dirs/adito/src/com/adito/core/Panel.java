
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;


/**
 * <i>Panels</i> are blocks of HTML / JSP that may be added by plugins to
 * one of the main areas of the Adito layout.
 * <p>
 * Every panel must have a unique ID.
 * <p>
 * Each panel has a <i>Placement</i> that determines where it will appear,
 * a weight for determining where it appears in relation to other panels with
 * the same placement and a path to JSP / HTML fragment to include.
 * <p>
 * Before the panel is rendered, {@link #isAvailable(HttpServletRequest, HttpServletResponse, String)}
 * is called to determine if the panel should be shown in the current context.
 */
public interface Panel {
    
    /**
     * Frame state of closed.
     */
    public final static String FRAME_CLOSED = "closed";
    
    /**
     * Frame state of minimized.
     */
    public final static String FRAME_MINIMIZED = "minimized";
    
    /**
     * Frame state of normal.
     */
    public final static String FRAME_NORMAL = "normal";
    
    /**
     * The side bar. The menus and resource editing details go here. The
     * default theme has this as a bar running down the left hand side
     */
    public final static int SIDEBAR = 0;
    
    /**
     * The message area. Page tasks, errors, warnings, info etc all go here.
     * Not yet supported by the default theme.
     */
    public final static int MESSAGES = 1;
    
    /**
     * The header area. Not yet support by the default theme.
     */
    public final static int HEADER = 2;
    
    /**
     * The footer area. Not yet supported by the default theme
     */
    public final static int FOOTER = 3;
    
    /**
     * The information area. Not yet supported by the default theme
     */
    public final static int INFO = 4;
    
    /**
     * The main content area
     */
    public final static int CONTENT = 5;
    
    /**
     * A tab on the status page
     */
    public final static int STATUS_TAB = 6;
    
    /**
     * Some panels may need to know th message resource bundle they are 
     * associated with. This method returns it.
     * 
     * @return bundle
     */
    public String getBundle();
    
    /**
     * Get the unique panel ID 
     * 
     * @return panel id
     */
    public String getId();
    
    /**
     * Get the placement of the panel. 
     * 
     * @return placement
     */
    public int getPlacement();
    
    /**
     * Get the weight used to determine this panel in relation to others with
     * the same placement. Lowest values are rendered first. 
     * 
     * @return weight
     */
    public int getWeight();
    
    /**
     * Get the path to the JSP / HTML fragment to include. 
     * 
     * @param pageContext page context
     * @return include path
     */
    public String getTileIncludePath(PageContext pageContext);
    
    /**
     * Get if this panel is available in the current context given the 
     * request and response.
     * 
     * @param request request
     * @param response response
     * @param layout TODO
     * @return available
     */
    public boolean isAvailable(HttpServletRequest request, HttpServletResponse response, String layout);
    
    /**
     * Get if this panel is closable 
     * 
     * @return closable
     */
    public boolean isCloseable();
    
    /**
     * Get if this frame is dragable
     * 
     * @return draggable
     */
    public boolean isDragable();
    
    /**
     * Get if this frame is dropable
     * 
     * @return dropable
     */
    public boolean isDropable();
    
    /**
     * Get the default frame state.
     * 
     * @return default frame state
     */
    public String getDefaultFrameState();

	/**
	 * Set whether the panel is dragable
	 * 
	 * @param dragable dragable
	 */
	public void setDragable(boolean dragable);


	/**
	 * Set whether the panel is dropable
	 * 
	 * @param dropable dropable
	 */
	public void setDropable(boolean dropable);
	
	/**
	 * Set whether the panel is minimizalbe
	 * 
	 * @param minimizable minimizable 
	 */
	public void setMinimizable(boolean minimizable);
	
	/**
	 * Get whether the panel is minimizalbe
	 * 
	 * @return minimizable 
	 */
	public boolean isMinimizable();

}
