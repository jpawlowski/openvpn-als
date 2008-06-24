
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.MessageFormat;

//#ifdef DEBUG
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//#endif

import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.DERInputStream;
import com.maverick.crypto.asn1.x509.CertificateException;
import com.maverick.crypto.asn1.x509.X509Certificate;
import com.maverick.crypto.asn1.x509.X509CertificateStructure;
import com.maverick.crypto.digests.MD5Digest;
import com.maverick.crypto.digests.SHA1Digest;
import com.maverick.crypto.publickey.PublicKey;
import com.maverick.crypto.publickey.Rsa;
import com.maverick.crypto.publickey.RsaPublicKey;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
class SSLHandshakeProtocol {

    final static int HANDSHAKE_PROTOCOL_MSG = 22;

    final static int HELLO_REQUEST_MSG = 0;
    final static int CLIENT_HELLO_MSG = 1;
    final static int SERVER_HELLO_MSG = 2;

    final static int CERTIFICATE_MSG = 11;
    final static int KEY_EXCHANGE_MSG = 12;

    final static int CERTIFICATE_REQUEST_MSG = 13;
    final static int SERVER_HELLO_DONE_MSG = 14;

    final static int CERTIFICATE_VERIFY_MSG = 15;
    final static int CLIENT_KEY_EXCHANGE_MSG = 16;
    final static int FINISHED_MSG = 20;

    final static int HANDSHAKE_PENDING_OR_COMPLETE = -1;

    SSLContext context;
    SSLTransportImpl socket;

    MD5Digest handshakeMD5 = new MD5Digest();
    SHA1Digest handshakeSHA1 = new SHA1Digest();

    // The session components as selected by the server
    SSLCipherSuiteID cipherSuiteID;
    int compressionID;
    byte[] sessionID;
    int majorVersion;
    int minorVersion;
    byte[] clientRandom;
    byte[] serverRandom;
    byte[] premasterSecret;
    byte[] masterSecret;

    X509Certificate x509;

    SSLCipherSuite pendingCipherSuite;
    boolean wantsClientAuth = false;
    int currentHandshakeStep = HANDSHAKE_PENDING_OR_COMPLETE;

    // #ifdef DEBUG
    Log log = LogFactory.getLog(SSLHandshakeProtocol.class);

    // #endif

    public SSLHandshakeProtocol(SSLTransportImpl socket, SSLContext context) throws IOException {
        this.socket = socket;
        this.context = context;

    }

    boolean isComplete() {
        return currentHandshakeStep == HANDSHAKE_PENDING_OR_COMPLETE;
    }

    void processMessage(byte[] fragment, int off, int len) throws SSLException {

        ByteArrayInputStream reader = new ByteArrayInputStream(fragment, off, len);

        // Update the handshake hashes
        updateHandshakeHashes(fragment);

        while (reader.available() > 0 && !isComplete()) {

            int type = reader.read();

            int length = (reader.read() & 0xFF) << 16 | (reader.read() & 0xFF) << 8 | (reader.read() & 0xFF);

            // #ifdef DEBUG
            log.debug(MessageFormat.format(Messages.getString("SSLHandshakeProtocol.processingType"), new Object[] { new Integer(type), new Long(length) })); //$NON-NLS-1$
            // #endif

            byte[] msg = new byte[length];
            try {
                reader.read(msg);
            } catch (IOException ex) {
                throw new SSLException(SSLException.INTERNAL_ERROR, ex.getMessage() == null ? ex.getClass().getName()
                    : ex.getMessage());
            }

            switch (type) {
                case HELLO_REQUEST_MSG:

                    // #ifdef DEBUG
                    log.debug(Messages.getString("SSLHandshakeProtocol.receivedHELLO")); //$NON-NLS-1$
                    // #endif
                    /**
                     * If we receive a hello request then a handshake must be
                     * re-negotiated. But ignore it if were already performing a
                     * handshake operation
                     */
                    if (currentHandshakeStep == HANDSHAKE_PENDING_OR_COMPLETE) {
                        startHandshake();
                    }
                    break;

                case SERVER_HELLO_MSG:

                    // #ifdef DEBUG
                    log.debug(Messages.getString("SSLHandshakeProtocol.receivedServerHELLO")); //$NON-NLS-1$
                    // #endif

                    if (currentHandshakeStep != CLIENT_HELLO_MSG) {
                        throw new SSLException(SSLException.PROTOCOL_VIOLATION,
                            MessageFormat.format(Messages.getString("SSLHandshakeProtocol.receivedUnexpectedServerHello"), new Object[] { new Integer(currentHandshakeStep) })); //$NON-NLS-1$
                    }

                    onServerHelloMsg(msg);
                    break;

                case CERTIFICATE_MSG:

                    // #ifdef DEBUG
                    log.debug(Messages.getString("SSLHandshakeProtocol.receivedServerCertificate")); //$NON-NLS-1$
                    // #endif

                    if (currentHandshakeStep != SERVER_HELLO_MSG) {
                        throw new SSLException(SSLException.PROTOCOL_VIOLATION,
                            MessageFormat.format(Messages.getString("SSLHandshakeProtocol.unexpectedCertificateMessageReceived"), new Object[] { new Integer(currentHandshakeStep) })); //$NON-NLS-1$
                    }
                    onCertificateMsg(msg);
                    break;

                case KEY_EXCHANGE_MSG:

                    // #ifdef DEBUG
                    log.debug(Messages.getString("SSLHandshakeProtocol.receivedUnsupportedServerKEX")); //$NON-NLS-1$
                    // #endif

                    throw new SSLException(SSLException.UNSUPPORTED_OPERATION,
                        Messages.getString("SSLHandshakeProtocol.kexNotSupported")); //$NON-NLS-1$

                case CERTIFICATE_REQUEST_MSG:

                    // #ifdef DEBUG
                    log.debug(Messages.getString("SSLHandshakeProtocol.receivedUnsupportedClientCert")); //$NON-NLS-1$
                    // #endif

                    wantsClientAuth = true;
                    break;

                case SERVER_HELLO_DONE_MSG:

                    // #ifdef DEBUG
                    log.debug(Messages.getString("SSLHandshakeProtocol.helloDone")); //$NON-NLS-1$
                    // #endif

                    if (currentHandshakeStep != CERTIFICATE_MSG) {
                        throw new SSLException(SSLException.PROTOCOL_VIOLATION,
                            MessageFormat.format(Messages.getString("SSLHandshakeProtocol.unexpectedServerHelloDone"), new Object[] { new Integer(currentHandshakeStep) })); //$NON-NLS-1$
                    }

                    if (wantsClientAuth) {

                        // #ifdef DEBUG
                        log.debug(Messages.getString("SSLHandshakeProtocol.sendingNoCert")); //$NON-NLS-1$
                        // #endif
                        socket.sendMessage(SSLTransportImpl.ALERT_PROTOCOL, new byte[] { (byte) SSLTransportImpl.WARNING_ALERT,
                            (byte) SSLException.NO_CERTIFICATE });
                    }
                    onServerHelloDoneMsg();
                    break;

                case FINISHED_MSG:

                    // #ifdef DEBUG
                    log.debug(Messages.getString("SSLHandshakeProtocol.receivedServerFinished")); //$NON-NLS-1$
                    // #endif

                    if (currentHandshakeStep != FINISHED_MSG) {
                        throw new SSLException(SSLException.PROTOCOL_VIOLATION);
                    }

                    currentHandshakeStep = HANDSHAKE_PENDING_OR_COMPLETE;

                    break;

                default:

            }
        }
    }
    
    public X509Certificate getCertificate() {
    	return x509;
    }

    private void sendMessage(int type, byte[] data) throws SSLException {

        ByteArrayOutputStream msg = new ByteArrayOutputStream();

        try {
            msg.write(type);
            msg.write((data.length >> 16) & 0xFF);
            msg.write((data.length >> 8) & 0xFF);
            msg.write(data.length);
            msg.write(data);
        } catch (IOException ex) {
            throw new SSLException(SSLException.INTERNAL_ERROR, ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
        }

        byte[] m = msg.toByteArray();

        // Update the handshake hashes if its anything but the FINISHED_MSG
        if (type != FINISHED_MSG) {
            updateHandshakeHashes(m);

        }
        socket.sendMessage(HANDSHAKE_PROTOCOL_MSG, m);
    }

    public void startHandshake() throws SSLException {

        if (currentHandshakeStep != HANDSHAKE_PENDING_OR_COMPLETE) {
            throw new SSLException(SSLException.PROTOCOL_VIOLATION, Messages.getString("SSLHandshakeProtocol.alreadyInProgress")); //$NON-NLS-1$
        }

        // #ifdef DEBUG
        log.debug(Messages.getString("SSLHandshakeProtocol.starting")); //$NON-NLS-1$
        // #endif

        sendClientHello();
    }

    private void calculateMasterSecret() throws SSLException {

        // #ifdef DEBUG
        log.debug(Messages.getString("SSLHandshakeProtocol.calculatingMasterSecret")); //$NON-NLS-1$
        // #endif

        try {
            MD5Digest md5 = new MD5Digest();
            SHA1Digest sha1 = new SHA1Digest();

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            String[] mixers = new String[] { "A", "BB", "CCC" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            for (int i = 0; i < mixers.length; i++) {
                md5.reset();
                sha1.reset();
                sha1.update(mixers[i].getBytes(), 0, mixers[i].getBytes().length);
                sha1.update(premasterSecret, 0, premasterSecret.length);
                sha1.update(clientRandom, 0, clientRandom.length);
                sha1.update(serverRandom, 0, serverRandom.length);

                md5.update(premasterSecret, 0, premasterSecret.length);
                byte[] tmp = new byte[sha1.getDigestSize()];
                sha1.doFinal(tmp, 0);

                md5.update(tmp, 0, tmp.length);

                tmp = new byte[md5.getDigestSize()];
                md5.doFinal(tmp, 0);

                out.write(tmp);

            }

            masterSecret = out.toByteArray();
        } catch (IOException ex) {
            throw new SSLException(SSLException.INTERNAL_ERROR, ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
        }

    }

    private void calculatePreMasterSecret() {

        // #ifdef DEBUG
        log.debug(Messages.getString("SSLHandshakeProtocol.generatingPreMasterSecret")); //$NON-NLS-1$
        // #endif

        premasterSecret = new byte[48];
        context.getRND().nextBytes(premasterSecret);
        premasterSecret[0] = (byte) SSLTransportImpl.VERSION_MAJOR;
        premasterSecret[1] = (byte) SSLTransportImpl.VERSION_MINOR;

    }

    private void debugBytes(byte[] b, String s) {

        System.out.print(s + ": "); //$NON-NLS-1$

        for (int i = 0; i < b.length; i++) {

            System.out.print(Integer.toHexString((int) (b[i] & 0xFF)));

        }

        System.out.println();

    }

    private void onServerHelloDoneMsg() throws SSLException {

        // Generate the premaster secret
        calculatePreMasterSecret();

        byte[] secret = null;

        try {

            // Encrypt the premaster secret
            BigInteger input = new BigInteger(1, premasterSecret);

            PublicKey key = x509.getPublicKey();

            if (key instanceof RsaPublicKey) {

                BigInteger padded = Rsa.padPKCS1(input, 0x02, 128);
                BigInteger s = Rsa.doPublic(padded, ((RsaPublicKey) key).getModulus(), ((RsaPublicKey) key).getPublicExponent());

                secret = s.toByteArray();
            } else {
                throw new SSLException(SSLException.UNSUPPORTED_CERTIFICATE);
            }
        } catch (CertificateException ex) {
            throw new SSLException(SSLException.UNSUPPORTED_CERTIFICATE, ex.getMessage());
        } 

        if (secret[0] == 0) {
            byte[] tmp = new byte[secret.length - 1];
            System.arraycopy(secret, 1, tmp, 0, secret.length - 1);
            secret = tmp;
        }

        sendMessage(CLIENT_KEY_EXCHANGE_MSG, secret);

        // Calculate the master secret
        calculateMasterSecret();

        // #ifdef DEBUG
        log.debug(Messages.getString("SSLHandshakeProtocol.generatingKeyData")); //$NON-NLS-1$
        // #endif

        // Generate the keys etc and put the cipher into use
        byte[] keydata;
        int length = 0;

        length += pendingCipherSuite.getKeyLength() * 2;
        length += pendingCipherSuite.getMACLength() * 2;
        length += pendingCipherSuite.getIVLength() * 2;

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        MD5Digest md5 = new MD5Digest();
        SHA1Digest sha1 = new SHA1Digest();

        int turn = 0;
        while (out.size() < length) {
            md5.reset();
            sha1.reset();

            for (int i = 0; i <= turn; i++) {
                sha1.update((byte) ('A' + turn));
            }

            sha1.update(masterSecret, 0, masterSecret.length);
            sha1.update(serverRandom, 0, serverRandom.length);
            sha1.update(clientRandom, 0, clientRandom.length);

            md5.update(masterSecret, 0, masterSecret.length);
            byte[] tmp = new byte[sha1.getDigestSize()];
            sha1.doFinal(tmp, 0);
            md5.update(tmp, 0, tmp.length);
            tmp = new byte[md5.getDigestSize()];
            md5.doFinal(tmp, 0);

            // Write out a block of key data
            out.write(tmp, 0, tmp.length);

            turn++;
        }

        keydata = out.toByteArray();

        ByteArrayInputStream in = new ByteArrayInputStream(keydata);

        byte[] encryptKey = new byte[pendingCipherSuite.getKeyLength()];
        byte[] encryptIV = new byte[pendingCipherSuite.getIVLength()];
        byte[] encryptMAC = new byte[pendingCipherSuite.getMACLength()];
        byte[] decryptKey = new byte[pendingCipherSuite.getKeyLength()];
        byte[] decryptIV = new byte[pendingCipherSuite.getIVLength()];
        byte[] decryptMAC = new byte[pendingCipherSuite.getMACLength()];

        try {
            in.read(encryptMAC);
            in.read(decryptMAC);
            in.read(encryptKey);
            in.read(decryptKey);
            in.read(encryptIV);
            in.read(decryptIV);
        } catch (IOException ex) {
            throw new SSLException(SSLException.INTERNAL_ERROR, ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());

        }

        pendingCipherSuite.init(encryptKey, encryptIV, encryptMAC, decryptKey, decryptIV, decryptMAC);

        currentHandshakeStep = SERVER_HELLO_DONE_MSG;

        // Send the change cipher spec
        socket.sendCipherChangeSpec(pendingCipherSuite);

        // Send the finished msg
        sendHandshakeFinished();

    }

    SSLCipherSuite getPendingCipherSuite() {
        return pendingCipherSuite;
    }

    void updateHandshakeHashes(byte[] data) {

        // #ifdef DEBUG
        log.debug(Messages.getString("SSLHandshakeProtocol.updatingHandshakeHashes")); //$NON-NLS-1$
        // #endif

        handshakeMD5.update(data, 0, data.length);
        handshakeSHA1.update(data, 0, data.length);
    }

    private void completeHandshakeHashes() {

        // #ifdef DEBUG
        log.debug(Messages.getString("SSLHandshakeProtocol.completingHandshakeHashes")); //$NON-NLS-1$
        // #endif

        // Complete the handshale hashes
        handshakeMD5.update((byte) 0x43);
        handshakeMD5.update((byte) 0x4c);
        handshakeMD5.update((byte) 0x4e);
        handshakeMD5.update((byte) 0x54);

        handshakeMD5.update(masterSecret, 0, masterSecret.length);

        for (int i = 0; i < 48; i++) {
            handshakeMD5.update((byte) 0x36);
        }

        byte[] tmp = new byte[handshakeMD5.getDigestSize()];
        handshakeMD5.doFinal(tmp, 0);

        handshakeMD5.reset();

        // #ifdef DEBUG
        log.debug(MessageFormat.format(Messages.getString("SSLHandshakeProtocol.masterSecret"), new Object[] { new Long(masterSecret.length), String.valueOf(masterSecret[0]) })); //$NON-NLS-1$
        // #endif

        handshakeMD5.update(masterSecret, 0, masterSecret.length);
        for (int i = 0; i < 48; i++) {
            handshakeMD5.update((byte) 0x5c);
        }

        handshakeMD5.update(tmp, 0, tmp.length);

        handshakeSHA1.update((byte) 0x43);
        handshakeSHA1.update((byte) 0x4c);
        handshakeSHA1.update((byte) 0x4e);
        handshakeSHA1.update((byte) 0x54);

        handshakeSHA1.update(masterSecret, 0, masterSecret.length);
        for (int i = 0; i < 40; i++) {
            handshakeSHA1.update((byte) 0x36);
        }

        tmp = new byte[handshakeSHA1.getDigestSize()];
        handshakeSHA1.doFinal(tmp, 0);

        handshakeSHA1.reset();
        handshakeSHA1.update(masterSecret, 0, masterSecret.length);
        for (int i = 0; i < 40; i++) {
            handshakeSHA1.update((byte) 0x5c);
        }

        handshakeSHA1.update(tmp, 0, tmp.length);

    }

    private void sendHandshakeFinished() throws SSLException {

        completeHandshakeHashes();

        // #ifdef DEBUG
        log.debug("Sending client FINISHED"); //$NON-NLS-1$
        // #endif

        byte[] msg = new byte[handshakeMD5.getDigestSize() + handshakeSHA1.getDigestSize()];

        handshakeMD5.doFinal(msg, 0);
        handshakeSHA1.doFinal(msg, handshakeMD5.getDigestSize());

        sendMessage(FINISHED_MSG, msg);

        currentHandshakeStep = FINISHED_MSG;

    }

    private void onCertificateMsg(byte[] msg) throws SSLException {

        ByteArrayInputStream in = new ByteArrayInputStream(msg);

        // Get the length of the certificate chain
        int length2 = (in.read() & 0xFF) << 16 | (in.read() & 0xFF) << 8 | (in.read() & 0xFF);

        try {

            boolean trusted = false;

            X509Certificate chainCert;
            while (in.available() > 0 && !trusted) {
                // The length of the next certificate (we dont need this as rthe
                // DERInputStream does the work
                int certlen = (in.read() & 0xFF) << 16 | (in.read() & 0xFF) << 8 | (in.read() & 0xFF);

                // Now read the certificate
                DERInputStream der = new DERInputStream(in);

                ASN1Sequence certificate = (ASN1Sequence) der.readObject();

                // Get the x509 certificate structure
                chainCert = new X509Certificate(X509CertificateStructure.getInstance(certificate));

                if (x509 == null)
                    x509 = chainCert;

                // Verify if this part of the chain is trusted
                try {
                    trusted = context.getTrustedCACerts().isTrustedCertificate(chainCert,
                        context.isInvalidCertificateAllowed(),
                        context.isUntrustedCertificateAllowed());
                } catch (SSLException ex1) {
                    // #ifdef DEBUG
                    log.warn(Messages.getString("SSLHandshakeProtocol.failedToVerifyCertAgainstTruststore"), ex1); //$NON-NLS-1$
                    // #endif
                }
            }

            if (!trusted)
                throw new SSLException(SSLException.BAD_CERTIFICATE,
                    Messages.getString("SSLHandshakeProtocol.certInvalidOrUntrusted")); //$NON-NLS-1$

        } catch (IOException ex) {
            throw new SSLException(SSLException.INTERNAL_ERROR, ex.getMessage());
        }

        // #ifdef DEBUG
        log.debug(Messages.getString("SSLHandshakeProtocol.x509Cert")); //$NON-NLS-1$

        log.debug(Messages.getString("SSLHandshakeProtocol.x509Cert.subject") + x509.getSubjectDN()); //$NON-NLS-1$

        log.debug(Messages.getString("SSLHandshakeProtocol.x509Cert.issuer") + x509.getIssuerDN()); //$NON-NLS-1$
        // #endif

        currentHandshakeStep = CERTIFICATE_MSG;

    }

    private void onServerHelloMsg(byte[] msg) throws SSLException {

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(msg);

            majorVersion = in.read();
            minorVersion = in.read();

            serverRandom = new byte[32];
            in.read(serverRandom);

            sessionID = new byte[(in.read() & 0xFF)];
            in.read(sessionID);

            cipherSuiteID = new SSLCipherSuiteID(in.read(), in.read());

            pendingCipherSuite = (SSLCipherSuite) context.getCipherSuiteClass(cipherSuiteID).newInstance();

            compressionID = in.read();

            currentHandshakeStep = SERVER_HELLO_MSG;
        } catch (IllegalAccessException ex) {
            throw new SSLException(SSLException.INTERNAL_ERROR, ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());

        } catch (InstantiationException ex) {
            throw new SSLException(SSLException.INTERNAL_ERROR, ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
        } catch (IOException ex) {
            throw new SSLException(SSLException.INTERNAL_ERROR, ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
        }

    }

    private void sendClientHello() throws SSLException {

        // #ifdef DEBUG
        log.debug(Messages.getString("SSLHandshakeProtocol.sendingClientHello")); //$NON-NLS-1$
        // #endif

        ByteArrayOutputStream msg = new ByteArrayOutputStream();

        try {
            clientRandom = new byte[32];
            context.getRND().nextBytes(clientRandom);
            long time = System.currentTimeMillis();
            clientRandom[0] = (byte) ((time >> 24) & 0xFF);
            clientRandom[1] = (byte) ((time >> 16) & 0xFF);
            clientRandom[2] = (byte) ((time >> 8) & 0xFF);
            clientRandom[3] = (byte) (time & 0xFF);

            // Write the version
            msg.write(SSLTransportImpl.VERSION_MAJOR);
            msg.write(SSLTransportImpl.VERSION_MINOR);

            // Write the random bytes
            msg.write(clientRandom);

            // Write the session identifier - currently were not caching so zero
            // length
            msg.write(0);

            // Write the cipher ids - TODO: we need to set the preferred as
            // first
            SSLCipherSuiteID[] ids = context.getCipherSuiteIDs();
            msg.write(0);
            msg.write(ids.length * 2);

            for (int i = 0; i < ids.length; i++) {
                msg.write(ids[i].id1);
                msg.write(ids[i].id2);
            }

            // Compression - no compression is currently supported
            msg.write(1);
            msg.write(0);
        } catch (IOException ex) {
            throw new SSLException(SSLException.INTERNAL_ERROR, ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
        }

        sendMessage(CLIENT_HELLO_MSG, msg.toByteArray());

        currentHandshakeStep = CLIENT_HELLO_MSG;
    }
}
