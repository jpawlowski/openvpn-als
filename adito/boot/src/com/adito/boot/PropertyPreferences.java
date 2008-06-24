
				/*
 *  Adito
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
			
package com.adito.boot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A simple implementation for the preferences API. That stores preferences
 * in propery files. We do not have to worry about sharing the preferencese 
 * with other JVM instance so there is no need for any kind of synchronising
 * or locking.
 */
public class PropertyPreferences extends AbstractPreferences {
    
    /**
     * System root
     */
    public final static Preferences SYSTEM_ROOT = new PropertyPreferences(new File(new File(ContextHolder.getContext().getConfDirectory(), "prefs"), "system"));
    
    /**
     * User root
     */
    public final static Preferences USER_ROOT = new PropertyPreferences(new File(new File(ContextHolder.getContext().getConfDirectory(), "prefs"), "system"));
    
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final int FLUSH_INTERVAL = 30; // seconds
    
    final static Log log = LogFactory.getLog(PropertyPreferences.class);


    private static Timer flushTimer = new Timer(true); // Daemon Thread

    static {
        // Add periodic timer task to periodically sync cached prefs
        flushTimer.schedule(new TimerTask() {
            public void run() {
                flushAll();
            }
        }, FLUSH_INTERVAL*1000, FLUSH_INTERVAL*1000);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                flushTimer.cancel();
                flushAll();
            }
        });
    }
    
    // Private instance variables
    
    private File dir;
    private File prefFile;
    private Properties prefs;

    /**
     * Constructor for root node.
     *
     * @param dir directory
     */
    public PropertyPreferences(File dir) {
        this(dir, null, "");
    }

    /**
     * Constructor.
     *
     * @param dir directory containing preferences and children
     * @param parent
     * @param name
     */
    public PropertyPreferences(File dir, AbstractPreferences parent, String name) {
        super(parent, name);
        this.dir = dir;
    }

    /* (non-Javadoc)
     * @see java.util.prefs.AbstractPreferences#childSpi(java.lang.String)
     */
    protected AbstractPreferences childSpi(String name) {
        return new PropertyPreferences(new File(dir, encodeDirName(name)), this, name);
    }

    /* (non-Javadoc)
     * @see java.util.prefs.AbstractPreferences#childrenNamesSpi()
     */
    protected String[] childrenNamesSpi() throws BackingStoreException {   
        List result = new ArrayList();
        File[] dirContents = dir.listFiles();
        if (dirContents != null) {
            for (int i = 0; i < dirContents.length; i++)
                if (dirContents[i].isDirectory())
                    result.add(decodeDirName(dirContents[i].getName()));
        }
        return (String[])result.toArray(EMPTY_STRING_ARRAY);
    }

    /* (non-Javadoc)
     * @see java.util.prefs.AbstractPreferences#flushSpi()
     */
    protected void flushSpi() throws BackingStoreException {
        if(prefs == null) {
            return;
        }
        if(prefFile == null) {
            prefFile = new File(dir, "prefs.properties");
        }
        if(!dir.exists() && !dir.mkdirs()) {
            throw new BackingStoreException("Failed to create node directory " + dir.getPath() + ".");
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(prefFile);
            prefs.store(fos, name());
        }
        catch(IOException ioe) {
            throw new BackingStoreException(ioe);
        }
        finally {
            Util.closeStream(fos);
        }
    }

    /* (non-Javadoc)
     * @see java.util.prefs.AbstractPreferences#getSpi(java.lang.String)
     */
    protected String getSpi(String key) {
        checkLoaded();
        return prefs == null ? null : prefs.getProperty(key);
    }

    /* (non-Javadoc)
     * @see java.util.prefs.AbstractPreferences#keysSpi()
     */
    protected String[] keysSpi() throws BackingStoreException {
        checkLoaded();
        return prefs == null ? EMPTY_STRING_ARRAY : (String[])
            prefs.keySet().toArray(new String[prefs.size()]);
    }

    /* (non-Javadoc)
     * @see java.util.prefs.AbstractPreferences#putSpi(java.lang.String, java.lang.String)
     */
    protected void putSpi(String key, String value) {
        checkLoaded();
        if(prefs == null) {
            prefs = new Properties();
        }
        prefs.setProperty(key, value);
    }

    /* (non-Javadoc)
     * @see java.util.prefs.AbstractPreferences#removeNodeSpi()
     */
    protected void removeNodeSpi() throws BackingStoreException {
        if(!Util.delTree(dir)) {
            throw new BackingStoreException("Failed to remove preferencese node " + dir.getPath() + ".");
        }
    }

    /* (non-Javadoc)
     * @see java.util.prefs.AbstractPreferences#removeSpi(java.lang.String)
     */
    protected void removeSpi(String key) {
        checkLoaded();
        if(prefs != null) {
            prefs.remove(key);
        }

    }

    /* (non-Javadoc)
     * @see java.util.prefs.AbstractPreferences#syncSpi()
     */
    protected void syncSpi() throws BackingStoreException {
        flushSpi();

    }
    
    String encodeDirName(String dirName) {
        for (int i=0, n=dirName.length(); i < n; i++) {
            if (!isValidChar(dirName.charAt(i))) {
                return "_" + Base64.encode(dirName.getBytes());
            }
        }
        return dirName;
    }
    
    String decodeDirName(String dirName) {
        if(dirName.startsWith("_")) {
            return new String(Base64.decode(dirName.substring(1)));
        }
        return dirName;
    }
    
    boolean isValidChar(char ch) {
        return ch > 0x1f && ch < 0x7f && ch != '/' && ch != '.' && ch != '_' && ch != '\\';
    }
    
    synchronized void checkLoaded() {
        if(prefFile == null) {
            prefFile = new File(dir, "prefs.properties");
        }
        if(prefFile.exists()) {
            if(prefs == null) {
                prefs = new Properties();
                FileInputStream fin = null;
                try {
                    fin = new FileInputStream(prefFile);
                    prefs.load(fin);
                }
                catch(IOException ioe) {
                    log.error("Failed to open preferences file " + prefFile.getPath() + ".", ioe);
                }
                finally {
                    Util.closeStream(fin);
                }
                
            }
        }
    }
    
    static void flushAll() {
        try {
            if (SYSTEM_ROOT != null)
                SYSTEM_ROOT.flush();
        } catch(BackingStoreException e) {
            log.warn("Couldn't flush system prefs.", e);
        }
        try {
            if (USER_ROOT != null)
                USER_ROOT.flush();
        } catch(BackingStoreException e) {
            log.warn("Couldn't flush user prefs.", e);
        }
    }

}
