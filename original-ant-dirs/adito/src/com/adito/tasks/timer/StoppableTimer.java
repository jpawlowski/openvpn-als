package com.adito.tasks.timer;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class StoppableTimer extends Timer {

	public static String NAME = "stoppable.timer";

	Map tasks = new HashMap();
	
	public StoppableTimer() {
		super();
	}

	public StoppableTimer(boolean isDaemon) {
		super(isDaemon);
	}
    
    public TimerTask getTimerTask(String taskName) {
        return (TimerTask)tasks.get(taskName);
    }

	public void schedule(String taskName, TimerTask timerTask, long delay) {
		super.schedule(timerTask, delay);
		tasks.put(taskName, timerTask);
	}
	
	public void cancelTimerTask(String taskName){
		((TimerTask) tasks.get(taskName)).cancel() ;
		tasks.remove(taskName);
	}
	
	public boolean containsTimerTask(String taskName){
		return tasks.containsKey(taskName);
	}
}
