
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
			
package net.openvpn.als.agent.client.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void testIsSupportedJREGreatherThan() {
		System.setProperty("java.version", "1.4");
		assertTrue("1.3 supported by 1.4", Utils.isSupportedJRE("+1.3"));
		assertTrue("1.4 supported by 1.4", Utils.isSupportedJRE("+1.4"));
		assertFalse("1.4.5 not supported by 1.4", Utils.isSupportedJRE("+1.4.5"));
		assertFalse("1.5 not supported by 1.4", Utils.isSupportedJRE("+1.5"));
		assertFalse("1.5.0 not supported by 1.4", Utils.isSupportedJRE("+1.5.0"));

		System.setProperty("java.version", "1.1.1");
		assertTrue("+1.0 is supported by 1.1.1", Utils.isSupportedJRE("+1.0"));
		assertTrue("1.1 supported by 1.1.1", Utils.isSupportedJRE("1.1"));
		assertTrue("1.1.1 supported by 1.1.1", Utils.isSupportedJRE("1.1.1"));
		assertFalse("1.1.0 not supported by 1.1.1", Utils.isSupportedJRE("1.1.0"));
		assertTrue("1.1.1 supported by 1.1.1", Utils.isSupportedJRE("+1.1.1"));
		assertFalse("1.1.2 not supported by 1.1.1", Utils.isSupportedJRE("+1.1.2"));
		assertFalse("1.4.5 not supported by 1.1.1", Utils.isSupportedJRE("+1.4.5"));
	}

	@Test
	public void testIsSupportedJRELessThan() {
		System.setProperty("java.version", "1.4");
		assertFalse("-1.3 not supported by 1.4", Utils.isSupportedJRE("-1.3"));
		assertTrue("-1.4 supported by 1.4", Utils.isSupportedJRE("-1.4"));
		assertTrue("-1.5 supported by 1.4", Utils.isSupportedJRE("-1.5"));

		System.setProperty("java.version", "1.1.4");
		assertTrue("-1.3 not supported by 1.1.4", Utils.isSupportedJRE("-1.3"));
		assertFalse("+1.3 not supported by 1.1.4", Utils.isSupportedJRE("+1.3"));
	}

	@Test
	public void testIsSupportedOSVersionLessThan() {
		System.setProperty("os.version", "6.0");
		assertFalse("-5.0 not supported by 6.0", Utils.isSupportedOSVersion("-5.0"));
		assertFalse("-5.9 not supported by 6.0", Utils.isSupportedOSVersion("-5.9"));
		assertTrue("6.0 supported by 6.0", Utils.isSupportedOSVersion("-6.0"));
		assertTrue("6.1 supported by 6.0", Utils.isSupportedOSVersion("-6.1"));
	}
}
