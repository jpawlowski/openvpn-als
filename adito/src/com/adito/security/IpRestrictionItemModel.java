package com.adito.security;

import com.adito.table.AbstractTableItemTableModel;

public class IpRestrictionItemModel extends AbstractTableItemTableModel {

    public int getColumnCount() {
        return 2;
    }
    
    public Class getColumnClass(int col) {
        switch (col) {
            case 0:
                return String.class;
            default:
                return Boolean.class;
        }
    }
    
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "restriction";
            default:
                return "type";
        }
    }
    
    public String getId() {
        return "ipRestrictions";
    }
    
    public int getColumnWidth(int col) {
        return 0;
    }
}
