
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
			
package net.openvpn.als.rss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

/**
 */
public class FeedManagerTest  {
    
    @Test
    public void retrieveFeeds() throws Exception {
    	FeedManager mgr = new FeedManager(getClass().getClassLoader().getResource("resources/rss/"));  
    	
    	// Get the available feeds
    	mgr.loadAvailable();
    	Collection<String> feedNames = mgr.getAvailableFeedNames();
        assertTrue(feedNames.size() == 5);
        
        // Retrieve them
    	mgr.retrieveFeeds();
    	assertEquals(mgr.getFeed("testFeed1").getStatus(), Feed.STATUS_LOADED);
    	assertEquals(mgr.getFeed("testFeed2").getStatus(), Feed.STATUS_LOADED);
    	assertEquals(mgr.getFeed("testFeed3").getStatus(), Feed.STATUS_LOADED);
    	assertEquals(mgr.getFeed("testFeed4").getStatus(), Feed.STATUS_LOADED);
    	assertEquals(mgr.getFeed("testFeed5").getStatus(), Feed.STATUS_LOADED);
    }
}
