
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
 * execution of Java applications using a running VPN client.
 * <p>
 * This launcher will provide links to launch the VPN client if it is not
 * running before launching the applicaiton itself.
 */
public class JavasType implements ApplicationLauncherType,
		ApplicationServerType {

	final static Log log = LogFactory.getLog(JavasType.class);

	/**
	 * Type name
	 */
	public final static String TYPE = "javas";

	// Private instance variables
	private String jre;

	private ExtensionDescriptor descriptor;

	private String classpath = "";

	private String mainclass;

	private File workingDir;

	private String[] jvm;

	private List<String> programArgs = new ArrayList<String>();

	private List<String> jvmArgs = new ArrayList<String>();

	private ProcessMonitor process;

	private String javaLibraryPath = "";

	protected ServerLauncherEvents events;

	protected ServerLauncher launcher;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.extensions.ExtensionType#start(com.adito.extensions.ExtensionDescriptor,
	 *      org.jdom.Element)
	 */
	public void start(ExtensionDescriptor descriptor, Element element)
			throws ExtensionException {
		this.descriptor = descriptor;
		if (element.getName().equals(TYPE)) {

			jre = element.getAttribute("jre").getValue();

			if (jre == null) {
				throw new ExtensionException(
						ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
						"<application> element requires attribute 'jre'");
			}

			try {
				ExtensionDescriptor.getVersion(jre);
			} catch (Throwable ex) {
				throw new ExtensionException(
						ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
						"Invalid value '" + jre
								+ "' specified for 'jre' attribute");
			}

			for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
				Element e = (Element) it.next();

				if (e.getName().equalsIgnoreCase("classpath")) {
					verifyClasspath(e);
				} else if (e.getName().equalsIgnoreCase("main")) {
					verifyMain(e);
				} else {
					throw new ExtensionException(
							ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
							"Unexpected element <" + e.getName()
									+ "> found in <application>");
				}
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.extensions.ExtensionType#verifyRequiredElements()
	 */
	public void verifyRequiredElements() throws ExtensionException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.extensions.ExtensionType#isHidden()
	 */
	public boolean isHidden() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.extensions.ExtensionType#getType()
	 */
	public String getType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.applications.server.ApplicationServerType#prepare(com.adito.applications.server.ServerLauncher,
	 *      com.adito.applications.server.ServerLauncherEvents,
	 *      com.adito.boot.XMLElement)
	 */
	public void prepare(ServerLauncher launcher, ServerLauncherEvents events,
			XMLElement element) throws IOException {

		if (events != null)
			events.debug("Processing <" + element.getName()
					+ "> for java application type");

		this.launcher = launcher;
		this.events = events;

		if (element.getName().equals("java")) {

			String jre = (String) element.getAttribute("jre");

			if (events != null)
				events
						.debug("Checking our version against the required application version "
								+ jre);

			if (!ServerLauncher.checkVersion(jre)) {
				throw new IOException(
						"Application requires Java Runtime Environment " + jre);
			}

			/**
			 * LDP - Don't reset the classpath as this stops extended extensions
			 * (such as the agent extension itself) from adding addtional
			 * classpath entries.
			 */
			if (SystemProperties.get("java.version").startsWith("1.1")
					&& !SystemProperties.get("java.vendor").startsWith(
							"Microsoft"))
				classpath = SystemProperties.get("java.home")
						+ File.pathSeparator + "lib" + File.pathSeparator
						+ "classes.zip";

			Enumeration e = element.enumerateChildren();

			while (e.hasMoreElements()) {
				XMLElement el = (XMLElement) e.nextElement();

				if (el.getName().equalsIgnoreCase("classpath")) {
					buildClassPath(el);
				} else if (el.getName().equalsIgnoreCase("main")) {
					mainclass = (String) el.getAttribute("class");
					if (events != null)
						events.debug("Main class is " + mainclass);
					String dir = (String) el.getAttribute("dir");
					if (events != null)
						events.debug("Dir is " + dir);
					if (dir != null) {
						workingDir = new File(launcher.replaceTokens(dir));
					} else {
						workingDir = null;
					}
					buildProgramArguments(el);
				}
			}

			if (events != null)
				events.debug("Finished preparing application descriptor.");
		} else {
			if (events != null)
				events.debug("Ignoring <" + element.getName()
						+ "> tag as it is not a java application tag");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.applications.server.ApplicationServerType#start()
	 */
	public void start() {
		execute(classpath, mainclass, workingDir);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.vpn.util.ApplicationType#checkFileCondition(com.adito.vpn.util.XMLElement)
	 */
	public boolean checkFileCondition(XMLElement el) throws IOException,
			IllegalArgumentException {
		String jre = (String) el.getAttribute("jre");
		if (jre == null) {
			throw new IllegalArgumentException(
					"No supported attributes in condition.");
		} else {
			return isSupportedJRE(jre);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.vpn.util.ApplicationType#getProcessMonitor()
	 */
	public ProcessMonitor getProcessMonitor() {
		return process;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.extensions.ExtensionType#stop()
	 */
	public void stop() throws ExtensionException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.extensions.ExtensionType#activate()
	 */
	public void activate() throws ExtensionException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.extensions.ExtensionType#canStop()
	 */
	public boolean canStop() throws ExtensionException {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.applications.ApplicationLauncherType#launch(java.util.Map,
	 *      com.adito.extensions.ExtensionDescriptor,
	 *      com.adito.applications.ApplicationShortcut,
	 *      org.apache.struts.action.ActionMapping,
	 *      com.adito.policyframework.LaunchSession, java.lang.String,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	public ActionForward launch(Map<String, String> parameters,
			ExtensionDescriptor descriptor, ApplicationShortcut shortcut,
			ActionMapping mapping, LaunchSession launchSession,
			String returnTo, HttpServletRequest request)
			throws ExtensionException {
		if (log.isInfoEnabled())
			log.info("Starting Java server application "
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.applications.ApplicationLauncherType#isAgentRequired(com.adito.applications.ApplicationShortcut,
	 *      com.adito.extensions.ExtensionDescriptor)
	 */
	public boolean isAgentRequired(ApplicationShortcut shortcut,
			ExtensionDescriptor descriptor) {
		return false;
	}

	protected void addClasspathEntry(XMLElement e) throws IOException {
		addClasspathEntry(e, null);
	}

	protected void addClasspathEntry(XMLElement e, String app)
			throws IOException {

		events.debug("Adding "
				+ launcher.getInstallDir()
				+ (e.getContent() != null ? File.separatorChar + e.getContent()
						: "") + " to CLASSPATH");

		classpath += (!classpath.equals("") ? File.pathSeparator : "")
				+ launcher.getInstallDir()
				+ (e.getContent() != null ? File.separatorChar + e.getContent()
						: "");
	}

	protected void buildClassPath(XMLElement element) throws IOException {
		buildClassPath(element, null);
	}

	protected void buildClassPath(XMLElement element, String app)
			throws IOException {

		if (events != null)
			events.debug("Building classpath");
		Enumeration en = element.enumerateChildren();
		XMLElement e;

		while (en.hasMoreElements()) {
			e = (XMLElement) en.nextElement();
			if (e.getName().equalsIgnoreCase("jar")) {
				addClasspathEntry(e, app);
			} else if (e.getName().equals("if")) {

				String jre = (String) e.getAttribute("jre");
				if (jre == null) {
					String parameter = (String) e.getAttribute("parameter");

					if (parameter != null) {
						String requiredValue = (String) e.getAttribute("value");
						boolean not = "true".equalsIgnoreCase(((String) e
								.getAttribute("not")));

						// Check the parameter
						String value = (String) launcher.getDescriptorParams()
								.get(parameter);

						if ((!not && requiredValue.equalsIgnoreCase(value))
								|| (not && !requiredValue
										.equalsIgnoreCase(value))) {
							buildClassPath(e, app);
						}

					} else
						throw new IOException(
								"<if> element requires jre or parameter attribute");
				} else {

					if (isSupportedJRE(jre)) {
						buildClassPath(e, app);
					}
				}
			} else
				throw new IOException("Invalid element <" + e.getName()
						+ "> found in <classpath>");
		}

	}

	private boolean isSupportedJRE(String jre) {

		int[] ourVersion = ServerLauncher.getVersion(System
				.getProperty("java.version"));

		if (jre.startsWith(">")) {

			// Our JRE must be greater than the value specified
			int[] requiredVersion = ServerLauncher.getVersion(jre.substring(1));
			for (int i = 0; i < ourVersion.length && i < requiredVersion.length; i++) {
				if (ourVersion[i] < requiredVersion[i])
					return false;
			}
			return true;

		} else if (jre.startsWith("<")) {
			// Our JRE must be less than the value specified
			int[] requiredVersion = ServerLauncher.getVersion(jre.substring(1));
			for (int i = 0; i < ourVersion.length && i < requiredVersion.length; i++) {
				if (ourVersion[i] > requiredVersion[i])
					return false;
			}
			return true;

		} else {
			// Direct comparison
			int[] requiredVersion = ServerLauncher.getVersion(jre);
			for (int i = 0; i < ourVersion.length && i < requiredVersion.length; i++) {
				if (ourVersion[i] != requiredVersion[i])
					return false;
			}
			return true;

		}

	}

	protected void addArgument(String arg) {
		if (arg != null)
			programArgs.add(launcher.replaceTokens(arg));
	}

	protected void addJVMArgument(String arg) {
		if (arg != null) {

			if (arg.startsWith("java.library.path")) {
				int idx = arg.indexOf('=');

				if (idx > -1) {
					String val = arg.substring(idx + 1).replace('/',
							File.separatorChar);
					javaLibraryPath += (javaLibraryPath.equals("") ? val
							: SystemProperties.get("path.separator") + val);

					if (events != null)
						events
								.debug(val
										+ " has been appened to system property java.library.path");
				} else if (events != null)
					events.debug("Invalid java.library.path system property: "
							+ arg);

			} else
				jvmArgs.add(launcher.replaceTokens(arg));
		}
	}

	private void addArgument(XMLElement e) throws IOException {
		if (e.getName().equalsIgnoreCase("arg"))
			addArgument(e.getContent());
		else if (e.getName().equalsIgnoreCase("jvm")) {
			addJVMArgument(e.getContent());
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
			else if (e.getName().equalsIgnoreCase("jvm")) {
				addArgument(e);
			} else if (e.getName().equalsIgnoreCase("if")) {

				String jre = (String) e.getAttribute("jre");
				if (jre == null) {
					String parameter = (String) e.getAttribute("parameter");
					boolean not = "true".equalsIgnoreCase((String) e
							.getAttribute("not"));

					if (parameter != null) {
						String requiredValue = (String) e.getAttribute("value");

						// Check the parameter
						String value = (String) launcher.getDescriptorParams()
								.get(parameter);

						if ((!not && requiredValue.equalsIgnoreCase(value))
								|| (not && !requiredValue
										.equalsIgnoreCase(value))) {
							buildProgramArguments(e);
						}

					} else
						throw new IOException(
								"<if> element requires jre or parameter attribute");
				} else {
					// Check the jre
					if (isSupportedJRE(jre)) {
						buildProgramArguments(e);
					}

				}

			} else
				throw new IOException("Unexpected element <" + e.getName()
						+ "> found in <main>");
		}

	}

	private void execute(String classpath, String mainclass, File workingDir) {

		String[] args = new String[programArgs.size()];
		programArgs.toArray(args);

		if (!javaLibraryPath.equals(""))
			jvmArgs.add("java.library.path="
					+ launcher.replaceTokens(javaLibraryPath));

		jvm = new String[jvmArgs.size()];
		jvmArgs.toArray(jvm);

		String[] cmdargs = new String[jvm.length + args.length + 4];
		/**
		 * Setup the command line in the format expected by Sun Microsystems
		 * java command line interpreter
		 */
		cmdargs[0] = SystemProperties.get("java.home") + File.separator + "bin"
				+ File.separator + "java";
		cmdargs[1] = "-classpath";
		cmdargs[2] = classpath;

		for (int i = 0; i < jvm.length; i++) {
			cmdargs[3 + i] = "-D" + jvm[i];
		}

		cmdargs[jvm.length + 3] = mainclass;

		System.arraycopy(args, 0, cmdargs, jvm.length + 4, args.length);

		String cmdline = "";
		for (int i = 0; i < cmdargs.length; i++)
			cmdline += " " + cmdargs[i];

		if (events != null)
			events.debug("Executing command: " + cmdline);

		try {

			if (events != null)
				events.executingApplication(launcher.getName(), cmdline.trim());

			// Can we change the working directory of the process?
			Process prc = Runtime.getRuntime().exec(cmdargs, null, workingDir);
			process = new ProcessMonitor(launcher.getName(), prc);
		} catch (IOException ex) {
			if (events != null)
				events.debug("Process execution failed: " + ex.getMessage());
		}

	}

	private void verifyClasspath(Element element) throws ExtensionException {
		for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
			Element e = (Element) it.next();

			if (e.getName().equalsIgnoreCase("jar")) {
				descriptor.processFile(e);
			} else if (e.getName().equals("if")) {
				verifyClasspath(e);
			} else {
				throw new ExtensionException(
						ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
						"Invalid element <" + e.getName()
								+ "> found in <classpath>");
			}
		}
	}

	private void verifyMain(Element element) throws ExtensionException {
		for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
			Element e = (Element) it.next();
			if (e.getName().equalsIgnoreCase("if")) {
				verifyMain(e);
			} else if (!e.getName().equalsIgnoreCase("arg")
					&& !e.getName().equalsIgnoreCase("jvm")) {
				throw new ExtensionException(
						ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
						"Unexpected element <" + e.getName()
								+ "> found in <main>");
			}
		}
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