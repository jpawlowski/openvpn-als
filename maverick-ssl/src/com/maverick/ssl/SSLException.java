
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

import java.text.MessageFormat;

/**
 * An exception thrown by the API
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class SSLException extends Exception {

    /**
     * Status codes defined by the specification
     */
    public static final int CLOSE_NOTIFY = 0;
    public static final int UNEXPECTED_MESSAGE = 10;
    public static final int BAD_RECORD_MAC = 20;
    public static final int DECOMPRESSION_FAILURE = 30;
    public static final int HANDSHAKE_FAILURE = 40;
    public static final int NO_CERTIFICATE = 41;
    public static final int BAD_CERTIFICATE = 42;
    public static final int UNSUPPORTED_CERTIFICATE = 43;
    public static final int CERTIFICATE_REVOKED = 44;
    public static final int CERTIFICATE_EXPIRED = 45;
    public static final int CERTIFICATE_UNKNOWN = 46;

    /**
     * Maverick SSL status codes
     */
    public static final int PROTOCOL_VIOLATION = 999;
    public static final int UNSUPPORTED_OPERATION = 998;
    public static final int INTERNAL_ERROR = 997;
    public static final int UNEXPECTED_TERMINATION = 996;
    public static final int READ_TIMEOUT = 997;

    int status;

    public SSLException(int status) {
        super(getDescription(status));
        this.status = status;
    }

    public SSLException(int status, String msg) {
        super(msg);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static String getDescription(int status) {
        switch (status) {
            case CLOSE_NOTIFY:
                return Messages.getString("SSLException.remoteSideClosedConnection"); //$NON-NLS-1$
            case UNEXPECTED_MESSAGE:
                return Messages.getString("SSLException.unexpectedMessage"); //$NON-NLS-1$
            case BAD_RECORD_MAC:
                return Messages.getString("SSLException.badRecordMAC"); //$NON-NLS-1$
            case DECOMPRESSION_FAILURE:
                return Messages.getString("SSLException.decompressionFailure"); //$NON-NLS-1$
            case HANDSHAKE_FAILURE:
                return Messages.getString("SSLException.handshakeFailure"); //$NON-NLS-1$
            case NO_CERTIFICATE:
                return Messages.getString("SSLException.noCert"); //$NON-NLS-1$
            case BAD_CERTIFICATE:
                return Messages.getString("SSLException.badCert"); //$NON-NLS-1$
            case UNSUPPORTED_CERTIFICATE:
                return Messages.getString("SSLException.unsupportedCert"); //$NON-NLS-1$
            case CERTIFICATE_REVOKED:
                return Messages.getString("SSLException.certRevoked"); //$NON-NLS-1$
            case CERTIFICATE_EXPIRED:
                return Messages.getString("SSLException.certExpired"); //$NON-NLS-1$
            case CERTIFICATE_UNKNOWN:
                return Messages.getString("SSLException.certUnknown"); //$NON-NLS-1$
            case PROTOCOL_VIOLATION:
                return Messages.getString("SSLException.protocolViolation"); //$NON-NLS-1$
            case READ_TIMEOUT:
                return Messages.getString("SSLException.readTimeout"); //$NON-NLS-1$
            default:
                return MessageFormat.format(Messages.getString("SSLException.unexpectedStatusError"), new Object[] { new Integer(status) }); //$NON-NLS-1$

        }
    }

}
