package com.adito.extensions;

import java.util.ArrayList;
import java.util.List;

import com.adito.table.Pager;
import com.adito.table.TableItem;
import com.adito.table.TableItemModel;


public class ExtensionBundleTypeItem implements TableItemModel {

    private Integer type ;
    private List<TableItem> extensionBundles;
    private Pager pager;
    private ExtensionBundleCategoryItem categoryItem;

    public ExtensionBundleTypeItem(Integer type, ExtensionBundleItem extensionBundleItem, ExtensionBundleCategoryItem categoryItem) {
        this.extensionBundles = new ArrayList();
        this.type = type;
        this.addItem(extensionBundleItem);
        this.categoryItem = categoryItem;
        pager = new Pager(this);
    }
    
    public Pager getPager() {
        return pager;
    }

    public List getExtensionBundles() {
        return extensionBundles;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void addItem(TableItem extensionBundle) {
        extensionBundles.add(extensionBundle);        
    }

    public void clear() {
        extensionBundles.clear();        
    }

    public boolean contains(TableItem item) {
        return extensionBundles.contains(item);
    }

    public boolean getEmpty() {
        return extensionBundles.size() == 0;
    }

    public String getId() {
        return "extensionStore." + categoryItem.getCategory() + "." +  type.intValue()  ;
    }

    public TableItem getItem(int index) {
        return extensionBundles.get(index);
    }

    public List getItems() {
        return extensionBundles;
    }

    public Class getColumnClass(int col) {
        return String.class;
    }

    public int getColumnCount() {
        return 1;
    }

    public String getColumnName(int col) {
        return "name";
    }

    public int getRowCount() {
        return extensionBundles.size();
    }

    public Object getValue(int row, int col) {
        return getItem(row).getColumnValue(col);
    }
}
