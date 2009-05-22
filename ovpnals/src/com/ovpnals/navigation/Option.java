/*
 */
package com.ovpnals.navigation;

public class Option {


    String forward;
    String label;
    String styleId;

    public Option(String forward, String label, String styleId) {
        this.forward = forward;
        this.label = label;
        this.styleId = styleId;
    }
    
    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    public String getStyleId() {
        return styleId;
    }
}