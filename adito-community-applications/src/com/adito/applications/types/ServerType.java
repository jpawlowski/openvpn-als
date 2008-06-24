
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
			
package com.adito.applications.types;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jdom.Element;

import com.adito.applications.ApplicationLauncherType;
import com.adito.applications.ApplicationShortcut;
import com.adito.applications.server.ApplicationServerType;
import com.adito.applications.server.ProcessMonitor;
import com.adito.applications.server.ServerApplicationLauncher;
import com.adito.applications.server.ServerLauncher;
import com.adito.applications.server.ServerLauncherEvents;
import com.adito.boot.SystemProperties;
import com.adito.boot.XMLElement;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionException;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.Resource.LaunchRequirement;
import com.adito.security.SessionInfo;

/**
 * Implementation of an
 * {@link com.adito.applications.ApplicationLauncherType} that allows
 * launching of native applications installed on the server.
 */

public class ServerType implements ApplicationLauncherType,
		ApplicationServerType {

	/**
	 * Type name
	 */
	public final static String TYPE = "server";

	private ServerLauncher launcher;

	private String program;

	private File workingDir;

	private List<String> programArgs = new ArrayList<String>();

	private ProcessMonitor process;

	static Log log = LogFactory.getLog(ServerType.class);

	public void start(ExtensionDescriptor descriptor, Element element)
			throws ExtensionException {

		if (element.getName().equals(TYPE)) {
			verifyExecutable(element);
		}

	}

	public void verifyRequiredElements() throws ExtensionException {
	}

	public boolean isHidden() {
		return false;
	}

	public String getType() {
		return TYPE;
	}

	public void prepare(ServerLauncher launcher, ServerLauncherEvents events,
			XMLElement element) throws IOException {
		this.launcher = launcher;

		if (element.getName().equalsIgnoreCase(getType())) {
			program = launcher.replaceTokens((String) element
					.getAttribute("program"));
			String dir = (String) element.getAttribute("dir");
			if (dir != null) {
				workingDir = new File(launcher.replaceTokens(dir));
			} else {
				workingDir = null;
			}
			buildProgramArguments(element);
		}
	}

	public void start() {
		execute(program, workingDir);
	}

	public boolean checkFileCondition(XMLElement el) throws IOException,
			IllegalArgumentException {
		throw new IllegalArgumentException(
				"No supported attributes in condition.");
	}

	public ProcessMonitor getProcessMonitor() {
		return process;
	}

	public void stop() throws ExtensionException {
	}

	public ActionForward launch(Map<String, String> parameters,
			ExtensionDescriptor descriptor, ApplicationShortcut shortcut,
			ActionMapping mapping, LaunchSession launchSession,
			String returnTo, HttpServletRequest request)
			throws ExtensionException {
		if (log.isInfoEnabled())
			log.info("Starting server application "
					+ shortcut.getResourceName());

		ServerApplicationLauncher app;
		try {
			app = new ServerApplicationLauncher(parameters, shortcut
					.getApplication(), launchSession.getSession(), shortcut);
			app.start();
		} catch (Exception e) {
			throw new ExtensionException(ExtensionException.FAILED_TO_LAUNCH, e);
		}

		return null;
	}

	public boolean isAgentRequired(ApplicationShortcut shortcut,
			ExtensionDescriptor descriptor) {
		return false;
	}

	private void verifyExecutable(Element element) throws ExtensionException {
		for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
			Element e = (Element) it.next();
			if (e.getName().equalsIgnoreCase("if")) {
				verifyExecutable(e);
			} else if (!e.getName().equalsIgnoreCase("arg")
					&& !e.getName().equalsIgnoreCase("jvm")) {
				throw new ExtensionException(
						ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
						"Unexpected element <" + e.getName()
								+ "> found in <executable>");
			}
		}

	}

	private void addArgument(XMLElement e) throws IOException {
		if (e.getName().equalsIgnoreCase("arg")) {

			String arg = launcher.replaceTokens(e.getContent());
			if (arg.indexOf(' ') > -1)
				arg = "\"" + arg + "\"";
			programArgs.add(arg);
		} else {
			throw new IOException("Unexpected element <" + e.getName()
					+ "> found");
		}
	}

	private void buildProgramArguments(XMLElement element) throws IOException {

		Enumeration en = element.enumerateChildren();

		while (en.hasMoreElements()) {

			XMLElement e = (XMLElement) en.nextElement();
			if (e.getName().equalsIgnoreCase("arg"))
				addArgument(e);
			else if (e.getName().equalsIgnoreCase("if")) {

				try {
					if (checkFileCondition(e)) {
						buildProgramArguments(e);
					}
				} catch (IllegalArgumentException iae) {

					String parameter = (String) e.getAttribute("parameter");
					boolean not = "true".equalsIgnoreCase(((String) e
							.getAttribute("not")));

					if (parameter != null) {
						String requiredValue = (String) e.getAttribute("value");

						String value = (String) launcher.getDescriptorParams()
								.get(parameter);

						if ((!not && requiredValue.equalsIgnoreCase(value))
								|| (not && !requiredValue
										.equalsIgnoreCase(value))) {
							buildProgramArguments(e);
						}

					} else
						throw new IOException(
								"<if> element requires parameter attribute");
				}

			} else
				throw new IOException("Unexpected element <" + e.getName()
						+ "> found in <executable>");
		}

	}

	private void execute(String program, File workingDir) {
		
		List<String> fullArgs = new ArrayList<String>(programArgs);

		// Add the program to execute

		File tmp = new File(workingDir != null ? workingDir.getAbsolutePath()
				: launcher.getInstallDir().getAbsolutePath(), program);
		if (tmp.exists())
			program = tmp.getAbsolutePath();
		if (program.indexOf(' ') > -1)
			program = "\"" + program + "\"";
		fullArgs.add(0, program);
		
		// Let windows executables work on Linux using Wine
		
		if(program.toLowerCase().endsWith(".exe") && SystemProperties.get("os.name").toLowerCase().startsWith("linux")) {
			fullArgs.add(0, "wine");
		}
		
		// To the array
		
		String[] args = new String[fullArgs.size()];
		fullArgs.toArray(args);

		// Build up the command line (for debug only)

		String cmdline = "";
		for (int i = 0; i < args.length; i++)
			cmdline += " " + args[i];

		if (log.isDebugEnabled())
			log.debug("Executing command: " + cmdline);

		try {
			Process prc = Runtime.getRuntime().exec(args, null, workingDir);
			process = new ProcessMonitor(launcher.getName(), prc);
		} catch (IOException ex) {
			log.error("Failed to launch server command", ex);
		}

	}

	public void activate() throws ExtensionException {
	}

	public boolean canStop() throws ExtensionException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.extensions.ExtensionType#descriptorCreated(org.jdom.Element)
	 */
	public void descriptorCreated(Element element, SessionInfo session)
			throws IOException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.extensions.ExtensionType#getTypeBundle()
	 */
	public String getTypeBundle() {
		return "applications";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.applications.ApplicationLauncherType#isServiceSide()
	 */
	public boolean isServerSide() {
		return true;
	}

    /* (non-Javadoc)
     * @see com.adito.extensions.ExtensionType#getLaunchRequirement()
     */
    public LaunchRequirement getLaunchRequirement() {
        return LaunchRequirement.LAUNCHABLE;
    }
}