
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
			
package net.openvpn.als.security;


public class DefaultClientRestriction implements ClientRestriction {

  private String osName;
  private String propertyName;
  private boolean allow;
  private String exceptions;
  
  public DefaultClientRestriction(String osName, String propertyName, boolean allow, String exceptions) {
    super();
    this.osName = osName;
    this.propertyName = propertyName;
    this.allow = allow;
    this.exceptions = exceptions;
  }

  public boolean getAllow() {
    return allow;
  }
  
  public void setAllow(boolean allow) {
    this.allow = allow;
  }
  
  public String getExceptions() {
    return exceptions;
  }
  
  public void setExceptions(String exceptions) {
    this.exceptions = exceptions;
  }
  public String getOsName() {
    return osName;
  }
  
  public void setOsName(String osName) {
    this.osName = osName;
  }
  
  public String getPropertyName() {
    return propertyName;
  }
  
  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }
}
