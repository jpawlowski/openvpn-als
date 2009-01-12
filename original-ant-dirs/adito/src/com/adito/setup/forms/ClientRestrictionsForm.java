
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
			
package com.adito.setup.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.LabelValueBean;

import com.adito.setup.ClientRestrictionItem;

public class ClientRestrictionsForm
    extends ActionForm {
  
  List clientRestrictionItems;
  String action;
  List osList;
  String selectedOs = "Windows XP";
  List selectedItems;

  public ClientRestrictionsForm() {
  }

  public void reset(ActionMapping mapping,
                    javax.servlet.http.HttpServletRequest request) {    
  }
  
  public void initialise(List clientRestrictionItems) {
    this.clientRestrictionItems = clientRestrictionItems;
    osList = new ArrayList();
    osList.add(new LabelValueBean("All", "All"));
    selectedItems = new ArrayList();
    String lastOs = null;
    for(Iterator i  = clientRestrictionItems.iterator(); i.hasNext(); ) {
      ClientRestrictionItem item = (ClientRestrictionItem)i.next();
      if(!item.getClientRestriction().getOsName().equals(lastOs)) {
        lastOs = item.getClientRestriction().getOsName();
        osList.add(new LabelValueBean(lastOs, lastOs));
      }
    }
    buildSelectedList();
  }
  
  public String getSelectedOs() {
    return selectedOs;
  }
  
  public void setSelectedOs(String selectedOs) {
    this.selectedOs =selectedOs;
  }
  
  public List getClientRestrictionItems() {
    return selectedItems;
  }

  public String getAction() {
    return action;
  }
  
  public void setAction(String action) {
    this.action = action;
  }
  
  public List getOsList() {
    return osList;
  }
  
  public void setClientRestrictionItems(List selectedItems) {
    this.selectedItems = selectedItems;
  }
  
  public void setClientRestrictionItem(int idx, ClientRestrictionItem item) {
    selectedItems.set(idx, item);
  }
  
  public ClientRestrictionItem getClientRestrictionItem(int idx) {
    return (ClientRestrictionItem)selectedItems.get(idx);
  }

  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
    if("commit".equals(request.getParameter("action"))) {
	    ActionErrors msgs = new ActionErrors();
        for(Iterator i = selectedItems.iterator(); i.hasNext(); ) {
          ClientRestrictionItem item = (ClientRestrictionItem)i.next();
          String exceptions = item.getClientRestriction().getExceptions();
          StringTokenizer t = new StringTokenizer(exceptions, "\r\n");
          while(t.hasMoreTokens()) {
            String pattern = t.nextToken();
		    try {
		      Pattern.compile(pattern);
		    }
		    catch(Exception e) {
		      msgs.add(Globals.MESSAGE_KEY, new ActionMessage("clientRestrictions.error.invalidExceptionPattern", e.getMessage(),
		          		item.getClientRestriction().getOsName(), item.getClientRestriction().getPropertyName()));	      
		    }
          }
        }
	    return msgs;
    }
    else {
      return null;
    }
  }
  
  private void buildSelectedList() {
    selectedItems.clear();
    for(Iterator i = clientRestrictionItems.iterator(); i.hasNext(); ) {
      ClientRestrictionItem item = (ClientRestrictionItem)i.next();
      if("All".equals(selectedOs) || item.getClientRestriction().getOsName().equals(selectedOs)) {
        selectedItems.add(item);
      }
    }
  }
}