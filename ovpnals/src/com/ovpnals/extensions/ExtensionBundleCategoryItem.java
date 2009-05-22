package com.ovpnals.extensions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ExtensionBundleCategoryItem {

    private String category ;
    private List<ExtensionBundleTypeItem> types;

    public ExtensionBundleCategoryItem(String category, ExtensionBundleItem extensionBundleItem) {
        this.category = category;
        this.types = new ArrayList<ExtensionBundleTypeItem>();
        addExtensionBundleItem(extensionBundleItem);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    public void addExtensionBundleItem(ExtensionBundleItem extensionBundleItem){
        ExtensionBundleTypeItem typeItem = getTypeItem(new Integer(extensionBundleItem.getBundle().getType()));
        if (typeItem == null){
            ExtensionBundleTypeItem newTypeItem = new ExtensionBundleTypeItem(new Integer(extensionBundleItem.getBundle().getType()), extensionBundleItem, this);
            types.add(newTypeItem);
        }
        else{
            typeItem.addItem(extensionBundleItem); 
        }
    }
    
    private ExtensionBundleTypeItem getTypeItem(Integer type){
        for (Iterator iter = types.iterator(); iter.hasNext();) {
            ExtensionBundleTypeItem element = (ExtensionBundleTypeItem) iter.next();
            if (element.getType().equals(type)){
                return element; 
            }
        }
        return null;
    }
    
    public List getTypes() {
        return this.types;
    }

    public void setTypes(List types) {
        this.types = types;
    }

    public void rebuild(String filterText) {
        for(ExtensionBundleTypeItem item : types) {
            item.getPager().rebuild(filterText);
        }
    }
}
