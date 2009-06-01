/*
 * Created on 08-May-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.maverick.multiplex;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * @author lee
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MessageStore {


    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
    	.getLog(MessageStore.class);
    // #endif

	Message header = new Message();
	public static final int NO_MESSAGES = -1;
	Channel channel;
	boolean closed = false;
	MessageObserver stickyMessageObserver;

	public MessageStore(Channel channel, MessageObserver stickyMessageObserver) {
		this.channel = channel;
		this.stickyMessageObserver = stickyMessageObserver;
		header.next = header.previous = header;
	}

	/**
	 * 
	 * @param messagefilter
	 * @return
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public Message nextMessage(MessageObserver observer) throws IOException, EOFException {
		return nextMessage(observer, 0);
	}

	public Message nextMessage(MessageObserver observer, int timeoutMS) throws IOException, EOFException {
		// #ifdef DEBUG
		if (log.isDebugEnabled())
			log.debug("Waiting for next message timeout=" + timeoutMS);
		// #endif

		try {

			long startTime = System.currentTimeMillis();

			synchronized (header) {
				Message msg = null;

				while (msg == null && !isClosed()) {

					msg = hasMessage(observer);

					if (msg != null) {
						if (stickyMessageObserver != null && stickyMessageObserver.wantsNotification(msg)) {
							// #ifdef DEBUG
							if (log.isDebugEnabled())
								log.debug("Message that wants notification found");
							// #endif
							return msg;
						}

						// #ifdef DEBUG
						if (log.isDebugEnabled())
							log.debug("Message that wants notification found");
						// #endif
						
						remove(msg);
						return msg;
					}

					if (timeoutMS > 0) {
						if ((System.currentTimeMillis() - startTime) > timeoutMS)
							throw new InterruptedIOException("Timeout waiting for message");
					}
					header.wait(1000);
				}
			}
		} catch (InterruptedException ex) {
		}
		// #ifdef DEBUG
		if (log.isDebugEnabled())
			log.debug("Message could not be found");
		// #endif

		throw new EOFException("The required message could not be found in the message store");
	}

	public boolean isClosed() {
		synchronized (header) {
			return closed;
		}
	}

	private void remove(Message e) {

		if (e == header) {
			throw new IndexOutOfBoundsException();
		}

		e.previous.next = e.next;
		e.next.previous = e.previous;
	}

	public Message hasMessage(MessageObserver observer) {

		synchronized (header) {

			if (header.next == null) {
				return null;
			}

			for (Message e = header.next; e != header; e = e.next) {
				if (observer.wantsNotification(e))
					return e;
			}

			return null;

		}
	}

	public void close() {

		synchronized (header) {
			closed = true;
			header.notifyAll();
		}
	}

	void addMessage(Message msg) {
		synchronized (header) {
			msg.next = header;
			msg.previous = header.previous;
			msg.previous.next = msg;
			msg.next.previous = msg;
			header.notifyAll();
		}
	}
}
