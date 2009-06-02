
				/*
 *  OpenVPNALS
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package net.openvpn.als.core;

/**
 * Encapsulates a script fragment that will be added to every page in 
 * OpenVPNALS user interface. For example, the Xtra plugin has its own
 * JavaScript libraries that need to be placed on every page for the
 * virtual keyboard to work. The plugin creates an instance of this
 * class and registers it using {@link net.openvpn.als.core.CoreServlet#addPageScript(CoreScript)}.
 * The list of registered scripts is then iterated over on ever.
 * <p> 
 * Currently a script may be placed in one of three places, in the 
 * {@link #PAGE_HEADER}, {@link #AFTER_BODY_START} or {@link #BEFORE_BODY_END}.
 * 
 * @since 0.2
 */

public class CoreScript {

    /**
     * Type that places the script in the page header
     */
    public final static int PAGE_HEADER = 0;
    
    /**
     * Type that places the script just after the &lt;body&gt; tag.
     */
    public final static int AFTER_BODY_START = 1;

    /**
     * Type that places the script just before the end &lt;body&gt; tag.
     */
    public final static int BEFORE_BODY_END = 2;
    
    // Private instance variables
    
    private String language;
    private String path;
    private String script;
    private String type;
    private int position;

    /**
     * Constructor that by default places the script after the start body tag. Either
     * the path of the script text must be supplied although you may omit either. 
     * 
     * @param language script language, e.g. Javascript
     * @param path the path to script. This may be <code>null</code> if the <i>script</i> argument is supplied
     * @param script the actual script fragment as a string. This may be <code>null</code> if the <i>path</i> argument is supplied.
     * @param type mime type. May be omitted.
     */
    public CoreScript(String language, String path, String script, String type) {
        this(AFTER_BODY_START, language, path, script, type);
    }

    /**
     * Constructor that by default places the script after the start body tag. Either
     * the path of the script text must be supplied although you may omit either. 
     * 
     * @param position may be one of {@link #PAGE_HEADER}, {@link #AFTER_BODY_START} or {@link #BEFORE_BODY_END}
     * @param language script language, e.g. Javascript
     * @param path the path to script. This may be <code>null</code> if the <i>script</i> argument is supplied
     * @param script the actual script fragment as a string. This may be <code>null</code> if the <i>path</i> argument is supplied.
     * @param type mime type. May be omitted.
     */
    public CoreScript(int position, String language, String path, String script, String type) {
        super();
        this.position = position;
        this.language = language;
        this.path = path;
        this.script = script;
        this.type = type;
    }

    /**
     * Get the language or <code>null</code> if not specified. If not specified
     * the generated HTML will not contain the <i>language</i> attribute.
     * 
     * @return the language.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set the language or <code>null</code>. If null the generated HTML will
     * not contain the <i>language</i> attribute.
     * 
     * @param language The language to set.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Get the path to the script or <code>null</code> if the path is not specified. 
     *  
     * @return returns the path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the path to script. 
     * 
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get the script fragment text.
     * 
     * @return returns the script fragment text.
     */
    public String getScript() {
        return script;
    }

    /**
     * Set the script fragment text.
     * 
     * @param script The script fragment to set.
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * Get the MIME type or <code>null</code> if not specified.
     * 
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the MIME type of <code>null</code> to not specify.
     * 
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the HTML to render for this script
     * 
     * @return HTML
     */
    public String getRenderedHTML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<script");
        if(language != null && !language.equals("")) {
            buf.append(" language=\"");
            buf.append(language);
            buf.append("\"");
        }
        if(type != null && !type.equals("")) {
            buf.append(" language=\"");
            buf.append(language);
            buf.append("\"");
        }        
        if(path != null && !path.equals("")) {
            buf.append(" src=\"");
            buf.append(path);
            buf.append("\"");
        }        
        if(script != null && !script.equals("")) {
            buf.append(">");
            buf.append(script);
            buf.append("</script>");
        }
        else {
            buf.append("/>");
        }
        return buf.toString();
    }

    /**
     * Return the position the script should be place at. Will be one
     * of {@link #PAGE_HEADER}, {@link #AFTER_BODY_START} or {@link #BEFORE_BODY_END}.
     * 
     * @return the position.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Set the position the script should be place at. Will be one
     * of {@link #PAGE_HEADER}, {@link #AFTER_BODY_START} or {@link #BEFORE_BODY_END}.
     * 
     * @param position the position.
     */
    public void setPosition(int position) {
        this.position = position;
    }
}
