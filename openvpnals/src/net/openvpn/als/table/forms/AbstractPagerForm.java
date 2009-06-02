
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
			
package net.openvpn.als.table.forms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.forms.CoreForm;
import net.openvpn.als.navigation.AbstractFavoriteItem;
import net.openvpn.als.properties.PersistentSettings;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.table.Pager;
import net.openvpn.als.table.TableItem;
import net.openvpn.als.table.TableItemModel;

public class AbstractPagerForm<T extends TableItem> extends CoreForm {

    ArrayList accounts = new ArrayList();
    boolean isEditable;
    TableItemModel model;
    Pager pager;
    int startRow;
    String sortName;
    boolean sortReverse;
    int pageSize;
    HttpSession session;
    String filterText;
    String selectedItem;
    boolean filterMatchedNothing;
    List userFavorites;
    List globalFavorites;
    String defaultSortColumnId;
    boolean defaultSortReverse;

    public AbstractPagerForm(TableItemModel<T> model) {
        this.model = model;
        pager = new Pager(model);
    }
    
    public List getUserFavorites() {
        return userFavorites;
    }
    
    public void setUserFavorites(List userFavorites) {
        this.userFavorites = userFavorites;
    }
    
    public List getGlobalFavorites() {
        return globalFavorites;
    }
    
    public void setGlobalFavorites(List policyFavorites) {
        this.globalFavorites = policyFavorites;
    }
    
    public String getFavoriteType(int resourceId) {
        if(globalFavorites != null && globalFavorites.contains(new Integer(resourceId))) {
            return AbstractFavoriteItem.GLOBAL_FAVORITE;
        }
        else if(userFavorites != null && userFavorites.contains(new Integer(resourceId))) {
            return AbstractFavoriteItem.USER_FAVORITE;            
        }
        else {
            return AbstractFavoriteItem.NO_FAVORITE;
        }
    }
    
    public TableItemModel<T> getModel() {
        return model;
    }
    
    public String getFilterText() {
        return filterText;
    }
    
    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    public void initialize(HttpSession session, String defaultSortColumnId) {
        initialize(session, defaultSortColumnId, false);
    }

    public void initialize(HttpSession session, String defaultSortColumnId, boolean defaultSortReverse) {
        this.session = session;
        this.defaultSortColumnId = defaultSortColumnId;
        this.defaultSortReverse = defaultSortReverse;
        model.clear();
        
        SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(session);
        
        if(pageSize != -1) {
            pager.setPageSize(pageSize);
            PersistentSettings.setIntValue(info, "pager." + model.getId() + ".pageSize", pageSize, 10);
        }
        else {
            pager.setPageSize(PersistentSettings.getIntValue(info, "pager." + model.getId() + ".pageSize", 10));
        }
        
        if(startRow != -1) {
            pager.setStartRow(startRow);
            session.setAttribute("pager." + model.getId() + ".startRow", new Integer(startRow));
        }
        else {
            Integer startRowAttr = (Integer)session.getAttribute("pager." + model.getId() + ".startRow");
            if(startRowAttr != null) {
                pager.setStartRow(startRowAttr.intValue());
            }
            else {
                pager.setStartRow(0);
            }
        }
        checkSort();
    }
    
    public String getSortName() {
        return sortName;
    }
    
    public void setSortName(String sortName) {
        this.sortName = sortName;
    }
    
    public boolean getSortReverse() {
        return sortReverse;
    }
    
    public void setSortReverse(boolean sortReverse) {
        this.sortReverse = sortReverse;
    }
    
    public int getStartRow() {
        return startRow;
    }
    
    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public Pager getPager() {
        return pager;
    }
    
    public String getSelectedItem() {
        return selectedItem;
    }
    
    public void setSelectedItem(String selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        startRow = -1;
        sortName = null;
        sortReverse = false;
        pageSize = -1;
    }
    
    protected void checkSort() {
        if(getSortName() != null) {
            String sortId = getSortName().substring(getSortName().lastIndexOf(".") + 1);
            getPager().setSortName(sortId);
            getPager().setSortReverse(getSortReverse());
            session.setAttribute("pager." + model.getId() + ".sortName", sortId);
            session.setAttribute("pager." + model.getId() + ".sortReverse", Boolean.valueOf(getSortReverse()));
        }
        else {
            /* If no sort name has been supplied then use whatever the last sort for this table
             * id.
             */
            String lastSortName = (String)session.getAttribute("pager." + model.getId() + ".sortName");
            if(lastSortName != null) {
                getPager().setSortName(lastSortName);
                getPager().setSortReverse(((Boolean)session.getAttribute("pager." + model.getId() + ".sortReverse")).booleanValue());
            }
            else {
                String colId =  defaultSortColumnId == null ? getModel().getColumnName(0) : defaultSortColumnId;
                getPager().setSortName(colId);
                getPager().setSortReverse(defaultSortReverse);
            }
            setSortName(getPager().getSortName());
            setSortReverse(getPager().getSortReverse());
        }
    }
}