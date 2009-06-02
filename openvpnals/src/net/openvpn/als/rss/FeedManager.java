
				/*
 *  OpenVPN-ALS
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.SystemProperties;
import net.openvpn.als.boot.Util;
import com.sun.syndication.io.SyndFeedInput;

/**
 * Manages RSS feeds used for context sensitive online help.
 */
public final class FeedManager implements Runnable {
    
    /**
     * Feed connect timeout
     */
    public static final int CONNECT_TIMEOUT = 30000;
    
    /**
     * Feed read timeout
     */
    public static final int READ_TIMEOUT = 30000;

	private static final Log log = LogFactory.getLog(FeedManager.class);
	private static final int ONE_DAY_MILLIS = 1000 * 60 * 60 * 24;
	private static final int FOUR_HOURS_MILLIS = 1000 * 60 * 60 * 4;
	private static final String HOME_URL = "http://download.localhost/";
	private static final String FEED_3SP_URL = HOME_URL + "feeds/";

	private Map<String, Feed> feeds;
	private List<String> availableFeeds;
	private boolean running = false;
	private Thread thread;
	private int interval = ONE_DAY_MILLIS;
	private URL baseLocation;

	private static FeedManager instance;

	/**
	 * Default consuctor
	 * 
	 * @param baseLocation base feed location
	 */
	protected FeedManager(URL baseLocation) {
		this.baseLocation = baseLocation;
		feeds = new HashMap<String, Feed>();
		availableFeeds = new ArrayList<String>();
	}

	/**
	 * Get an instance of the feed manager
	 * 
	 * @return feed manager
	 */
	public static FeedManager getInstance() {
		if (instance == null) {
			try {
				URL baseLocation = new URL(SystemProperties.get("openvpnals.rssFeeds.baseLocation", FEED_3SP_URL));
				instance = new FeedManager(baseLocation);
			}
			catch(MalformedURLException murle) {
				try {
					URL baseLocation = new URL(FEED_3SP_URL);
					instance = new FeedManager(baseLocation);
				}
				catch(MalformedURLException murle2) {
					// Should not happen
					throw new Error("Invalid default feed location.");
				}
			}
		}
		return instance;
	}

	/**
	 * Get if the feed manager is currently checking for updates
	 * 
	 * @return checking for updates
	 */
	public boolean isUpdating() {
		return running;
	}

	/**
	 * Start checking for feed updates
	 * 
	 * @throws IllegalStateException if already updating
	 */
	public void startUpdating() {
		if (running) {
			throw new IllegalStateException("Already updating.");
		}
		try {
			loadAvailable();
		}
		catch(Exception e) {
			log.error("Failed to get initial feeds. Next update attempt will occur in 4 hours", e);
			interval = FOUR_HOURS_MILLIS;
		}
		
        thread = new Thread(this, "FeedManager");
        thread.setPriority(Thread.MIN_PRIORITY);
        running = true;
        
        if (!isTestMode()) {
            thread.start();
        }
	}

    private boolean isTestMode() {
        return Boolean.valueOf(SystemProperties.get("openvpnals.testing", "false"));
    }

	public void run() {
		try {
			while (running) {

				if (!running)
					break;

				if (log.isInfoEnabled())
					log.info("Checking for feed updates");
				
				try {
					retrieveFeeds();
					interval = ONE_DAY_MILLIS;
				}
				catch (Exception e) {
					log.error("Failed to check for updated feeds. Will check again in 4 hours");
					interval = FOUR_HOURS_MILLIS;
				} 
				sleep(interval);
			}
		} catch(InterruptedException ie) {
			
		} finally {
			running = false;
			if(log.isInfoEnabled()) {
				log.info("Stopped checking for RSS updates");
			}
		}
	}

	/**
	 * Stop checking for feed updates
	 * 
	 * @throws IllegalStateException if not updating
	 */
	public void stopUpdating() {
		if (running) {
			running = false;
			synchronized (this) {
				thread.interrupt();
			}
		} else {
            if(!isTestMode()) {
                throw new IllegalStateException("Not updating.");
            }
		}
	}

	/**
	 * Get a list of available RSS feeds as {@link Feed} objects.
	 * 
	 * @param feedName
	 * @return list of feeds
	 */
	public Feed getFeed(String feedName) {

		/**
		 * LDP - If the feed server is down this causes problems with logging into the system
		 * until the server has attempted to obtain all the feeds. If the download server is 
         * causing connection timeouts the server wont be usable for a good while. We should 
         * not care so much about feeds being available so I've removed the synchronization 
         * on the feeds hashtable. 
		 */
//		synchronized (feeds) {
//		if (!feeds.containsKey(feedName) && !feedName.equals("${rssFeed}") &&
//							availableFeeds.contains(feedName)) {
//				try {
//					Feed feed = new Feed(feedName, new SyndFeedInput(), new URL(baseLocation, feedName + ".xml"));					
//					feeds.put(feedName, feed);
//					feed.load();
//					return feed;
//				} catch (Exception ex) {
//					log.error("Failed to load feed.", ex);
//				}
//			}
			return (Feed) feeds.get(feedName);
//		}
	}
	
	protected Collection<String> getAvailableFeedNames() {
		return availableFeeds;
	}

	private static void sleep(int checkAgainIn) throws InterruptedException {
		if (log.isInfoEnabled())
			log.info("Finished checking for updates / feeds, next check will occur at " + getDateAsString(checkAgainIn));
		Thread.sleep(checkAgainIn);
	}

	private static String getDateAsString(int checkAgainIn) {
		return DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis() + checkAgainIn));
	}

	protected void retrieveFeeds() throws IOException {
		
		if (log.isInfoEnabled())
			log.info("Retrieving RSS feeds");

		Map<String, Feed> updatedFeeds = new HashMap<String, Feed>();
		synchronized (feeds) {
			for (String feedName : availableFeeds) {
				try {
					Feed feed = new Feed(feedName, new SyndFeedInput(), new URL(baseLocation, feedName + ".xml"));					
					updatedFeeds.put(feedName, feed);
					feed.load();
				} catch (Exception ex) {
					log.error("Failed to load feed.", ex);
				}
			}
			feeds = updatedFeeds;
		}
	}

	protected void loadAvailable() throws IOException {
		URL location = new URL(baseLocation, "index.txt");
		
		availableFeeds.clear();
		URLConnection conx = location.openConnection();
		conx.setConnectTimeout(CONNECT_TIMEOUT);
		conx.setReadTimeout(READ_TIMEOUT);

		if (log.isInfoEnabled()) {
			log.info("Retrieving RSS feeds index from " + location);
		}
		InputStream inputStream = null;
		try {
			inputStream = conx.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ( ( line = reader.readLine() ) != null) {
				availableFeeds.add(line);
			}
		} finally {
			Util.closeStream(inputStream);
		}
		if (log.isInfoEnabled())
			log.info("There are " + availableFeeds.size() + " available feeds");
	}
}