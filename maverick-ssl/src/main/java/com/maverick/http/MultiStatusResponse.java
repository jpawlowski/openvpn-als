
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
			
package com.maverick.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import com.maverick.util.URLUTF8Encoder;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class MultiStatusResponse {

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(MultiStatusResponse.class);
    // #endif

    String href;
    int status;
    String version;
    String reason;
    Properties properties;
    boolean collection = false;

    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

    MultiStatusResponse(IXMLElement element) throws IOException {
        if (element.getFirstChildNamed("href", element.getNamespace()) == null) //$NON-NLS-1$
            throw new IOException(Messages.getString("MultiStatusResponse.unexpectedHref")); //$NON-NLS-1$

        href = URLUTF8Encoder.decode(element.getFirstChildNamed("href", element.getNamespace()).getContent()); //$NON-NLS-1$

        IXMLElement props = element.getFirstChildNamed("propstat", element.getNamespace()); //$NON-NLS-1$

        if (props == null)
            throw new IOException(Messages.getString("MultiStatusResponse.noPropertyElements")); //$NON-NLS-1$

        if (props.getFirstChildNamed("status", element.getNamespace()) == null) //$NON-NLS-1$
            throw new IOException(Messages.getString("MultiStatusResponse.unexpectedStatusResponse")); //$NON-NLS-1$

        String status = props.getFirstChildNamed("status", element.getNamespace()).getContent(); //$NON-NLS-1$

        StringTokenizer tokens = new StringTokenizer(status, " ", false); //$NON-NLS-1$
        reason = ""; //$NON-NLS-1$

        try {
            version = tokens.nextToken();
            this.status = Integer.parseInt(tokens.nextToken());

            while (tokens.hasMoreTokens()) {
                reason += tokens.nextToken() + " "; //$NON-NLS-1$
            }
            reason = URLDecoder.decode(reason.trim());
        } catch (NoSuchElementException e) {
            throw new IOException(Messages.getString("MultiStatusResponse.failedToReadHTTPResponseHeader")); //$NON-NLS-1$
        } catch (NumberFormatException e) {
            throw new IOException(Messages.getString("MultiStatusResponse.failedToReadHTTPResponseHeader")); //$NON-NLS-1$
        }

        // Create a new set of properties
        properties = new Properties();

        // Check the status, if its not found then return
        if (this.status == 404)
            return;

        props = props.getFirstChildNamed("prop", props.getNamespace()); //$NON-NLS-1$

        if (props == null)
            throw new IOException(Messages.getString("MultiStatusResponse.noPropElementsInPropStat")); //$NON-NLS-1$

        IXMLElement child;

        for (Enumeration e = props.getChildren().elements(); e.hasMoreElements();) {
            child = (IXMLElement) e.nextElement();

            if (child.getName().equalsIgnoreCase("resourcetype")) { //$NON-NLS-1$
                if (child.getChildrenNamed("collection") != null) //$NON-NLS-1$
                    collection = true;
            } else {
                properties.put(child.getName().toLowerCase(), child.getContent() == null ? "" : child.getContent()); //$NON-NLS-1$
            }
        }

    }

    public long getContentLength() {
        if (properties.containsKey("getcontentlength")) { //$NON-NLS-1$
            return Long.parseLong(properties.getProperty("getcontentlength")); //$NON-NLS-1$
        } else
            return 0;
    }

    private long processDate(String date) {

        long retval = 0;
        try {
            retval = Date.parse(date);
        } catch (Throwable t) {

            try {
                /*
                 * org.joda.time.DateTime dt = new
                 * org.joda.time.DateTime(properties.getProperty("creationdate"));
                 * retval = dt.getMillis();
                 */
                return 0; // TODO change
            } catch (Throwable t2) {
            }
        }

        return retval;
    }

    public long getCreationDate() {

        if (properties.containsKey("creationdate")) { //$NON-NLS-1$
            return processDate(properties.getProperty("creationdate")); //$NON-NLS-1$
        } else
            return 0;
    }

    public long getLastModified() {

        if (properties.containsKey("getlastmodified")) { //$NON-NLS-1$
            return processDate(properties.getProperty("getlastmodified")); //$NON-NLS-1$
        } else
            return 0;
    }

    public String getContentType() {
        return getProperty("getcontenttype"); //$NON-NLS-1$
    }

    public String getDisplayName() {
        if (properties.containsKey("displayname")) //$NON-NLS-1$
            return getProperty("displayname"); //$NON-NLS-1$
        else {
            int idx = href.lastIndexOf('/');
            if (idx > -1) {
                return href.substring(idx + 1);
            } else
                return href;
        }
    }

    public int getStatus() {
        return status;
    }

    public String getHref() {
        return href;
    }

    public boolean isCollection() {
        return collection;
    }

    public String getProperty(String property) {
        return properties.getProperty(property.toLowerCase());
    }

    public static MultiStatusResponse[] createResponse(HttpResponse response) throws IOException {

        if (response.getStatus() != 207) {
            throw new IOException(Messages.getString("MultiStatusResponse.not207")); //$NON-NLS-1$
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read;
        byte[] buf = new byte[4096];
        while ((read = response.getInputStream().read(buf)) > -1) {
            out.write(buf, 0, read);
        }

        // #ifdef DEBUG
        if (log.isDebugEnabled())
            log.debug(new String(out.toByteArray()));
        // #endif

        try {
            IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
            IXMLReader reader = StdXMLReader.stringReader(new String(out.toByteArray(), "UTF8")); //$NON-NLS-1$
            parser.setReader(reader);
            IXMLElement rootElement = (IXMLElement) parser.parse();

            if (!rootElement.getName().equalsIgnoreCase("multistatus")) //$NON-NLS-1$
                throw new IOException(Messages.getString("MultiStatusResponse.invalidDavRootElement") + rootElement.getName()); //$NON-NLS-1$

            // Now process the responses
            Vector children = rootElement.getChildrenNamed("response", rootElement.getNamespace()); //$NON-NLS-1$
            Vector responses = new Vector();

            for (Enumeration e = children.elements(); e.hasMoreElements();) {
                responses.addElement(new MultiStatusResponse((IXMLElement) e.nextElement()));
            }

            MultiStatusResponse[] array = new MultiStatusResponse[responses.size()];
            responses.copyInto(array);
            return array;

        } catch (Exception ex) {
            // #ifdef DEBUG
            log.error(Messages.getString("MultiStatusResponse.failedToProcessMultistatusResponse"), ex); //$NON-NLS-1$
            // #endif
            throw new IOException(ex.getMessage());
        }

    }

}
