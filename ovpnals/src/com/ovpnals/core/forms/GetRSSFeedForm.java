
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
			
package com.ovpnals.core.forms;

import com.ovpnals.rss.Feed;
import com.ovpnals.rss.FeedManager;

public class GetRSSFeedForm extends CoreForm {
	
	private Feed feed;
	private String title;

	public void init(String feedName) {
		feed = FeedManager.getInstance().getFeed(feedName);
		if(feedName!=null && feed.getStatus() != Feed.STATUS_FAILED_TO_LOAD && feed.getStatus() != Feed.STATUS_LOADING) {
			title = "Loading";
			if(feed.getStatus() == Feed.STATUS_FAILED_TO_LOAD) {
				title = "Failed";
			}
			else if(feed.getStatus() == Feed.STATUS_LOADING) {
				title = "Loading";
			}
			else {
				title = feed.getFeed().getTitle();
			}
		}
	}

	public Feed getFeed() {
		return feed;
	}

	public String getTitle() {
		return title;
	}
}
