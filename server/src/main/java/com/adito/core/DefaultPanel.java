
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


/**
 * Default implementation of an {@link com.adito.core.AbstractPanel}
 * that is always 'available'.
 */
public class DefaultPanel extends AbstractPanel {
    
    /**
     * Main layout
     */
    public final static String MAIN_LAYOUT = "main";
    
    /**
     * Popup layout
     */
    public final static String POPUP_LAYOUT = "popup";

    /**
     * Constructor.
     *
     * @param id panel id
     * @param placement placement
     * @param weight weight
     * @param includePath include path
     * @param includeAttribute include attribute
     */
    public DefaultPanel(String id, int placement, int weight, String includePath, String includeAttribute) {
        this(id, placement, weight, includePath, includeAttribute, "navigation");
    }
    /**
     * Constructor.
     *
     * @param id panel id
     * @param placement placement
     * @param weight weight
     * @param includePath include path
     * @param includeAttribute include attribute
     * @param bundle bundle
     */
    public DefaultPanel(String id, int placement, int weight, String includePath, String includeAttribute, String bundle) {
        super(id, placement, weight, includePath, includeAttribute, bundle, true, true, false, false);
    }

    /**
     * Constructor.
     *
     * @param id panel id
     * @param placement placement
     * @param weight weight
     * @param includePath include path
     * @param includeAttribute include attribute
     * @param bundle bundle
     * @param closeable closeable
     */
    public DefaultPanel(String id, int placement, int weight, String includePath, String includeAttribute, String bundle, boolean closeable) {
        super(id, placement, weight, includePath, includeAttribute, bundle, true, closeable, false, false);
    }

    /**
     * Constructor.
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
     * @param droppable droppable
     */
    public DefaultPanel(String id, int placement, int weight, String includePath, String includeAttribute, String bundle, boolean minimizable, boolean closeable, boolean dragable, boolean droppable) {
        super(id, placement, weight, includePath, includeAttribute, bundle, minimizable, closeable, dragable, droppable);
    }

    /* (non-Javadoc)
     * @see com.adito.core.Panel#isAvailable(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public boolean isAvailable(HttpServletRequest request, HttpServletResponse response, String layout) {
        return true;
    }

}
