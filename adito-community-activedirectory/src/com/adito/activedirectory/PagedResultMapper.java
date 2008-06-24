
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
			
package com.adito.activedirectory;

import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

import com.adito.security.UserDatabaseException;

interface PagedResultMapper {
    
    /**
     * @param searchResult
     * @throws Exception 
     */
    void mapSearchResult(SearchResult searchResult) throws NamingException, UserDatabaseException;

    /**
     * @param searchResult
     * @throws Exception 
     */
    void processSearchResultException(SearchResult searchResult, Exception e);
    
    /**
     * @param e
     */
    void processException(Exception e);
    
    /**
     * @return <tt>true</tt> if exceptions occurred
     */
    boolean containsExceptions();
    
    /**
     * @return exception count
     */
    int getExceptionCount();
    
    /**
     * @return exception
     */
    Exception getLastException();
}