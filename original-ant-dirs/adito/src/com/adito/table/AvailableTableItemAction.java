package com.adito.table;

import javax.servlet.http.HttpServletRequest;

import com.adito.core.AvailableMenuItem;
import com.adito.core.MenuItem;
import com.adito.security.SessionInfo;

public class AvailableTableItemAction extends AvailableMenuItem {
	
	private TableItemModel itemModel;
	private int rowIndex;
	private TableItem rowItem;
	private Pager pager;

	public AvailableTableItemAction(MenuItem menuItem,
			AvailableMenuItem parent, HttpServletRequest request,
			String referer, int checkNavigationContext, SessionInfo info) {
		super(menuItem, parent, request, referer, checkNavigationContext, info);
	}
	
	public String getOnClick() {
		return ((TableItemAction)getMenuItem()).getOnClick(this);
	}
	
	public String getToolTipContentLocation() {
		return ((TableItemAction)getMenuItem()).getToolTipContentLocation(this);
	}
	
	public int getToolTipWidth() {
		return ((TableItemAction)getMenuItem()).getToolTipWidth(this);
	}
	
	public String getAdditionalAttributeName() {
		return ((TableItemAction)getMenuItem()).getAdditionalAttributeName();
	}
	
	public String getAdditionalAttributeValue() {
		return ((TableItemAction)getMenuItem()).getAdditionalAttributeValue(this);
	}
	
	public boolean isEnabled() {
		return ((TableItemAction)getMenuItem()).isEnabled(this);
	}
	
	public String getPath() {
		return ((TableItemAction)getMenuItem()).getPath(this);
	}
	
	public TableItemModel getItemModel() {
		return itemModel;
	}
	
	public int getRowIndex() {
		return rowIndex;
	}
	
	public TableItem getRowItem() {
		return rowItem;
	}
	
	public Pager getPager() {
		return pager;
	}
	
	public void init(Pager pager, int rowIndex) {
		this.pager = pager;
		this.itemModel = pager.getModel();
		this.rowIndex = rowIndex;
		rowItem = pager.getFilteredItem(rowIndex + pager.getStartRow());
	}

}
