
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
			
package net.openvpn.als.reverseproxy;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ReverseProxyMethodHandlerTest {
    @Test
    public void stripHttpProtocol() {
        String stripProtocol = ReverseProxyMethodHandler.stripProtocol("http://");
        assertEquals("", stripProtocol);        
    }

    @Test
    public void stripHttpsProtocol() {
        String stripProtocol = ReverseProxyMethodHandler.stripProtocol("https://");
        assertEquals("", stripProtocol);        
    }
    
    @Test
    public void stripHttp() {
        String stripProtocol = ReverseProxyMethodHandler.stripProtocol("http://google.com");
        assertEquals("google.com", stripProtocol);
    }

    @Test
    public void stripHttps() {
        String stripProtocol = ReverseProxyMethodHandler.stripProtocol("https://google.com");
        assertEquals("google.com", stripProtocol);
    }
    
    @Test
    public void stripNothing() {
        String stripProtocol = ReverseProxyMethodHandler.stripProtocol("");
        assertEquals("", stripProtocol);
    }
    
    @Test
    public void stripForwardSlash() {
        String stripProtocol = ReverseProxyMethodHandler.stripProtocol("/");
        assertEquals("/", stripProtocol);
    }
    
    @Test
    public void stripHost() {
        String stripProtocol = ReverseProxyMethodHandler.stripProtocol("google.com");
        assertEquals("google.com", stripProtocol);
    }
    
    @Test
    public void rebuildHttpLocalhost() {
        String newLocation = ReverseProxyMethodHandler.rebuildLocation("http://localhost/home", "localhost", "google.com");
        assertEquals("https://localhost/home", newLocation);
    }
    
    @Test
    public void rebuildHttpsLocalhost() {
        String newLocation = ReverseProxyMethodHandler.rebuildLocation("https://localhost/home", "localhost", "google.com");
        assertEquals("https://localhost/home", newLocation);
    }
    
    @Test
    public void rebuildLocalhostWithParmeters() {
        String newLocation = ReverseProxyMethodHandler.rebuildLocation("https://localhost/home/blah?username=karl", "localhost", "google.com");
        assertEquals("https://localhost/home/blah?username=karl", newLocation);
    }
    
    @Test
    public void rebuildHttpRemotehost() {
        String newLocation = ReverseProxyMethodHandler.rebuildLocation("http://google.com/home", "localhost", "google.com");
        assertEquals("https://localhost/home", newLocation);
    }
    
    @Test
    public void rebuildHttpsRemotehost() {
        String newLocation = ReverseProxyMethodHandler.rebuildLocation("https://google.com/home", "localhost", "google.com");
        assertEquals("https://localhost/home", newLocation);
    }
    
    @Test
    public void rebuildRemotehostWithParmeters() {
        String newLocation = ReverseProxyMethodHandler.rebuildLocation("https://google.com/home/blah?username=karl", "localhost", "google.com");
        assertEquals("https://localhost/home/blah?username=karl", newLocation);
    }
    
    @Test
    public void rebuildSlashHost() {
        String newLocation = ReverseProxyMethodHandler.rebuildLocation("/", "localhost", "google.com");
        assertEquals("/", newLocation);
    }
    
    @Test
    public void rebuildSlashPath() {
        String newLocation = ReverseProxyMethodHandler.rebuildLocation("/home/blah?username=karl", "localhost", "google.com");
        assertEquals("/home/blah?username=karl", newLocation);
    }
    
    @Test
    public void rebuildHttpUnknownHost() {
        String newLocation = ReverseProxyMethodHandler.rebuildLocation("http://unknown/", "localhost", "google.com");
        assertEquals("http://unknown/", newLocation);
    }
    
    @Test
    public void rebuildHttpsUnknownHost() {
        String newLocation = ReverseProxyMethodHandler.rebuildLocation("https://unknown/", "localhost", "google.com");
        assertEquals("https://unknown/", newLocation);
    }
}