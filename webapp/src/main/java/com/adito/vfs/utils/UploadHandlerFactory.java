package com.adito.vfs.utils;

import java.util.HashMap;
import java.util.Map;

import com.adito.core.RepositoryUploadHandler;
import com.adito.core.UploadHandler;
import com.adito.extensions.ExtensionUploadHandler;


public class UploadHandlerFactory {
	
    private static UploadHandlerFactory instance;
    
    private Map handlers;
    
    public UploadHandlerFactory() {
        handlers = new HashMap();
        handlers.put(RepositoryUploadHandler.TYPE_REPOSITORY, RepositoryUploadHandler.class);
        handlers.put(ExtensionUploadHandler.TYPE_EXTENSION, ExtensionUploadHandler.class);
    }
    
    public void addHandler(String id, Class clazz) {
        handlers.put(id, clazz);
    }

	public static UploadHandlerFactory getInstance() {
        if(instance == null) {
            instance = new UploadHandlerFactory();
        }
        return instance;
    }
	
	public UploadHandler getUploader(String type) throws Exception {
        Class clazz = (Class)handlers.get(type);
        if(clazz == null) {
            throw new Exception("No registered upload handler of type " + type + ".");
        }
        return (UploadHandler)clazz.newInstance();
	}

    public void removeHandler(String id) {
        handlers.remove(id);        
    }
}
