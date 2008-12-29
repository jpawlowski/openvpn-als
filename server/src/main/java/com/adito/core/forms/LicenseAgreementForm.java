
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
			
package com.adito.core.forms;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.core.LicenseAgreement;

/**
 */
public class LicenseAgreementForm extends CoreForm {

    private LicenseAgreement agreement;
    private final static Log log = LogFactory.getLog(LicenseAgreementForm.class);
    
    
    public void setAgreement(LicenseAgreement agreement) {
        this.agreement = agreement;
    }
    
    public void reset() {
        agreement = null;
    }

    public LicenseAgreement getAgreement() {
        return agreement;
    }
    
    public String getAgreementText() {
        InputStream in = null;
        StringWriter sw = new StringWriter();
        try {
            in = new FileInputStream(getAgreement().getLicenseTextFile());
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String l = null;
            PrintWriter pw = new PrintWriter(sw, true);
            while( ( l = r.readLine() ) != null) {
                pw.println(l);
            }
            
        }
        catch(Exception e) {
            log.error("Failed to load license text.", e);
            e.printStackTrace(new PrintWriter(sw));
        }
        return sw.toString();
    }
}
