package com.adito.agent.client.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;

public class FileCleaner {

	protected static Vector filesToRemove = new Vector();

	public static void deleteAllFiles() {
		Enumeration e = filesToRemove.elements();
		while(e.hasMoreElements()) {
			File file = (File) e.nextElement();
			file.delete();
		}
		filesToRemove.removeAllElements();
	}
	
	public static File deleteOnExit(File file) {
		
        try {
			Method m = File.class.getMethod("deleteOnExit", new Class[] { });
			if(m!=null) {
				m.invoke(file, new Object[] { });
			}
		} catch (Exception e) {
			// Add to file removal array
			filesToRemove.addElement(file);
		} 
		
		return file;
		
	}
}
