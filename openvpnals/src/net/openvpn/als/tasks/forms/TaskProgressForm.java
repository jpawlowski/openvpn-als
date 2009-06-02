package net.openvpn.als.tasks.forms;

import net.openvpn.als.core.forms.CoreForm;
import net.openvpn.als.tasks.Task;

public class TaskProgressForm extends CoreForm {

    private Task task;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
    
    
}
