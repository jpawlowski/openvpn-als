
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
			
package com.adito.policyframework;

import java.io.Serializable;
import java.util.Iterator;


public final class PrincipalCacheIterator<T extends Principal> implements Iterator<T> {
    private final Serializable[] keysForGroup;
    private final PrincipalCache<T> principalContainer;
    private int index;
    private T nextItem;
    
    public PrincipalCacheIterator(Serializable[] keysForGroup, PrincipalCache<T> principalContainer) {
        this.keysForGroup = keysForGroup;
        this.principalContainer = principalContainer;
    }
    
    public boolean hasNext() {
        nextItem = findNextItem();
        return nextItem != null;
    }
    
    @SuppressWarnings("unchecked")
    private T findNextItem() {
        for (; index < keysForGroup.length; index++) {
            Serializable itemAt = keysForGroup[index];
            T retrieve = (T) principalContainer.retrievePrincipal(itemAt.toString());
            if (retrieve != null) {
                index++;
                return retrieve;
            }
        }
        return null;
    }

    public T next() {
        return nextItem;
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove not implemented");
    }
}