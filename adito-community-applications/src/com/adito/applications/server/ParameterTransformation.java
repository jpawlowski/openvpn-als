
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
			
package com.adito.applications.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.XMLElement;

public class ParameterTransformation {
    
    final static Log log = LogFactory.getLog(ParameterTransformation.class);

    XMLElement el;
    ServerLauncher launcher;
    Transformation trans;
    String outputParam;
    String inputParam;

    ParameterTransformation(XMLElement el, ServerLauncher launcher) throws IOException {

        if (el.getAttribute("class") == null || el.getAttribute("input") == null || el.getAttribute("output") == null)
            throw new IOException("<transform> element requires class, input and output attributes!");

        this.el = el;
        this.launcher = launcher;

    }

    public void processTransformation() throws IOException {

        String classFile = el.getAttribute("class").toString();
        inputParam = el.getAttribute("input").toString();
        outputParam = el.getAttribute("output").toString();

        File f = new File(launcher.getInstallDir(), classFile + ".class");

        if (!f.exists())
            throw new IOException(classFile + " does not exist! Did you forget to place the .class file in a <file> element?");

        FileInputStream in = new FileInputStream(f);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buf = new byte[16384];
        int read;

        while ((read = in.read(buf)) > -1) {
            out.write(buf, 0, read);
        }

        in.close();
        buf = out.toByteArray();
        try {

            log.debug("Loading transformation class " + classFile);
            Class cls = ByteArrayClassLoader.getInstance().createFromByteArray(classFile, buf, 0, buf.length);

            log.debug("Creating transformation instance");
            Transformation t = (Transformation) cls.newInstance();

            log.debug("Invoking transformation");
            launcher.addParameter(outputParam, t.transform(launcher.replaceTokens(inputParam)));
        } catch (Exception ex) {
            log.debug("Exception in Transformation class: " + ex.getMessage());
        }

    }
}
