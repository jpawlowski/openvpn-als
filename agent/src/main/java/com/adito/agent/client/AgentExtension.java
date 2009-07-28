
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
			
package com.adito.agent.client;

/**
 * The Adito agent may be extended by using <i>Agent Extensions</i>.
 * Agent extensions may provide a Java class that is instantiated and ivoked
 * when the agent starts. This is the interface that must be implemeted to
 * support this.
 */
public interface AgentExtension {

    /**
     * Initialise the extension. Invoked when the agent starts
     * up.
     * 
     * @param agent agent instance
     * @throws Exception on any error
     */
    public void init(Agent agent) throws Exception;

    /**
     * Clean up the extension. Invoked when the agent shuts
     * down.
     */
    public void exit();

    /**
     * Get the name of the extension.
     * 
     * @return name
     */
    public String getName();
}