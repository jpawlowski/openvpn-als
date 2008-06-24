package com.adito.tasks;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.security.SessionInfo;

public class TaskManager {
    final static Log log = LogFactory.getLog(TaskManager.class);
    
    private Map<Integer, Task> tasks = new HashMap<Integer, Task>();
    private int id;
    
    private static TaskManager instance;
    
    public static TaskManager getInstance() {
        if(instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }
    
    public synchronized void startTask(final Task task, SessionInfo session) {
        id++;
        tasks.put(id, task);
        task.init(session, id);
        Thread t = new Thread() {
            public void run() {
                try {
                    task.run();
                }
                catch(Exception e) {
                    log.error("Task failed. ", e);
                }
                finally {
                    task.complete();
                }
            }
        };
        t.start();
    }
    
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public void remove(Task task) {
        log.info("Removing task " + task);
        tasks.remove(task.hashCode());        
    }
}
