
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
			
package com.ovpnals.extensions;

import java.io.IOException;

import org.jdom.Element;

import com.ovpnals.policyframework.Resource.LaunchRequirement;
import com.ovpnals.security.SessionInfo;

public interface ExtensionType {
    public void start(ExtensionDescriptor descriptor, Element element) throws ExtensionException;

    public void activate() throws ExtensionException;

    public void stop() throws ExtensionException;

    public boolean canStop() throws ExtensionException;

    public void verifyRequiredElements() throws ExtensionException;

    public boolean isHidden();

    public String getType();
    
    public void descriptorCreated(Element element, SessionInfo session)  throws IOException;
    
    public String getTypeBundle();

    public LaunchRequirement getLaunchRequirement();
}
