
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
			
package net.openvpn.als.activedirectory;

import java.io.IOException;
import java.util.Collection;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class PagedResultTemplate {
    private static final Log logger = LogFactory.getLog(PagedResultTemplate.class);
    private final Collection<String> includedOuBasesList; 
    private final Collection<String> excludedOuBasesList; 
    private final Collection<String> ouSearchBase; 
    private final int pageSize;

    PagedResultTemplate(Collection<String> includedOuBasesList, Collection<String> excludedOuBasesList, Collection<String> ouSearchBase, int pageSize) {
        this.includedOuBasesList = includedOuBasesList;
        this.excludedOuBasesList = excludedOuBasesList;
        this.ouSearchBase = ouSearchBase;
        this.pageSize = pageSize;
    }

    boolean searchForResult(InitialLdapContext context, String searchBase, String filter) throws NamingException {
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> results = context.search(searchBase, filter, constraints);
        return results.hasMore();
    }

    /**
     * @param context
     * @param filter
     * @param attributes
     * @param mapper
     * @throws NamingException 
     * @throws Exception
     */
    void search(InitialLdapContext context, String filter, String[] attributes, PagedResultMapper mapper) throws NamingException {
        if (pageSize == 0) {
            doSearch(context, filter, attributes, mapper);
        } else {
            doPagedSearch(context, filter, attributes, mapper);
        }
        assertExceptions(mapper);
    }
    
    private void assertExceptions(PagedResultMapper mapper) throws NamingException {
        if (mapper.containsExceptions()) {
            Exception e = mapper.getLastException();
            logger.error(mapper.getExceptionCount() + " exceptions occurred, throwing the last one", e);
            throw e instanceof NamingException ? (NamingException) e : new NamingException(e.getMessage());
        }
    }

    private void doSearch(InitialLdapContext context, String filter, String[] attributes, PagedResultMapper mapper)
                    throws NamingException {
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

        for (String searchBase : ouSearchBase) {
            if (logger.isDebugEnabled()) {
                logger.debug("Looking for items starting at " + searchBase + " (filter = " + filter + ")");
            }

            try {
                constraints.setReturningAttributes(attributes);
                NamingEnumeration<SearchResult> results = context.search(searchBase, filter, constraints);
                mapResults(mapper, results);
            } catch (PartialResultException e) {
                // ignore
            } catch (NamingException e) {
                mapper.processException(e);
                logger.error("Possible configuration error! Did you enter your OUs correctly? [" + searchBase + "]", e);
            }
        }
    }

    private void doPagedSearch(InitialLdapContext context, String filter, String[] attributes, PagedResultMapper mapper)
                    throws NamingException {
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        applyControls(context, pageSize);

        for (String searchBase : ouSearchBase) {
            if (logger.isDebugEnabled()) {
                logger.debug("Looking for items starting at " + searchBase + " (filter = " + filter + ")");
            }

            try {
                int currentPage = 1;
                int startPosition = 0;
                int endPosition = pageSize - 1;
                byte[] cookie = null;
        
                do {
                    String range = startPosition + "-" + endPosition;

                    if (logger.isDebugEnabled()) {
                        logger.debug("Starting search on page " + currentPage + " " + range);
                    }

                    constraints.setReturningAttributes(attributes);
                    NamingEnumeration<SearchResult> results = context.search(searchBase, filter, constraints);

                    try {
                        mapResults(mapper, results);
                    } catch (PartialResultException pre) {
                        // We're paging so we dont care and don't log anymore
                    }

                    // Examine the paged results control response
                    Control[] controls = context.getResponseControls();
                    if (controls != null) {
                        for (int index = 0; index < controls.length; index++) {
                            if (controls[index] instanceof PagedResultsResponseControl) {
                                PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[index];
                                cookie = prrc.getCookie();
                            } 
                        }
                    }

                    applyControls(context, pageSize, cookie);
                    startPosition = startPosition + pageSize;
                    endPosition = endPosition + pageSize;
                    currentPage++;
                } while ((cookie != null) && (cookie.length != 0));
            } catch (NamingException e) {
                mapper.processException(e);
                logger.error("Possible configuration error! Did you enter your OUs correctly? [" + searchBase + "]", e);
            }
        }
    }

    private void mapResults(PagedResultMapper mapper, NamingEnumeration<SearchResult> results) throws NamingException {
        while (results != null && results.hasMore()) {
            SearchResult searchResult = results.next();
            String dn = searchResult.getNameInNamespace();
            
            try {
                if (isDnValid(dn)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Included result " + dn);
                    }
                    mapper.mapSearchResult(searchResult);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Excluding result " + dn);
                    }
                }
            } catch (Exception e) {
                mapper.processSearchResultException(searchResult, e);
            }
        }
    }

    boolean isDnValid(String dn) {
        boolean included = isInOuList(includedOuBasesList, dn);
        boolean notExcluded = !isInOuList(excludedOuBasesList, dn);
        return included && notExcluded;
    }

    private static boolean isInOuList(Collection<String> basesList, String dn) {
        for (String dnToCheck : basesList) {
            if (dn.toLowerCase().endsWith(dnToCheck.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    private void applyControls(InitialLdapContext context, int pageSize) throws NamingException {
        try {
            Control[] control = new Control[] { new PagedResultsControl(pageSize, Control.CRITICAL) };
            context.setRequestControls(control);
        } catch (IOException e) {
            logger.warn("Tried to configure paged search but got error", e);
        }
    }

    private void applyControls(InitialLdapContext context, int pageSize, byte[] cookie) throws NamingException {
        try {
            context.setRequestControls(new Control[] { new PagedResultsControl(pageSize, cookie, Control.CRITICAL) });
        } catch (IOException ex) {
            logger.warn("Tried to reconfigure paged result controls with error", ex);
        }
    }
}