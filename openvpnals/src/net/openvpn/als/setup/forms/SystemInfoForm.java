
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.setup.forms;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.SystemProperties;
import net.openvpn.als.core.forms.CoreForm;
import net.openvpn.als.setup.SystemInformationRegistry;


/**
 * Implementation of a {@link CoreForm} that is used to display information
 * about the system OpenVPNALS is running on.
 */
public class SystemInfoForm extends CoreForm {

    static Log log = LogFactory.getLog(SystemInfoForm.class);

    
    
    /**
     * Get the total amount of memory (in KiB) that is available to the
     * Java runtime.
     * 
     * @return total memory in KiB
     */
    public String getTotalMemoryK() {
        return String.valueOf(Runtime.getRuntime().totalMemory() / 1024);
    }

    /**
     * Get the amount of free memory (in KiB) that is available to the
     * Java runtime.
     * 
     * @return free memory in KiB
     */
    public String getFreeMemoryK() {
        return String.valueOf(Runtime.getRuntime().freeMemory() / 1024);
    }


    /**
     * Get the amount of memory (in KiB) that has been used by the
     * Java runtime.
     * 
     * @return used memory in KiB
     */
    public String getUsedMemoryK() {
        return String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);
    }

    /**
     * Get the architecture of the CPU that the Java runtime is running on.
     * 
     * @return os architecture
     */
    public String getOSArch() {
        return SystemProperties.get("os.arch");
    }

    /**
     * Get the name of the operating system that the Java runtime is running on
     * 
     * @return os name
     */
    public String getOSName() {
        return SystemProperties.get("os.name");
    }

    /**
     * Get the version of the operating system that the Java runtime is running on
     * 
     * @return os version
     */
    public String getOSVersion() {
        return SystemProperties.get("os.version");
    }


    /**
     * Get the version of the Java runtime
     * 
     * @return java version
     */
    public String getJavaVersion() {
        return SystemProperties.get("java.version");
    }

    /**
     * Get the port number the service is running on
     * 
     * @return server port number
     */
    public String getServerPort() {
        return String.valueOf(ContextHolder.getContext().getPort());
    }

    /**
     * Get the number of active threads
     * 
     * @return active threads
     */
    public String getActiveThreads() {
        try {
            return String.valueOf(Thread.activeCount());
        } catch (RuntimeException e) {
            return "unknown";
        }
    }

    /**
     * Get the number of CPUs available to the Java runtime.
     * 
     * @return cpus
     */
    public String getCPUCount() {
        return String.valueOf(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Get a list (as strings) of the nwrok interfaces that are available
     * on this host.
     *   
     * TODO This should be the interfaces that OpenVPNALS is using.
     *   
     * @return network interfaces
     */
    public List getNetworkInterfaces() {
        Enumeration e = null;
        try {
            List niList = new ArrayList();
            e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface netface = (NetworkInterface) e.nextElement();
                Enumeration e2 = netface.getInetAddresses();
                while (e2.hasMoreElements()) {
                    InetAddress ip = (InetAddress) e2.nextElement();
                    niList.add(netface.getName() + "=" + ip.toString());
                }
            }
            return niList;
        } catch (SocketException e1) {
            return new ArrayList();
        }
    }

    /**
     * Get a thread dump as a string.
     * 
     * @return thread dump string
     */
    public String getThreadDump() {
        StringBuffer buf = new StringBuffer();
        Thread thread = ContextHolder.getContext().getMainThread();
        if (thread != null) {
            ThreadGroup group = thread.getThreadGroup();
            try {
                if (group != null) {
                    dumpThread(group, 0, buf);
                } else {
                    buf.append("[No main thread group]");
                }
            } catch (Throwable t) {
                log.error("Failed to get thread dump.", t);
            }
        } else {
            buf.append("[No main thread]");
        }
        return buf.toString();
    }

    private void dumpThread(ThreadGroup group, int level, StringBuffer buf) {
        for (int i = 0; i < level; i++) {
            buf.append("  ");
        }
        buf.append("[");
        buf.append(group.getName());
        buf.append("]");
        Thread[] t = new Thread[group.activeCount()];
        group.enumerate(t);
        for (int i = 0; t != null && i < t.length; i++) {
            if (t[i].getThreadGroup() == group) {
                buf.append("\n");
                for (int j = 0; j < level + 1; j++) {
                    buf.append("  ");
                }
                buf.append(t[i].getName());
                buf.append(" (pri. ");
                buf.append(t[i].getPriority());
                buf.append(")");
            }
        }
        ThreadGroup[] g = new ThreadGroup[group.activeGroupCount()];
        group.enumerate(g);
        for (int i = 0; g != null && i < g.length; i++) {
            buf.append("\n");
            dumpThread(g[i], level + 1, buf);
        }
    }
    
    
    public List getSystemInformationProviders() {
    	return SystemInformationRegistry.getInstance().getProviders();
    }
}