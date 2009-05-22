
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
			
package com.ovpnals.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;

import com.ovpnals.boot.ContextHolder;
import com.ovpnals.boot.Util;

public class StatsLogger extends Thread {
	final static Log log = LogFactory.getLog(StatsLogger.class);
	
	private Server server;
	private int update;
	
	public StatsLogger(Server server, int update) {
		super("StatsLogger");
		server.setStatsOn(true);
		setDaemon(true);
		this.server = server;
		this.update = update;
		start();
	}

	public void run() {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(ContextHolder.getContext().getLogDirectory(), "stats.csv"));
			PrintWriter pw = new PrintWriter(out, true);
			pw.println("connections,connectionsOpen,connectionsOpenMax,connectionsDurationAve," + "connectionsDurationMax,connectionsRequestsAve,connectionsRequestsMax,"
				+ "errors,requests,requestsActive,requestsActiveMax,requestsDurectionAve,requestsDurationMax");
			StringBuffer sb = new StringBuffer();
			while (true) {
				sb.append(server.getConnections());
				sb.append(",");
				sb.append(server.getConnectionsOpen());
				sb.append(",");
				sb.append(server.getConnectionsOpenMax());
				sb.append(",");
				sb.append(server.getConnectionsDurationAve());
				sb.append(",");
				sb.append(server.getConnectionsDurationMax());
				sb.append(",");
				sb.append(server.getConnectionsRequestsAve());
				sb.append(",");
				sb.append(server.getConnectionsRequestsMax());
				sb.append(",");
				sb.append(server.getErrors());
				sb.append(",");
				sb.append(server.getRequests());
				sb.append(",");
				sb.append(server.getRequestsActive());
				sb.append(",");
				sb.append(server.getRequestsActiveMax());
				sb.append(",");
				sb.append(server.getRequestsDurationAve());
				sb.append(",");
				sb.append(server.getRequestsDurationMax());
				pw.println(sb.toString());
				sb.setLength(0);
				Thread.sleep(update);
			}
		} catch (Exception e) {
			log.error("Failed to create stats files.", e);
		} finally {
			Util.closeStream(out);
		}
	}

}
