
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
			
package com.sshtools.ui.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

public class FileSelection extends Vector implements Transferable {
    public static DataFlavor FILE_SELECTION_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "FileSelection");

    public FileSelection() {
        super();
    }

    /* Returns the array of flavors in which it can provide the data. */
    public synchronized DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { FILE_SELECTION_FLAVOR };
    }

    /* Returns whether the requested flavor is supported by this object. */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return FILE_SELECTION_FLAVOR == flavor;
    }

    /**
     * If the data was requested in the "java.lang.String" flavor, return the
     * String representing the selection.
     */
    public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(FILE_SELECTION_FLAVOR)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("File selection of");
        buf.append(size());
        for (Iterator i = iterator(); i.hasNext();) {
            buf.append("\n");
            File file = (File) i.next();
            buf.append("    ");
            buf.append(file.getAbsolutePath());
            buf.append(" (");
            try {
                buf.append(file.length());
            } catch (Exception ioe0) {
            }
            buf.append(")");
        }
        return buf.toString();
    }
}