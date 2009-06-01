/* HEADER  */
package net.openvpn.als.agent.client.util.types;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Vector;

import net.openvpn.als.agent.client.util.AbstractApplicationLauncher;
import net.openvpn.als.agent.client.util.ApplicationLauncherEvents;
import net.openvpn.als.agent.client.util.ApplicationType;
import net.openvpn.als.agent.client.util.ProcessMonitor;
import net.openvpn.als.agent.client.util.Utils;
import net.openvpn.als.agent.client.util.XMLElement;

/**
 * Application type that locates a suitable JVM and launchs a Java application.
 */
public class JavaApplicationType implements ApplicationType {

	// Protected instance variables

	protected ApplicationLauncherEvents events;
	protected AbstractApplicationLauncher launcher;

	// Prive instance variables

	private String classpath = ""; //$NON-NLS-1$
	private String mainclass;
	private File workingDir;
	private String[] jvm;
	private Vector programArgs = new Vector();
	private Vector jvmArgs = new Vector();
    private Vector envVars = new Vector();
	private ProcessMonitor process;
	private String javaLibraryPath = ""; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.vpn.util.ApplicationType#prepare(net.openvpn.als.vpn.util.ApplicationLauncher,
	 *      net.openvpn.als.vpn.util.XMLElement)
	 */
	public void prepare(AbstractApplicationLauncher launcher, ApplicationLauncherEvents events, XMLElement element)
					throws IOException {

		if (events != null)
			events.debug(MessageFormat.format(Messages.getString("JavaApplicationType.processingForJavaType"), new Object[] { element.getName() })); //$NON-NLS-1$

		this.launcher = launcher;
		this.events = events;

		if (element.getName().equals(getTypeName())) { //$NON-NLS-1$

			String jre = (String) element.getAttribute("jre"); //$NON-NLS-1$

			if (events != null)
				events.debug(MessageFormat.format(Messages.getString("JavaApplicationType.checkingJRE"), //$NON-NLS-1$
					new Object[] { jre }));

			if (!Utils.checkVersion(jre)) {
				String mesage = Messages.getString("JavaApplicationType.applicationRequires", new Object[] { jre }); //$NON-NLS-1$
                if (events != null) {
                    events.error(mesage);
                }
                return;
            }

			/**
			 * LDP - Don't reset the classpath as this stops extended extensions
			 * (such as the agent extension itself) from adding addtional
			 * classpath entries.
			 */
			// Reset the classpath
			// classpath = "";
			if (System.getProperty("java.version").startsWith("1.1") //$NON-NLS-1$ //$NON-NLS-2$
				&& !System.getProperty("java.vendor").startsWith( //$NON-NLS-1$
				"Microsoft")) //$NON-NLS-1$
				classpath = System.getProperty("java.home") //$NON-NLS-1$
					+ File.pathSeparator
					+ "lib" + File.pathSeparator //$NON-NLS-1$
					+ "classes.zip"; //$NON-NLS-1$

			Enumeration e = element.enumerateChildren();

			while (e.hasMoreElements()) {
				XMLElement el = (XMLElement) e.nextElement();

				if (el.getName().equalsIgnoreCase("classpath")) { //$NON-NLS-1$
					buildClassPath(el);
				} else if (el.getName().equalsIgnoreCase("main")) { //$NON-NLS-1$
					mainclass = (String) el.getAttribute("class"); //$NON-NLS-1$
					if (events != null)
						events.debug(MessageFormat.format(Messages.getString("JavaApplicationType.mainClass"), new Object[] { mainclass })); //$NON-NLS-1$
					String dir = (String) el.getAttribute("dir"); //$NON-NLS-1$
					if (events != null)
						events.debug(MessageFormat.format(Messages.getString("JavaApplicationType.directoryIs"), new Object[] { dir })); //$NON-NLS-1$
					if (dir != null) {
						workingDir = new File(launcher.replaceTokens(dir));
					} else {
						workingDir = null;
					}
					buildProgramArguments(el);
				}
			}

			if (events != null)
				events.debug(Messages.getString("JavaApplicationType.finishedPreparingDescriptor")); //$NON-NLS-1$
		} else {
			if (events != null)
				events.debug(MessageFormat.format(Messages.getString("JavaApplicationType.ignoringTagAsNotJavaTag"), new Object[] { element.getName() })); //$NON-NLS-1$
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.vpn.util.ApplicationType#start()
	 */
	public void start() {
		execute(classpath, mainclass, workingDir);
	}

	/**
	 * Add an entry to the classpath.
	 * 
	 * @param e classpath element
	 * @throws IOException
	 */
	public void addClasspathEntry(XMLElement e) throws IOException {
		addClasspathEntry(e, null);
	}

	/**
	 * Add an entry to the classpath.
	 * 
	 * @param e classpath element
	 * @param app application
	 * @throws IOException
	 */
	public void addClasspathEntry(XMLElement e, String app) throws IOException {

		String shared = (String) e.getAttribute("shared"); //$NON-NLS-1$
		File entry;
		if (shared != null && shared.equalsIgnoreCase("true")) { //$NON-NLS-1$
			entry = launcher.addShared(e);
		} else {
			entry = launcher.addFile(e, app);
		}

		if (entry != null && events != null)
			events.debug(MessageFormat.format(Messages.getString("JavaApplicationType.addingToClasspath"), new Object[] { entry.getAbsolutePath() })); //$NON-NLS-1$ //$NON-NLS-2$

		// The entry may be null because were not the correct platform
		if (entry != null)
			classpath += (!classpath.equals("") ? File.pathSeparator : "") //$NON-NLS-1$ //$NON-NLS-2$
				+ entry.getAbsolutePath();
	}

	protected void buildClassPath(XMLElement element) throws IOException {
		buildClassPath(element, null);
	}

	protected void buildClassPath(XMLElement element, String app) throws IOException {

		if (events != null)
			events.debug(Messages.getString("JavaApplicationType.buildingClasspath")); //$NON-NLS-1$
		Enumeration en = element.enumerateChildren();
		XMLElement e;

		while (en.hasMoreElements()) {
			e = (XMLElement) en.nextElement();
			if (e.getName().equalsIgnoreCase("jar")) { //$NON-NLS-1$
				if (AbstractApplicationLauncher.checkCondition(this, e, launcher.getDescriptorParams())) {
					addClasspathEntry(e, app);
				}
			} else if (e.getName().equals("if")) { //$NON-NLS-1$
				if (AbstractApplicationLauncher.checkCondition(this, e, launcher.getDescriptorParams())) {
					buildClassPath(e, app);
				}
			} else
				throw new IOException(MessageFormat.format(Messages.getString("JavaApplicationType.invalidElementInClasspath"), new Object[] { e.getName() })); //$NON-NLS-1$
		}

	}

	protected void addArgument(String arg) {
		if (arg != null)
			programArgs.addElement(launcher.replaceTokens(arg));
	}

    protected void addEnvVar(String envVar) {
        if (envVar != null)
            envVars.addElement(launcher.replaceTokens(envVar));
    }

	protected void addJVMArgument(String arg) {
		if (arg != null) {

			if (arg.startsWith("java.library.path")) { //$NON-NLS-1$
				int idx = arg.indexOf('=');

				if (idx > -1) {
					String val = arg.substring(idx + 1).replace('/', File.separatorChar);
					javaLibraryPath += (javaLibraryPath.equals("") ? val : System.getProperty("path.separator") + val); //$NON-NLS-1$//$NON-NLS-2$

					if (events != null)
						events.debug(MessageFormat.format(Messages.getString("JavaApplicationType.valueAppendedToJavaLibraryPath"), new Object[] { val })); //$NON-NLS-1$
				} else if (events != null)
					events.debug(MessageFormat.format(Messages.getString("JavaApplicationType.invalidJavaLibraryPath"), new Object[] { arg })); //$NON-NLS-1$

			} else
				jvmArgs.addElement(launcher.replaceTokens(arg));
		}
	}

	private void addArgument(XMLElement e) throws IOException {
		if (e.getName().equalsIgnoreCase("arg")) //$NON-NLS-1$
			addArgument(Utils.trimmedBothOrBlank(e.getContent()));
		else if (e.getName().equalsIgnoreCase("env")) //$NON-NLS-1$
            addEnvVar(Utils.trimmedBothOrBlank(e.getContent()));
        else if (e.getName().equalsIgnoreCase("jvm")) { //$NON-NLS-1$
			addJVMArgument(Utils.trimmedBothOrBlank(e.getContent()));
		} else {
			throw new IOException(MessageFormat.format(Messages.getString("JavaApplicationType.unexpectedElementFound"), new Object[] { e.getName() })); //$NON-NLS-1$
		}
	}

	private void buildProgramArguments(XMLElement element) throws IOException {

		Enumeration en = element.enumerateChildren();

		while (en.hasMoreElements()) {

			XMLElement e = (XMLElement) en.nextElement();
			if (e.getName().equalsIgnoreCase("arg")) //$NON-NLS-1$
				addArgument(e);
			else if (e.getName().equalsIgnoreCase("jvm")) { //$NON-NLS-1$
				addArgument(e);
			} else if (e.getName().equalsIgnoreCase("env")) { //$NON-NLS-1$
                addArgument(e);
            } else if (e.getName().equalsIgnoreCase("if")) { //$NON-NLS-1$

				String jre = (String) e.getAttribute("jre"); //$NON-NLS-1$
				if (jre == null) {
					String parameter = (String) e.getAttribute("parameter"); //$NON-NLS-1$
					boolean not = "true".equalsIgnoreCase((String) e //$NON-NLS-1$
					.getAttribute("not")); //$NON-NLS-1$

					if (parameter != null) {
						String requiredValue = (String) e.getAttribute("value"); //$NON-NLS-1$

						// Check the parameter
						String value = (String) launcher.getDescriptorParams().get(parameter);

						if ((!not && requiredValue.equalsIgnoreCase(value)) || (not && !requiredValue.equalsIgnoreCase(value))) {
							buildProgramArguments(e);
						}

					} else
						throw new IOException(Messages.getString("JavaApplicationType.ifElementRequiresJreOrParameterAttribute")); //$NON-NLS-1$
				} else {
					// Check the jre
					if (Utils.isSupportedJRE(jre)) {
						buildProgramArguments(e);
					}

				}

			} else
				throw new IOException(MessageFormat.format(Messages.getString("JavaApplicationType.unexpectedElementFoundInMain"), new Object[] { e.getName() })); //$NON-NLS-1$
		}

	}
	
	private void execute(String classpath, String mainclass, File workingDir) {

		String[] args = new String[programArgs.size()];
		programArgs.copyInto(args);
		String[] envp = null;
		
		if(envVars.size() > 0 && Utils.isSupportedJRE("+1.5")) {
			addDefaultEnvironment();
			envp = new String[envVars.size()];
			envVars.copyInto(envp);
		}
        
		if (!javaLibraryPath.equals("")) //$NON-NLS-1$
			jvmArgs.addElement("java.library.path=" + launcher.replaceTokens(javaLibraryPath)); //$NON-NLS-1$

		jvm = new String[jvmArgs.size()];
		jvmArgs.copyInto(jvm);

		String[] cmdargs = new String[jvm.length + args.length + 4];

		if (!System.getProperty("java.vendor").startsWith("Microsoft")) { //$NON-NLS-1$ //$NON-NLS-2$
			/**
			 * Setup the command line in the format expected by Sun Microsystems
			 * java command line interpreter
			 */
			cmdargs[0] = Utils.getJavaHome() + File.separator //$NON-NLS-1$
				+ "bin" + File.separator + "java"; //$NON-NLS-1$ //$NON-NLS-2$
			cmdargs[1] = "-classpath"; //$NON-NLS-1$
			cmdargs[2] = classpath;

			for (int i = 0; i < jvm.length; i++) {
				cmdargs[3 + i] = "-D" + jvm[i]; //$NON-NLS-1$
			}

			cmdargs[jvm.length + 3] = mainclass;

			System.arraycopy(args, 0, cmdargs, jvm.length + 4, args.length);

		} else {
			/**
			 * Were using the Microsoft VM. This requires quotes around any
			 * arguments and different command line syntax
			 */
			cmdargs[0] = "jview.exe"; //$NON-NLS-1$
			cmdargs[1] = "/cp"; //$NON-NLS-1$
			cmdargs[2] = "\"" + classpath + "\""; //$NON-NLS-1$ //$NON-NLS-2$

			for (int i = 0; i < jvm.length; i++) {
				cmdargs[3 + i] = "\"/d:" + jvm[i] + "\""; //$NON-NLS-1$ //$NON-NLS-2$
			}

			cmdargs[jvm.length + 3] = mainclass;

			for (int i = 0; i < args.length; i++) {
				if (args[i].indexOf(' ') > -1)
					cmdargs[4 + i + jvm.length] = "\"" + args[i] + "\""; //$NON-NLS-1$ //$NON-NLS-2$
				else
					cmdargs[4 + i + jvm.length] = args[i];
			}

		}

		String cmdline = ""; //$NON-NLS-1$
		for (int i = 0; i < cmdargs.length; i++)
			cmdline += " " + cmdargs[i]; //$NON-NLS-1$

		if (events != null && launcher.isDebug())
			events.debug(MessageFormat.format(Messages.getString("JavaApplicationType.executingCommand"), new Object[] { cmdline })); //$NON-NLS-1$

		try {

			if (events != null)
				events.executingApplication(launcher.getName(), cmdline.trim());

			
			/**
			 * The following code caused problems with class loading in non-debug mode agent! Error
			 * message stated that "Invalid length 65528 in LocalVariableTable" and it seems to be
			 * related to the if(1 == 2) as I found references to this same error with if(true) after
			 * some googling.
			 */
			// Write a script to do the launch as well (for debug)
			
//			if (1 == 2) { // TODO get from a configuration property somehow
//				File scriptFile = new File(workingDir == null ? launcher.getInstallDir() : workingDir,
//								"debug-launch." + (Utils.isSupportedPlatform("Windows") ? "bat" : "sh"));
//				FileOutputStream fos = new FileOutputStream(scriptFile);
//				try {
//					PrintWriter pw = new PrintWriter(fos);
//					if (!Utils.isSupportedPlatform("Windows")) {
//						pw.println("#!/bin/sh");
//					}
//					for (int i = 0; i < cmdargs.length; i++) {
//						if (i > 0) {
//							pw.print(" ");
//						}
//						pw.print("\"" + cmdargs[i] + "\"");
//					}
//					pw.flush();
//				} finally {
//					if (fos != null)
//						fos.close();
//				}
//			}

			// // Can we change the working directory of the process?
			try {
				Method m = Runtime.class.getMethod("exec", new Class[] { //$NON-NLS-1$
					String[].class, String[].class, File.class });
				Process prc = (Process) m.invoke(Runtime.getRuntime(), new Object[] { cmdargs, envp, workingDir });
				process = new ProcessMonitor(launcher.getName(), prc);
			} catch (Throwable t) {
				if (workingDir != null) {
					throw new IOException(Messages.getString("JavaApplicationType.failedToChangeWorkingDirectory") //$NON-NLS-1$
						+ "changed, but this Java Virtual Machine does not support that."); //$NON-NLS-1$
				}
				process = new ProcessMonitor(launcher.getName(), Runtime.getRuntime().exec(cmdargs));
			}
		} catch (IOException ex) {
			if (events != null)
				events.debug(MessageFormat.format(Messages.getString("JavaApplicationType.processExecutionFailed"), new Object[] { ex.getMessage() })); //$NON-NLS-1$
		}

	}
    
    void addDefaultEnvironment() {
        /* This will only work on 1.5+ runtimes. It is currently only needed
         * by Converse which is 1.5 dependant anyway
         */
        try {
            Class iteratorClass = getClass().getClassLoader().loadClass("java.util.Iterator");
            Class mapClass = getClass().getClassLoader().loadClass("java.util.Map"); 
            Object mapObject = System.class.getMethod("getenv", new Class[] { }).invoke(null, new Object[] { });
            Object setObject = mapClass.getMethod("keySet", new Class[] { }).invoke(mapObject, new Object[] { });
            Object iteratorObject =  getClass().getClassLoader().loadClass("java.util.Set").getMethod("iterator", new Class[] { }).invoke(setObject, new Object[] { });
            while( ((Boolean)iteratorClass.getMethod("hasNext", new Class[] { }).invoke(iteratorObject, new Object[] { })).booleanValue()) {                
                String name = (String)iteratorClass.getMethod("next", new Class[] { }).invoke(iteratorObject, new Object[] { });                
                if(!envVarExists(name)) {
                    envVars.addElement(name + "=" + ( mapClass.getMethod("get", new Class[] { Object.class }).invoke(mapObject, new Object[] { name })));
                }
            }
        } catch (Exception e) {
        }
        
    }
    
    boolean envVarExists(String name) {
        for(Enumeration e = envVars.elements(); e.hasMoreElements(); ) {
            if(((String)e.nextElement()).startsWith(name)) {
                return true;
            }
        }
        return false;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.vpn.util.ApplicationType#getProcessMonitor()
	 */
	public ProcessMonitor getProcessMonitor() {
		return process;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.vpn.util.ApplicationType#getRedirectParameters()
	 */
	public String getRedirectParameters() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.agent.client.util.ApplicationType#getTypeName()
	 */
	public String getTypeName() {
		return "java";
	}
}
