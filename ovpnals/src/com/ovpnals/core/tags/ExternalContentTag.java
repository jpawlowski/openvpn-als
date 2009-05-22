
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
			
package com.ovpnals.core.tags;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseHandlerTag;

import com.ovpnals.boot.Util;

public class ExternalContentTag extends BaseHandlerTag {
  
  final static Log log = LogFactory.getLog(ExternalContentTag.class);

  String url;

  public ExternalContentTag() {
  }

  public int doEndTag() throws JspException {
      InputStream in = null;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {	
          in = new URL(url).openStream();
          Util.copy(in, baos);
          TagUtils.getInstance().write(pageContext, new String(baos.toByteArray()));
      }
      catch(IOException ioe) {
          throw new JspException("Failed to load external page.", ioe);
      }
      finally {
          Util.closeStream(in);
          Util.closeStream(baos);
      }
      return (EVAL_PAGE);
  }

  public void release() {
      super.release();
      url = null;
  }

  public int doStartTag() {
      return EVAL_BODY_INCLUDE;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}