
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
			
package com.adito.replacementproxy;

public interface Replacement {
  
  public final static int REPLACEMENT_TYPE_RECEIVED_CONTENT = 0;
  public final static int REPLACEMENT_TYPE_SENT_CONTENT = 1;
  public final static int REPLACEMENT_TYPE_RECEIVED_HEADER = 2;
  public final static int REPLACEMENT_TYPE_SENT_HEADER = 3;

  public String getMimeType();  
  public void setMimeType(String mimeType);
  public String getSitePattern();
  public void setSitePattern(String sitePattern);
  public int getSequence();
  public String getMatchPattern();
  public void setMatchPattern(String matchPattern);
  public String getReplacePattern();
  public void setReplacePattern(String matchPattern);
  public int getReplaceType();
  public void setReplaceType(int replaceType);

}