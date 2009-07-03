
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
			
package com.adito.networkplaces;

import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;

import org.junit.BeforeClass;
import org.junit.Test;

import com.adito.boot.SystemProperties;

/**
 */
public class NetworkPlaceInstallTest {
	
	static NetworkPlaceInstall install;

    @BeforeClass
    public static void setUp() throws Exception {
    	install = new NetworkPlaceInstall();
    }
    
	@Test
	public void uncPath() throws Exception {
		// Simple UNC path
		NetworkPlace np = create("\\\\windowsserver");
		NetworkPlaceUtil.convertNetworkPlace(np);
		test(np, "smb", "windowsserver", 0, "", "", "");
		
		// Simple UNC path with share
		np = create("\\\\windowsserver\\MyShare");
		NetworkPlaceUtil.convertNetworkPlace(np);
		test(np, "smb", "windowsserver", 0, "/MyShare", "", "");
		
		// Simple UNC path with share with spaces
		np = create("\\\\windowsserver\\My Share");
		NetworkPlaceUtil.convertNetworkPlace(np);
		test(np, "smb", "windowsserver", 0, "/My Share", "", "");
	}

    
	@Test
	public void localPath() throws Exception {
		// Local path
		String dir = SystemProperties.get("user.dir");
		NetworkPlace np = create(dir);
		NetworkPlaceUtil.convertNetworkPlace(np);
		test(np, "file", "", 0, dir, "", "");
		
		// Windows path
		dir = "C:\\Program Files\\Adito";
		np = create(dir);
		NetworkPlaceUtil.convertNetworkPlace(np);
		test(np, "file", "", 0, dir, "", "");
		
		// UNIX path
		dir = "/home/joeb/My Documents";
		np = create(dir);
		NetworkPlaceUtil.convertNetworkPlace(np);
		test(np, "file", "", 0, dir, "", "");
	}
	
	@Test
	public void uriPath() throws Exception {
		// FTP URI
		NetworkPlace np = create("ftp://joeb:secret@ftpserver.test.com/home/joeb");
		NetworkPlaceUtil.convertNetworkPlace(np);
		test(np, "ftp", "ftpserver.test.com", 0, "/home/joeb", "joeb", "secret");

		// FTP URI with replacements
		np = create("ftp://${session:username}:${session:password}@ftpserver.test.com/home/brett");
		NetworkPlaceUtil.convertNetworkPlace(np);
		test(np, "ftp", "ftpserver.test.com", 0, "/home/brett", "${session:username}", "${session:password}");

		// FTP URI with encoded bits
		np = create("ftp://joeb%3A:sec%2Bret@ftpserver.test.com/home/joeb/Dir%20With+Spaces");
		NetworkPlaceUtil.convertNetworkPlace(np);
		test(np, "ftp", "ftpserver.test.com", 0, "/home/joeb/Dir With Spaces", "joeb:", "sec+ret");
		
		// SMB URI 
		np = create("smb://smbserver.test.com/MyShare");
		NetworkPlaceUtil.convertNetworkPlace(np);
		test(np, "smb", "smbserver.test.com", 0, "/MyShare", "", "");

		// Absolute file URI
		np = create("file://opt/adito/logs");
		NetworkPlaceUtil.convertNetworkPlace(np);
		test(np, "file", "", 0, "/opt/adito/logs", "", "");
		
		// Windows Absolute file URI
		np = create("file:///C:/Documents and Settings/joeb");
		NetworkPlaceUtil.convertNetworkPlace(np);
		test(np, "file", "", 0, "C:/Documents and Settings/joeb", "", "");
		
	}
	
	public static void test(NetworkPlace np,  String scheme, String host, int port, String path, String username, String password) {
		assertEquals("Scheme", scheme, np.getScheme());
		assertEquals("Host", host, np.getHost());
		assertEquals("Port", port, np.getPort());
		assertEquals("Path", path, np.getPath());
		assertEquals("Username", username, np.getUsername());
		assertEquals("Password", password, np.getPassword());
	}

	public static NetworkPlace create(String path) throws Exception {
		return new DefaultNetworkPlace(0,
						0,
						"",
						"NP",
						"NP",
						null,
						path,
						0,
						"",
						"",
						NetworkPlace.TYPE_NORMAL,
						false,
						false,
						false,
						false,
						false,
						new GregorianCalendar(),
						new GregorianCalendar());

	}
}
