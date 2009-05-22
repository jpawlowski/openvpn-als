/**
 * 
 */
package com.ovpnals.properties;

public class Pair {
    Object value;

    String label;

    public Pair(Object value, String label) {
        this.value = value;
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String toString() {
        return "pair[label=" + label + ",value=" + value + "]";
    }
}