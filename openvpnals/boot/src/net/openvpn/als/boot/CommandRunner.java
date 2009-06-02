
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
			
package net.openvpn.als.boot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Executes a system command give a list of arguments and gather the output in a
 * string that be accessed when the command is complete.
 * 
 */
public class CommandRunner extends Thread {
    final static Log log = LogFactory.getLog(CommandRunner.class);

    // Private instance variables

    private String[] arguments;
    private StringBuffer outBuf = new StringBuffer();
    private Process process;
    private boolean includeOutputOnException;

    /**
     * Constructor. The first argument supplied must be the name of the command
     * (if it is on the system path) or the full path the command. Any command
     * arguments should be supplied as further elements.
     * 
     * @param arguments
     */
    public CommandRunner(String[] arguments) {
        this.arguments = arguments;
    }

    /**
     * Constructor. The first argument supplied must be the name of the command
     * (if it is on the system path) or the full path the command. Any command
     * arguments should be supplied as further elements.
     * 
     * @param arguments
     */
    public CommandRunner(Vector arguments) {
        this.arguments = new String[arguments.size()];
        arguments.copyInto(this.arguments);
    }

    /**
     * Get the output of the command.
     * 
     * @return output
     */
    public String getOutput() {
        return outBuf.toString();
    }

    /**
     * Run the command. Any output will then be availabie from
     * {@link #getOutput()}. An exception will be thrown if the command returns
     * a non-zero exit value.
     * 
     * @throws Exception if any error occurs running the command.
     */
    public void runCommand() throws Exception {
        try {

            if (log.isDebugEnabled() || "true".equals(SystemProperties.get("openvpnals.debugSystemCommands", "false"))) {
                
                StringBuffer debug = new StringBuffer("Running command '");
                for (int i = 0; i < arguments.length; i++) {
                    if (i > 0) {
                        debug.append(" ");
                    }
                    debug.append("\"");
                    debug.append(arguments[i]);
                    debug.append("\"");
                }
    
                if(log.isDebugEnabled()) {
                	log.debug(debug.toString());
                }
                
                if("true".equals(SystemProperties.get("openvpnals.debugSystemCommands", "false"))) {
                    log.error("CMD " + debug.toString());
                }
            }

            process = Runtime.getRuntime().exec(arguments);
            InputStream in = process.getInputStream();
            start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if ("true".equals(SystemProperties.get("openvpnals.debugSystemCommands", "false"))) {
                    log.error("STDOUT " + line);
                }
                synchronized (outBuf) {
                    if (outBuf.length() > 0) {
                        outBuf.append("\n");
                    }
                    outBuf.append(line);
                }
            }
            process.waitFor();
            if (process.exitValue() != 0) {
                log.error("Command '" + arguments[0] + "' failed with exit code " + process.exitValue());
                log.error("Start of command output ---->");
                log.error(outBuf.toString());
                log.error("<---- End of command output");
                throw new Exception("Command returned exit code " + process.exitValue() + ". "
                                + (includeOutputOnException ? outBuf.toString() : "Check the logs for more detail."));
            }
        } finally {
        }
    }

    /**
     * Set whther the output should be included in the exception text should an
     * error occur.
     * 
     * @param includeOutputOnException include output on exception
     */
    public void setIncludeOutputOnException(boolean includeOutputOnException) {
        this.includeOutputOnException = includeOutputOnException;

    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if ("true".equals(SystemProperties.get("openvpnals.debugSystemCommands", "false"))) {
                    log.error("STDERR " + line);
                }
                synchronized (outBuf) {
                    if (outBuf.length() > 0) {
                        outBuf.append("\n");
                    }
                    outBuf.append(line);
                }
            }
        } catch (IOException ioe) {
        }
    }
}