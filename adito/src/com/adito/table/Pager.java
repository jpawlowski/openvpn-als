
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
			
package com.adito.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.adito.boot.Util;

/**
 * Provides an additional layer over a
 * {@link com.adito.table.TableItemModel} that allows the items within the
 * model to be displayed a page at a time.
 * <p>
 * The pager also deals with the sorting of the displayed items as well as
 * filtering, leaving the contents of the model alone.
 */
public class Pager {

    private TableItemModel model;
    private int pageSize;
    private int startRow;
    private String sortName;
    private boolean sortReverse;
    private List filteredList;
    private boolean sorts = true;

    /**
     * Constructor
     * 
     * @param model table model to page
     */
    public Pager(TableItemModel model) {
        super();
        this.model = model;
        pageSize = 10;
        startRow = 0;
        filteredList = null;
    }

    /**
     * Get whether this pager sorts
     * 
     * @return sorts
     */
    public boolean isSorts() {
        return sorts;
    }

    /**
     * Set whether this pager sorts
     * 
     * @param sorts pager sorts
     */
    public void setSorts(boolean sorts) {
        this.sorts = sorts;
    }

    /**
     * Get the number of items
     * 
     * @return the number of rows after the filter has been applied
     */
    public int getFilteredRowCount() {
        return filteredList == null ? 0 : filteredList.size();
    }

    /**
     * Get an item given its filtered index
     * 
     * @param row filtered row
     * @return the item at the row
     */
    public TableItem getFilteredItem(int row) {
        return (TableItem) filteredList.get(row);
    }

    /**
     * Get if there are no rows after the filter has been applied.
     * 
     * @return empty table
     */
    public boolean getEmpty() {
        return getFilteredRowCount() == 0;
    }

    /**
     * Set the number of items on each page
     * 
     * @param pageSize items per page
     */
    public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
    }

    /**
     * Get the number of items on each page
     * 
     * @return number of items on each page
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Create an {@link Iterator} for all the items on the current page.
     * 
     * @return iterator of items for the current page
     */
    public Iterator getPageItems() {
        return new ItemIterator(startRow);
    }

    /**
     * Get if there are any more pages after the current one
     * 
     * @return more pages available
     */
    public boolean getHasNextPage() {
        boolean hasNextPage = (startRow + calcPageSize()) < getFilteredRowCount();
        return hasNextPage;
    }

    /**
     * Get if there are any pages before the current one
     * 
     * @return previous pages available
     */
    public boolean getHasPreviousPage() {
        boolean hasNextPage = (startRow - calcPageSize()) >= 0;
        return hasNextPage;
    }

    /**
     * Get the index the current page starts at
     * 
     * @return start index of current page
     */
    public int getStartRow() {
        return startRow;
    }

    /**
     * Set the index the current page starts at
     * 
     * @param startRow start row
     */
    public void setStartRow(int startRow) {
        // JDR we need to find any remaining rows as the start row must be a multiple of the page size.
        if (startRow == 0 || pageSize == 0){
            this.startRow = startRow;
        }
        else{
            this.startRow = startRow - (startRow % pageSize);
        }
    }

    /**
     * Move on to the next page
     */
    public void nextPage() {
        if (!getHasNextPage()) {
            throw new IllegalArgumentException("No more pages.");
        }
        setStartRow(startRow + calcPageSize());
    }

    /**
     * Move back to the previous page
     */
    public void previousPage() {
        if (!getHasPreviousPage()) {
            throw new IllegalArgumentException("No more pages.");
        }
        setStartRow(startRow - calcPageSize());
    }

    /**
     * Get the model this pager overlays.
     * 
     * @return model
     */
    public TableItemModel getModel() {
        return model;
    }

    /**
     * Get the name of the field that is currently being used for sorting
     * 
     * @return name of sort field
     */
    public String getSortName() {
        return sortName;
    }

    /**
     * Get whether the pager is currently sorting in reverse
     * 
     * @return sort in reverse
     */
    public boolean getSortReverse() {
        return sortReverse;
    }

    /**
     * Set the name of the field that is currenting being used for sorting. You
     * must call {@link #rebuild(String)} to do the actual filter / sort.
     * 
     * @param sortName name of sort field
     */
    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    /**
     * Set whether the current sort should be in reverse order. You must call
     * {@link #rebuild(String)} to do the actual filter / sort.
     * 
     * @param sortReverse sort in reverse order
     */
    public void setSortReverse(boolean sortReverse) {
        this.sortReverse = sortReverse;
    }

    /**
     * Rebuild the pager using the specified filter text. All items will also be
     * re-sorted based on the current criteria.
     * 
     * @param filterText
     */
    public void rebuild(String filterText) {
        if (filterText == null || filterText.equals("") || filterText.equals("*")) {
            filteredList = new ArrayList(model.getItems());
        } else {
            filteredList = new ArrayList();
            for (Iterator i = model.getItems().iterator(); i.hasNext();) {
                TableItem ti = (TableItem) i.next();
                if (matchedFilter(ti, filterText)) {
                    filteredList.add(ti);
                }
            }
        }
        if (isSorts()) {
            if (sortName == null || sortName.equals("")) {
                sortName = model.getId() + "." + model.getColumnName(0);
            }
            if (sortName != null && !sortName.equals("")) {
                for (int i = model.getColumnCount() - 1; i >= 0; i--) {
                    if (model.getColumnName(i).equals(sortName)) {
                        Collections.sort(filteredList, new TableItemComparator(i));
                        return;
                    }
                }
            }
        }
    }

    /**
     * Move to the first page
     */
    public void firstPage() {
        setStartRow(0);
    }

    /**
     * Move to the last page
     */
    public void lastPage() {
        int lastPageRow = getFilteredRowCount() - calcPageSize();
        setStartRow(lastPageRow < 0 ? 0 : lastPageRow);
    }

    /**
     * Calculate the actual page size taking into account if the 'all' page size
     * is set (in which case the total filtered rows will be returned)
     * 
     * @return page size
     */
    public int calcPageSize() {
        return (pageSize == 0 ? getFilteredRowCount() : pageSize);
    }

    boolean matchedFilter(TableItem item, String filterText) {
        if (filterText == null || filterText.equals("")) {
            return true;
        } else {
            if (filterText.startsWith("!")) {
                filterText = filterText.substring(1);
                Pattern p = Pattern.compile(Util.parseSimplePatternToRegExp(filterText), Pattern.CASE_INSENSITIVE);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    Object cv = item.getColumnValue(i);
                    String val = cv == null ? "" : cv.toString();
                    Matcher matcher = p.matcher(val);
                    if (matcher.matches()) {
                        return false;
                    }
                }
            } else {
                Pattern p = Pattern.compile(Util.parseSimplePatternToRegExp(filterText), Pattern.CASE_INSENSITIVE);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    Object cv = item.getColumnValue(i);
                    String val = cv == null ? "" : cv.toString();
                    Matcher matcher = p.matcher(val);
                    if (matcher.matches()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    class TableItemComparator implements Comparator {

        int col;

        TableItemComparator(int col) {
            this.col = col;
        }

        public int compare(Object arg0, Object arg1) {
            return (sortReverse ? -1 : 1)
                            * ((Comparable) ((TableItem) arg0).getColumnValue(col)).compareTo((Comparable) ((TableItem) arg1)
                                            .getColumnValue(col));
        }
    }

    class ItemIterator implements Iterator {

        int idx;
        int end;

        ItemIterator(int idx) {
            this.idx = idx;
            this.end = idx + calcPageSize();
        }

        public boolean hasNext() {
            return idx < getFilteredRowCount() && idx < end;
        }

        public Object next() {
            return filteredList.get(idx++);
        }

        public void remove() {
        }

    }
}
