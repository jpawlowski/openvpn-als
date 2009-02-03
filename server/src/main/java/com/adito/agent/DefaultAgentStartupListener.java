package com.adito.agent;

import com.adito.security.SessionInfo;
import com.maverick.multiplex.MultiplexedConnectionListener;

public class DefaultAgentStartupListener implements
		MultiplexedConnectionListener {

	SessionInfo session;
	
	DefaultAgentStartupListener(SessionInfo session) {
		this.session = session;
		
	}
	
	public void onConnectionClose() {

	}

	public void onConnectionOpen() {
	}


}
