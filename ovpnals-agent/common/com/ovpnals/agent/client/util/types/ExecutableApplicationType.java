
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.agent.client.util.types;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Vector;

import com.ovpnals.agent.client.util.AbstractApplicationLauncher;
import com.ovpnals.agent.client.util.ApplicationLauncherEvents;
import com.ovpnals.agent.client.util.ApplicationType;
import com.ovpnals.agent.client.util.ProcessMonitor;
import com.ovpnals.agent.client.util.Utils;
import com.ovpnals.agent.client.util.XMLElement;

/**
 * Application type that launchs a native application, either one that is in the
 * path or in a specified directory.
 */
public class ExecutableApplicationType implements ApplicationType {
    
    //  Private instance variables

    private ApplicationLauncherEvents events;
    private AbstractApplicationLauncher launcher;
    private String program;
    private File workingDir;
    private Vector programArgs = new Vector();
    private ProcessMonitor process;

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.vpn.util.ApplicationType#prepare(com.ovpnals.vpn.util.ApplicationLauncher,
     *      com.ovpnals.vpn.util.XMLElement)
     */
    public void prepare(AbstractApplicationLauncher launcher, ApplicationLauncherEvents events, XMLElement element) throws IOException {
        this.launcher = launcher;
        this.events = events;

        if (element.getName().equalsIgnoreCase("executable")) { //$NON-NLS-1$
        	String programName = (String) element.getAttribute("program");  //$NON-NLS-1$
        	if(programName == null) {
        		programName = findProgram(element);
        	}
            program = launcher.replaceTokens(programName);
            program = launcher.replaceAllTokens(program, "/", File.separator);
            String dir = (String) element.getAttribute("dir"); //$NON-NLS-1$
            if (dir != null) {
                workingDir = new File(launcher.replaceTokens(dir));
            } else {
                workingDir = null;
            }
            buildProgramArguments(element);
        }
    }

    /* (non-Javadoc)
     * @see com.ovpnals.vpn.util.ApplicationType#start()
     */
    public void start() {
        execute(program, workingDir);
    }

    private void addArgument(XMLElement e) throws IOException {
        if (e.getName().equalsIgnoreCase("arg")) { //$NON-NLS-1$

            String arg = launcher.replaceTokens(Utils.trimmedBothOrBlank(e.getContent()));
            // Don't add quote when there are space in the path
            //if (arg.indexOf(' ') > -1)
            //    arg = "\"" + arg + "\""; //$NON-NLS-1$ //$NON-NLS-2$
            programArgs.addElement(arg);
        } else {
            throw new IOException(MessageFormat.format(
                Messages.getString("ExecutableApplicationType.unexpectedElementFound"), //$NON-NLS-1$ 
                new Object[] { e.getName() } ) ); 
        }
    }

    private void buildProgramArguments(XMLElement element) throws IOException {

        Enumeration en = element.enumerateChildren();

        while (en.hasMoreElements()) {

            XMLElement e = (XMLElement) en.nextElement();
            if (e.getName().equalsIgnoreCase("arg")) //$NON-NLS-1$
                addArgument(e);
            else  if (e.getName().equalsIgnoreCase("program")) //$NON-NLS-1$
                continue;
            else if (e.getName().equalsIgnoreCase("if")) { //$NON-NLS-1$
                if (AbstractApplicationLauncher.checkCondition(this, e, launcher.getDescriptorParams())) {
                    buildProgramArguments(e);
                }
            } else
                throw new IOException(MessageFormat.format(Messages.getString("ExecutableApplicationType.unexpectedElementFoundInExecutable"), new Object[] { e.getName() } ) ) ;//$NON-NLS-1$
        }

    }

    private String findProgram(XMLElement element) throws IOException {
        Enumeration en = element.enumerateChildren();

        while (en.hasMoreElements()) {

            XMLElement e = (XMLElement) en.nextElement();
            if (e.getName().equalsIgnoreCase("program")) //$NON-NLS-1$
                return Utils.trimmedBothOrBlank(e.getContent());
            else if (e.getName().equalsIgnoreCase("if")) { //$NON-NLS-1$
                if (AbstractApplicationLauncher.checkCondition(this, e, launcher.getDescriptorParams())) {
                    String program = findProgram(e);
                    if(program != null) {
                    	return program;
                    }
                }
            } 
        }
        throw new IOException("No valid program found for conditions.");

    }

    private void execute(String program, File workingDir) {

        String[] args = new String[programArgs.size()];
        programArgs.copyInto(args);

        // LDP - Look for the program in our working dir.. looks like we need to
        // specify
        // this fully in order for it to be executed properly. Windows will not
        // search
        // the working directory for the executable file!!
        File tmp = new File(workingDir != null ? workingDir.getAbsolutePath() : launcher.getInstallDir().getAbsolutePath(), program);
        if (tmp.exists())
            program = tmp.getAbsolutePath();

        String[] cmdargs = new String[args.length + 1];
        System.arraycopy(args, 0, cmdargs, 1, args.length);
        // Don't add quote when there are space in the path
        //if (program.indexOf(' ') > -1)
        //    program = "\"" + program + "\""; //$NON-NLS-1$ //$NON-NLS-2$
        cmdargs[0] = program;

        String cmdline = ""; //$NON-NLS-1$
        for (int i = 0; i < cmdargs.length; i++)
            cmdline += " " + cmdargs[i]; //$NON-NLS-1$

        if (events != null && launcher.isDebug())
            events.debug(MessageFormat.format(Messages.getString("ExecutableApplicationType.executingCommand"), new Object[] { cmdline } ) ); //$NON-NLS-1$

        try {

            if (events != null)
                events.executingApplication(launcher.getName(), cmdline.trim());

            // Can we change the working directory of the process?
            try {
                Method m = Runtime.class.getMethod("exec", new Class[] { String[].class, String[].class, File.class }); //$NON-NLS-1$
                process = new ProcessMonitor(launcher.getName(), (Process) m.invoke(Runtime.getRuntime(), new Object[] { cmdargs,
                    null,
                    workingDir }));
            } catch (Throwable t) {
                if (workingDir != null) {
                    t.printStackTrace();

                    // Try cmd.exe on windows

                    throw new IOException(Messages.getString("ExecutableApplicationType.applicationRequestsThatTheWorkingDirectoryIsChange") + ""); //$NON-NLS-1$ //$NON-NLS-2$
                }
                process = new ProcessMonitor(launcher.getName(), Runtime.getRuntime().exec(cmdargs));
            }
        } catch (IOException ex) {
            if (events != null)
                events.debug(MessageFormat.format(Messages.getString("ExecutableApplicationType.processExecutionFailed"), new Object[] { ex.getMessage() } ) ); //$NON-NLS-1$
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.vpn.util.ApplicationType#getProcessMonitor()
     */
    public ProcessMonitor getProcessMonitor() {
        return process;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.vpn.util.ApplicationType#getRedirectParameters()
     */
    public String getRedirectParameters() {
        return null;
    }

	/* (non-Javadoc)
	 * @see com.ovpnals.agent.client.util.ApplicationType#getTypeName()
	 */
	public String getTypeName() {
		return "executable";
	}

}
