
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.security;

import net.openvpn.als.core.CoreException;


/**
 * Specialisation of {@link CoreException} for exceptions generate during
 * use of <i>Extensions</i>.
 */
public class ExtensionException extends CoreException {
	/**
	 * Extension requires agent which is not available
	 */
	public final static int NO_AGENT = 1;
	
	/**
	 * The agent refused to launch the application
	 */
	public final static int AGENT_REFUSED_LAUNCH = 2;

	/**
	 * Generic failed to launch error
	 */
	public final static int FAILED_TO_LAUNCH = 3;

	/**
	 * A request for an unknown application extension has been made. 
	 */
	public final static int INVALID_EXTENSION = 4;


	/**
	 * Generic internal error processing an extension.
	 */
	public static final int INTERNAL_ERROR = 5;

    /**
     * Failed to parse descriptor. The XML descriptor could not be parsed 
     * because something is fundamentally wrong with the format of the file. 
     */
    public static final int FAILED_TO_PARSE_DESCRIPTOR = 6;

    /**
     * Unknown extension type. Arg0 is the requested type, Arg1 is the id
     * of the extension bundle attempting to use the type
     */
    public static final int UNKNOWN_EXTENSION_TYPE = 7;
    
    /**
     * The descriptor was passed but contains invalid details.
     */
    public static final int FAILED_TO_PROCESS_DESCRIPTOR = 8;

    /**
     * The version of OpenVPNALS hosting the plugin is not 
     * sufficient. Arg0 is the plugin name, Arg1 is the required version
     */
    public static final int INSUFFICIENT_OpenVPNALS_HOST_VERSION = 9;
	
    /**
     * The plugin instance could not be created for some reason.
     * Arg0 will be the plugin name, Arg1 will be the plugin class name
     * and Arg2 will be the error text.
     */
    public static final int FAILED_TO_CREATE_PLUGIN_INSTANCE = 10;

    /**
     * The bundle is not in the required state for the requested operation.
     * Arg0 will contain the bundle name and Arg1 will contain any additional
     * information.
     */
    public static final int INVALID_EXTENSION_BUNDLE_STATUS = 11;

    /**
     * Unknown plugin. Arg0 is the plugin id
     */
    public static final int UNKNOWN_PLUGIN = 12;

    /**
     * An attempt was made to start an extension that requires
     * a dependency that is not installed. Arg0 is the dependency
     * name required, Arg1 is the bundle that requries the
     * dependency.
     */
    public static final int DEPENDENCY_NOT_INSTALLED = 13;

    /**
     * An attempt was made to start an extension that requires
     * a dependency that is not started. Arg0 is the dependency
     * name required, Arg1 is the bundle that requries the
     * dependency.
     */
    public static final int DEPENDENCY_NOT_STARTED = 14;

	/**
	 * Licensing error. Arg0 is the message.
	 */
	public static final int LICENSE_ERROR = 15;
    
	/**
	 * Error category
	 */
	public final static String ERROR_CATEGORY = "extensions";



	/**
	 * Constructor.
	 *
	 * @param code
	 */
	public ExtensionException(int code) {
		super(code, ERROR_CATEGORY);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param bundle
	 * @param cause
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public ExtensionException(int code, String bundle, Throwable cause, String arg0, String arg1, String arg2, String arg3) {
		super(code, ERROR_CATEGORY, bundle, cause, arg0, arg1, arg2, arg3);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param bundle
	 * @param cause
	 * @param arg0
	 */
	public ExtensionException(int code,String bundle, Throwable cause, String arg0) {
		super(code, ERROR_CATEGORY, bundle, cause, arg0);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param bundle
	 * @param cause
	 */
	public ExtensionException(int code, String bundle, Throwable cause) {
		super(code, ERROR_CATEGORY, bundle, cause);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param arg0
	 */
	public ExtensionException(int code, String arg0) {
		super(code, ERROR_CATEGORY, arg0);
	}

    /**
     * Constructor.
     *
     * @param code
     * @param arg0
     * @param arg1
     */
    public ExtensionException(int code, String arg0, String arg1) {
        super(code, ERROR_CATEGORY, DEFAULT_BUNDLE, null, arg0, arg1, null, null);
    }

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param cause
	 */
	public ExtensionException(int code, Throwable cause) {
		super(code, ERROR_CATEGORY, DEFAULT_BUNDLE, cause, cause.getMessage());
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param cause
	 * @param arg0 
	 */
	public ExtensionException(int code, Throwable cause, String arg0) {
		super(code, ERROR_CATEGORY, DEFAULT_BUNDLE, cause, arg0);
	}

    /**
     * Constructor.
     * 
     * @param code
     * @param arg0
     * @param arg1
     * @param arg2
     * @param cause
     */
    public ExtensionException(int code, String arg0, String arg1, String arg2, Throwable cause) {
        super(code, ERROR_CATEGORY, DEFAULT_BUNDLE, cause, arg0, arg1, arg2, null);
    }
}
