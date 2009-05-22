/*
 */
package com.ovpnals.boot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Extension of an {@link java.util.ArrayList} that is used to store lists
 * of strings. This class is used with OpenVPN-ALSs custom input components
 * such as the {@link com.ovpnals.input.tags.MultiSelectListBoxTag} and
 * {@link com.ovpnals.input.tags.MultiEntryListBoxTag}. 
 * <p>
 * This list of strings may be created from or converted to and from 
 * either <i>Text Field Text</i> (a newline separated list of strings) or 
 * <i>Property Text</i> (a ! delimited list).
 * 
 */
public class PropertyList extends ArrayList<String> {

    private static final long serialVersionUID = -6816117522796660457L;
	public static final PropertyList EMPTY_LIST = new PropertyList();

    /**
     * Constructor. Create empty property list
     */
    public PropertyList() {
        super();
    }
    
    /**
     * Constructor. 
     * @param values
     */
    public PropertyList(Collection<String> values) {
        super(values);
    }

    /**
     * Constructor. 
     * 
     * @param propertyList string in the <i>Property List</i> format.
     * @see #setAsPropertyText(String) 
     */
    public PropertyList(String propertyList) {
        this();
        setAsPropertyText(propertyList);
    }

    /**
     * Constructor. 
     * 
     * @param propertyList string in the any character separated format. Occurences of the delimited may be escaed
     * @param delimiter
     * @see #setAsPropertyText(String) 
     */
    public PropertyList(String propertyList, char delimiter) {
        this();
        setAsDelimitedList(propertyList, delimiter);
    }

    /**
     * Get the string item at the specified index
     * 
     * @param i index
     * @return string item
     */
    public String getPropertyItem(int i) {
        return (String) get(i);
    }

    /**
     * Set list of strings from a string in the <i>Property Text</i> format.
     * This is a bang (!) delimited list of string elements. Strings containing ! should escape this
     * character with a backslash (\).
     * 
     * @param propertyText property list string
     */
    public void setAsPropertyText(String propertyText) {
        setAsDelimitedList(propertyText, '!');
    }

    /**
     * Set list of strings from a string in the any character delimited format.
     * Occurences of the delimiter should be escaped by a backslash (\) character.
     * 
     * @param propertyText list string
     * @param delimiter element delimiter
     */
    public void setAsDelimitedList(String propertyText, char delimiter) {
        clear();
        StringBuffer buf = new StringBuffer();
        char ch = ' ';
        boolean escaped = false;
        if(propertyText!=null) {
            for (int i = 0; i < propertyText.length(); i++) {
                ch = propertyText.charAt(i);
                if (ch == '\\' && !escaped) {
                    escaped = true;
                } else {
                    if (ch == delimiter && !escaped) {
                        add(buf.toString());
                        buf.setLength(0);
                    } else {
                        buf.append(ch);
                        escaped = false;
                    }
                }
            }
            if (buf.length() > 0) {
                add(buf.toString());
            }
        }
    }

    /**
     * Get the list of strings in <i>Property Text</i> format. This is a  
     * strings in which each string element is delimited by a bang (!) character.
     * <p>
     * Strings containing the ! character escape it using a backslash (\). 
     * 
     * @return list of strings in property text format
     */
    public String getAsPropertyText() {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = iterator(); i.hasNext();) {
            if (buf.length() > 0) {
                buf.append("!");
            }
            String toString = i.next().toString();
            String backslashEscaped = toString.replaceAll("\\\\", "\\\\\\\\");
            String bangEscaped = backslashEscaped.replaceAll("\\!", "!!");
            buf.append(bangEscaped);
        }
        return buf.toString();
    }

    /**
     * Set list of strings from a string in the <i>Text Field Text</i> format.
     * This is a newline (\n) delimited list of string elements. 
     * 
     * @param textFieldText string in text field text format
     */
    public void setAsTextFieldText(String textFieldText) {
        clear();
        StringTokenizer t = new StringTokenizer(textFieldText, "\r\n");
        while (t.hasMoreTokens()) {
            add(((String) t.nextToken()).trim());
        }
    }

    /**
     * Get the list of strings in <i>Property Text</i> format. This is a  
     * strings in which each string element is delimited by a newline (\n) character. It is often
     * used as the request parameter used to pass a value into and out from
     * one of OpenVPN-ALSs custom input components such as {@link com.ovpnals.input.tags.MultiEntryListBoxTag}
     * or {@link com.ovpnals.input.tags.MultiSelectListBoxTag}.
     * 
     * @return list of strings in property text format
     */
    public String getAsTextFieldText() {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = iterator(); i.hasNext();) {
            if (buf.length() > 0) {
                buf.append("\n");
            }
            buf.append(i.next().toString());
        }
        return buf.toString();
    }
    
    /**
     * Utility method to create a list from a string in the <i>Text Field Text</i>
     * format.
     * 
     * @param textFieldText list of strings in text field text format.
     * @return property list
     * @see #setAsTextFieldText(String)
     */
    public static PropertyList createFromTextFieldText(String textFieldText) {
        PropertyList l = new PropertyList();
        l.setAsTextFieldText(textFieldText);
        return l;
    }
    
    /**
     * Create a property list from an array of strings
     * 
     * @param strings array of strings
     * @return property list
     */
    public static PropertyList createFromArray(String[] strings) {
        PropertyList l = new PropertyList();
        if (strings != null) {
            for (int i = 0; i < strings.length; i++) {
                l.add(strings[i]);
            }
        }
        return l;
    }

    /**
     * Get this property list as an array of strings
     * 
     * @return property list as array of strings
     */
    public String[] asArray() {
        String[] arr = new String[size()];
        toArray(arr);
        return arr;
    }

    /**
     * Get this property list as an array of primitive integers. If any
     * string is not an integer then an exception will be thrown
     * 
     * @return property list as array of primitive integers
     * @throws NumberFormatException
     */
    public int[] toIntArray() throws NumberFormatException {
        int[] arr = new int[size()];
        for(int i = size() - 1 ; i >=0 ; i--) {
            arr[i] = Integer.parseInt(get(i).toString());
        }
        return arr;
    }

    /**
     * Create a new property list from an array of primitive integers.
     * 
     * @param integers array of primitive integers 
     * @return property list
     */
    public static PropertyList createFromArray(int[] integers) {
        PropertyList l = new PropertyList();
        if (integers != null) {
            for (int i = 0; i < integers.length; i++) {
                l.add(String.valueOf(integers[i]));
            }
        }
        return l;
    }

    /**
     * Return a new list as a list of {@link Integer} objects
     * 
     * @return property list as list of Integer objects
     */
    public List getIntegerObjectList() {
        List<Integer> l =new ArrayList<Integer>();
        for(Iterator i = iterator(); i.hasNext(); ) {
            l.add(new Integer((String)i.next()));
        }
        return l;
    }

    /**
     * Get this list as a {@link Properties} object assuming that each
     * item in the list is a string in name / value format (separated by =).
     * 
     * @return list as name / value pairs
     */
    public Properties getAsNameValuePairs() {
        Properties p = new Properties();
        for(String nameValuePair : this) {
            new NameValuePair(nameValuePair).add(p);
        }
        return p;
    }

}
