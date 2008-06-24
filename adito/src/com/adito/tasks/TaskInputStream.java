package com.adito.tasks;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


public class TaskInputStream extends FilterInputStream {

    private TaskProgressBar bar;

    public TaskInputStream(TaskProgressBar bar, InputStream in) {
        super(in);
        this.in = in;
        this.bar = bar;
    }
    
    public TaskProgressBar getProgressBar() {
        return bar;
    }

    public int read(byte b[], int off, int len) throws IOException {
        int x = in.read(b, off, len);
        bar.setValue(bar.getValue() + x);
        return x;
    }

    public int read() throws IOException {
        int x = in.read();
        bar.setValue(bar.getValue() + 1);
        return x;
    }

}
