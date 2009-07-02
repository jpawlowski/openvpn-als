package com.adito.tasks;

import org.apache.struts.action.ActionMessage;

import com.adito.core.BundleActionMessage;

public class TaskProgressBar {
    private int minValue, maxValue, value;
    private String id;
    private BundleActionMessage note;

    public TaskProgressBar(String id, int minValue, int maxValue, int value) {
        super(); 
        this.id = id;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = value;
    }
    
    public BundleActionMessage getNote() {
        return note;
    }
    
    public void setNote(BundleActionMessage note) {
        this.note = note;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
