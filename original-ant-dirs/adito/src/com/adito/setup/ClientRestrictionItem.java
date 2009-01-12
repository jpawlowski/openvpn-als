
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
			
package com.adito.setup;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.util.LabelValueBean;

import com.adito.security.ClientRestriction;

public class ClientRestrictionItem {
  
  final static List ALLOW_DENY_LIST = new ArrayList();
  static {
    ALLOW_DENY_LIST.add(new LabelValueBean("Allow all", "true"));
    ALLOW_DENY_LIST.add(new LabelValueBean("Deny all", "false"));
  }

  private ClientRestriction clientRestriction;
  private int index; 
  
  public ClientRestrictionItem(int index, ClientRestriction clientRestriction) {
    this.clientRestriction = clientRestriction;
    this.index = index;
  }
  
  public String getIndexString() {
    return String.valueOf(index);
  }
  
  public int getIndex() {
    return index;
  }
  
  public void setIndex(int index) {
    this.index = index;
  }
  
  public ClientRestriction getClientRestriction() {
    return clientRestriction;
  }
  
  public void setAllowDeny(String allowDeny) {
    clientRestriction.setAllow(Boolean.valueOf(allowDeny).booleanValue());
  }
  
  public List getAllowDenyList() {
    return ALLOW_DENY_LIST;    
  }
  
  public String getAllowDeny() {
    return String.valueOf(clientRestriction.getAllow());
  }

}
