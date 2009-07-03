
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
			
package com.adito.applications.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;

import com.adito.applications.ApplicationShortcut;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.Util;
import com.adito.boot.XMLElement;
import com.adito.core.CoreException;
import com.adito.core.stringreplacement.SessionInfoReplacer;
import com.adito.core.stringreplacement.VariableReplacement;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.actions.UndefinedParameterException;
import com.adito.security.SessionInfo;

/**
 * This class do the almost the same things as agent application launcher. It
 * check the extemsion file and do some replacement.
 */
public class ServerLauncher {

	File installDir;

	File sharedDir;

	String typeName;

	String name;

	public String exitMessage = "";

	long totalBytesToDownload = 0;

	Vector filesToDownload = new Vector();

	Hashtable sharedFilesToDowload = new Hashtable();

	protected Map<String, String> parameters;

	Hashtable descriptorParams = new Hashtable();

	boolean debug = false;

	ApplicationServerType type;

	int shortcutId;

	Hashtable replacements = new Hashtable();

	Vector transformations = new Vector();

	String dependencies;

	ExtensionDescriptor descriptor;

	SessionInfo session;

	ApplicationShortcut shortcut;

	static Log log = LogFactory.getLog(ServerLauncher.class);

	public ServerLauncher(ExtensionDescriptor descriptor, SessionInfo session,
			ApplicationShortcut shortcut, Map<String, String> parameters) {
		this.descriptor = descriptor;
		this.session = session;
		this.shortcut = shortcut;
		this.parameters = parameters;
	}

	public int getShortcutId() {
		return shortcutId;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public ApplicationServerType getApplicationType() {
		return type;
	}

	private String processParameters(SessionInfo sessionInfo, Element element,
			ExtensionDescriptor app, final ApplicationShortcut shortcut)
			throws UndefinedParameterException, JDOMException, IOException,
			CoreException {

		List params = element.getChildren("parameter");

		for (Iterator it = params.iterator(); it.hasNext();) {
			Element p = (Element) it.next();

			String name = p.getAttribute("name").getValue();
			String value = (String) shortcut.getParameters().get(name);

			if (value == null) {
				value = app.getParameterDefinition(name).getDefaultValue();
				if (value == null
						|| value.equals(PropertyDefinition.UNDEFINED_PARAMETER)) {
					throw new UndefinedParameterException("Parameter " + name
							+ " is undefined");
				}
			}

			VariableReplacement r = new VariableReplacement();
			r.setApplicationShortcut(app, null);
			r.setSession(sessionInfo);
			p.setAttribute("value", r.replace(value));
		}

		XMLOutputter output = new XMLOutputter();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		output.output(element, out);

		String xml = new String(out.toByteArray());

		params = element.getChildren("parameter");

		Map parameters = new HashMap();
		for (Iterator it = params.iterator(); it.hasNext();) {
			Element p = (Element) it.next();
			parameters.put(p.getAttributeValue("name"), p
					.getAttributeValue("value"));
		}

		VariableReplacement r = new VariableReplacement();
		r.setApplicationShortcut(app, parameters);
		r.setSession(sessionInfo);
		String processed = r.replace(xml);

		if (log.isDebugEnabled())
			log.debug("Returning '" + processed + "'");
		return processed;
	}

	public void prepare() throws IOException {

		if (log.isDebugEnabled())
			log.debug("Checking parameters");

		XMLElement element = new XMLElement();
		try {
			element.parseFromReader(new InputStreamReader(
					new ByteArrayInputStream(processParameters(
							session,
							descriptor
									.createProcessedDescriptorElement(session),
							descriptor, shortcut).getBytes())));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (log.isDebugEnabled())
			log.debug("Received a response from server");

		if (!element.getName().equals("application")
				&& !element.getName().equals("error")
				&& !element.getName().equals("extension")) {
			throw new IOException(
					"URL does not point to an application descriptor");
		} else if (element.getName().equals("error")) {
			throw new IOException(element.getContent());
		}

		name = (String) element.getAttribute("extension");
		if (name == null) {
			name = (String) element.getAttribute("application");
		}

		typeName = (String) element.getAttribute("type");

		try {
			type = (ApplicationServerType) Class
					.forName(
							"com.adito.applications.types."
									+ (String.valueOf(typeName.charAt(0))
											.toUpperCase() + typeName
											.substring(1)) + "Type")
					.newInstance();
		} catch (Throwable t) {
			throw new IOException(
					"Failed to load the application description extension for application type of "
							+ typeName + ".");
		}

		if (log.isDebugEnabled())
			log.debug("Application name is " + name);

		if (log.isDebugEnabled())
			log.debug("Creating install folder");

		installDir = File.createTempFile("server", "tmp");
		installDir.delete();
		installDir = new File(installDir.getParent(), installDir.getName()
				+ "dir");
		installDir.mkdirs();

		if (log.isDebugEnabled())
			log.debug("Installing to " + installDir.getAbsolutePath());

		Enumeration e = element.enumerateChildren();

		while (e.hasMoreElements()) {
			XMLElement el = (XMLElement) e.nextElement();

			if (el.getName().equalsIgnoreCase("files")) {
				processFiles(el);
			} else if (el.getName().equalsIgnoreCase("parameter")) {
				addParameter(el);
			} else if (el.getName().equalsIgnoreCase("messages")) {
				// Ignore as its a server side element
			} else if (el.getName().equalsIgnoreCase("description")) {
				// Simply ignore.. should we throw an exception if an element is
				// not known?
			} else if (el.getName().equalsIgnoreCase("replacements")) {
				FileReplacement replacement = new FileReplacement(installDir);
				replacement.processReplacementXML(el, this);
				replacements.put(replacement.getId(), replacement);
			} else if (processLauncherElement(el)) {
				// This allows us to override more element types in extended
				// application launchers (i.e. registry parameters)
				continue;
			} else if (el.getName().equalsIgnoreCase("transform")) {
				ParameterTransformation trans = new ParameterTransformation(el,
						this);
				transformations.addElement(trans);
			} else {
				type.prepare(this, null, el);
			}
		}

		if (log.isDebugEnabled())
			log.debug("Applying parameter transformations");

		for (Enumeration ep = transformations.elements(); ep.hasMoreElements();) {
			ParameterTransformation trans = (ParameterTransformation) ep
					.nextElement();
			trans.processTransformation();
		}

		if (log.isDebugEnabled())
			log.debug("Creating replacements");

		for (Enumeration ep = replacements.elements(); ep.hasMoreElements();) {
			FileReplacement replacement = (FileReplacement) ep.nextElement();
			replacement.createReplacementsFile(this);
		}

		if (log.isDebugEnabled())
			log
					.debug("Replacements created, preparation of launcher complete.");
	}

	protected boolean processLauncherElement(XMLElement e) {
		return false;
	}

	public String getName() {
		return name;
	}

	public File getInstallDir() {
		return installDir;
	}

	public void start() {
		type.start();
	}

	private void addParameter(XMLElement e) throws IOException {
		String parameter = (String) e.getAttribute("name");
		String value = (String) e.getAttribute("value");
		if (log.isDebugEnabled())
			log.debug("Adding parameter " + parameter + " with value of "
					+ value);
		descriptorParams.put(parameter, SessionInfoReplacer.replace(session,
				value));
	}

	public void addParameter(String parameter, String value) {

		if (log.isDebugEnabled())
			log.debug("Adding parameter " + parameter + " with value of "
					+ value);

		descriptorParams.put(parameter, value);
	}

	public String replaceTokens(String str) {
		str = replaceAllTokens(str, "${client:installDir}", installDir
				.getAbsolutePath());
		for (Enumeration e = descriptorParams.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String val = (String) descriptorParams.get(key);
			str = replaceAllTokens(str, "${param:" + key + "}", val);
		}
		return str;
	}

	static String replaceAllTokens(String source, String token, String value) {
		int idx;

		do {
			idx = source.indexOf(token);

			if (idx > -1) {
				source = source.substring(0, idx)
						+ value
						+ ((source.length() - idx <= token.length()) ? ""
								: source.substring(idx + token.length()));
			}

		} while (idx > -1);

		return source;
	}

	private boolean isArgument(XMLElement e) {
		return e.getName().equalsIgnoreCase("arg")
				|| e.getName().equalsIgnoreCase("jvm");
	}

	public static boolean checkVersion(String applicationJDK) {

		int[] applicationVersion = ServerLauncher.getVersion(applicationJDK);
		int[] installedJREVersion = ServerLauncher.getVersion(System
				.getProperty("java.version"));

		for (int i = 0; i < applicationVersion.length
				&& i < installedJREVersion.length; i++) {
			if (applicationVersion[i] > installedJREVersion[i])
				return false;
		}

		return true;
	}

	public static int[] getVersion(String version) {

		int idx = 0;
		int pos = 0;
		int[] result = new int[0];
		do {

			idx = version.indexOf('.', pos);
			int v;
			if (idx > -1) {
				v = Integer.parseInt(version.substring(pos, idx));
				pos = idx + 1;
			} else {
				try {
					int sub = version.indexOf('_', pos);
					if (sub == -1) {
						sub = version.indexOf('-', pos);
					}
					if (sub > -1) {
						v = Integer.parseInt(version.substring(pos, sub));
					} else {
						v = Integer.parseInt(version.substring(pos));
					}
				} catch (NumberFormatException ex) {
					// Ignore the exception and return what version we have
					break;
				}
			}
			int[] tmp = new int[result.length + 1];
			System.arraycopy(result, 0, tmp, 0, result.length);
			tmp[tmp.length - 1] = v;
			result = tmp;

		} while (idx > -1);

		return result;
	}

	public void processFiles(XMLElement element) throws IOException {
		processFiles(element, null);
	}

	public void processFiles(XMLElement element, String app) throws IOException {

		Enumeration en = element.enumerateChildren();
		XMLElement e;

		while (en.hasMoreElements()) {
			e = (XMLElement) en.nextElement();
			if (e.getName().equalsIgnoreCase("file")) {
				File f = descriptor.getFile(e.getContent());
				Util.copy(f, new File(installDir, e.getContent()));
			} else if (e.getName().equalsIgnoreCase("if")) {

				try {
					if (type.checkFileCondition(e)) {
						processFiles(e, app);
					}
				} catch (IllegalArgumentException iae) {
					String parameter = (String) e.getAttribute("parameter");

					if (parameter != null) {
						String requiredValue = (String) e.getAttribute("value");
						boolean not = "true".equalsIgnoreCase(((String) e
								.getAttribute("not")));

						// Check the parameter
						String value = (String) descriptorParams.get(parameter);

						if ((!not && requiredValue.equalsIgnoreCase(value))
								|| (not && !requiredValue
										.equalsIgnoreCase(value))) {
							processFiles(e, app);
						}

					} else
						throw new IOException(
								"<if> element requires type specific attributes or parameter/value attributes");
				}

			} else
				throw new IOException("Invalid element <" + e.getName()
						+ "> found in <files>");
		}

	}

	public Hashtable getDescriptorParams() {
		return descriptorParams;
	}
}
