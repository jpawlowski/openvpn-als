package com.maverick.multiplex;

public interface RequestHandler {
	public boolean processRequest(Request request, MultiplexedConnection connection);
	public void postReply(MultiplexedConnection connection);
}
