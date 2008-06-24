
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
			
package com.maverick.http;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public interface WebDAVConstants {

    // XML Elements
    String ACTIVELOCK_ELEM = "activelock"; //$NON-NLS-1$
    String DEPTH_ELEM = "depth"; //$NON-NLS-1$
    String LOCKTOKEN_ELEM = "locktoken"; //$NON-NLS-1$
    String TIMEOUT_ELEM = "timeout"; //$NON-NLS-1$
    String COLLECTION_ELEM = "collection"; //$NON-NLS-1$
    String HREF_ELEM = "href"; //$NON-NLS-1$
    String LINK_ELEM = "link"; //$NON-NLS-1$
    String DST_ELEM = "dst"; //$NON-NLS-1$
    String SRC_ELEM = "src"; //$NON-NLS-1$
    String LOCKENTRY_ELEM = "lockentry"; //$NON-NLS-1$
    String LOCKINFO_ELEM = "lockinfo"; //$NON-NLS-1$
    String LOCKSCOPE_ELEM = "lockscope"; //$NON-NLS-1$
    String EXCLUSIVE_ELEM = "exclusive"; //$NON-NLS-1$
    String SHARED_ELEM = "shared"; //$NON-NLS-1$
    String LOCKTYPE_ELEM = "locktype"; //$NON-NLS-1$
    String WRITE_ELEM = "write"; //$NON-NLS-1$
    String MULTISTATUS_ELEM = "multistatus"; //$NON-NLS-1$
    String RESPONSE_ELEM = "response"; //$NON-NLS-1$
    String PROPSTAT_ELEM = "propstat"; //$NON-NLS-1$
    String STATUS_ELEM = "status"; //$NON-NLS-1$
    String RESPONSEDESCRIPTION_ELEM = "responsedescription"; //$NON-NLS-1$
    String OWNER_ELEM = "owner"; //$NON-NLS-1$
    String PROP_ELEM = "prop"; //$NON-NLS-1$
    String PROPERTYBEHAVIOR_ELEM = "propertybehavior"; //$NON-NLS-1$
    String KEEPALIVE_ELEM = "keepalive"; //$NON-NLS-1$
    String OMIT_ELEM = "omit"; //$NON-NLS-1$
    String PROPERTYUPDATE_ELEM = "propertyupdate"; //$NON-NLS-1$
    String REMOVE_ELEM = "remove"; //$NON-NLS-1$
    String SET_ELEM = "set"; //$NON-NLS-1$
    String PROPFIND_ELEM = "propfind"; //$NON-NLS-1$
    String ALLPROP_ELEM = "allprop"; //$NON-NLS-1$
    String PROPNAME_ELEM = "propname"; //$NON-NLS-1$

    // WebDAV properties
    String CREATIONDATE_PROP = "creationdate"; //$NON-NLS-1$
    String DISPLAYNAME_PROP = "displayname"; //$NON-NLS-1$
    String GETCONTENTLANGUAGE_PROP = "getcontentlanguage"; //$NON-NLS-1$
    String GETCONTENTLENGTH_PROP = "getcontentlength"; //$NON-NLS-1$
    String GETCONTENTTYPE_PROP = "getcontenttype"; //$NON-NLS-1$
    String GETETAG_PROP = "getetag"; //$NON-NLS-1$
    String GETLASTMODIFIED_PROP = "getlastmodified"; //$NON-NLS-1$
    String LOCKDISCOVERY_PROP = "lockdiscovery"; //$NON-NLS-1$
    String RESOURCETYPE_PROP = "resourcetype"; //$NON-NLS-1$
    String SOURCE_PROP = "source"; //$NON-NLS-1$
    String SUPPORTEDLOCK_PROP = "supportedlock"; //$NON-NLS-1$

    // Constructs for XML document
    String XML_TEMPLATE = "<?xml version=\"1.0\" ?>"; //$NON-NLS-1$
    String XML_NAMESPACE_ATTR = "xmlns"; //$NON-NLS-1$
    String XML_DAV_NAMESPACE = "DAV:"; //$NON-NLS-1$

    String DEPTH_0 = "0"; //$NON-NLS-1$
    String DEPTH_1 = "1"; //$NON-NLS-1$
    String DEPTH_INFINITY = "infinity"; //$NON-NLS-1$
}
