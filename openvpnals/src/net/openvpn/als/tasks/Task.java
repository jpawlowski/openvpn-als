package net.openvpn.als.tasks;

import java.util.Collection;

import net.openvpn.als.security.SessionInfo;

public interface Task {
    public int getId();
    public String getBundle();
    public String getName();
    void init(SessionInfo session, int id);
    public void run() throws TaskException;
    public Collection<TaskProgressBar> getProgressBars();
    public String getNote();
    public SessionInfo getSession();
    public boolean isComplete();
    public void complete();
    public void addProgressBar(TaskProgressBar progressBar);
    public void waitForConfiguration();
    public void configured();
    public String getOnFinish();
    public boolean isConfigured();
    public void clearProgressBars();
}
