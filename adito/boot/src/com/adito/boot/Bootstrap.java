
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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * ALL CHANGED - REWRITE
 * 
 * 
 * Bootstrap an Adito server implementation. The first argument passed to
 * {@link #main(String[])} should be the class name of the server
 * implementation.
 * <p>
 * This class is expected to have two statics methods.
 * <code>setBootLoader(ClassLoader bootLoader)</code> and
 * <code>main(String[] args)</code>.
 * <p>
 * A classpath.properties file in the 
 * <p>
 * The server will be loaded using a new class loader that scans the
 * <b>serverlib</b> directory for any jars specific to this implementation. The
 * parent of this classloader is the <i>Boot Loader</i>
 * 
 * The <i>Boot Loader</i> is the class loader that loads all of the boot
 * classes and anything in lib.
 * <p>
 * The server implementation should use the <i>Boot Loader</i> as the parent
 * for the Adito web application. This hides the server implementation
 * from the Adito core and any of its extensions.
 * 
 */
public class Bootstrap {

    // Private instance variables

    private ClassLoader serverLoader;
    private ClassLoader bootLoader;   
    private File conf;
    private Properties classpath;

    /**
     * Start the server implementation.
     * 
     * @param serverImplClassName server implementation
     * @param args arguments
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void start(String serverImplClassName, String[] args) throws ClassNotFoundException, SecurityException,
                    NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Class<?> serverClass = Class.forName(serverImplClassName, true, serverLoader);
        Method setBootLoaderMethod = serverClass.getMethod("setBootLoader", new Class[] { ClassLoader.class });
        setBootLoaderMethod.invoke(null, new Object[] { bootLoader });
        Method mainMethod = serverClass.getMethod("main", new Class[] { String[].class });
        mainMethod.invoke(null, new Object[] { args });
    }

    /**
     * Configure the class loaders.
     * 
     * @param conf conf directory
     * @throws IOException
     * @throws URISyntaxException 
     */
    public void configureClassLoaders(File conf) throws IOException, URISyntaxException {
        this.conf = conf;
        loadClasspathConfiguration();
        List<URL> serverLibs = new ArrayList<URL>();
        addJarPaths("server.classPath.jars", serverLibs);
        addDirPaths("server.classPath.dirs", serverLibs);
        List<URL> bootLibs = new ArrayList<URL>();
        addDir(conf, bootLibs);
        addJarPaths("boot.classPath.jars", bootLibs);
        addDirPaths("boot.classPath.dirs", bootLibs);
        ClassLoader extLoader = ClassLoader.getSystemClassLoader().getParent();
        bootLoader = new URLClassLoader(bootLibs.toArray(new URL[bootLibs.size()]), extLoader);
        serverLoader = new URLClassLoader(serverLibs.toArray(new URL[serverLibs.size()]), bootLoader);

    }
    
    private void addJarPaths(String propertyName, List<URL> libs) throws MalformedURLException, IOException {
        String paths = classpath.getProperty(propertyName);
        if(paths != null) {
            StringTokenizer t = new StringTokenizer(paths, ",");
            while(t.hasMoreTokens()) {
                addDirLibs(new File(t.nextToken()), libs);
            }
        }
    }
    
    private void addDirPaths(String propertyName, List<URL> libs) throws MalformedURLException, IOException {
        String paths = classpath.getProperty(propertyName);
        if(paths != null) {
            StringTokenizer t = new StringTokenizer(paths, ",");
            while(t.hasMoreTokens()) {
                addDir(new File(t.nextToken()), libs);
            }
        }
    }

    private void addDirLibs(File dir, List<URL> libs) throws MalformedURLException, IOException {
        if(!dir.exists() || !dir.isDirectory()) {
            return;
        }
        for (File jar : dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".jar");
            }
        })) {
            libs.add(jar.getCanonicalFile().toURI().toURL());
        }
    }

    private URL[] debugClasspath(String string, URL[] urls) throws URISyntaxException {
        System.out.println("Classloader " + string);
        for(int i = 0 ; i < urls.length ; i++) {
            System.out.println(new File(urls[i].toURI()).getAbsolutePath());
        }
        return urls;
    }

    private void addDir(File dir, List<URL> libs) throws MalformedURLException, IOException {
        if(dir.exists() || !dir.isDirectory()) {
            libs.add(dir.getCanonicalFile().toURI().toURL());
        }        
    }
    
    private void loadClasspathConfiguration() throws IOException {
        FileInputStream fin = new FileInputStream(new File(conf, "classpath.properties"));
        try {
            classpath = new Properties();
            classpath.load(fin);
        }
        finally {
            if(fin != null) {
                fin.close();
            }
        }
    }

    /**
     * Entry point. First argument should be the server implementation class
     * name. Remaining arguments are passed to create server.
     * 
     * @param args arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        List<String> argList = new ArrayList<String>(Arrays.asList(args));
        String serverImplClassName = "com.adito.server.Main";
        if(argList.size() > 1 && argList.get(0).equals("--serverImpl")) {
            argList.remove(0);
            serverImplClassName = argList.get(0);
            argList.remove(0);
        }
        args = argList.toArray(new String[argList.size()]);
        
        // Look for --conf argument and add that to that classpath
        File conf = new File("conf");
        for(String arg : argList) {
            if(arg.startsWith("--conf=")) {
                conf = new File(arg.substring(7));
            }
        }

        // Create the bootstrap, configure it and start the server
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.configureClassLoaders(conf);
        bootstrap.start(serverImplClassName, args);
    }
}
