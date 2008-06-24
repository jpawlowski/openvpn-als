package com.adito.tasks;

import com.adito.core.CoreException;

public class TaskException extends CoreException {
    
    public final static int INTERNAL_ERROR = 0;
    public final static String ERROR_CATEGORY = "tasks";

    public TaskException(int code) {
        super(code, ERROR_CATEGORY);
    }

    public TaskException(int code, String bundle, Throwable cause, String arg0, String arg1, String arg2, String arg3) {
        super(code, ERROR_CATEGORY, bundle, cause, arg0, arg1, arg2, arg3);
    }

    public TaskException(int code, String bundle, Throwable cause, String arg0) {
        super(code, ERROR_CATEGORY, bundle, cause, arg0);
    }

    public TaskException(int code, String bundle, Throwable cause) {
        super(code, ERROR_CATEGORY, bundle, cause);
    }

    public TaskException(int code, String arg0) {
        super(code, ERROR_CATEGORY, arg0);
    }

    public TaskException(int code, Throwable cause) {
        super(code, ERROR_CATEGORY, cause);
    }

}
