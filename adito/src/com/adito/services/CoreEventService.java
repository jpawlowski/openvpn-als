
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
			
package com.adito.services;

import com.adito.core.CoreEvent;
import com.adito.core.CoreListener;

/**
 * Interface to be implemented by classes that fire
 * {@link com.adito.core.CoreEvent} events.
 */
public interface CoreEventService {
    
    /**
     * Add a {@link CoreListener} to the list of object that will be sent
     * {@link CoreEvent}s.
     * 
     * @param listener listener to add
     */
    public void addCoreListener(CoreListener listener);

    /**
     * Remove a {@link CoreListener} from the list of object that will be sent
     * {@link CoreEvent}s.
     * 
     * @param listener listener to remove
     */
    public void removeCoreListener(CoreListener listener);
    
    /**
     * Fire a {@link CoreEvent} at all {@link CoreListener}s that have
     * registered an interest in events.
     * 
     * @param evt event to fire to all listener
     */
    public void fireCoreEvent(CoreEvent evt);
}
