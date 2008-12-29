
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
			
package com.adito.vfs.forms;

import java.util.Collection;

import javax.servlet.http.HttpSession;

import com.adito.table.AbstractTableItemTableModel;
import com.adito.table.TableItem;
import com.adito.table.forms.AbstractPagerForm;
import com.adito.vfs.VFSFileLock;

/**
 *
 */
public final class ShowVfsLocksForm extends AbstractPagerForm {
    /**
     * Default constructor
     */
    public ShowVfsLocksForm() {
        super(new VfsLockTableModel());
    }

    /**
     * Initialise the form.
     * @param session
     * @param currentLocks
     */
    public void initialize(HttpSession session, Collection<VFSFileLock> currentLocks) {
        super.initialize(session, "fileName");
        for (VFSFileLock entry : currentLocks) {
            getModel().addItem(new VfsLockTableItem(entry));            
        }
        getPager().rebuild(getFilterText());
    }

    private static final class VfsLockTableModel extends AbstractTableItemTableModel {

        public int getColumnWidth(int col) {
            return 0;
        }

        public String getId() {
            return "files";
        }

        public int getColumnCount() {
            return 2;
        }

        public String getColumnName(int col) {
            switch(col)
            {
                case 0:
                    return "fileName";
                case 1:
                    return "isActive";
            }
            return "";
        }

        public Class getColumnClass(int col) {
                if(col==1)
                    return Boolean.class;
                return String.class;
        }
    }

    /**
     *
     */
    public static final class VfsLockTableItem implements TableItem
    {
        private final VFSFileLock vfsFileLock_;
        private VfsLockTableItem(VFSFileLock vfsFileLock)
        {
            vfsFileLock_ = vfsFileLock;
        }
        
        public Object getColumnValue(int col) {
            switch(col)
            {
                case 0:
                    return vfsFileLock_.getFileName();
                case 1:
                    return vfsFileLock_.isActive();
            }
            return "";
        }
        
        /**
         * @return vfsFileLock
         */
        public VFSFileLock getVFSFileLock () {
            return vfsFileLock_;
        }
    }        
}