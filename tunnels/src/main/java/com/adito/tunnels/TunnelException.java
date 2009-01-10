
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
			
package com.adito.tunnels;

import com.adito.core.CoreException;


/**
 * Specialisation of {@link CoreException} for exceptions generate during
 * use of tunnels.
 */
public class TunnelException extends CoreException {
	/**
	 * Remote tunnel already opened by you
	 */
	public final static int REMOTE_TUNNEL_IN_USE = 1;
	
	/**
	 * Remote tunnel already opened by someone else
	 */
	public final static int REMOTE_TUNNEL_LOCKED = 2;
	
	/**
	 * Requested port on server is already in use
	 */
	public final static int PORT_ALREADY_IN_USE = 3;
	
	/**
	 * General internal error during processing of tunnels. Exception text
	 * will be arg0
	 */
	public final static int INTERNAL_ERROR = 4;

	/**
	 * Agent refused to start local tunnel
	 */
	public final static int AGENT_REFUSED_LOCAL_TUNNEL = 5;

	/**
	 * Agent refused to stop a local tunnel
	 */
	public final static int AGENT_REFUSED_LOCAL_TUNNEL_STOP = 6;
	
	/**
	 * Error category
	 */
	public final static String ERROR_CATEGORY = "tunnels";

	/**
	 * Constructor.
	 *
	 * @param code
	 */
	public TunnelException(int code) {
		super(code, ERROR_CATEGORY);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param cause
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public TunnelException(int code, Throwable cause, String arg0, String arg1, String arg2, String arg3) {
		super(code, ERROR_CATEGORY, TunnelPlugin.MESSAGE_RESOURCES_KEY, cause, arg0, arg1, arg2, arg3);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param cause
	 * @param arg0
	 */
	public TunnelException(int code,Throwable cause, String arg0) {
		super(code, ERROR_CATEGORY, TunnelPlugin.MESSAGE_RESOURCES_KEY, cause, arg0);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param bundle
	 * @param cause
	 */
	public TunnelException(int code, Throwable cause) {
		super(code, ERROR_CATEGORY, TunnelPlugin.MESSAGE_RESOURCES_KEY, cause);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param arg0
	 */
	public TunnelException(int code, String arg0) {
		super(code, ERROR_CATEGORY, arg0);
	}
}
