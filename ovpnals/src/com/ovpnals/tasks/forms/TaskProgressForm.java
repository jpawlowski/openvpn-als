package com.ovpnals.tasks.forms;

import com.ovpnals.core.forms.CoreForm;
import com.ovpnals.tasks.Task;

public class TaskProgressForm extends CoreForm {

    private Task task;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
    
    
}
