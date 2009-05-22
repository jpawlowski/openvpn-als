package com.ovpnals.networkplaces.store.sftp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.networkplaces.AbstractNetworkPlaceMount;
import com.ovpnals.networkplaces.AbstractNetworkPlaceStore;


import com.ovpnals.policyframework.LaunchSession;

public class SFTPStore extends AbstractNetworkPlaceStore{	
	final static Log log = LogFactory.getLog(SFTPStore.class);
	public final static String SFTP_SCHEME = "sftp";

	public SFTPStore(){
		super(SFTP_SCHEME, "UTF-8");
	}

	protected AbstractNetworkPlaceMount createMount(LaunchSession launchSession) throws Exception {
		return new SFTPMount(launchSession, this);
	}
}
