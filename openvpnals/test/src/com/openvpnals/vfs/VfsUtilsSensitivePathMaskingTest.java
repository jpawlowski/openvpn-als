
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
			
package net.openvpn.als.vfs;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.openvpn.als.vfs.VfsUtils;

/**
 */
public class VfsUtilsSensitivePathMaskingTest {

    /**
     */
    @Test
    public void testNoPasswordSmbShare() {
        final String toTest = "smb://auser@blue.southpark.net/Share/sarah";
        String masked = VfsUtils.maskSensitiveArguments(toTest);
        assertEquals("smb://auser@blue.southpark.net/Share/sarah", masked);
    }
    
    /**
     */
    @Test
    public void testEmptyPasswordSmbShare() {
        final String toTest = "smb://auser:@blue.southpark.net/Share/sarah";
        String masked = VfsUtils.maskSensitiveArguments(toTest);
        assertEquals("smb://auser:********@blue.southpark.net/Share/sarah", masked);
    }

    /**
     */
    @Test
    public void testSmbShare() {
        final String toTest = "smb://auser:apassword@blue.southpark.net/Share/sarah";
        String masked = VfsUtils.maskSensitiveArguments(toTest);
        assertEquals("smb://auser:********@blue.southpark.net/Share/sarah", masked);
    }
    
    /**
     */
    @Test
    public void testSmbShareLongPassword() {
        StringBuffer buffer = new StringBuffer(256);
        for (int index = 0; index < 256; index++) {
            buffer.append("a");
        }
        final String password = buffer.toString();
        final String toTest = "smb://auser:" + password + "@blue.southpark.net/Share/sarah";
        String masked = VfsUtils.maskSensitiveArguments(toTest);
        assertEquals("smb://auser:********@blue.southpark.net/Share/sarah", masked);
    }
    
    /**
     */
    @Test
    public void testLocalShare() {
        final String toTest = "file://c:/temp/";
        String masked = VfsUtils.maskSensitiveArguments(toTest);
        assertEquals(toTest, masked);
    }
    
    /**
     */
    @Test
    public void testWindowsShare() {
        final String toTest = "\\\\server\\c$";
        String masked = VfsUtils.maskSensitiveArguments(toTest);
        assertEquals(toTest, masked);
    }
    
    /**
     */
    @Test
    public void testNoPasswordFtpShare() {
        final String toTest = "ftp://auser@blue.southpark.net/Share/sarah";
        String masked = VfsUtils.maskSensitiveArguments(toTest);
        assertEquals("ftp://auser@blue.southpark.net/Share/sarah", masked);
    }
    
    /**
     */
    @Test
    public void testEmptyPasswordFtpShare() {
        final String toTest = "ftp://auser:@blue.southpark.net/Share/sarah";
        String masked = VfsUtils.maskSensitiveArguments(toTest);
        assertEquals("ftp://auser:********@blue.southpark.net/Share/sarah", masked);
    }
    
    /**
     */
    @Test
    public void testFtpShare() {
        final String toTest = "ftp://auser:apassword@blue.southpark.net/Share/sarah";
        String masked = VfsUtils.maskSensitiveArguments(toTest);
        assertEquals("ftp://auser:********@blue.southpark.net/Share/sarah", masked);
    }
}