package com.adito.tasks.forms;

import com.adito.core.forms.CoreForm;
import com.adito.tasks.Task;

public class TaskProgressForm extends CoreForm {

    private Task task;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
    
    
}
