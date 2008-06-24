package com.maverick.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.maverick.crypto.asn1.x509.X509Certificate;

public interface SSLTransport {

	public abstract void initialize(InputStream in, OutputStream out)
			throws IOException, SSLException;

	public abstract void close() throws SSLException;

	public abstract InputStream getInputStream() throws IOException;

	public abstract OutputStream getOutputStream() throws IOException;

}