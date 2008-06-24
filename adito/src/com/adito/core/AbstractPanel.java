
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

import javax.servlet.jsp.PageContext;

import org.apache.struts.taglib.tiles.ComponentConstants;
import org.apache.struts.tiles.ComponentContext;

/**
 * Abstract implementation of a {@link com.adito.core.Panel}.
 */
public abstract class AbstractPanel implements Panel {
    
    // Private instance variables
    private int placement;
    private int weight;
    private String id;
    private String includePath;
    private String includeAttribute;
    private String bundle;
    private boolean closeable;
    private boolean minimizable;
    private boolean dragable;
    private boolean dropable;
    private String defaultFrameState = FRAME_NORMAL;
    
	/**
     * Constructor. You must supply either an include path or an include
     * attribute. If both are supplied the path will be sued
     *
     * @param id panel id
     * @param placement placement
     * @param weight weight
     * @param includePath include path
     * @param includeAttribute include attribute
     * @param bundle bundle
     * @param minimizable minimizable
     * @param closeable closeable
     * @param dragable dragable
     * @param dropable dropable
     */
    public AbstractPanel(String id, int placement, int weight, String includePath, String includeAttribute, String bundle, boolean minimizable, boolean closeable, boolean dragable, boolean dropable) {
        super();
        this.id = id;
        this.placement = placement;
        this.weight = weight;
        this.includePath = includePath;
        this.includeAttribute = includeAttribute;
        this.bundle = bundle;
        this.minimizable = minimizable;
        this.closeable = closeable;
        this.dragable = dragable;
        this.dropable = dropable;
        defaultFrameState = FRAME_NORMAL;
    }
    
    /* (non-Javadoc)
     * @see com.adito.core.Panel#isDropable()
     */
    public boolean isDropable() {
        return dropable;
    }

    /* (non-Javadoc)
     * @see com.adito.core.Panel#isDragable()
     */
    public boolean isDragable() {
        return dragable;
    }

    /* (non-Javadoc)
     * @see com.adito.core.Panel#getPlacement()
     */
    public int getPlacement() {
        return placement;
    }

    /* (non-Javadoc)
     * @see com.adito.core.Panel#getWeight()
     */
    public int getWeight() {
        return weight;
    }
    
    /* (non-Javadoc)
     * @see com.adito.core.Panel#getId()
     */
    public String getId() {
        return id;
    }
    
    /* (non-Javadoc)
     * @see com.adito.core.Panel#getBundle()
     */
    public String getBundle() {
        return bundle;
    }
    
    /* (non-Javadoc)
     * @see com.adito.core.Panel#getTileIncludePath(javax.servlet.jsp.PageContext)
     */
    public String getTileIncludePath(PageContext pageContext) {
        if(includePath != null) {
            return includePath;
        } 
        ComponentContext cc = ((ComponentContext) pageContext.getAttribute(
            ComponentConstants.COMPONENT_CONTEXT,
            PageContext.REQUEST_SCOPE)); 
        return (String)cc.getAttribute(includeAttribute);
    }
    
    /* (non-Javadoc)
     * @see com.adito.core.Panel#isCloseable()
     */
    public boolean isCloseable() {
    	return closeable;
    }
    
    /* (non-Javadoc)
     * @see com.adito.core.Panel#getDefaultFrameState()
     */
    public String getDefaultFrameState() {
        return defaultFrameState;
    }
    
    /**
     * Set the default frame state
     * 
     * @param defaultFrameState frame state
     */
    public void setDefaultFrameState(String defaultFrameState) {
        this.defaultFrameState = defaultFrameState;
    }
	/* (non-Javadoc)
	 * @see com.adito.core.Panel#setDragable(boolean)
	 */
	public void setDragable(boolean dragable) {
		this.dragable = dragable;
		
	}
	/* (non-Javadoc)
	 * @see com.adito.core.Panel#setDropable(boolean)
	 */
	public void setDropable(boolean dropable) {
		this.dropable = dropable;		
	}

    /* (non-Javadoc)
     * @see com.adito.core.Panel#isMinimizable()
     */
    public boolean isMinimizable() {
		return minimizable;
	}

	/* (non-Javadoc)
	 * @see com.adito.core.Panel#setMinimizable(boolean)
	 */
	public void setMinimizable(boolean minimizable) {
		this.minimizable = minimizable;		
	}

}
