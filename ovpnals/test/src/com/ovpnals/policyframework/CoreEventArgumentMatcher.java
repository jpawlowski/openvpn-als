
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
			
package com.ovpnals.policyframework;

import org.easymock.IArgumentMatcher;

import com.ovpnals.core.CoreEvent;

/**
 */
public final class CoreEventArgumentMatcher implements IArgumentMatcher{
    private final CoreEvent expected;
    
    /**
     * @param expected
     */
    public CoreEventArgumentMatcher(CoreEvent expected) {
        this.expected = expected;
    }
    
    public boolean matches(Object arg) {
        if (!expected.getClass().equals(arg.getClass())) {
            return false;
        }
        CoreEvent foundEvent = (CoreEvent) arg;
        boolean idEqual = expected.getId() == foundEvent.getId();
        boolean stateEqual = expected.getState() == foundEvent.getState();
        return idEqual && stateEqual;
    }

    public void appendTo(StringBuffer buffer) {
        buffer.append("eqEvent(");
        buffer.append(expected.getClass().getName());
        buffer.append(" with message \"");
        buffer.append(expected.getId());
        buffer.append("\")");
    }
}