package com.adito.agent.client.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Utilities that may be used throughout the VPN client suite.
 */
public class Utils {

	/**
	 * Default buffer size for stream utility methods
	 */
	public static int BUFFER_SIZE = 8192;


    /**
     * Trim spaces from both ends of string
     * 
     * @param string string to trim
     * @return trimmed string
     */
    public static String trimBoth(String string) {
        string = string.trim();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != ' ') {
                return string.substring(i);
            }
        }
        return string;
    }

    /**
     * Return a trimmed string (both ends) regardless of whether the
     * source string is <code>null</code> (in which case an
     * empty string will be retuned). 
     * 
     * @param string
     * @return trimmed or blank string
     */
    public static String trimmedBothOrBlank(String string) {
        return trimBoth(string == null ? "" : string.trim());
    }
    
	/**
	 * Get the users home directory and if were operating in the MSJVM then try
	 * to get the users profile, since MSJVM does not return the correct
	 * location for the user.home system property.
	 * 
	 * @return String
	 */
	public static String getHomeDirectory() {

		String userHome = System.getProperty("user.home"); //$NON-NLS-1$

		if (System.getProperty("java.vendor") != null && System.getProperty("java.vendor").startsWith("Microsoft")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			try {
				Process process = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/C", "echo", "%USERPROFILE%" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String profileDir = reader.readLine();

				File f = new File(profileDir);
				if (f.exists()) {
					userHome = profileDir;
				}
			} catch (Throwable t) {
				// Ignore, we cant do anything about it!!
			}

		}
		return userHome;
	}
	
	
	public static String getWindowsDirectory() {
		String windir = "C:\\WINDOWS"; // Fall back position

			try {
				Process process = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/C", "echo", "%WINDIR%" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String tmp = reader.readLine();

				File f = new File(tmp);
				if (f.exists()) {
					windir = tmp;
				}
			} catch (Throwable t) {
				// Ignore, we can't do anything about it!!
			}

		return windir;
	}
	
	
	public static void main(String[] args) {
		System.out.println(getWindowsDirectory());
	}

    /**
     * Test if a string is <code>null</code> if it is an
     * empty string when trimmed.
     * 
     * @param string
     * @return null or trimmed blank string
     */
    public static boolean isNullOrTrimmedBlank(String string) {
        return string == null || string.trim().length() == 0;
    }

	/**
	 * Create a locale object given the code. This splits up the locale name
	 * into language, country and variant parts to keep 1.1 compatibility (which
	 * has no single string constructor in Locale)
	 * 
	 * @param localeName locale name
	 * @return locale
	 */
	public static Locale createLocale(String localeName) {
		String lang = localeName;
		String country = "";
		String variant = "";
		int idx = localeName.indexOf("_");
		if (idx != -1) {
			country = lang.substring(idx + 1);
			lang = lang.substring(0, idx);
		}
		idx = country.indexOf('_');
		if (idx != -1) {
			variant = country.substring(idx + 1);
			country = country.substring(0, idx);
		}
		return new Locale(lang, country, variant);
	}

	/**
	 * Get a resource bundle via the network class loader given the resource
	 * name and the locale. This is required for 1.1 compatibility.
	 * 
	 * @param basename
	 * @param locale
	 * @param cl
	 * @param url url
	 * @return resource bundle
	 * @throws MissingResourceException if bundle cannot be found for any reason
	 */
	public static ResourceBundle getBundle(String basename, Locale locale, ClassLoader cl, URL url) throws MissingResourceException {
		
		ResourceBundle resourceBundle = null;
		
		// ResourceBundle by default looks for loads of different ways of providing the 
		// resource bundle. We do not want it to go to the server unless a different locale is
		// specified so must provide our own locating and loading
		
		// Create the resource name
		String resource = basename.replace('.', '/') + ".properties";
		System.out.println("Looking for '" + resource + "' in locale " + locale + " (display name = " + locale.getDisplayName() + "'");
		
		// If the language is not specified and not "en" or "en_GB" then load from the network 
		if(locale != null && !"en".equalsIgnoreCase(locale.toString()) &&  
				!"en_GB".equalsIgnoreCase(locale.toString())) {
            try {
				URL resourceUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + resource);
        		System.out.println("Non GB resource, so trying server '" + resourceUrl.toExternalForm() + "'");
				InputStream in = resourceUrl.openStream();
				try {
					resourceBundle = new PropertyResourceBundle(in);
				}
				finally {
					closeStream(in);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} 
		}
		
		// If no resource bundle has yet been loaded, look for it in the class loader		
		if(resourceBundle == null && cl != null) {
    		System.out.println("Must GB resource, so trying class load");
			InputStream in = cl.getResourceAsStream(resource);
			try {
				resourceBundle = new PropertyResourceBundle(in);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} 
			finally {
				closeStream(in);
			} 
		}
		
		// Cannot find
		if(resourceBundle == null) {
			throw new MissingResourceException("No such bundle could be located for " + basename, basename, "");
		}
		
		return resourceBundle;
	}

	/**
	 * Check if the current JRE meets the specified application extension JRE
	 * version.
	 * 
	 * @param applicationJRE
	 * @return ok
	 */
	public static boolean checkVersion(String applicationJRE) {
        if (applicationJRE == null) {
            return false;
        }

        int[] applicationVersion = Utils.getVersion(applicationJRE);
        int[] installedJREVersion = Utils.getVersion(System.getProperty("java.version")); //$NON-NLS-1$

        for (int i = 0; i < applicationVersion.length && i < installedJREVersion.length; i++) {
            if (applicationVersion[i] > installedJREVersion[i])
                return false;
        }

        return true;
    }

	/**
	 * Get a dotted string version number an integer array.
	 * 
	 * @param version
	 * @return integer array version number
	 */
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

	public static boolean isSupportedJRE(String jre) {
		return isVersion(jre, System.getProperty("java.version"));
	}

	public static boolean isVersion(String required, String have) {

		int[] ourVersion = Utils.getVersion(have); //$NON-NLS-1$

		if (required.startsWith(">") || required.startsWith("+")) { //$NON-NLS-1$

			// Our JRE must be greater than the value specified
			int[] requiredVersion = Utils.getVersion(required.substring(1));
			for (int i = 0; i < Math.max(ourVersion.length,requiredVersion.length); i++) {
				if ( ( i < ourVersion.length ?ourVersion[i] : 0 ) < ( i < requiredVersion.length ? requiredVersion[i] : 0 ) )
					return false;
			}
			return true;

		} else if (required.startsWith("<") || required.startsWith("-")) { //$NON-NLS-1$
			// Our JRE must be less than the value specified
			int[] requiredVersion = Utils.getVersion(required.substring(1));
			for (int i = 0; i < Math.min(ourVersion.length,requiredVersion.length); i++) {
				if ( ( i < ourVersion.length ?ourVersion[i] : 0 ) > ( i < requiredVersion.length ? requiredVersion[i] : 0 ) )
					return false;
			}
			return true;

		} else {
			// Direct comparison
			int[] requiredVersion = Utils.getVersion(required);
			for (int i = 0; i < Math.min(ourVersion.length,requiredVersion.length); i++) {
				if ( ( i < ourVersion.length ?ourVersion[i] : 0 ) != ( i < requiredVersion.length ? requiredVersion[i] : 0 ) )
					return false;
			}
			return true;

		}

	}

	public static boolean isSupportedOSVersion(String osVersion) {
		return isVersion(osVersion, System.getProperty("os.version"));
	}

	public static boolean isSupportedPlatform(String os) {
		if (os != null && !os.equals("")) {
			// If the os does not start with the current platform then ignore
			// this
			String platform = System.getProperty("os.name").toUpperCase(); //$NON-NLS-1$
			if(os.startsWith("!")) { //$NON-NLS-1$
				return !platform.startsWith(os.substring(1).toUpperCase());
			} else
				return platform.startsWith(os.toUpperCase());
		} else
			return true;
	}

	public static boolean isSupportedArch(String arch) {
		if (arch != null && !arch.equals("")) {
			String platformArch = System.getProperty("os.arch").toUpperCase(); //$NON-NLS-1$
			if(arch.startsWith("!")) { //$NON-NLS-1$
				if(isWindows64JREAvailable())
					return !arch.substring(1).toUpperCase().equals("AMD64");
				else 
					return !platformArch.startsWith(arch.substring(1).toUpperCase());
			} else {
				if(isWindows64JREAvailable())
					return arch.toUpperCase().equals("AMD64");
				else 
					return platformArch.startsWith(arch.toUpperCase());
			}
		} else
			return true;
	}
	
	public static boolean isWindows64JREAvailable() {
		
		try {
			String javaHome = new File(System.getProperty("java.home")).getCanonicalPath();
			
			try {
				if(System.getProperty("os.name").startsWith("Windows")) {
					int dataModel = Integer.parseInt(System.getProperty("sun.arch.data.model"));
	
					if(dataModel!=64) {
				    	int idx = javaHome.indexOf(" (x86)");
				    	if(idx > -1) {
				    		// Looks like we have a 32bit Java version installed on 64 bit Windows
				    		String programFiles = javaHome.substring(0, idx);
				    		File j = new File(programFiles, "Java");
				    		if(j.exists()) {
				    			// We may have a 64 bit version of Java installed.
				    			String[] jres = j.list();
				    			for(int i=0;i<jres.length;i++) {
	
				    				File h = new File(j, jres[i]);
				    				File exe = new File(h, "bin\\java.exe");
				    				if(exe.exists()) {
				    					// Found a 64bit version of java
				    					return true;
				    				}
				    			}
				    		}
				    	}
				    }
				}
			} catch(NumberFormatException ex) {
			}
			
			return false;
		} catch(IOException ex) {
			return false;
		}
	}
	
	public static String getJavaHome() {
		
		/**
		 * Try to determine if we have a 64bit version of Java available. This
		 * is required to ensure that any native 64 bit code functions correctly. Why?
		 * well there is no Java Plug-in available for Windows x64 so the 32 bit version
		 * has to be installed. This means that when the agent is launched java.home will
		 * always point to a 32 bit version of Java on a Windows x64 machine. This 
		 * causes problems with drive mapping extension as we have x64 JNI code.
		 */
		try {
			String javaHome = new File(System.getProperty("java.home")).getCanonicalPath();
			
			try {
				if(System.getProperty("os.name").startsWith("Windows")) {
					int dataModel = Integer.parseInt(System.getProperty("sun.arch.data.model"));
	
					if(dataModel!=64) {
				    	int idx = javaHome.indexOf(" (x86)");
				    	if(idx > -1) {
				    		// Looks like we have a 32bit Java version installed on 64 bit Windows
				    		String programFiles = javaHome.substring(0, idx);
				    		File j = new File(programFiles, "Java");
				    		if(j.exists()) {
				    			// We may have a 64 bit version of Java installed.
				    			String[] jres = j.list();
				    			for(int i=0;i<jres.length;i++) {
	
				    				File h = new File(j, jres[i]);
				    				File exe = new File(h, "bin\\java.exe");
				    				if(exe.exists()) {
				    					// Found a 64bit version of java
				    					javaHome = h.getAbsolutePath();
				    					break;
				    				}
				    			}
				    		}
				    	}
				    }
				}
			} catch(NumberFormatException ex) {
			}
			
			return javaHome;
		} catch(IOException ex) {
			return System.getProperty("java.home");
		}
	}

	/**
	 * Attempt to make a file executable. Only current works on systems that
	 * have the <b>chmod</b> command available.
	 * 
	 * @param file file
	 * @throws IOException on any error
	 */
	public static void makeExecutable(File file) throws IOException {
		Process p = Runtime.getRuntime().exec(new String[] { "chmod", "ug+rx", file.getAbsolutePath() });
		try {
			copy(p.getErrorStream(), new ByteArrayOutputStream());
		} finally {
			try {
				if (p.waitFor() != 0) {
					throw new IOException("Failed to set execute permission. Return code " + p.exitValue() + ".");
				}
			} catch (InterruptedException e) {
			}
		}

	}

	/**
	 * Attempt to make a file read only. Uses Java method when possible,
	 * otherwise falls back to working only on systems that have the <b>chmod</b>
	 * command available.
	 * 
	 * @param file file
	 * @throws IOException on any error
	 */
	public static void makeReadOnly(File file) throws IOException {
		try {
			file.getClass().getMethod("setReadOnly", new Class[] {}).invoke(file, new Object[] {});
		} catch (Exception e) {
			Process p = Runtime.getRuntime().exec(new String[] { "chmod", "a-w", file.getAbsolutePath() });
			try {
				copy(p.getErrorStream(), new ByteArrayOutputStream());
			} finally {
				try {
					if (p.waitFor() != 0) {
						throw new IOException("Failed to set execute permission. Return code " + p.exitValue() + ".");
					}
				} catch (InterruptedException ie) {
				}
			}

		}

	}

	/**
	 * Copy from an input stream to an output stream. It is up to the caller to
	 * close the streams.
	 * 
	 * @param in input stream
	 * @param out output stream
	 * @throws IOException on any error
	 */
	public static void copy(InputStream in, OutputStream out) throws IOException {
		copy(in, out, -1);
	}

	/**
	 * Copy the specified number of bytes from an input stream to an output
	 * stream. It is up to the caller to close the streams.
	 * 
	 * @param in input stream
	 * @param out output stream
	 * @param count number of bytes to copy
	 * @throws IOException on any error
	 */
	public static void copy(InputStream in, OutputStream out, long count) throws IOException {
		copy(in, out, count, BUFFER_SIZE);
	}

	/**
	 * Copy the specified number of bytes from an input stream to an output
	 * stream. It is up to the caller to close the streams.
	 * 
	 * @param in input stream
	 * @param out output stream
	 * @param count number of bytes to copy
	 * @param bufferSize buffer size
	 * @throws IOException on any error
	 */
	public static void copy(InputStream in, OutputStream out, long count, int bufferSize) throws IOException {
		byte buffer[] = new byte[bufferSize];
		int i = bufferSize;
		if (count >= 0) {
			while (count > 0) {
				if (count < bufferSize)
					i = in.read(buffer, 0, (int) count);
				else
					i = in.read(buffer, 0, bufferSize);

				if (i == -1)
					break;

				count -= i;
				out.write(buffer, 0, i);
			}
		} else {
			while (true) {
				i = in.read(buffer, 0, bufferSize);
				if (i < 0)
					break;
				out.write(buffer, 0, i);
			}
		}
	}

	/**
	 * 
	 * 
	 * @param in
	 * 
	 * @return
	 */
	public static boolean closeStream(InputStream in) {
		try {
			if (in != null) {
				in.close();
			}

			return true;
		} catch (IOException ioe) {
			return false;
		}
	}

	public static void copyFile(File from, File to) throws IOException {

		if (from.isDirectory()) {
			if (!to.exists()) {
				to.mkdir();
			}
			String[] children = from.list();
			for (int i = 0; i < children.length; i++) {
				File f = new File(from, children[i]);
				if (f.getName().equals(".") || f.getName().equals("..")) {
					continue;
				}
				if (f.isDirectory()) {
					File f2 = new File(to, f.getName());
					copyFile(f, f2);
				} else {
					copyFile(f, to);
				}
			}
		} else if (from.isFile() && (to.isDirectory() || to.isFile())) {
			if (to.isDirectory()) {
				to = new File(to, from.getName());
			}
			FileInputStream in = new FileInputStream(from);
			FileOutputStream out = new FileOutputStream(to);
			byte[] buf = new byte[32678];
			int read;
			while ((read = in.read(buf)) > -1) {
				out.write(buf, 0, read);
			}
			closeStream(in);
			closeStream(out);

		}
	}

	/**
	 * 
	 * 
	 * @param out
	 * 
	 * @return
	 */
	public static boolean closeStream(OutputStream out) {
		try {
			if (out != null) {
				out.close();
			}

			return true;
		} catch (IOException ioe) {
			return false;
		}
	}

}
