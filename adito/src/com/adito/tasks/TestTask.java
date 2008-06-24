package com.adito.tasks;

import org.apache.struts.action.ActionMessage;

import com.adito.core.BundleActionMessage;


public class TestTask extends AbstractTask {
    
    private TaskProgressBar bar;
    
    public TestTask() {
        super("tasks", "test");
        addProgressBar(bar = new TaskProgressBar("testBar", 0, 100, 0));
        bar.setNote(new BundleActionMessage("tasks", "taskProgress.testTask.note.downloadingExtension", "NX Client"));
    }

    public void run() throws TaskException {
        for(int i = 0 ; i < 100; i++) {
            bar.setValue(i);
            bar.setNote(new BundleActionMessage("tasks", "taskProgress.testTask.note.progress", String.valueOf(i * 2048 ), "NX Client" ));
            try {
                Thread.sleep(1000);
            }
            catch(Exception e) {                
            }
            
        }
        
    }

    public String getOnFinish() {
        return null;
    }

}
