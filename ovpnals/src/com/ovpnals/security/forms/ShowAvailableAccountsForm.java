
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
			
package com.ovpnals.security.forms;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMapping;

import com.ovpnals.security.User;
import com.ovpnals.security.UserItem;
import com.ovpnals.security.UserItemModel;
import com.ovpnals.table.TableItemModel;
import com.ovpnals.table.forms.AbstractPagerForm;

/**
 */
public class ShowAvailableAccountsForm extends AbstractPagerForm<UserItem> {
    private ShowAccountsState showAccountsState;
    private final Collection<UserItem> items;
    
    /**
     */
    public ShowAvailableAccountsForm() {
        super(new UserItemModel());
        showAccountsState = ShowAccountsState.ALL;
        items = new ArrayList<UserItem>();
    }

    /**
     * @param users
     * @param session
     * @throws Exception 
     */
    public void initialize(User[] users, HttpSession session) throws Exception {
        super.initialize(session, "account");
        items.clear();
        for (User user : users) {
            items.add(new UserItem(user));            
        }
        rebuildModel();
    }
    
    /**
     * @param session
     * @throws Exception 
     */
    public void reInitialize(HttpSession session) throws Exception {
        super.initialize(session, "account");
        rebuildModel();
    }

    /**
     * @throws Exception
     */
    public void rebuildModel() throws Exception {
        TableItemModel<UserItem> model = getModel();
        model.clear();
        for (UserItem userItem : items) {
            if (canShowUser(userItem)) {
                model.addItem(userItem);
            }
        }
        getPager().rebuild(getFilterText());
    }
    

    private boolean canShowUser(UserItem userItem) {
        if (ShowAccountsState.ALL == showAccountsState) {
            return true;
        } else if (ShowAccountsState.AUTHORIZED == showAccountsState && userItem.isAuthorized()) {
            return true;
        } else if (ShowAccountsState.UNAUTHORIZED == showAccountsState && !userItem.isAuthorized()) {
            return true;
        } else if (ShowAccountsState.LOCKED == showAccountsState && userItem.isLocked()) {
            return true;
        } else if (ShowAccountsState.DISABLED == showAccountsState && userItem.isDisabled()) {
            return true;
        }
        return false;
    }
    
    /**
     * @return state
     */
    public String getShowAccountsState() {
        return showAccountsState.getState();
    }

    /**
     * @param showAccountsState
     */
    public void setShowAccountsState(String showAccountsState) {
        this.showAccountsState = ShowAccountsState.getState(showAccountsState);
    }
    
    
    @Override
    public void reset(ActionMapping mapping, ServletRequest request) {
        super.reset(mapping, request);
        showAccountsState = ShowAccountsState.ALL;
    }

    /**
     */
    public static enum ShowAccountsState {
        /** */
        ALL("all"),
        /** */
        AUTHORIZED("authorized"),
        /** */
        UNAUTHORIZED("unauthorized"),
        /** */
        DISABLED("disabled"),
        /** */
        LOCKED("locked");
        
        private final String stateName;
        
        private ShowAccountsState(String stateName) {
            this.stateName = stateName;
        }
        
        /**
         * @return state
         */
        public String getState() {
            return stateName;
        }
        
        private static ShowAccountsState getState(String stateName) {
            for (ShowAccountsState state : values()) {
                if (state.getState().equals(stateName)) {
                    return state;
                }
            }
            return ALL;
        }
    }
}