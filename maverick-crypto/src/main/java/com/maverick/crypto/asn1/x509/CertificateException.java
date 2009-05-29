
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
			
package com.maverick.crypto.asn1.x509;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 */

public class CertificateException extends Exception {

  public static final int CERTIFICATE_EXPIRED = 1;
  public static final int CERTIFICATE_NOT_YET_VALID = 2;
  public static final int CERTIFICATE_ENCODING_ERROR = 3;
  public static final int CERTIFICATE_GENERAL_ERROR = 4;
  public static final int CERTIFICATE_UNSUPPORTED_ALGORITHM = 5;

  int status;

  public CertificateException(int status, String msg) {
    super(msg);
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

}