package com.ovpnals.agent.client;


import java.io.IOException;

import com.maverick.http.AuthenticationCancelledException;
import com.maverick.http.HttpException;
import com.maverick.http.UnsupportedAuthenticationException;

public interface AgentProvider {
	public Agent getAgent(String ticketUri) throws IOException, HttpException, UnsupportedAuthenticationException, AuthenticationCancelledException;
}
