package com.ovpnals.agent.channels;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelOpenException;
import com.maverick.util.ByteArrayReader;
import com.ovpnals.agent.AgentTunnel;
import com.ovpnals.boot.Util;
import com.ovpnals.extensions.ExtensionDescriptor;
import com.ovpnals.extensions.store.ExtensionStore;
import com.ovpnals.policyframework.LaunchSession;
import com.ovpnals.policyframework.LaunchSessionFactory;

/**
 * Channel implementation used for download files required by the <i>Agent</i>
 * to launch an <i>Application Shortcut</i>.
 */
public class ApplicationFileChannel extends Channel implements Runnable {
	final static Log log = LogFactory.getLog(ApplicationFileChannel.class);

	/**
	 * Channel type identifier
	 */
	public static final String CHANNEL_TYPE = "applicationFile";

	String name;
	String launchId;
	String filename;
	File file;
	FileInputStream in;
	Thread thread;
	AgentTunnel agent;

	/**
	 * Constructor.
	 * 
	 * @param agent agent
	 * 
	 */
	public ApplicationFileChannel(AgentTunnel agent) {
		super(CHANNEL_TYPE, 32768, 0);
		this.agent = agent;
	}

	/* (non-Javadoc)
	 * @see com.maverick.multiplex.Channel#open(byte[])
	 */
	@Override
	public byte[] open(byte[] data) throws IOException, ChannelOpenException {

		try {
			ByteArrayReader reader = new ByteArrayReader(data);
			name = reader.readString();
			launchId = reader.readString();
			filename = reader.readString();

			LaunchSession launchSession = LaunchSessionFactory.getInstance().getLaunchSession(launchId);
			if (launchSession == null) {
				throw new ChannelOpenException(ChannelOpenException.CHANNEL_REFUSED, "No launch session with ID " + launchId + ", cannot read file " + filename + " for " + name);
			}

			launchSession.checkAccessRights(null, agent.getSession());

			ExtensionDescriptor descriptor = ExtensionStore.getInstance().getExtensionDescriptor(name);
			if (!descriptor.containsFile(filename))
				throw new ChannelOpenException(ChannelOpenException.CHANNEL_REFUSED, "Application does not contain file " + filename + " in extension " + name);

			this.file = descriptor.getFile(filename);
			this.in = new FileInputStream(file);

		} catch (Exception e) {
			throw new ChannelOpenException(ChannelOpenException.CHANNEL_REFUSED, e.getMessage());
		}

		return null;

	}

	/* (non-Javadoc)
	 * @see com.maverick.multiplex.Channel#onChannelData(byte[], int, int)
	 */
	public void onChannelData(byte[] buf, int off, int len) {
	}

	/* (non-Javadoc)
	 * @see com.maverick.multiplex.Channel#create()
	 */
	@Override
	public byte[] create() throws IOException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.maverick.multiplex.Channel#onChannelOpen(byte[])
	 */
	@Override
	public void onChannelOpen(byte[] data) {
		thread = new Thread(this);
		thread.start();
	}

	/* (non-Javadoc)
	 * @see com.maverick.multiplex.Channel#onChannelClose()
	 */
	@Override
	public void onChannelClose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			Util.copy(in, getOutputStream());
		} catch (Exception ex) {
			log.error("Failed to copy file contents.", ex);
		} finally {
			close();
		}
	}

}
