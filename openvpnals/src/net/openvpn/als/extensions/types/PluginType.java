/*
 */
package net.openvpn.als.extensions.types;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.SystemProperties;
import net.openvpn.als.boot.Util;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.extensions.ExtensionDescriptor;
import net.openvpn.als.extensions.ExtensionException;
import net.openvpn.als.extensions.ExtensionType;
import net.openvpn.als.policyframework.Resource.LaunchRequirement;
import net.openvpn.als.security.SessionInfo;

/**
 * Extension type that adds a new <i>Plugin</i>
 */
public class PluginType implements ExtensionType {

    final static Log log = LogFactory.getLog(PluginType.class);

    /**
     * Type name
     */
    public final static String TYPE = "plugin";
    
    // Private statics
    private static Map<String, Plugin> plugins = new HashMap<String, Plugin>();

    // Private instance variables

    private PluginDefinition def;
    private Plugin plugin;
    private boolean classpathAdded;
    private boolean isolate;
    private ClassLoader classLoader;

    /**
     * Constructor
     */
    public PluginType() {
    }
    
    /**
     * Utility method to get a plugin instance given its Id. <code>null</code>
     * will be returned if no such plugin exists. 
     * 
     * @param id plugin id
     * @return plugin
     */
    public static Plugin getPlugin(String id) {
    	return plugins.get(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.extensions.ExtensionType#start(net.openvpn.als.extensions.ExtensionDescriptor,
     *      org.jdom.Element)
     */
    public void start(ExtensionDescriptor descriptor, Element element) throws ExtensionException {
        readConfiguration(descriptor, element);
        createPlugin();
        startPlugin(descriptor, element);

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.extensions.ExtensionType#verifyRequiredElements()
     */
    public void verifyRequiredElements() throws ExtensionException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.extensions.ExtensionType#isHidden()
     */
    public boolean isHidden() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.extensions.ExtensionType#getType()
     */
    public String getType() {
        return TYPE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.extensions.ExtensionType#stop()
     */
    public void stop() throws ExtensionException {
        // TODO remove native dirs and classpath

        if (def != null) {
            for (URL u : def.getResourceBases()) {
                ContextHolder.getContext().removeResourceBase(u);
            }
            def.getResourceBases().clear();
        }

        if (plugin != null) {
            String tilesConfigFile = plugin.getTilesConfigFile();
            if (tilesConfigFile != null) {
                CoreServlet.getServlet().removeTileConfigurationFile(tilesConfigFile);
            }
            plugin.stopPlugin();
        }
    }

    public void activate() throws ExtensionException {
        if(isolate) {
        	ClassLoader cl = Thread.currentThread().getContextClassLoader();
        	Thread.currentThread().setContextClassLoader(classLoader);
        	try {
                plugin.activatePlugin();   		
        	}
        	finally {
            	Thread.currentThread().setContextClassLoader(cl);
        	}
        }
        else {
            plugin.activatePlugin();
        }
    }

    public boolean canStop() throws ExtensionException {
// Until struts actions can be unloaded we cannot dynamically stop plugins         
//        return plugin != null ? plugin.canStopPlugin() : true;
        return false;
    }

    void readConfiguration(ExtensionDescriptor descriptor, Element element) throws ExtensionException {

        // Plugin name
        String name = element.getAttributeValue("name");
        if (name == null || name.equals("")) {
            throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
                "The name attribute must be supplied for <plugin> elements (" + descriptor.getApplicationBundle().getFile().getPath() + ").");
        }

        // Plugin classname
        String className = element.getAttributeValue("class");
        if (className == null || className.equals("")) {
            throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
                "The class attribute must be supplied for <plugin> elements (" + descriptor.getApplicationBundle().getFile().getPath() + ").");
        }

        // Order
        String orderText = element.getAttributeValue("order");
        int order = 999;
        if (orderText != null && !orderText.equals("")) {
            order = Integer.parseInt(orderText);
        }
        
        isolate = "true".equals(element.getAttributeValue("isolate"));

        // Optional
        String dependencies = element.getAttributeValue("dependencies");
        if (dependencies != null) {
            log.warn("DEPRECATED. dependencies attribute in plugin definition in "
                + descriptor.getApplicationBundle().getFile().getAbsolutePath() + " should now use 'depends'.");
        } else {
            dependencies = element.getAttributeValue("depends");
        }

        def = new PluginDefinition(descriptor);

        // Required
        def.setName(name);
        def.setClassName(className);

        // Optional
        def.setOrder(order);

        /* If this is a dev extension, we generate the paths to add */
        if(!descriptor.getApplicationBundle().isDevExtension()) {
	        for (Iterator i = element.getChildren().iterator(); i.hasNext();) {
	            Element el = (Element) i.next();
	            if (el.getName().equals("classpath")) {
	                String path = Util.trimmedBothOrBlank(el.getText());
	                if (!path.equals("")) {
	                    File f = new File(descriptor.getApplicationBundle().getBaseDir(), path);
	                    if (f.exists()) {
	                        try {
	    						// NOTE - Do not fix this deprecation warning (Java 6+). It will break plugin JSP compiling. See brett
	                            URL u = f.getCanonicalFile().toURL();
	                            if (log.isInfoEnabled())
	                                log.info("Adding " + u + " to classpath");
	                            def.addClassPath(u);
	                        } catch (IOException murle) {
	                            throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
	                                murle, "Invalid classpath.");
	                        }
	                    } else {
	                        if (!"true".equals(SystemProperties.get("openvpnals.useDevConfig"))) {
	                            log.warn("Plugin classpath element " + f.getAbsolutePath() + " does not exist.");
	                        }
	                    }
	                }
	            } else if (el.getName().equals("resources")) {
	                File f = new File(descriptor.getApplicationBundle().getBaseDir(), el.getText());
	                if (f.exists() && f.isDirectory()) {
	                    try {
                            // NOTE - Do not fix this deprecation warning (Java 6+). It will break plugin JSP compiling. See brett
	                        def.addResourceBase(f.getCanonicalFile().toURL());
	                    } catch (Exception ex) {
	                        throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR, ex, "Invalid resource base.");
	                    }
	                } else {
	                    if (log.isInfoEnabled())
	                        log.info("<resources> element does not point to a valid directory.");
	                }
	            } else if (el.getName().equals("native")) {
	                File f = new File(descriptor.getApplicationBundle().getBaseDir(), el.getText());
	                if (f.exists() && f.isDirectory()) {
	                    try {
	                        def.addNativeDirectory(f.getCanonicalFile());
	                    } catch (Exception ex) {
	                        throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
	                            ex, "Invalid native directory.");
	                    }
	                } else {
	                    if (log.isInfoEnabled())
	                        log.info("<native> element does not point to a valid directory.");
	                }
	            } else {
	                throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
	                    "The <plugin> element only supports the nested <classpath>, <resources> or <native> elements");
	            }
	        }
        }
        
        /* If the descript was added as a devExtension, then search for and add all
         * webapps, classpaths and native directories
         */ 
        if(descriptor.getApplicationBundle().isDevExtension()) { 
	        File d = new File(new File(SystemProperties.get("user.dir")).getParentFile(), descriptor.getApplicationBundle().getId());
			File extensionDir = new File(new File(d, "extensions"), d.getName());
			File webapp = new File(d, "webapp");
			File classes = new File(new File(d, "build"), "classes");
			if (webapp.exists()) {
				try {
                    // NOTE - Do not fix this deprecation warning (Java 6+). It will break plugin JSP compiling. See brett
					def.addResourceBase(webapp.toURL());
				} catch (MalformedURLException e) {
				}
			}
			if (classes.exists()) {
				try {
                    // NOTE - Do not fix this deprecation warning (Java 6+). It will break plugin JSP compiling. See brett
					def.addClassPath(classes.toURL());
				} catch (MalformedURLException e) {
				}
			}
			File privateDir = new File(extensionDir, "private");
			if (privateDir.exists()) {
				File[] jars = privateDir.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						return pathname.getName().toLowerCase().endsWith(".jar");
					}
				});
				for (int idx = 0; jars != null && idx < jars.length; idx++) {
					try {
                        // NOTE - Do not fix this deprecation warning (Java 6+). It will break plugin JSP compiling. See brett
						def.addClassPath(jars[idx].toURL());
					} catch (MalformedURLException e) {
					}
				}
			}
			File nativeDir = new File(new File(d, "build"), "native");
			if (nativeDir.exists()) {
				try {
					def.addNativeDirectory(nativeDir.getCanonicalFile());
				}
				catch(Exception e) {					
				}
			}
        }
    }

    synchronized void createPlugin() throws ExtensionException {
    	
    	/* Get an configure the class loader to use.
    	 */


        classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
        	classLoader = getClass().getClassLoader();
        }
        
        /*
         * If this plugin should be isolated, the create a class loader 
         * for its own use with the current class loader as the parent
         */
    	if(isolate) {
    		log.info("Isolating " + def.getName());
    		URL[] classPath = def.getClassPath().toArray(new URL[def.getClassPath().size()]);
    		classLoader = new PluginClassLoader(classPath, classLoader);
    		if(!classpathAdded) {
	            addNativeDirs(def);
    			classpathAdded = true;
    		}
    	}
    	else {
	        // Add to the classpath and native search path
	        if (!classpathAdded) {
	            for (URL url : def.getClassPath()) {
	                ContextHolder.getContext().addContextLoaderURL(url);
	            }
	            addNativeDirs(def);
	            classpathAdded = true;
	        }
    	}

        try {
        	if(log.isInfoEnabled()) {
        		log.info("Creating plugin " + def.getClassName());
        	}
        	Class pluginClass = Class.forName(def.getClassName(), true, classLoader);
            plugin = (Plugin) pluginClass.newInstance();
        } catch (Exception e) {
            throw new ExtensionException(ExtensionException.FAILED_TO_CREATE_PLUGIN_INSTANCE,
                def.getName(),
                def.getClassName(),
                e.getMessage(),
                e);
        }
        
        plugins.put(def.getName(), plugin);

        String resn = plugin.getClass().getName().replace('.', '/') + ".class";
        if (log.isDebugEnabled())
            log.debug("Looking for resource " + resn);
        URL res = plugin.getClass().getClassLoader().getResource(resn);
        String resource = "";
        if (res == null)
            log.error("Could not locate resource " + resn);
        else {

            String n = res.toExternalForm();
            if (n.startsWith("jar:file:")) {
                n = n.substring(4);
                int idx = n.lastIndexOf('!');
                if (idx != -1)
                    n = n.substring(0, idx);
                n = n.substring(5);
                // Windows path?
                if (n.startsWith("/") && n.length() > 3 && n.charAt(2) == ':' && Character.isLetter(n.charAt(1))
                    && n.charAt(3) == '/')
                    n = n.substring(1);
                File f = new File(n);
                resource = f.getAbsolutePath();
            }
            if (log.isDebugEnabled())
                log.debug("Resource is " + resource);
        }
    }

    private void addNativeDirs(PluginDefinition def) {
    	
        for (Iterator j = def.getNativeDirectories(); j.hasNext();) {
            File f = (File) j.next();
            try {
                CoreUtil.addLibraryPath(f.getAbsolutePath());
            } catch (IOException e) {
                log.error("Failed to add native directory " + f.getAbsolutePath() + ". The plugin " + def.getName()
                    + " may not function correctly.", e);
            }
        }
		
	}

    void startPlugin(ExtensionDescriptor descriptor, Element element) throws ExtensionException {

        // Add the resource bases from the plugin
        for (URL url : def.getResourceBases()) {
            ContextHolder.getContext().addResourceBase(url);
        }
        
        try {
            String tilesConfigFile = plugin.getTilesConfigFile();
            if (tilesConfigFile != null) {
                CoreServlet.getServlet().addTileConfigurationFile(tilesConfigFile);
            }
            
            String path = "/WEB-INF/" + def.getDescriptor().getId() + "-struts-config.xml";
            CoreServlet.getServlet().addStrutsConfig(path);
            
            if(isolate) {
            	ClassLoader cl = Thread.currentThread().getContextClassLoader();
            	Thread.currentThread().setContextClassLoader(classLoader);
            	try {
                	plugin.startPlugin(def, descriptor, element);            		
            	}
            	finally {
                	Thread.currentThread().setContextClassLoader(cl);
            	}
            }
            else {
            	plugin.startPlugin(def, descriptor, element);
            }
        } catch (ExtensionException e) {
            // remove the resource bases from the plugin
            for (URL url : def.getResourceBases()) {
                ContextHolder.getContext().removeResourceBase(url);
            }
            throw e;
        } catch (ServletException se) {
            throw new ExtensionException(ExtensionException.INTERNAL_ERROR, se);
        }
    }

	/*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.extensions.ExtensionType#descriptorCreated(org.jdom.Element)
     */
	public void descriptorCreated(Element element, SessionInfo session) throws IOException {		
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.extensions.ExtensionType#getTypeBundle()
	 */
	public String getTypeBundle() {
		return "extensions";
	}

    /* (non-Javadoc)
     * @see net.openvpn.als.extensions.ExtensionType#getLaunchRequirement()
     */
    public LaunchRequirement getLaunchRequirement() {
        return LaunchRequirement.NOT_LAUNCHABLE;
    }
}
