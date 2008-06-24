
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
			
package com.sshtools.ui.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class FileDropListener implements DropTargetListener {

    public FileDropListener() {
    }
    
    public abstract void fileDropped(File[] files);

    public void drop(DropTargetDropEvent dtde) {
        try {
            doDrop(dtde);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void doDrop(DropTargetDropEvent dtde) throws Exception {
        // System.out.println("doDrop(" + dtde + ")");
        DataFlavor[] flavors = dtde.getCurrentDataFlavors();
        if (canImport(flavors)) {
            // System.out.println("can import");
            for (int i = 0; i < flavors.length; i++) {
                // System.out.println("flavor " + flavors[i]);
                // Drop from Windows
                if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable t = dtde.getTransferable();
                    if (t.getTransferData(DataFlavor.javaFileListFlavor) instanceof java.util.List) {
                        java.util.List fileList = (java.util.List) t.getTransferData(DataFlavor.javaFileListFlavor);
                        if (fileList.get(0) instanceof File) {
                            File[] f = new File[fileList.size()];
                            fileList.toArray(f);
                            fileDropped(f);
                        }
                    }
                    return;
                }
                // Drop from GNOME
                else if (flavors[i].getMimeType().startsWith("text/uri-list") && flavors[i].getRepresentationClass()
                                .equals(Reader.class)) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable t = dtde.getTransferable();
                    BufferedReader r = new BufferedReader((Reader) t.getTransferData(flavors[i]));
                    String s = null;
                    List l = new ArrayList();
                    while ((s = r.readLine()) != null) {
                        if (s.startsWith("file://")) {
                            s = s.substring(7);
                        }
                        l.add(new File(URLDecoder.decode(s, "UTF-8")));
                    }
                    File[] f = new File[l.size()];
                    l.toArray(f);
                    fileDropped(f);
                    return;
                }
                // Drop from GNOME
                else if (DataFlavor.stringFlavor.equals(flavors[i])) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable t = dtde.getTransferable();
                    Object o = t.getTransferData(DataFlavor.stringFlavor);
                    if (o instanceof String) {
                        // System.out.println("string uri list = " + o);
                        List l = new ArrayList();
                        StringTokenizer tz = new StringTokenizer(o.toString(), "\n\r");
                        while (tz.hasMoreTokens()) {
                            String path = (String) tz.nextToken();
                            if (path.startsWith("file://")) {
                                path = path.substring(7);
                            }
                            l.add(new File((URLDecoder.decode(path, "UTF-8"))));
                        }
                        File[] f = new File[l.size()];
                        l.toArray(f);
                        fileDropped(f);
                    }
                    return;
                }
            }
        }
        dtde.rejectDrop();
    }

    boolean canImport(DataFlavor[] flavors) {
        // System.out.println("Testing if can import");
        for (int i = 0; i < flavors.length; i++) {
            if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                // System.out.println("Yes, " + flavors[i] + " is
                // importable");
                return true;
            } else if (DataFlavor.stringFlavor.equals(flavors[i])) {
                // System.out.println("Yes, " + flavors[i] + " is
                // importable");
                return true;
            } else {
                if (flavors[i].getMimeType().startsWith("text/uri-list")) {
                    // System.out.println("Yes, " + flavors[i] + " is
                    // importable");
                    return true;
                }
                // System.out.println("Mime = " + flavors[i].getMimeType());
                // System.out.println("Class = " +
                // flavors[i].getRepresentationClass());
                //                    
                // System.out.println("No, " + flavors[i] + " is not
                // importable");
            }
        }
        return false;
    }

    public void dragEnter(DropTargetDragEvent dtde) {
        System.out.println("dragEnter(" + dtde + ")");
        dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
     */
    public void dragOver(DropTargetDragEvent dtde) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
     */
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
     */
    public void dragExit(DropTargetEvent dte) {
    }


}
