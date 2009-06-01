
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
			
package net.openvpn.als.applications.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.XMLElement;

/**
 * This utility will take an input file and replace any number of tokens before
 * outputing it to an output file, which may or may not be temporary.
 * 
 * @author Lee David Painter
 */
public class FileReplacement {
    
    final static Log log = LogFactory.getLog(FileReplacement.class);

    Hashtable tokens = new Hashtable();
    File templateFile;
    File outputFile;
    File cwd;
    String parameter;
    String encoding = "UTF8";

    public FileReplacement(File cwd) {
        this.cwd = cwd;
    }

    public String getId() {
        return parameter;
    }

    void processReplacementXML(XMLElement el, ServerLauncher launcher) throws IOException {
        if (!el.getName().equalsIgnoreCase("replacements")) {
            throw new IOException("Error! Element is not <replacements>");
        }

        if (el.getAttribute("templateFile") == null) {
            throw new IOException("Error! <replacements> element requires 'templateFile' attribute");
        }

        templateFile = new File(cwd, el.getAttribute("templateFile").toString());

        if (log.isDebugEnabled())
            log.debug("Template file will be " + templateFile.getAbsolutePath());

        if (el.getAttribute("outputFile") != null) {
            outputFile = new File(cwd, el.getAttribute("outputFile").toString());
        } else {
            outputFile = getTempFile(cwd);
        }

        if (log.isDebugEnabled())
            log.debug("Output file will be " + outputFile.getAbsolutePath());

        if (el.getAttribute("parameter") == null) {
            throw new IOException("Error! <replacements> element requires 'parameter' attribute");
        }

        parameter = el.getAttribute("parameter").toString();
        launcher.addParameter(parameter, outputFile.getAbsolutePath());

        if (el.getAttribute("encoding") != null)
            encoding = el.getAttribute("encoding").toString();

        if (log.isDebugEnabled())
            log.debug("Output file will be encoded in " + encoding);

        for (Enumeration e = el.getChildren().elements(); e.hasMoreElements();) {
            XMLElement child = (XMLElement) e.nextElement();

            if (!child.getName().equalsIgnoreCase("replace")) {
                throw new IOException("Error! <" + child.getName() + "> is not a supported element of <replacements>");
            }

            if (child.getAttribute("token") == null || child.getAttribute("value") == null) {
                throw new IOException("Error! <replace> element requires 'token' and 'value' attributes");
            }

            tokens.put(child.getAttribute("token").toString(), child.getAttribute("value").toString());
        }

    }

    void createReplacementsFile(ServerLauncher launcher) throws IOException {

        FileInputStream in = new FileInputStream(templateFile);
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();

        byte[] buf = new byte[4096];
        int read;

        while ((read = in.read(buf)) > -1) {
            tmp.write(buf, 0, read);
        }

        // First replace all standard parameters etc
        // String outputContent = null /*launcher.replaceTokens(new
        // String(tmp.toByteArray(), encoding))*/;
        String outputContent = launcher.replaceTokens(new String(tmp.toByteArray(), encoding));

        for (Enumeration e = tokens.keys(); e.hasMoreElements();) {

            String token = (String) e.nextElement();
            String value = (String) tokens.get(token);

            if (log.isDebugEnabled())
                log.debug("Processing replacement token " + token + "=" + value);

            // Perform replacement in String
            outputContent = ServerLauncher.replaceAllTokens(outputContent, token, value);

        }

        FileOutputStream out = new FileOutputStream(outputFile);
        try {
            out.write(outputContent.getBytes(encoding));
        } finally {
            out.close();
        }

        ClientCacheRemover.getInstance().trackFileForRemoval(outputFile);

    }

    static File getTempFile(File near) throws IOException {
        String path = null;
        if (near != null)
            if (near.isFile())
                path = near.getParent();
            else if (near.isDirectory())
                path = near.getPath();

        Random wheel = new Random(); // seeded from the clock
        File tempFile = null;
        do {
            // generate random a number 10,000,000 .. 99,999,999
            int unique = (wheel.nextInt() & Integer.MAX_VALUE) % 90000000 + 10000000;
            tempFile = new File(path, Integer.toString(unique) + ".tmp");
        } while (tempFile.exists());
        // We "finally" found a name not already used. Nearly always the first
        // time.
        // Quickly stake our claim to it by opening/closing it to create it.
        // In theory somebody could have grabbed it in that tiny window since
        // we checked if it exists, but that is highly unlikely.
        new FileOutputStream(tempFile).close();

        // debugging peek at the name generated.
        if (false) {
            System.out.println(tempFile.getCanonicalPath());
        }
        return tempFile;
    } // end getTempFile
}
