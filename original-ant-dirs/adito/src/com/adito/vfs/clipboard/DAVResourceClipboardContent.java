package com.adito.vfs.clipboard;


public class DAVResourceClipboardContent implements ClipboardContent {

	private boolean deleteOnPaste = false;
	private String davPath = null;

	public DAVResourceClipboardContent(String davPath) {
		this.davPath = davPath;
	}

	public DAVResourceClipboardContent(String davPath, boolean deleteOnPaste) {
		this.davPath = davPath;
		this.deleteOnPaste = deleteOnPaste;
	}
	
	public String getDAVPath(){
		return this.davPath;
	}

	public boolean deleteOnPaste() {
		return this.deleteOnPaste;
	}
    
    public String toString() {
        return davPath;
    }

}
