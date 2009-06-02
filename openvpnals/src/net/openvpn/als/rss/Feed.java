
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.Util;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Thread that wraps a {@link com.sun.syndication.io.SyndFeedInput} as is
 * responsible for downloading and maintaining the current status of the feed.
 * <p>
 * The feed may be in one of 4 states :-
 * <ul>
 * <li>Loading. The feed is currently being downloaded from the source site.</li>
 * <li>Loaded. The feed has successfuly been downloaded from the source site.</li>
 * <li>Empty. The feed has successfuly been downloaded but was empty.</li>
 * <li>Failed To Load. The feed failed to load.</li>
 * </ul>
 */
public class Feed {

    final static Log log = LogFactory.getLog(Feed.class);
    
    /**
     * Feed connect timeout
     */
    public static final int CONNECT_TIMEOUT = 5000;
    
    /**
     * Feed read timeout
     */
    public static final int READ_TIMEOUT = 5000;

    /**
     * Loading. The feed is currently being downloaded from the source site.
     */
    public final static int STATUS_LOADING = 0;

    /**
     * Loaded. The feed has successfuly been downloaded from the source site.
     */
    public final static int STATUS_LOADED = 1;

    /**
     * Empty. The feed has successfuly been downloaded but was empty.
     */
    public final static int STATUS_EMPTY = 2;

    /**
     * Failed To Load. The feed failed to load.
     */
    public final static int STATUS_FAILED_TO_LOAD = 3;

    // Private instance variables

    private String feedName;
    private SyndFeed feed;
    private int status;
    private SyndFeedInput input;
    private URL url;

    /**
     * Constructor.
     * 
     * @param feedName feed name
     * @param input feed input
     * @param url location
     * @throws IOException on error loading feed
     * @throws FeedException 
     */
    public Feed(String feedName, SyndFeedInput input, URL url) throws IOException, FeedException {
        super();
        this.url = url;
        this.feedName = feedName;
        this.input = input;
        this.status = STATUS_LOADING;
    }

    /**
     * Get the feed name.
     * 
     * @return feed
     */
    public String getFeedName() {
        return feedName;
    }

    /**
     * Get the feed object. This will only be available once the feed has
     * successfuly been downloaded otherwise <code>null</code> will be returned.
     * 
     * @return feed
     */
    public SyndFeed getFeed() {
        return feed;
    }

    /**
     * Get the feed status. See class documentation for details.
     * 
     * @return status
     */
    public int getStatus() {
        return status;
    }

    void load() throws IOException, FeedException {

		if (log.isInfoEnabled()) {
			log.info("Retrieving RSS feeds from " + url);
		}
		
		URLConnection conx = url.openConnection();
		conx.setConnectTimeout(Feed.CONNECT_TIMEOUT);
		conx.setReadTimeout(Feed.READ_TIMEOUT);
		InputStream inputStream = null;
		try {
			inputStream = conx.getInputStream();
            status = STATUS_LOADING;
            feed = input.build(new XmlReader(inputStream));
            if (log.isInfoEnabled())
            	log.info("Retrieved feed " + url);
            status = STATUS_LOADED;
            
		} catch (IOException e) {
            status = STATUS_FAILED_TO_LOAD;
            throw e;
        } catch (FeedException e) {
            status = STATUS_FAILED_TO_LOAD;
            throw e;
        } finally {
			Util.closeStream(inputStream);
        } 
    }
}
