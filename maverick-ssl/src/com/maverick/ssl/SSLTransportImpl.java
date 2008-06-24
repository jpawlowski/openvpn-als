
				/*
 *  Adito
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
			
package com.maverick.ssl;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.text.MessageFormat;

import com.maverick.crypto.asn1.x509.X509Certificate;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class SSLTransportImpl implements SSLTransport {

    public static final int VERSION_MAJOR = 3;
    public static final int VERSION_MINOR = 0;

    static final int WARNING_ALERT = 1;
    static final int FATAL_ALERT = 2;

    final static int CHANGE_CIPHER_SPEC_MSG = 20;
    final static int ALERT_PROTOCOL = 21;
    final static int APPLICATION_DATA = 23;

    SSLInputStream sslIn = new SSLInputStream();
    SSLOutputStream sslOut = new SSLOutputStream();

    SSLHandshakeProtocol handshake = null;
    SSLContext context;

    public String debug = "Standard transport"; //$NON-NLS-1$

    DataInputStream rawIn;
    DataOutputStream rawOut;

    long incomingSequence = 0;
    long outgoingSequence = 0;

    SSLCipherSuite writeCipherSuite = null;
    SSLCipherSuite readCipherSuite = null;

    // #ifdef DEBUG
    org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(SSLSocket.class);

    // #endif

    public SSLTransportImpl() {
    }

    public SSLContext getContext() {
        return context;
    }

    /* (non-Javadoc)
	 * @see com.maverick.ssl.SSLTransport#getCertificate()
	 */
    public X509Certificate getCertificate(){ 
    	return handshake.getCertificate();
    }
    
    /* (non-Javadoc)
	 * @see com.maverick.ssl.SSLTransport#initialize(java.io.InputStream, java.io.OutputStream)
	 */
    public void initialize(InputStream in, OutputStream out) throws IOException, SSLException {
        initialize(in, out, null);
    }

    public void initialize(InputStream in, OutputStream out, SSLContext context) throws IOException, SSLException {

        this.rawIn = new DataInputStream(in);
        this.rawOut = new DataOutputStream(out);
        writeCipherSuite = readCipherSuite = new SSL_NULL_WITH_NULL_NULL();

        // #ifdef DEBUG
        log.debug(Messages.getString("SSLTransport.initialising")); //$NON-NLS-1$
        // #endif

        if (context == null)
            context = new SSLContext();

        handshake = new SSLHandshakeProtocol(this, context);

        // Start the handshake
        handshake.startHandshake();

        // While the handshake is not complete process all the messages
        while (!handshake.isComplete()) {
            processMessages();

            // #ifdef DEBUG
            log.debug(Messages.getString("SSLTransport.initCompleteStartingAppProtocol")); //$NON-NLS-1$
            // #endif
        }
    }

    void processMessages() throws SSLException, EOFException {

        int type = 0;
        byte[] fragment = null;
        try {
            type = rawIn.read();
            int major = rawIn.read();
            int minor = rawIn.read();

            int length = rawIn.readShort();

            fragment = new byte[length];

            rawIn.readFully(fragment);

            readCipherSuite.decrypt(fragment, 0, fragment.length);

            if (readCipherSuite.getMACLength() > 0) {
                if (!readCipherSuite.verifyMAC(fragment,
                    0,
                    fragment.length - readCipherSuite.getMACLength(),
                    type,
                    incomingSequence,
                    fragment,
                    fragment.length - readCipherSuite.getMACLength(),
                    readCipherSuite.getMACLength())) {
                    throw new SSLException(SSLException.PROTOCOL_VIOLATION, Messages.getString("SSLTransport.invalidMAC")); //$NON-NLS-1$
                }

            }
        } catch (EOFException ex) {
            throw ex;
        } catch (InterruptedIOException ex) {
            throw new SSLException(SSLException.READ_TIMEOUT);
        } catch (IOException ex) {
            throw new SSLException(SSLException.UNEXPECTED_TERMINATION, ex.getMessage() == null ? ex.getClass().getName()
                : ex.getMessage());
        }

        incomingSequence++;

        // #ifdef DEBUG
        log.debug(MessageFormat.format(Messages.getString("SSLTransport.processingFragmentOfType"), new Object[] { new Integer(type) })); //$NON-NLS-1$
        // #endif

        switch (type) {
            case SSLHandshakeProtocol.HANDSHAKE_PROTOCOL_MSG:
                handshake.processMessage(fragment, 0, fragment.length - readCipherSuite.getMACLength());
                break;
            case CHANGE_CIPHER_SPEC_MSG:

                // #ifdef DEBUG

                log.debug(Messages.getString("SSLTransport.changingInputCipherSpec")); //$NON-NLS-1$
                // #endif

                readCipherSuite = handshake.getPendingCipherSuite();
                incomingSequence = 0;
                break;
            case ALERT_PROTOCOL:
                switch (fragment[0]) {
                    case FATAL_ALERT:
                        throw new SSLException(((int) (fragment[1] & 0xFF)));
                    case WARNING_ALERT:
                        switch (fragment[1]) {
                            case SSLException.CLOSE_NOTIFY:

                                // #ifdef DEBUG
                                log.debug(Messages.getString("SSLTransport.remoteSideClosing")); //$NON-NLS-1$
                                // #endif

                                sendMessage(ALERT_PROTOCOL, new byte[] { (byte) WARNING_ALERT, (byte) SSLException.CLOSE_NOTIFY });
                                // close();
                                // Let the InputStream know that we're at EOF
                                throw new EOFException();

                            default:

                                // #ifdef DEBUG
                                log.warn(SSLException.getDescription(fragment[1]));
                                // #endif

                                break;
                        }

                        break;
                    default:

                        // #ifdef DEBUG
                        log.debug(MessageFormat.format(Messages.getString("SSLTransport.unexpectedAlert"), new Object[] { new Integer(fragment[0]), new Integer(fragment[1]) })); //$NON-NLS-1$
                        // #endif

                        break;
                }
            case APPLICATION_DATA:
                sslIn.write(fragment, 0, fragment.length - readCipherSuite.getMACLength());
                break;
            default:
                throw new SSLException(SSLException.PROTOCOL_VIOLATION,
                    Messages.getString("SSLTransport.unexpecedSSLProtocolType") + type); //$NON-NLS-1$

        }

    }

    /* (non-Javadoc)
	 * @see com.maverick.ssl.SSLTransport#close()
	 */
    public void close() throws SSLException {
        sendMessage(ALERT_PROTOCOL, new byte[] { WARNING_ALERT, SSLException.CLOSE_NOTIFY });
    }

    void sendMessage(int type, byte[] fragment) throws SSLException {
        sendMessage(type, fragment, 0, fragment.length);
    }

    void sendMessage(int type, byte[] fragment, int off, int len) throws SSLException {

        // Compress the record?? - since were not using compression the
        // record remains the same
        byte[] encrypted;
        // Calculate the mac
        if (writeCipherSuite.getMACLength() > 0) {
            byte[] mac = writeCipherSuite.generateMAC(fragment, off, len, type, outgoingSequence);
            // Create the final encrypted packet
            encrypted = new byte[len + mac.length];
            System.arraycopy(fragment, off, encrypted, 0, len);
            System.arraycopy(mac, 0, encrypted, len, mac.length);

            // Encrypt the packet
            writeCipherSuite.encrypt(encrypted, 0, encrypted.length);
        } else {
            if (off > 0 || fragment.length != len) {
                encrypted = new byte[len];
                System.arraycopy(fragment, off, encrypted, 0, len);
            } else {
                encrypted = fragment;
            }
        }

        // Create a record for sending
        ByteArrayOutputStream record = new ByteArrayOutputStream();

        try {
            record.write(type);
            record.write(SSLTransportImpl.VERSION_MAJOR);
            record.write(SSLTransportImpl.VERSION_MINOR);
            record.write((encrypted.length >> 8) & 0xFF);
            record.write(encrypted.length);
            record.write(encrypted);
        } catch (IOException ex) {
            throw new SSLException(SSLException.INTERNAL_ERROR, ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
        }

        try {
            // Send the record
            rawOut.write(record.toByteArray());
        } catch (IOException ex) {
            throw new SSLException(SSLException.UNEXPECTED_TERMINATION, ex.getMessage() == null ? ex.getClass().getName()
                : ex.getMessage());
        }

        // TODO: check the limit and reset to zero if required
        outgoingSequence++;
    }

    void sendCipherChangeSpec(SSLCipherSuite pendingCipherSuite) throws SSLException {

        // #ifdef DEBUG
        log.debug(Messages.getString("SSLTransport.changingOutputCipherSpec")); //$NON-NLS-1$
        // #endif

        sendMessage(CHANGE_CIPHER_SPEC_MSG, new byte[] { 1 });
        writeCipherSuite = pendingCipherSuite;
        outgoingSequence = 0;
    }

    /* (non-Javadoc)
	 * @see com.maverick.ssl.SSLTransport#getInputStream()
	 */
    public InputStream getInputStream() throws IOException {
        return sslIn;
    }

    /* (non-Javadoc)
	 * @see com.maverick.ssl.SSLTransport#getOutputStream()
	 */
    public OutputStream getOutputStream() throws IOException {
        return sslOut;
    }

    class SSLOutputStream extends OutputStream {
        public synchronized void write(int b) throws IOException {
            // #ifdef DEBUG
            log.debug(Messages.getString("SSLTransport.sending1Byte")); //$NON-NLS-1$
            // #endif
            try {
                sendMessage(APPLICATION_DATA, new byte[] { (byte) b });
            } catch (SSLException ex) {
                throw new SSLIOException(ex);
            }
        }

        public synchronized void write(byte[] b, int off, int len) throws IOException {
            // #ifdef DEBUG
            log.debug(MessageFormat.format(Messages.getString("SSLTransport.bytesToSend"), new Object[] { new Integer(len) })); //$NON-NLS-1$
            // #endif

            try {
                int pos = 0;
                while (pos < len) {
                    // #ifdef DEBUG
                    log.debug(MessageFormat.format(Messages.getString("SSLTransport.sendingBlock"), new Object[] { new Integer((len - pos) < 16384 ? len - pos : 16384) })); //$NON-NLS-1$
                    // #endif
                    sendMessage(APPLICATION_DATA, b, off + pos, ((len - pos) < 16384 ? len - pos : 16384));
                    pos += (len < 16384 ? len : 16384);
                }
            } catch (SSLException ex) {
                throw new SSLIOException(ex);
            }
        }
    }

    class SSLInputStream extends InputStream {

        byte[] buffer;
        int unread = 0;
        int position = 0;
        int base = 0;
        boolean isEOF = false;

        SSLInputStream() {
            buffer = new byte[16384];
        }

        public int available() {
            return unread;
        }

        public int read() throws IOException {
            byte[] b = new byte[1];
            int ret = read(b, 0, 1);
            if (ret > 0) {
                return b[0] & 0xFF;
            } else {
                return -1;
            }
        }

        long transfered = 0;

        public int read(byte[] buf, int offset, int len) throws IOException {

            try {

                while (unread <= 0 && !isEOF) {
                    processMessages();
                }

                int count = unread < len ? unread : len;
                int index = base;
                base = (base + count) % buffer.length;
                if (buffer.length - index > count) {
                    System.arraycopy(buffer, index, buf, offset, count);
                } else {
                    int remaining = buffer.length - index;
                    System.arraycopy(buffer, index, buf, offset, remaining);
                    System.arraycopy(buffer, 0, buf, offset + remaining, count - remaining);
                }

                unread -= count;

                return count;
            } catch (EOFException ex) {
                return -1;
            } catch (SSLException ex) {
                throw new SSLIOException(ex);
            }

        }

        void write(byte[] buf, int offset, int len) {

            for (int i = offset; i < offset + len; i++) {
                // puts a value from the circular buffer
                int index = (base + unread) % buffer.length;
                buffer[index] = buf[i];
                unread++;
            }

        }
    }

}
