
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
			
package net.openvpn.als.replacementproxy;

import java.io.File;
import java.io.Serializable;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.cache.SimpleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.properties.Property;
import net.openvpn.als.properties.impl.systemconfig.SystemConfigKey;
import net.openvpn.als.security.User;

/**
 */
public class ContentCache extends SimpleCache implements HttpSessionBindingListener {
    private static final long serialVersionUID = 2384792384792374982L;
    private static final Log LOG = LogFactory.getLog(ContentCache.class);
    private ContentStash contentStash;
    private User user;

    public ContentCache(User user, File cacheDir, int maxMB, int maxObjects) {
        this(user, new ContentStash(1024 * 1024 * maxMB, maxObjects, cacheDir, 20, true));
        if (LOG.isInfoEnabled()) {
            LOG.info("Created new cache at " + cacheDir.getAbsolutePath() + " for " + user.getPrincipalName() + " of capacity "
                            + contentStash.capacity() + ".");
        }
    }

    private ContentCache(User user, ContentStash stash) {
        super(stash);
        contentStash = (ContentStash) stash;
        this.user = user;
    }

    @Override
    public synchronized boolean contains(Serializable key) {
        // commons cache doesn't evict expired entries until the item is
        // retrieved, hence force a retrieve so we get the correct answer
        retrieve(key);
        return super.contains(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueBound(HttpSessionBindingEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueUnbound(HttpSessionBindingEvent arg0) {
        if (Property.getPropertyBoolean(new SystemConfigKey("webForwards.cache.clearOnLogout"))) {
            return;
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("Clearing content cache for " + user.getPrincipalName());
        }

        contentStash.clear();

        if (LOG.isInfoEnabled()) {
            LOG.info("Cleared content cache for " + user.getPrincipalName());
        }
    }
}