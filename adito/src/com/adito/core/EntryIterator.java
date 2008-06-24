/**
 * 
 */
package com.adito.core;

import java.util.Iterator;
import java.util.Map;

public class EntryIterator implements Iterator {

    Iterator iterator;

    /**
     * @param map
     */
    public EntryIterator(Map map) {
        iterator = map.entrySet().iterator();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object next() {
        return ((Map.Entry) iterator.next()).getValue();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        iterator.remove();
    }

}