
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class BrowserCheckerTest  {

	@Test
	public void browserExactMatch() throws Exception {
		/*
		 * Match IE6 = IE6 
		 */
		assertTrue(new BrowserChecker(
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)").isBrowserVersion(BrowserChecker.BROWSER_IE, 6));

		/*
		 * Match Opera 9 = Opera 9 
		 */
		assertTrue(new BrowserChecker(
			"Opera/9.01 (X11; Linux i686; U; en)").isBrowserVersion(BrowserChecker.BROWSER_OPERA, 9));

		/*
		 * Reject PSP = IE4 
		 */
		assertFalse(new BrowserChecker(
			"Mozilla/4.0 (PSP (PlayStation Portable); 2.00)").isBrowserVersion(BrowserChecker.BROWSER_IE, 4));
	}

	@Test
	public void browserGreaterVersion() throws Exception {
		/*
		 * Match IE5 >= IE5 
		 */
		assertTrue(new BrowserChecker(
			"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0)").isBrowserVersionExpression(BrowserChecker.BROWSER_IE, "+=5"));

		/*
		 * Reject IE5.5 > IE6  
		 */
		assertFalse(new BrowserChecker(
			"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0)").isBrowserVersionExpression(BrowserChecker.BROWSER_IE, "+6"));

		/*
		 * Reject IE5.5 > Firefox2 
		 */
		assertFalse(new BrowserChecker(
			"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0)").isBrowserVersionExpression(BrowserChecker.BROWSER_FIREFOX, ">2"));
	}

	@Test
	public void browserAnyVersion() throws Exception {		
		/*
		 * Match Firefox1 = ANY Firefox 
		 */
		assertTrue(new BrowserChecker(
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.7.9) Gecko/20050711 Firefox/1.0.5").isBrowserVersionExpression(BrowserChecker.BROWSER_FIREFOX, "*"));

		
		/*
		 * Match Opera 8 = Opera Any Version
		 */
		assertTrue(new BrowserChecker(
			"Opera/8.0 (X11; Linux i686; U; cs)").isBrowserVersionExpression(BrowserChecker.BROWSER_OPERA, "*"));
		
		/*
		 * Reject Omniweb = Firefox Any Version
		 */
		assertFalse(new BrowserChecker(
			"Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en-US) AppleWebKit/125.4 (KHTML, like Gecko, Safari) OmniWeb/v563.51").isBrowserVersion(BrowserChecker.BROWSER_FIREFOX, -1));
		
	}

	@Test
	public void browserLessVersion() throws Exception {
		
		/*
		 * Match Firefox1 < Firefox2 
		 */
		assertTrue(new BrowserChecker(
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.7.9) Gecko/20050711 Firefox/1.0.5").isBrowserVersionExpression(BrowserChecker.BROWSER_FIREFOX, "-2"));
		
		/*
		 * Match Opera 8 <= Opera 9 
		 */
		assertTrue(new BrowserChecker(
			"Mozilla/5.0 (Windows NT 5.1; U; en) Opera 8.50").isBrowserVersionExpression(BrowserChecker.BROWSER_OPERA, "-=9"));
		
		/*
		 * Reject Safari 1 < IE4 
		 */
		assertFalse(new BrowserChecker(
			"Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en) AppleWebKit/124 (KHTML, like Gecko) Safari/125").isBrowserVersionExpression(BrowserChecker.BROWSER_IE, "<4"));

		/*
		 * Match Opera 8 <= Opera ANY 
		 */
		assertTrue(new BrowserChecker(
			"Mozilla/5.0 (Windows NT 5.1; U; en) Opera 8.50").isBrowserVersionExpression(BrowserChecker.BROWSER_OPERA, "*"));
	}

	@Test
	public void unknownUserAgents() throws Exception {
		
		/*
		 * Reject XXXXXX < Firefox2 
		 */
		assertFalse(new BrowserChecker(
			"XXXXXX").isBrowserVersionExpression(BrowserChecker.BROWSER_FIREFOX, "-2"));
		
		/*
		 * Match Unknown Safari > Safar1 
		 */
		assertFalse(new BrowserChecker(
			"Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en) AppleWebKit/124 (KHTML, like Gecko) Safari/999").isBrowserVersionExpression(BrowserChecker.BROWSER_SAFARI, "+1"));
		
		/*
		 * Match Unknown Safari > Safar1 
		 */
		assertFalse(new BrowserChecker(
			"Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en) AppleWebKit/124 (KHTML, like Gecko) Safari/999").isBrowserVersionExpression(BrowserChecker.BROWSER_SAFARI, "+1"));
		
	}
}