
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
			
package com.ovpnals.core.actions;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

public abstract class XMLOutputAction
    extends Action {

  public XMLOutputAction() {
  }

  protected void sendError(String message,
                           HttpServletResponse response) throws IOException {

    Element root = new Element("error");
    Document doc = new Document(root);
    root.setText(message);
    sendDocument(doc, response);

  }

  protected void sendDocument(Document doc,
                              HttpServletResponse response) throws IOException {
    response.setHeader("Content-type", "text/xml");
    XMLOutputter o = new XMLOutputter();
    o.output(doc, response.getOutputStream());
    response.getOutputStream().close();

  }

  protected void sendSuccess(String content,
                             HttpServletResponse response) throws IOException {
    sendSuccess(content, response, null);
  }

  protected void sendSuccess(String content,
                             HttpServletResponse response,
                             Attribute[] attributes) throws IOException {

    Element root = new Element("success");

    for (int i = 0; attributes != null && i < attributes.length; i++) {
      root.setAttribute(attributes[i]);

    }
    Document doc = new Document(root);
    root.setText(content);
    sendDocument(doc, response);

  }

}