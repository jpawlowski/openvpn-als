
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.wizard;

/**
 * At the end of a wizard sequence, the wizard is likely to perform a number of
 * actions. This object is used to return the status of each individual action
 * for reporting back to the user.
 */
public class WizardActionStatus {
    /**
     * The action completed ok with no errors or warnings
     */
    public final static int COMPLETED_OK = 0;
    /**
     * The action completed ok, but warnings occured that the user should be
     * aware of
     */
    public final static int COMPLETED_WITH_WARNINGS = 1;
    /**
     * The action failed to complete as errors occured.
     */
    public final static int COMPLETED_WITH_ERRORS = 2;
    // Private instance variables
    private int status;
    private String key;
    private String arg0;
    private String arg1;
    private String arg2;
    private String arg3;
    private String arg4;
    private String bundle;

    public WizardActionStatus(int status, String key) {
        this(status, key, "");
    }

    public WizardActionStatus(int status, String key, String arg0) {
        this(status, key, arg0, "");
    }

    public WizardActionStatus(int status, String key, String arg0, String arg1) {
        this(status, key, arg0, arg1, "");
    }

    public WizardActionStatus(int status, String key, String arg0, String arg1, String arg2) {
        this(status, key, arg0, arg1, arg2, "");
    }

    public WizardActionStatus(int status, String key, String arg0, String arg1, String arg2, String arg3) {
        this(status, key, arg0, arg1, arg2, arg3, "");
    }

    public WizardActionStatus(int status, String key, String arg0, String arg1, String arg2, String arg3, String arg4) {
        this(status, key, arg0, arg1, arg2, arg3, arg4, null);
    }

    public WizardActionStatus(int status, String key, String arg0, String arg1, String arg2, String arg3, String arg4, String bundle) {
        super();
        this.status = status;
        this.key = key;
        this.arg0 = arg0;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.arg4 = arg4;
        this.bundle = bundle;
    }

    /**
     * @return Returns the key.
     */
    public String getKey() {
        return key;
    }

    /**
     * @return Returns the status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return Returns the arg0.
     */
    public String getArg0() {
        return arg0;
    }

    /**
     * @return Returns the arg1.
     */
    public String getArg1() {
        return arg1;
    }

    /**
     * @return Returns the arg2.
     */
    public String getArg2() {
        return arg2;
    }

    /**
     * @return Returns the arg3.
     */
    public String getArg3() {
        return arg3;
    }

    /**
     * @return Returns the arg4.
     */
    public String getArg4() {
        return arg4;
    }
    
    public String getBundle() {
        return bundle;
    }
}
