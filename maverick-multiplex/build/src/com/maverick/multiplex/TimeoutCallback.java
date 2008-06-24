package com.maverick.multiplex;

public interface TimeoutCallback {

	public boolean isAlive(MultiplexedConnection con);
}
