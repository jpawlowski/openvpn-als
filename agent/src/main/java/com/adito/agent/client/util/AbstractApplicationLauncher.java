
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
			
package com.adito.agent.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

import com.adito.agent.client.util.types.DefaultAgentApplicationType;
import com.adito.agent.client.util.types.ExecutableApplicationType;
import com.adito.agent.client.util.types.HtmlApplicationType;
import com.adito.agent.client.util.types.JavaApplicationType;

/**
 * Launches an application shortcut by downloading the extension descriptor for
 * the appropriate application extension, processing it then launching the
 * application.
 * <p>
 * During processing of the descriptor, additional files may be download and
 * cached on the client.
 * <p>
 * How the application is launched depends on its type. Types include
 * {@link JavaApplicationType}, {@link DefaultAgentApplicationType},
 * {@link HtmlApplicationType}, {@link ExecutableApplicationType}.
 */
public abstract class AbstractApplicationLauncher {

	static Vector EMPTY_VECTOR = new Vector();
    File installDir;
    File sharedDir;
    String typeName;

    String name;
    String ticket;

    //	Protected instance variables
    
    protected String applicationStoreProtocol;
    protected String applicationStoreHost;
    protected String applicationStoreUser;
    protected int applicationStorePort;
    protected ApplicationLauncherEvents events;
    protected Hashtable parameters;
    
    // Privaet instance variables

    private long totalBytesToDownload = 0;
    private Vector filesToDownload = new Vector();
    private Hashtable sharedFilesToDowload = new Hashtable();
    private Hashtable descriptorParams = new Hashtable();
    private boolean debug = false;
    private String localProxyURL;
    private ApplicationType type;
    private Hashtable replacements = new Hashtable();
    private Vector transformations = new Vector();
    private boolean hasPrepared = false;
    private File cacheDir;
    private String smallIcon, largeIcon;
    
    /**
     * Constructor.
     *
     * @param cacheDir cache directory
     * @param applicationStoreProtocol application store protocol (http / https)
     * @param applicationStoreUser application store userinfo (CHECK user /
     *        password or ticket?)
     * @param applicationStoreHost application store host
     * @param applicationStorePort application store port
     * @param parameters parmaeters
     * @param events callback interface for events
     */
    public AbstractApplicationLauncher(File cacheDir, String applicationStoreProtocol, String applicationStoreUser, String applicationStoreHost,
                               int applicationStorePort, Hashtable parameters, ApplicationLauncherEvents events) {
        this.cacheDir = cacheDir;
        this.applicationStoreProtocol = applicationStoreProtocol;
        this.applicationStoreHost = applicationStoreHost;
        this.applicationStoreUser = applicationStoreUser;
        this.applicationStorePort = applicationStorePort;
        this.parameters = parameters;
        this.events = events;
    }

    /**
     * Set the URL of the local proxy server (if required). This is used when
     * replacing variables in the extension descriptor.
     * 
     * @param localProxyURL local proxy URL
     */
    public void setLocalProxyURL(String localProxyURL) {
        this.localProxyURL = localProxyURL;
    }

    /**
     * If set to true, a <b>debug</b> property with be added to parameters.
     * 
     * @param debug send debug parameter
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    /**
     * Is the application in debug mode?
     * @return
     */
    public boolean isDebug() {
    	return debug;
    }

    /**
     * Get the application type implementation.
     * 
     * @return application type
     */
    public ApplicationType getApplicationType() {
        return type;
    }

    /**
     * Prepare and download and process the extension descriptor.
     * 
     * @throws IOException on any error
     */
    public void prepare() throws IOException {

    	hasPrepared = true;
    	
        if (events != null)
            events.debug(Messages.getString("ApplicationLauncher.checkingParameters")); //$NON-NLS-1$

        // Add a debug parameter if were debugging
        if (debug) {
            parameters.put("debug", "true"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        ticket = (String) parameters.get("ticket"); //$NON-NLS-1$
        if (events != null && ticket!=null)
            events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.storingTicket"), new Object[] { ticket })); //$NON-NLS-1$

        XMLElement element = new XMLElement();
        
        // Read the XML into a string first so it can be displayed upon exception

        events.debug(Messages.getString("ApplicationLauncher.gettingDescriptorStream")); //$NON-NLS-1$
        InputStreamReader reader = new InputStreamReader(getApplicationDescriptor());
        events.debug(Messages.getString("ApplicationLauncher.readingDescriptorStream")); //$NON-NLS-1$
        char[] cbuf = new char[65536];
        StringBuffer xml = new StringBuffer();
        int read = 0;
        while (read != -1) {
        	read = reader.read(cbuf,0,cbuf.length);
        	if(read != -1) {
                events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.readBlock"), new Object[] { String.valueOf(read) })); //$NON-NLS-1$
                xml.append(cbuf,0,read);
        	}
        } 
        try {
            events.debug(Messages.getString("ApplicationLauncher.parsingDescriptor")); //$NON-NLS-1$
        	element.parseFromReader(new StringReader(xml.toString()));
            events.debug(Messages.getString("ApplicationLauncher.parsedDescriptor")); //$NON-NLS-1$
        }
        catch(XMLParseException xmlpe) {
            if (events != null) {
                events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.failedToParseXML"), new Object[] { xmlpe.getMessage() } ) ); //$NON-NLS-1$
                events.debug(xml.toString());
            }
            throw xmlpe;
        }
        
        if(debug)
        	events.debug("[XML] [" + xml.toString() + "]");

        if (events != null)
            events.debug(Messages.getString("ApplicationLauncher.receivedResponseFromServer")); //$NON-NLS-1$

        if (!element.getName().equals("application") && !element.getName().equals("error") //$NON-NLS-1$ //$NON-NLS-2$
            && !element.getName().equals("extension")) { //$NON-NLS-1$
            throw new IOException(Messages.getString("ApplicationLauncher.urlDoesNotPointToApplicationDescriptor")); //$NON-NLS-1$
        } else if (element.getName().equals("error")) { //$NON-NLS-1$
            throw new IOException(element.getContent());
        }

        name = (String) element.getAttribute("extension"); //$NON-NLS-1$
        if (name == null) {
            name = (String) element.getAttribute("application"); //$NON-NLS-1$
        }

        typeName = (String) element.getAttribute("type"); //$NON-NLS-1$ 
        smallIcon = (String) element.getAttribute("smallIcon"); //$NON-NLS-1$
        largeIcon = (String) element.getAttribute("largeIcon"); //$NON-NLS-1$
        
        try {
           type = ApplicationTypeManager.getInstance().createType(typeName);
        } catch (Throwable t) {
        	t.printStackTrace();
            throw new IOException(MessageFormat.format(Messages.getString("ApplicationLauncher.failedToLoadApplicationDescriptor"), new Object[] { typeName + " " + t.getMessage() })); //$NON-NLS-1$
        }

        if (events != null)
            events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.applicationNameIs"), new Object[] { name, type.getClass().getName() })); //$NON-NLS-1$

        if (events != null) {
            events.processingDescriptor();
        }

        if (events != null)
            events.debug(Messages.getString("ApplicationLauncher.creatingInstallFolder")); //$NON-NLS-1$

        // This code attempts to detect the MSJVM and obtain the users profile
        // directory so that it can be used as the users home.

        if (events != null)
            events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.userHomeIs"), new Object[] { cacheDir.getAbsolutePath() })); //$NON-NLS-1$

        installDir = new File(cacheDir, "applications" + File.separator + name); //$NON-NLS-1$ //$NON-NLS-2$

        sharedDir = new File(cacheDir, "shared"); //$NON-NLS-1$ //$NON-NLS-2$

        if (!installDir.exists()) {
            installDir.mkdirs();
        }

        if (!sharedDir.exists()) {
            sharedDir.mkdirs();
        }

        if (events != null)
            events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.installingTo"), new Object[] { installDir.getAbsolutePath() })); //$NON-NLS-1$

        
        processElements(element);
        
    }
    
    void processElements(XMLElement element) throws IOException {
        
    	Enumeration e = element.enumerateChildren();
    	
    	while (e.hasMoreElements()) {
            XMLElement el = (XMLElement) e.nextElement();

            if(el.getName().equalsIgnoreCase("if")) {
            	// This catch all if allows <if> at top level of all elements.
            	// WARNING: do not use lightly!!
            	if(checkCondition(type, el, parameters)) {
                   processElements(el);	
            	}
            } else if (el.getName().equalsIgnoreCase("files")) { //$NON-NLS-1$
                processFiles(el);
            } else if (el.getName().equalsIgnoreCase("tunnel")) { //$NON-NLS-1$
                // This should contain the host and port of a tunnel that
                // we need to create
                createTunnel(el);
            } else if (el.getName().equalsIgnoreCase("parameter")) { //$NON-NLS-1$
                addParameter(el);
            } else if (el.getName().equalsIgnoreCase("messages")) { //$NON-NLS-1$
                // Ignore as its a server side element
            } else if (el.getName().equalsIgnoreCase("description")) { //$NON-NLS-1$
                // Simply ignore.. should we throw an exception if an element is
                // not known?
            } else if (el.getName().equalsIgnoreCase("replacements")) { //$NON-NLS-1$
                FileReplacement replacement = new FileReplacement(installDir);
                replacement.processReplacementXML(el, this);
                replacements.put(replacement.getId(), replacement);
            } else if (processLauncherElement(el)) {
                // This allows us to override more element types in extended
                // application launchers (i.e. registry parameters)
                continue;
            } else if (el.getName().equalsIgnoreCase("transform")) { //$NON-NLS-1$
                ParameterTransformation trans = new ParameterTransformation(el, this);
                transformations.addElement(trans);
            } else {
                type.prepare(this, events, el);
            }
        }    	
    }
    
    public void download() throws IOException {
    	
    	downloadFiles();

        if (events != null)
            events.debug(Messages.getString("ApplicationLauncher.applyingParameterTransformations")); //$NON-NLS-1$

        for (Enumeration ep = transformations.elements(); ep.hasMoreElements();) {
            ParameterTransformation trans = (ParameterTransformation) ep.nextElement();
            trans.processTransformation();
        }

        if (events != null)
            events.debug(Messages.getString("ApplicationLauncher.creatingReplacements")); //$NON-NLS-1$

        for (Enumeration ep = replacements.elements(); ep.hasMoreElements();) {
            FileReplacement replacement = (FileReplacement) ep.nextElement();
            replacement.createReplacementsFile(this);
        }

        if (events != null)
            events.debug(Messages.getString("ApplicationLauncher.replacementsCreatedPreparationComplete")); //$NON-NLS-1$    	
    }
    
    /**
     * Get the name of the small icon for this application.
     * 
     * @return small icon
     */
    public String getSmallIcon() {
    	return smallIcon;
    }
    
    /**
     * Get the name of the large icon for this application.
     * 
     * @return small icon
     */
    public String getLargeIcon() {
    	return largeIcon;
    }

    /**
     * Download the application descriptor. This should not be called directory,
     * it gets called during {@link #prepare()}.
     * 
     * @return application descriptor stream
     * @throws IOException
     */
    protected abstract InputStream getApplicationDescriptor() throws IOException;

    protected boolean processLauncherElement(XMLElement e) {
        return false;
    }

    /**
     * Get the application name
     * 
     * @return application name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the directory where the applications files will be installed.
     * 
     * @return install directory
     */
    public File getInstallDir() {
        return installDir;
    }

    /**
     * Start the application
     */
    public void start() throws IOException {
    	download();
        type.start();
    }

    /**
     * Add a new parameter to the launcher. This must be done before the
     * application is launched.
     * 
     * @param parameter
     * @param value
     */
    public void addParameter(String parameter, String value) {

        if (events != null) {
            if (parameter == "password"){
                events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.addingParameter"), new Object[] { parameter, "**********" }));//$NON-NLS-1$
            }
            else{
                events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.addingParameter"), new Object[] { parameter, value }));//$NON-NLS-1$
            }
        }

        descriptorParams.put(parameter, value);
    }

    /**
     * Process the application extension descriptor.
     * 
     * @param element XML element
     * @throws IOException
     */
    public void processFiles(XMLElement element) throws IOException {
        processFiles(element, null);
    }

    /**
     * Process the application extension descriptor.
     * 
     * @param element XML element
     * @param app application
     * @throws IOException
     */
    public void processFiles(XMLElement element, String app) throws IOException {

        Enumeration en = element.enumerateChildren();
        XMLElement e;

        while (en.hasMoreElements()) {
            e = (XMLElement) en.nextElement();
            if (e.getName().equalsIgnoreCase("file")) { //$NON-NLS-1$
            	if(checkCondition(type, e, parameters)) {
                    addFile(e, app);            		
            	}
            } else if (e.getName().equalsIgnoreCase("if")) { //$NON-NLS-1$
                if (checkCondition(type, e, parameters)) {                	
                    processFiles(e, app);
                }

            } else
                throw new IOException(MessageFormat.format(Messages.getString("ApplicationLauncher.invalidElementInFiles"), new Object[] { e.getName() }));//$NON-NLS-1$ //$NON-NLS-2$
        }

    }

    /**
     * Check for the conditions of elements to make sure they are appropriate
     * for the current platform / environment.
     * <p>
     * Only files that are ok will be downloaded / synchornized
     * 
     * @param el file element
     * @return ok for use
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static boolean checkCondition(ApplicationType type, XMLElement el, Hashtable params) throws IOException, IllegalArgumentException {
    	String jre = (String) el.getAttribute("jre"); //$NON-NLS-1$
    	String os = (String) el.getAttribute("os"); //$NON-NLS-1$
    	String osVersion = (String) el.getAttribute("osversion"); //$NON-NLS-1$
    	String arch = (String) el.getAttribute("arch"); //$NON-NLS-1$
        String parameter = (String) el.getAttribute("parameter"); //$NON-NLS-1$
        String string = (String) el.getAttribute("string"); //$NON-NLS-1$
    	if(jre != null) {
			if(!Utils.isSupportedJRE(jre)) {
				return false;
			}
    	}
    	if(os != null) {
			if(!Utils.isSupportedPlatform(os)) {
				return false;
			}
    	}
    	if(osVersion != null) {
			if(!Utils.isSupportedOSVersion(osVersion)) {
				return false;
			}
    	}
    	if(arch != null) {
			if(!Utils.isSupportedArch(arch)) {
				return false;
			}
    	}
        if (parameter != null) {
        	if(params == null) {
        		throw new IOException(Messages.getString("ApplicationLauncher.noParametersToTestAgainst")); //$NON-NLS-1$
        	}
            String requiredValue = (String) el.getAttribute("value"); //$NON-NLS-1$
            boolean not = "true".equalsIgnoreCase(((String) el.getAttribute("not"))); //$NON-NLS-1$ //$NON-NLS-2$

            // Check the parameter
            String paramValue = (String) params.get(parameter);

            if ((!not && !requiredValue.equalsIgnoreCase(paramValue)) || (not && requiredValue.equalsIgnoreCase(paramValue))) {
            	return false;
            }
        }
        if(string != null) {
            String requiredValue = (String) el.getAttribute("value"); //$NON-NLS-1$
            boolean not = "true".equalsIgnoreCase(((String) el.getAttribute("not"))); //$NON-NLS-1$ //$NON-NLS-2$
            
            if ((!not && !requiredValue.equalsIgnoreCase(string)) || (not && requiredValue.equalsIgnoreCase(string))) {
            	return false;
            }
        }
    	return true;
    }

    /**
     * Get the application extentsion descriptor parameters as a hastable of
     * string objects
     * 
     * @return descriptor parameters
     */
    public Hashtable getDescriptorParams() {
        return descriptorParams;
    }

    /**
     * Add shared file element.
     * 
     * @param e shared file element
     * @return file
     * @throws IOException
     */
    public File addShared(XMLElement e) throws IOException {
        return addShared(e, null);
    }

    /**
     * Add shared file element.
     * 
     * @param e shared file element
     * @param app application
     * @return file
     * @throws IOException
     */
    public File addShared(XMLElement e, String app) throws IOException {
        return addFile(e, app, true);

    }

    /**
     * Add a file.
     * 
     * @param e file element
     * @param app application
     * @return file
     * @throws IOException
     */
    public File addFile(XMLElement e, String app) throws IOException {
    	return addFile(e, app, false);
    }

    /**
     * Add a file.
     * 
     * @param e file element
     * @param app application
     * @param fileMap file map
     * @return file
     * @throws IOException
     */
    File addFile(XMLElement e, String app, boolean shared) throws IOException {

        boolean executable = "true".equals(e.getStringAttribute("exec", "false"));
        boolean readOnly = "true".equals(e.getStringAttribute("readOnly", "false"));
        String targetDirName = e.getStringAttribute("targetDir");
        

        /**
         * LDP - Make sure any downloaded files are using the correct seperator
         * char for the platform.
         */
        String name = e.getStringAttribute("name", "");
        if(name.equals("")) {
        	name = Utils.trimmedBothOrBlank(e.getContent());
        }
        name = name.replace('/', File.separatorChar);
        
        // Work out the target file
        File targetDir = installDir;
        File entry = new File(installDir, name);
        if(!Utils.isNullOrTrimmedBlank(targetDirName)) {
        	if(shared) {
        		throw new IOException("Cannot specify target directory for shared files.");
        	}
        	targetDir = new File(installDir, targetDirName);
        	String filename = name;
        	int idx = name.lastIndexOf(File.separatorChar);
        	if(idx != -1) {
        		filename = filename.substring(idx + 1);
        	}
        	entry = new File(targetDir, filename);
            if (events != null) {
                events.debug("Alternative target directory of " + targetDirName + " (" + targetDir.getAbsolutePath() + ") provided. Target is " + entry.getAbsolutePath());
            }
        }
        
        DownloadableFile df = new DownloadableFile(name, app, executable, readOnly, entry, 0);
        long size = 0;
        try {
        	size = Long.parseLong((String) e.getAttribute("size")); //$NON-NLS-1$
        }
        catch(NumberFormatException nfe) {
            throw new IOException(MessageFormat.format(Messages.getString("ApplicationLauncher.invalidSize"), new Object[] { name })); //$NON-NLS-1$
        	
        }
        
        if (events != null) {
            events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.addingFile"), new Object[] { entry.getAbsolutePath(), new Long(size) } )  ); //$NON-NLS-1$ 
        }

        boolean download = true;
        
        if (e.getAttribute("checksum") == null || e.getAttribute("size") == null) { //$NON-NLS-1$ //$NON-NLS-2$
            df.setChecksum(0);
        } else {
        	df.setChecksum(Long.parseLong((String) e.getAttribute("checksum")));
        }
        
        if (entry.exists()) {

            // The file exists so lets check its size and checksum to determine
            // whether we need to download it again$

            long currentChecksum = generateChecksum(entry);

            if (events != null) {
                events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.fileExistsCurrentChecksum"), new Object[] { new Long(currentChecksum), new Long(df.getChecksum()) })); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (currentChecksum != df.getChecksum() || entry.length() != size) {
                if (events != null) {
                    events.debug(Messages.getString("ApplicationLauncher.checksumMismatchDownloading")); //$NON-NLS-1$
                }

                if(shared)
                	sharedFilesToDowload.put(name, df);
                else
                	filesToDownload.addElement(df);
                
                totalBytesToDownload += entry.length();
            }
        } else {
            if (events != null) {
                events.debug(Messages.getString("ApplicationLauncher.fileDoesntExistDownloading")); //$NON-NLS-1$
            }

            // We currently do not have the file so add it to the download list
            if(shared)
            	sharedFilesToDowload.put(name, df);
            else
            	filesToDownload.addElement(df);
            totalBytesToDownload += size;
        }
        
        // Add aliases
        Enumeration ae = e.enumerateChildren();
        while (ae.hasMoreElements()) {
            XMLElement el = (XMLElement) ae.nextElement();
            if(el.getName().equals("alias")) {
            	df.addAlias(Utils.trimmedBothOrBlank(el.getContent()));
            }
        }

        return entry;
    }

    /**
     * Download a single file from an application extension.
     * 
     * @param basedir directory to install to
     * @param app application extension id
     * @param filename filename to download
     * @param target file to download to
     * @param bytesSoFar value to start byte count at, this value will be added
     *        to then returned
     * @param executable make executable
     * @param readOnly make read only
     * @param aliases aliases
     * @return bytes read so far
     * @throws IOException on any error
     */
    public long downloadFile(File basedir, String app, String filename, File target, long bytesSoFar, boolean executable, boolean readOnly, Vector aliases, long checksum) throws IOException {

        /**
         * LDP - Another method that delegates collection of a HTTP request to a
         * seperate method so that it can be overidden.
         */
        if (events != null)
            events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.downloading"), new Object[] { filename, target.getAbsolutePath() })); //$NON-NLS-1$ //$NON-NLS-2$

        File f = new File(target.getParent());
        f.mkdirs();

        if (events != null)
            events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.creating"), new Object[] { target.getAbsolutePath() })); //$NON-NLS-1$

        target.delete();
        FileOutputStream out = new FileOutputStream(target);
        InputStream in = getDownloadFile(app, ticket, filename);
        byte[] buf = new byte[16384];
        int read;
        while ((read = in.read(buf)) > -1) {
            out.write(buf, 0, read);
            bytesSoFar += read;

            if (events != null)
                events.progressedDownload(bytesSoFar);
        }
        if (events != null)
            events.debug(Messages.getString("ApplicationLauncher.finishingDownloadFile")); //$NON-NLS-1$

        in.close();
        out.close();
        
        if(executable) {
        	try {
                if (events != null)
                    events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.makingExecutable"), new Object[] { target.getPath() } ) ); //$NON-NLS-1$
        		Utils.makeExecutable(target);
        	}
        	catch(IOException ioe) {
                if (events != null)
                    events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.failedToMakeExecutable"), new Object[] { target.getPath(), ioe.getMessage() } ) ); //$NON-NLS-1$        		
        	}
        }
        
        long newChecksum = generateChecksum(target);
        if(newChecksum != checksum) {
        	events.debug("WARNING. File " + target.getPath() + " does not match expected checksum. Checksum is " + newChecksum + ", should be " + checksum);
        }
        
        if(readOnly) {
        	try {
                if (events != null)
                    events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.makingReadOnly"), new Object[] { target.getPath() } ) ); //$NON-NLS-1$
        		Utils.makeReadOnly(target);
        	}
        	catch(IOException ioe) {
                if (events != null)
                    events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.failedToMakeReadOnly"), new Object[] { target.getPath(), ioe.getMessage() } ) ); //$NON-NLS-1$        		
        	}
        }
        
        if(aliases != null && aliases.size() >0) {
        	for(Enumeration e = aliases.elements(); e.hasMoreElements(); ) {
        		String alias = (String)e.nextElement();
        		// Copy for now, look into how to link
    			File aliasTarget = new File(basedir, alias);
        		if(events != null) {
        			events.debug("Copying " + target.getPath() + " to " + aliasTarget.getPath());
        		}
        		Utils.copyFile(target, aliasTarget); 
        	}
        }

        return bytesSoFar;

    }

    /**
     * Get the hostname on which the application store (i.e. the Adito
     * server) is running.
     * 
     * @return application store host
     */
    public String getApplicationStoreHost() {
        return applicationStoreHost;
    }

    /**
     * Get the port on which the application store (i.e. the Adito
     * server) is running.
     * 
     * @return application store port
     */
    public int getApplicationStorePort() {
        return applicationStorePort;
    }

    /**
     * Get the protocol of where the application store (i.e. the Adito
     * server) is locate. This will be either <i>http</i> or <i>https</i>.
     * 
     * @return application store protocol
     */
    public String getApplicationStoreProtocol() {
        return applicationStoreProtocol;
    }

    // Utility methods

    /**
     * Replace the standard tokens in the source string with the value know by
     * this VPN client.
     * <p>
     * Supports values include :- <code>${client:installDir}</code> - Client
     * installation directory<br/> <code>${adito:user}</code> -
     * Aditouser<br/> <code>${adito:host}</code> - Adito
     * host<br/> <code>${adito:port}</code> - Adito port<br/>
     * <code>${adito:protocol}</code> - Protocol (http / https)<br/>
     * <code>${adito:localProxyURL}</code> - Local proxy server URL<br/>
     * <code>${tunnel:XXXXX.hostname}</code> - Named tunnel hostname<br/>
     * <code>${tunnel:XXXXX.port}</code> - Named tunnel port<br/>
     * 
     * @param str source string
     * @return processed string
     */
    public String replaceTokens(String str) {
        str = replaceAllTokens(str, "${client:installDir}", installDir.getAbsolutePath()); //$NON-NLS-1$
        str = replaceAllTokens(str, "${adito:user}", applicationStoreUser == null ? "" : applicationStoreUser); //$NON-NLS-1$ //$NON-NLS-2$
        str = replaceAllTokens(str, "${adito:host}", applicationStoreHost == null ? "" : applicationStoreHost); //$NON-NLS-1$ //$NON-NLS-2$
        str = replaceAllTokens(str, "${adito:port}", applicationStorePort == -1 ? "" : String.valueOf(applicationStorePort)); //$NON-NLS-1$ //$NON-NLS-2$
        str = replaceAllTokens(str, "${adito:protocol}", applicationStoreProtocol == null ? "" : applicationStoreProtocol); //$NON-NLS-1$ //$NON-NLS-2$
        str = replaceAllTokens(str, "${client:localProxyURL}", localProxyURL == null ? "" : localProxyURL); //$NON-NLS-1$ //$NON-NLS-2$
        for (Enumeration e = descriptorParams.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String val = (String) descriptorParams.get(key);
            str = replaceAllTokens(str, "${param:" + key + "}", val); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return str;
    }

    public InputStream getDownloadFile(String name, String ticket, String filename) throws IOException {
        URL file = new URL(applicationStoreProtocol, applicationStoreHost, applicationStorePort, "/getApplicationFile.do" //$NON-NLS-1$
            + "?name=" + name + "&ticket=" + ticket + "&file=" + filename); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (events != null)
            events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.requestApplicationUsing"), new Object[] { file.toExternalForm() })); //$NON-NLS-1$

        URLConnection con = file.openConnection();
        con.setUseCaches(false);

        try {
            Method m = con.getClass().getMethod("setConnectTimeout", new Class[] { int.class }); //$NON-NLS-1$
            if (events != null) {
                events.debug(Messages.getString("ApplicationLauncher.runtime5")); //$NON-NLS-1$
            }
            m.invoke(con, new Object[] { new Integer(20000) });
            m = con.getClass().getMethod("setReadTimeout", new Class[] { int.class }); //$NON-NLS-1$
            m.invoke(con, new Object[] { new Integer(20000) });
        } catch (Throwable t) {
        }

        return con.getInputStream();
    }

    /**
     * Replace all occures of a token in a source string with another value
     * 
     * @param source source string
     * @param token token to replace
     * @param value value to replace with
     * @return result
     */
    public static String replaceAllTokens(String source, String token, String value) {
    	// Prevent infinite loop
    	if(token.equals(value)) {
    		return source;
    	}
    	
        int idx;

        do {
            idx = source.indexOf(token);

            if (idx > -1) {
                source = source.substring(0, idx) + value
                    + ((source.length() - idx <= token.length()) ? "" : source.substring(idx + token.length())); //$NON-NLS-1$
            }

        } while (idx > -1);

        return source;
    }


	private void addParameter(XMLElement e) throws IOException {
        String parameter = (String) e.getAttribute("name"); //$NON-NLS-1$
        String value = (String) e.getAttribute("value"); //$NON-NLS-1$
        if (events != null) {
            events.debug(MessageFormat.format(Messages.getString("ApplicationLauncher.addingParameter"), new Object[] { parameter, value }));//$NON-NLS-1$
        }

        descriptorParams.put(parameter, value);
    }

    protected void createTunnel(XMLElement e) throws IOException {
         throw new IOException(Messages.getString("ApplicationLauncher.tunnelRequiredButNoEventHandler")); //$NON-NLS-1$
    }

    private long generateChecksum(File f) throws IOException {

        Adler32 alder = new Adler32();
        CheckedInputStream in = new CheckedInputStream(new FileInputStream(f), alder);
        try {
            byte[] buf = new byte[4096];
            int read = 0;
            while ((read = in.read(buf)) > -1)
                ;

            alder = (Adler32) in.getChecksum();

            return alder.getValue();
        } finally {
            in.close();
        }
    }

    private void downloadFiles() throws IOException {

        if (events != null)
            events.debug(Messages.getString("ApplicationLauncher.downloadingFiles")); //$NON-NLS-1$

        if (events != null)
            events.startDownload(totalBytesToDownload);

        long bytesSoFar = 0;

        DownloadableFile df;
        for (int i = 0; i < filesToDownload.size(); i++) {
            df = (DownloadableFile) filesToDownload.elementAt(i);
            bytesSoFar = downloadFile(installDir,
                (df.getApplicationName() == null ? name : df.getApplicationName()),
                df.getName(),
                df.getTarget(),
                bytesSoFar,
                df.isExecutable(),
                df.isReadOnly(),
                df.getAliases(),
                df.getChecksum());
        }

        for (Enumeration e = sharedFilesToDowload.keys(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            df = (DownloadableFile) sharedFilesToDowload.get(name);
            bytesSoFar = downloadFile(sharedDir,
                (df.getApplicationName() == null ? name : df.getApplicationName()),
                df.getName(),
                df.getTarget(),
                bytesSoFar,
                df.isExecutable(),
                df.isReadOnly(),
                df.getAliases(),
                df.getChecksum());
        }

        if (events != null)
            events.completedDownload();

        if (events != null)
            events.debug(Messages.getString("ApplicationLauncher.completedDownloadingFiles")); //$NON-NLS-1$
    }

	public Vector getTunnels() {
		return EMPTY_VECTOR;
	}
    
	public void processErrorMessage(String text) {
	    events.error(text);
    }
}