package com.ovpnals.vfs.clipboard;

import java.util.ArrayList;
import java.util.List;

/**
 * A class which holds a list of clipboard content.
 */
public class Clipboard {

    private List<ClipboardContent> content;

    /**
     * Constructor
     */
    public Clipboard() {
        this.content = new ArrayList<ClipboardContent>();
    }

    /**
     * @return List of ClipboardContent
     */
    public List<ClipboardContent> getContent() {
        return content;
    }

    /**
     * @param clipboardContent
     */
    public void addContent(ClipboardContent clipboardContent) {
        this.content.add(clipboardContent);
    }

    /**
     * 
     */
    public void clearClipboard() {
        this.content.clear();
    }

}
