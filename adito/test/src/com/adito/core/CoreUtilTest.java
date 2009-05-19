
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
			
package com.adito.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class CoreUtilTest {

    @Test
    public void removeParameterFromPath() throws Exception {
        assertEquals("First parm in a list",
            "/blah.do?parm1=val1&parm2=val2",
            CoreUtil.removeParameterFromPath("/blah.do?msg=123&parm1=val1&parm2=val2", "msg"));
        assertEquals("Second parm in a list",
            "/blah.do?parm1=1&parm2=val2",
            CoreUtil.removeParameterFromPath("/blah.do?parm1=1&msg=123&parm2=val2", "msg"));
        assertEquals("Last parm in a list",
            "/blah.do?parm1=1&parm2=val2",
            CoreUtil.removeParameterFromPath("/blah.do?parm1=1&parm2=val2&msg=123", "msg"));
        assertEquals("Not in a list",
            "/blah.do?parm1=1&parm2=val2",
            CoreUtil.removeParameterFromPath("/blah.do?parm1=1&parm2=val2&msg=123", "msg"));
    }
    
    @Test
    public void filterJavascript() throws Exception {
    	assertEquals("/error.jsp", CoreUtil.filterSafeURI("javascript:alert('hello')", "/error.jsp"));
    	assertEquals("/ok.do", CoreUtil.filterSafeURI("/ok.do", "/error.jsp"));
    	assertEquals("ok.do", CoreUtil.filterSafeURI("ok.do", "/error.jsp"));
    	assertEquals("http://localhost/ok.do", CoreUtil.filterSafeURI("http://localhost/ok.do", "/error.jsp"));
    	assertEquals("https://localhost/ok.do", CoreUtil.filterSafeURI("https://localhost/ok.do", "/error.jsp"));
    }

    @Test
    public void testReplaceAllTokens() {
        String s1 = "/foo/bar/%tmp%/doo/%tmp%";
        String t1 = "%tmp%";
        String r1 = "temproary";
        assertTrue(CoreUtil.replaceAllTokens(s1,t1,r1).equals(s1.replace(t1,r1)));
        assertFalse(CoreUtil.replaceAllTokens(s1,t1,r1).equals(s1));
    }
}
