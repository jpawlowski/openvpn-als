package com.adito.agent.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.MessageFormat;

import com.adito.agent.client.util.Utils;

/**
 * Manipulate and examine the Windows registry, using either Microsoft classes
 * or the jRegistry library.
 */
public class WinRegistry {

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(WinRegistry.class);
    // #endif
    
    private static final String REGQUERY_UTIL = "reg query ";
    private static final String REGSTR_TOKEN = "REG_SZ";
    private static final String REGDWORD_TOKEN = "REG_DWORD";
    
    private static File exeLocation = new File(".");

    /**
     * Set a value
     * 
     * @param scope scope
     * @param key key
     * @param value value
     * @param val value type
     * @return set ok
     */
    static public boolean setRegistryValue(String scope, String key, String value, String val) {

        // #ifdef DEBUG
        log.info(MessageFormat.format(Messages.getString("WinRegistry.lookingUpKeyWithScope"), new Object[] { scope , key, value } ) ) ;//$NON-NLS-1$
        // #endif
        if (Utils.checkVersion("1.3") && System.getProperty("os.name") != null //$NON-NLS-1$ //$NON-NLS-2$
                        && System.getProperty("os.name").startsWith("Windows")) { //$NON-NLS-1$ //$NON-NLS-2$

        	return setValue(!scope.equalsIgnoreCase("user"), key, value, val);
        	
//            try {
//                RegistryKey regkey = new RegistryKey((scope.equalsIgnoreCase("user") ? RootKey.HKEY_CURRENT_USER //$NON-NLS-1$
//                                : RootKey.HKEY_LOCAL_MACHINE), key);
//
//                if (!regkey.exists()) {
//                    regkey.create();
//                }
//
//                RegistryValue v = new RegistryValue(value, val);
//                regkey.setValue(v);
//                return true;
//            } catch (RegistryException ex) {
//                // #ifdef DEBUG
//                log.error(MessageFormat.format(Messages.getString("WinRegistry.failedToSetRegistryValue"), new Object[] { value, key, scope } ) , ex) ;//$NON-NLS-1$
//                // #endif
//                return false;
//            }

        } else {
        	
            if (System.getProperty("java.vendor").startsWith("Microsoft")) { //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    Class clazz = Class.forName("com.ms.lang.RegKey"); //$NON-NLS-1$
                    int userRoot = clazz.getField(scope.equalsIgnoreCase("user") ? "USER_ROOT" : "LOCALMACHINE_ROOT").getInt(null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    int keyOpenAll = clazz.getField("KEYOPEN_ALL").getInt(null); //$NON-NLS-1$
                    // #ifdef DEBUG
                    log.info(Messages.getString("WinRegistry.lookingForRoot")); //$NON-NLS-1$
                    // #endif
                    Object rootKey = clazz.getMethod("getRootKey", new Class[] { int.class }).invoke(null, //$NON-NLS-1$
                        new Object[] { new Integer(userRoot) });
                    // #ifdef DEBUG
                    log.info(MessageFormat.format(Messages.getString("WinRegistry.gettingRegistryKey"), new Object[] { key } )); //$NON-NLS-1$
                    // #endif
                    Object obj = clazz.getConstructor(new Class[] { clazz, String.class, int.class }).newInstance(
                        new Object[] { rootKey, key, new Integer(keyOpenAll) });
                    // #ifdef DEBUG
                    log.info(Messages.getString("WinRegistry.checkingRegistryValue")); //$NON-NLS-1$
                    // #endif

                    clazz.getMethod("setValue", new Class[] { String.class, String.class }) //$NON-NLS-1$
                                    .invoke(obj, new Object[] { value, val });
                } catch (Throwable t) {
                    // #ifdef DEBUG
                    log.error(MessageFormat.format(Messages.getString("WinRegistry.failedToSetRegistryValue"), new Object[] { value, key, scope } ), t ) ;//$NON-NLS-1$
                    // #endif
                    return false;
                }
            } else {
                // #ifdef DEBUG
                log.info(MessageFormat.format(Messages.getString("WinRegistry.setNotSupported") //$NON-NLS-1$
                    , new Object[] { System.getProperty("java.version"), System.getProperty("java.vendor") } ) ); //$NON-NLS-1$  //$NON-NLS-2$
                // #endif
                return false;
            }

        }

        return true;
    }

    public static void setLocation(File exeLocation) {
    	WinRegistry.exeLocation = exeLocation;
    }
    
    /**
     * Get a value
     * 
     * @param scope scope
     * @param key key
     * @param value value
     * @param defaultValue default value
     * @return set ok
     */
    static public String getRegistryValue(String scope, String key, String value, String defaultValue) {

        // #ifdef DEBUG
        log.info(MessageFormat.format(Messages.getString("WinRegistry.lookingUpKeyWithScope"), new Object[] { scope , key, value } ) ) ;//$NON-NLS-1$
        // #endif
        if (Utils.checkVersion("1.3") && System.getProperty("os.name") != null //$NON-NLS-1$ //$NON-NLS-2$
                        && System.getProperty("os.name").startsWith("Windows")) { //$NON-NLS-1$ //$NON-NLS-2$

        	
        	  String ret = getValue(!scope.equalsIgnoreCase("user"), key, value);
        	  if(ret==null) {
        		
        		  // #ifdef DEBUG
        		  log.error("Cannot locate value, returning default " + defaultValue); //$NON-NLS-1$
        		  // #endif
        		  return defaultValue;
        	  }
        	  else {
        	        // #ifdef DEBUG
        	        log.info("Found value: " + ret) ;//$NON-NLS-1$
        	        // #endif
        		  
        		  return ret;
        	  }
        	  
//            try {
//                RegistryKey regkey = new RegistryKey((scope.equalsIgnoreCase("user") ? RootKey.HKEY_CURRENT_USER //$NON-NLS-1$
//                                : RootKey.HKEY_LOCAL_MACHINE), key);
//
//                return regkey.getValue(value).getStringValue();
//            } catch (RegistryException ex) {
//                // #ifdef DEBUG
//                log.error(MessageFormat.format(Messages.getString("WinRegistry.cannotAccessRegistryKey"), new Object[] { key, scope } ), ex); //$NON-NLS-1$
//                // #endif
//                return defaultValue;
//            }

        } else {
            if (System.getProperty("java.vendor").startsWith("Microsoft")) { //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    Class clazz = Class.forName("com.ms.lang.RegKey"); //$NON-NLS-1$
                    int userRoot = clazz.getField(scope.equalsIgnoreCase("user") ? "USER_ROOT" : "LOCALMACHINE_ROOT").getInt(null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    int keyOpenAll = clazz.getField("KEYOPEN_READ").getInt(null); //$NON-NLS-1$
                    // #ifdef DEBUG
                    log.info(Messages.getString("WinRegistry.lookingForRoot")); //$NON-NLS-1$
                    // #endif
                    Object rootKey = clazz.getMethod("getRootKey", new Class[] { int.class }).invoke(null, //$NON-NLS-1$
                        new Object[] { new Integer(userRoot) });
                    // #ifdef DEBUG
                    log.info(MessageFormat.format(Messages.getString("WinRegistry.gettingRegistryKey"), new Object[] { key } )); //$NON-NLS-1$
                    // #endif

                    Object obj = clazz.getConstructor(new Class[] { clazz, String.class, int.class }).newInstance(
                        new Object[] { rootKey, key, new Integer(keyOpenAll) });
                    // #ifdef DEBUG
                    log.info(Messages.getString("WinRegistry.checkingRegistryValue")); //$NON-NLS-1$
                    // #endif

                    return (String) (clazz.getMethod("getStringValue", new Class[] { String.class, String.class }).invoke(obj, //$NON-NLS-1$
                        new Object[] { value, "" })); //$NON-NLS-1$
                } catch (Throwable t) {
                    // #ifdef DEBUG
                    log.error(MessageFormat.format(Messages.getString("WinRegistry.cannotAccessRegistryKey"), new Object[] { key, scope } ), t); //$NON-NLS-1$
                    // #endif
                    return defaultValue;
                }

            } else {
                // #ifdef DEBUG
                log.info(MessageFormat.format(Messages.getString("WinRegistry.getNotSupported") //$NON-NLS-1$
                    , new Object[] { System.getProperty("java.version"), System.getProperty("java.vendor") } ) ); //$NON-NLS-1$  //$NON-NLS-2$
                // #endif
            }
        }

        return defaultValue;
    }

    private static boolean setValue(boolean localmachine, String key, String value, String val) {
    	return false;
    }
    
	private static String getValue(boolean localmachine, String key, String value) {
		
		  String[] args = new String[5];
		  args[0] = exeLocation.getAbsolutePath() + "\\key.exe";
		  args[1] = "query";
		  args[2] = (localmachine ? "HKLM" : "HKCU");
		  args[3] = key ; //key.indexOf(" ") > -1 ? "\"" + key + "\"" : key;
		  args[4] = value; //value.indexOf(" ") > -1 ? "\"" + value + "\"" : value;
		  
		  try {
		      Process process = Runtime.getRuntime().exec(args);
		      StreamReader reader = new StreamReader(process.getInputStream());

		      reader.start();
		      process.waitFor();
		      reader.join();

		      String result = reader.getResult();
		      int p = result.indexOf("ERROR");

		      if (p == -1) {
			      return result;
		      } else {
		    	  return null;
		      }
		    }
		    catch (Exception e) {
		    	// #ifdef DEBUG
		    	log.error(e);
		    	// #endif
		      return null;
		    }
	  }
	
	  static class StreamReader extends Thread {
		    private InputStream is;
		    private StringWriter sw;

		    StreamReader(InputStream is) {
		      this.is = is;
		      sw = new StringWriter();
		    }

		    public void run() {
		      try {
		        int c;
		        while ((c = is.read()) != -1)
		          sw.write(c);
		        }
		        catch (IOException e) { ; }
		      }

		    String getResult() {
		      return sw.toString();
		    }
		  }
}