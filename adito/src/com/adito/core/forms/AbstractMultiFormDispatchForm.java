
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
			

package com.adito.core.forms;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.adito.core.DefaultPanel;
import com.adito.core.Panel;
import com.adito.core.PanelManager;
import com.adito.core.actions.AbstractMultiFormDispatchAction.SubActionWrapper;
import com.adito.tabs.TabModel;

public abstract class AbstractMultiFormDispatchForm extends CoreForm implements TabModel {
    private Collection<SubActionWrapper> subActionWrappers_;
    private List<Panel> panels_;
    private String selectedTab_;
    private String subForm_;
    private final int placement_;

    public AbstractMultiFormDispatchForm(int placement) {
        placement_ = placement;
    }

    public void init(Collection<SubActionWrapper> subActionWrappers, ActionMapping mapping, HttpServletRequest request) {
        subActionWrappers_ = subActionWrappers;
    }

    public void setSubForm(String subForm) {
        subForm_ = subForm;
    }

    public String getSubForm() {
        return subForm_;
    }
    
    public boolean isSubFormEmpty () {
        return subForm_ == null || subForm_.length() == 0;
    }

    void resetForms(HttpServletRequest request) {
        if (subActionWrappers_ != null) {
            for (SubActionWrapper wrapper : subActionWrappers_)
                wrapper.getForm().reset(wrapper.getMapping(), request);
        }
    }

    public String getSelectedTab() {
        return selectedTab_;
    }

    public int getTabCount() {
        return panels_.size();
    }

    public String getTabBundle(int idx) {
        return ((Panel) panels_.get(idx)).getBundle();
    }

    public String getTabName(int idx) {
        return ((Panel) panels_.get(idx)).getId();
    }

    public String getTabTitle(int idx) {
        return null;
    }

    public void setSelectedTab(String selectedTab) {
        selectedTab_ = selectedTab;
    }

    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        panels_ = getPannels(request);
        resetForms(request);
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors allActionErrors = new ActionErrors();
        if (subActionWrappers_ != null) {
            for (SubActionWrapper wrapper : subActionWrappers_) {
                ActionErrors actionErrors = wrapper.getForm().validate(mapping, request);
                if (null != actionErrors)
                    allActionErrors.add(actionErrors);
            }
        }
        return allActionErrors;
    }
    
    public List<Panel> getPannels(HttpServletRequest request){
        return PanelManager.getInstance().getPanels(placement_, request, null, DefaultPanel.MAIN_LAYOUT);
    }
}