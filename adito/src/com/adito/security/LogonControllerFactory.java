package com.adito.security;


public class LogonControllerFactory {

	
	static Class logonControllerClass = DefaultLogonController.class;
	static LogonController logonController = null;
	static private boolean locked = false;
	
	public static LogonController getInstance() {
		
		if(logonController!=null)
			return logonController;
		
        // Use the default logon controller if no other has been registered
        try {
        	logonController = (LogonController) logonControllerClass.newInstance();
            logonController.init();
        } catch (Exception e) {
            logonController = new DefaultLogonController();
        }
        
        return logonController;
	}
	
	public static void setLogonControllerClass(Class logonControllerClass, boolean lock) {

		// TODO: locked state is not implemented.
		LogonControllerFactory.logonControllerClass = logonControllerClass;
		logonController = null;
		locked = lock;
	}
}
