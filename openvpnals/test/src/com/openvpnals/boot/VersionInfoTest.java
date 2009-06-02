
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
			

package net.openvpn.als.boot;

import org.jdom.JDOMException;
import org.junit.Assert;
import org.junit.Test;

public class VersionInfoTest {

    @Test
    public void testDateTaggedVersion() throws JDOMException {
        VersionInfo.Version v1 = new VersionInfo.Version("1.0.0_RC1_24082007_1204");
        VersionInfo.Version v2 = new VersionInfo.Version("1.0.0_RC1_25082007_1504");
        Assert.assertTrue(v1.compareTo(v2) == 0);
    }

    @Test
    public void testVersion() throws JDOMException {
    	
    	VersionInfo.Version v1 = new VersionInfo.Version("0.2.14");
    	VersionInfo.Version v2 = new VersionInfo.Version("0.2.15");
    	VersionInfo.Version v3 = new VersionInfo.Version("0.2.15_01");
        VersionInfo.Version v4 = new VersionInfo.Version("1.0.0_RC1_24082007_1204");
    	VersionInfo.Version v5 = new VersionInfo.Version("1.0.0_RC2");
    	VersionInfo.Version v6 = new VersionInfo.Version("1.0.0");
    	VersionInfo.Version v7 = new VersionInfo.Version("1.0.1");
    	VersionInfo.Version v8 = new VersionInfo.Version("1.0.0_RC5");
    	VersionInfo.Version v9 = new VersionInfo.Version("1.0.0_RC6");
    	
    	Assert.assertTrue(v1.compareTo(v2) < 0);
    	Assert.assertTrue(v2.compareTo(v3) < 0);
    	Assert.assertTrue(v3.compareTo(v4) < 0);
    	Assert.assertTrue(v4.compareTo(v5) < 0);
    	Assert.assertTrue(v5.compareTo(v6) < 0);
    	Assert.assertTrue(v6.compareTo(v7) < 0);
    	Assert.assertTrue(v8.compareTo(v9) < 0);
    }
}
