package com.adito.tasks;

import java.util.ArrayList;
import java.util.Collection;

import com.adito.boot.Util;
import com.adito.security.SessionInfo;

public abstract class AbstractTask implements Task {
    
    private String bundle;
    private String name;
    private String note;
    private Collection<TaskProgressBar> progressBars;
    private SessionInfo session;
    private boolean completed;
    private int id;
    private boolean configured;
    private Object configLock = new Object();
    
    public AbstractTask(String bundle, String name) {
        this.bundle = bundle;
        this.name = name;
        progressBars = new ArrayList<TaskProgressBar>();
    }
    
    public void configured() {
        if(configured) {
            throw new IllegalStateException("Already configured.");
        }
        notifyConfigLock();
    }
    
    void notifyConfigLock() {
        synchronized(configLock) {
            configured = true;
            configLock.notifyAll();
        }        
    }

    public void waitForConfiguration() {
        synchronized(configLock) {
            while(!completed && !configured) {
                try {
                    configLock.wait(1000);
                } catch (InterruptedException e) {
                }
            }            
        }
        
    }

    public SessionInfo getSession() {
        return session;
    }
    
    public void init(SessionInfo session, int id) {
        if(this.session != null) {
            throw new IllegalStateException("Task already attached to session.");
        }
        this.id = id;
        this.session = session;
    }
    
    public void addProgressBar(TaskProgressBar progressBar) {
        progressBars.add(progressBar);
    }
    
    public void clearProgressBars() {
        progressBars.clear();
    }

    public String getBundle() {
        return bundle;
    }

    public String getName() {
        return name;
    }
    
    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public Collection<TaskProgressBar> getProgressBars() {
        return progressBars;
    }
    
    public boolean isComplete() {
        return completed;
    }
    
    public boolean isConfigured() {
        return configured;
    }
    
    public int getId() {
        return id;
    }
    
    public void complete() {
        if(completed) {
            throw new IllegalStateException("Already completed.");
        }
        completed = true;
        notifyConfigLock();
    }

}
