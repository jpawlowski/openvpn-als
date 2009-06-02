package com.adito.clientcerts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.File;
import java.security.KeyStore;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.net.ssl.X509TrustManager;
import java.security.*;
import java.security.cert.*;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;
import java.io.*;

public class ClientCertTrustManager implements X509TrustManager {
	private X509TrustManager X509TM=null;          //default X.509 TrustManager
	private TrustManagerFactory ClientTMF=null;    //SunX509 factory from SunJSSE provider
	private KeyStore ClientKS=null;                //keystore SSLCert - just an example
	private TrustManager[] ClientTMs=null;         //all the TrustManagers from SunX509 factory
	private static final Log logger = LogFactory.getLog(ClientCertTrustManager.class);



	public ClientCertTrustManager(KeyStore ks) {
		logger.info("ClientCertTrustManager: Constructor");
		//TrustManagerFactory of SunJSSE
		try{
			ClientTMF=TrustManagerFactory.getInstance("SunX509","SunJSSE");
		}catch(java.security.NoSuchAlgorithmException e)
		{System.out.println("5: "+e.getMessage());
		}catch(java.security.NoSuchProviderException e)
		{System.out.println("6: "+e.getMessage());}

		this.ClientKS = ks;


		//call init method for ClientTMF
		try{
			ClientTMF.init(ClientKS);
		}catch(java.security.KeyStoreException e)
		{System.out.println("7: "+e.getMessage());}

		//get all the TrustManagers
		ClientTMs=ClientTMF.getTrustManagers();

		//looking for a X509TrustManager instance
		for(int i=0;i<ClientTMs.length;i++)
		{
			if(ClientTMs[i] instanceof X509TrustManager)
			{
				System.out.println("X509TrustManager certificate found...");
				X509TM=(X509TrustManager)ClientTMs[i];
				return;
			}
		}

	}

	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		logger.info("ClientCertTrustManager: checkClientTrusted");
		try {
			X509Certificate x = chain[0];
			logger.info("Checking cert.getSubjectDN(): "+x.getSubjectDN().getName());
			logger.info("Checking cert.getIssuerDN(): "+x.getIssuerDN().getName());
		} catch (Exception e) {
		}
		try {
			X509TM.checkClientTrusted(chain, authType);
		} catch (Exception e) {
			logger.info("ClientCertTrustManager: cert seems to be wrong: "+e.getMessage());
			throw new CertificateException(e.getMessage());
		}
		logger.info("ClientTrustManager: cert seems to be accepted");
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		throw new CertificateException("Server certs are not trusted by the custom SSL trust manager.");
	}

	public X509Certificate[] getAcceptedIssuers() {
		logger.info("ClientTrustManager: getAcceptedIssuers");
		return X509TM.getAcceptedIssuers();
		// return null;
	}
}
