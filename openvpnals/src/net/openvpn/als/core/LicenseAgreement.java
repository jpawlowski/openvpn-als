
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
			
package net.openvpn.als.core;

import java.io.File;

import org.apache.struts.action.ActionForward;

import net.openvpn.als.setup.LicenseAgreementCallback;

/**
 */
public class LicenseAgreement {
    
    private String title;
    private File licenseTextFile;
    private LicenseAgreementCallback callback;
    private ActionForward returnTo;
    
    public LicenseAgreement(String title, File licenseTextFile, LicenseAgreementCallback callback, ActionForward returnTo) {
        this.title = title;
        this.licenseTextFile = licenseTextFile;
        this.callback = callback;
        this.returnTo = returnTo;
    }

    /**
     * @return Returns the callback.
     */
    public LicenseAgreementCallback getCallback() {
        return callback;
    }
    /**
     * @param callback The callback to set.
     */
    public void setCallback(LicenseAgreementCallback callback) {
        this.callback = callback;
    }
    /**
     * @return Returns the licenseTextFile.
     */
    public File getLicenseTextFile() {
        return licenseTextFile;
    }
    /**
     * @param licenseTextFile The licenseTextFile to set.
     */
    public void setLicenseTextFile(File licenseTextFile) {
        this.licenseTextFile = licenseTextFile;
    }
    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return Returns the returnTo.
     */
    public ActionForward getReturnTo() {
        return returnTo;
    }
    /**
     * @param returnTo The returnTo to set.
     */
    public void setReturnTo(ActionForward returnTo) {
        this.returnTo = returnTo;
    }
}
