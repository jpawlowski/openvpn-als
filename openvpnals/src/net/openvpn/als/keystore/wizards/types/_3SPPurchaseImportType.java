package net.openvpn.als.keystore.wizards.types;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import net.openvpn.als.boot.ContextKey;
import net.openvpn.als.boot.KeyStoreManager;
import net.openvpn.als.boot.Util;
import net.openvpn.als.core.CoreAttributeConstants;
import net.openvpn.als.core.CoreEvent;
import net.openvpn.als.core.CoreEventConstants;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.keystore.wizards.AbstractKeyStoreImportType;
import net.openvpn.als.properties.Property;
import net.openvpn.als.properties.impl.systemconfig.SystemConfigKey;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.util.ZipExtract;
import net.openvpn.als.wizard.AbstractWizardSequence;

/**
 * Implementation of {@link net.openvpn.als.keystore.wizards.AbstractKeyStoreImportType}
 *  for the _3SPPurchaseImportType.
 */
public class _3SPPurchaseImportType extends AbstractKeyStoreImportType {

    /**
     * Constant for importing a reply from a CA 
     */
    public final static String _3SP_PURCHASE = "3spPurchase";
    
	/**
	 * Constructor
	 */
	public _3SPPurchaseImportType() {
		super(_3SP_PURCHASE, "keystore", false, false, 1);
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.keystore.wizards.AbstractKeyStoreImportType#doInstall(java.io.File, java.lang.String, java.lang.String, net.openvpn.als.wizard.AbstractWizardSequence)
	 */
	public void doInstall(File file, String alias, String passphrase,
			AbstractWizardSequence seq, SessionInfo sessionInfo) throws Exception {

		
        KeyStoreManager mgr = KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE);
        
        X509Certificate web = (X509Certificate) mgr.getCertificate(Property.getProperty(new ContextKey("webServer.alias")));

        final String actualCert = KeyStoreManager.getX509CertificateEntity(web, "cn").replaceAll("\\.", "_") + ".crt";        

        File parent = new File(file.getParentFile(), "comodo");
        
        Util.delTree(parent);
        parent.mkdirs();
        
        try {
            ZipExtract.extractZipFile(parent, new FileInputStream(file));
            
            String[] certs = parent.list(new FilenameFilter() {
            	public boolean accept(File file, String filename) {
            		return filename.endsWith(".crt") && (!filename.equals(actualCert) && !filename.equals(actualCert.replaceAll("\\*", "STAR")));
            	}
            });
            String pw = Property.getProperty(new ContextKey("webServer.keystore.sslCertificate.password"));
    
            for(int i=0;i<certs.length;i++) {
            	File tmp = new File(parent, certs[i]);
            	mgr.importCert(tmp.getName().toLowerCase(), tmp, pw);
            	
                Certificate certif = mgr.getCertificate(tmp.getName().toLowerCase());
    
                CoreEvent coreEvent = new CoreEvent(this, CoreEventConstants.KEYSTORE_CERTIFICATE_SIGNED_IMPORTED, Property.getProperty(new ContextKey("webServer.alias")), seq.getSession())
                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_ALIAS, tmp.getName())
                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_TYPE, certif.getType())
                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_HOSTNAME, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "cn"))
                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_ORGANISATIONAL_UNIT, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "ou"))
                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_COMPANY, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "o"))
                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_STATE, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "st"))
                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_LOCATION, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "l"))
                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_COUNTRY_CODE, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "c"));
                
                 CoreServlet.getServlet().fireCoreEvent(coreEvent);
    
            }
            
            File cert = new File(parent, actualCert);
            
            mgr.importCert(Property.getProperty(new ContextKey("webServer.alias")), cert, pw);
            mgr.reloadKeystore();
            Certificate certif = mgr.getCertificate(Property.getProperty(new ContextKey("webServer.alias")));
            
            CoreServlet.getServlet().fireCoreEvent(new CoreEvent(
                            this, CoreEventConstants.KEYSTORE_CERTIFICATE_SIGNED_IMPORTED, Property.getProperty(new ContextKey("webServer.alias")), seq.getSession())
                            .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_ALIAS, Property.getProperty(new ContextKey("webServer.alias")))
                            .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_TYPE, certif.getType())
                            .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_HOSTNAME, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "cn"))
                            .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_ORGANISATIONAL_UNIT, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "ou"))
                            .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_COMPANY, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "o"))
                            .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_STATE, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "st"))
                            .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_LOCATION, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "l"))
                            .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_COUNTRY_CODE, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "c")));
            
            Property.setProperty(new ContextKey("webServer.disableCertificateWarning"), true, sessionInfo);
            
        } finally {
            Util.delTree(parent);
        }
  	}
}