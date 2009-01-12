package com.adito.vfs;

public class VFSPath
{
	private int position;
	private String folder;
	private String path;
	
	public VFSPath(int position, String path){
		this.position = position;
		this.path = path;
		this.folder = path.substring(path.lastIndexOf("/")+1, path.length()) ;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
