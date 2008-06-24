
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
			
package com.adito.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.adito.testcontainer.AbstractTest;

/**
 */
public class IpRestrictionsTest  extends AbstractTest {

    /**
     * @throws Exception
     */
    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        setUp("");
    }

    /**
     * @throws Exception
     */
    @Before
    @After
    public void intialize() throws Exception {
        for (IpRestriction ipRestriction : getIpRestrictions()) {
            deleteIpRestriction(ipRestriction);
        }
    }

    /**
     * @throws Exception
     */
    @Test
    public void localLoopback() throws Exception {
        addIpRestriction("*.*.*.*", true);
        assertTrue(isValid("localhost"));
    }

    /**
     * @throws Exception
     */
    @Test
    public void localHost() throws Exception {
        addIpRestriction("*.*.*.*", true);
        assertTrue(isValid("127.0.0.1"));
    }

    /**
     * @throws Exception
     */
    @Test
    public void noIpRestrictions() throws Exception {
        addIpRestriction("*.*.*.*", true);
        assertTrue(isValid("192.168.1.16"));
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void deniedAddress() throws Exception {
        addIpRestriction("*.*.*.*", true);
        addIpRestriction("192.168.1.16", false);
        assertFalse(isValid("192.168.1.16"));
        assertTrue(isValid("192.168.1.17"));
    }

    /**
     * @throws Exception
     */
    @Test
    public void deniedAndAllowedAddress() throws Exception {
        addIpRestriction("*.*.*.*", true);
        addIpRestriction("192.168.1.16", false);
        assertFalse(isValid("192.168.1.16"));        
        addIpRestriction("192.168.1.16", true);
        assertTrue(isValid("192.168.1.16"));
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void allowedAddress() throws Exception {
        addIpRestriction("*.*.*.*", true);
        addIpRestriction("192.168.1.17", false);
        assertTrue("Allowed", isValid("192.168.1.16"));
        assertFalse("Denied", isValid("192.168.1.17"));
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void allowedThenDeniedThenAllowedAddress() throws Exception {
        addIpRestriction("*.*.*.*", true);
        addIpRestriction("192.168.1.*", false);
        addIpRestriction("192.168.1.14", true);
        assertTrue("Allowed", isValid("192.168.1.14"));
        assertFalse("Denied", isValid("192.168.1.17"));
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void noAllowedWithDeniedAddress() throws Exception {
        addIpRestriction("*.*.*.*", true);
        addIpRestriction("192.168.1.16", false);
        assertFalse(isValid("192.168.1.16"));
        assertTrue(isValid("192.168.1.17"));
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void exactMatch() throws Exception {
        addIpRestriction("*.*.*.*", true);
        addIpRestriction("192.168.1.16", false);
        assertFalse(isValid("192.168.1.16"));
        assertTrue(isValid("192.168.1.17"));
    }

    /**
     * @throws Exception
     */
    @Test
    public void wildcardMatch() throws Exception {
        addIpRestriction("*.*.*.*", true);
        addIpRestriction("192.168.1.*", false);
        assertFalse(isValid("192.168.1.16"));
        assertFalse(isValid("192.168.1.17"));
        assertTrue(isValid("192.168.10.16"));
    }

    /**
     * @throws Exception
     */
    @Test
    public void wildcardInMiddleMatch() throws Exception {
        addIpRestriction("*.*.*.*", true);
        addIpRestriction("192.168.*.16", false);
        assertFalse(isValid("192.168.1.16"));
        assertFalse(isValid("192.168.10.16"));
        assertTrue(isValid("192.168.1.17"));
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void wildcardAtStartMatch() throws Exception {
        addIpRestriction("*.*.*.*", true);
        addIpRestriction("*.168.1.16", false);
        assertFalse(isValid("192.168.1.16"));
        assertFalse(isValid("193.168.1.16"));
        assertTrue(isValid("192.168.1.17"));
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void cidrEightMatch() throws Exception {
        addIpRestriction("*.*.*.*", true);
        addIpRestriction("192.168.1.0/8", false);
        assertFalse(isValid("192.168.1.16"));
        assertFalse(isValid("192.168.1.17"));
        assertTrue(isValid("193.168.1.16"));
    }

    /**
     * @throws Exception
     */
    @Test
    public void cidrSixteenMatch() throws Exception {
        addIpRestriction("*.*.*.*", true);
        addIpRestriction("192.168.1.0/16", false);
        assertFalse(isValid("192.168.1.16"));
        assertFalse(isValid("192.168.1.17"));
        assertTrue(isValid("192.169.1.16"));
    }

    /**
     * @throws Exception
     */
    @Test
    public void cidrTwentyFourMatch() throws Exception {
        addIpRestriction("*.*.*.*", true);
        addIpRestriction("192.168.1.0/24", false);
        assertFalse(isValid("192.168.1.16"));
        assertFalse(isValid("192.168.1.17"));
        assertTrue(isValid("192.168.10.16"));
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void cidrThirtyTwoMatch() throws Exception {
        addIpRestriction("*.*.*.*", true);
        addIpRestriction("192.168.0.8/30", false);
        assertFalse(isValid("192.168.0.9"));
        assertFalse(isValid("192.168.0.10"));
        assertFalse(isValid("192.168.0.11"));
        assertTrue(isValid("192.168.0.12"));
        assertTrue(isValid("192.168.0.13"));
        assertTrue(isValid("192.168.0.14"));
        assertTrue(isValid("192.168.0.15"));
        assertTrue(isValid("192.168.0.16"));
    }
    
    /**
     */
    @Test
    public void z() {
    }

    private static boolean isValid(String ipAddress) throws Exception {
        return getSystemDatabase().verifyIPAddress(ipAddress);
    }

    private static IpRestriction[] getIpRestrictions() throws Exception {
        return getSystemDatabase().getIpRestrictions();
    }

    private static void addIpRestriction(String ipAddress, boolean isGrant) throws Exception {
        getSystemDatabase().addIpRestriction(ipAddress, IpRestriction.getType(ipAddress, isGrant));
    }
    
    private static void deleteIpRestriction(IpRestriction ipRestriction) throws Exception {
        getSystemDatabase().removeIpRestriction(ipRestriction.getID());
    }

    private static SystemDatabase getSystemDatabase() {
        return SystemDatabaseFactory.getInstance();
    }
}
