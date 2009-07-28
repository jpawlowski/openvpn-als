
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
			
package com.adito.clientcerts;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import com.adito.core.UserDatabaseManager;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionException;
import com.adito.extensions.types.DefaultPlugin;
import com.adito.extensions.types.PluginDefinition;
import com.adito.security.UserDatabaseDefinition;
import com.adito.boot.ContextHolder;
import com.adito.boot.Context;
import com.adito.boot.KeyStoreManager;
import com.adito.navigation.MenuTree;
import com.adito.navigation.NavigationManager;
import com.adito.table.TableItemActionMenuTree;
import com.adito.clientcerts.itemactions.CreateCertAction;

public class ClientCertsPlugin extends DefaultPlugin {
	private static final Log LOG = LogFactory.getLog(ClientCertsPlugin.class);
	public static final String KEYSTORE_NAME = "clientcerts";
	public static final String KEYSTORE_PASSWORD = "aditoclientcerts";
	private static ClientCertsPlugin instance = null;
	private File keyStoreFile = null;
	private KeyStore clientKS = null;
	private char[] ClientKeystorePassword="testtest".toCharArray();
	private ClientCertTrustManager clientCertTrustManager = null;

	public ClientCertTrustManager getClientCertTrustManager() {
		return clientCertTrustManager;
	}

	public static ClientCertsPlugin getInstance() {
		return instance;
	}

	public KeyStore getClientKeyStore() {
		return clientKS;
	}

	public File getKeyStoreFile() {
		return keyStoreFile;
	}

	/**
	 * Constructor.
	 */
	public ClientCertsPlugin() {
		super("/WEB-INF/clientcerts-tiles-defs.xml", false);
		instance = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.extensions.types.Plugin#startPlugin(com.adito.extensions.types.PluginDefinition,
	 *      com.adito.extensions.ExtensionDescriptor, org.jdom.Element)
	 */
	public void startPlugin(PluginDefinition pluginDefinition, ExtensionDescriptor descriptor, Element element) throws ExtensionException {
		super.startPlugin(pluginDefinition, descriptor, element);
		LOG.info("ClientCert plugin starting");

		/*
		// Initialise Keystore
		this.keyStoreFile = new File(ContextHolder.getContext().getConfDirectory(), "clientCerts.jks");
		
		//get an KeyStore object of type JKS (default type)
		try{
			clientKS=KeyStore.getInstance("JKS");
		}catch(java.security.KeyStoreException e)
		{System.out.println("1: "+e.getMessage());}

		//loading SSLCert keystore
		try{
			clientKS.load(new FileInputStream(keyStoreFile),ClientKeystorePassword);
		}catch(java.io.IOException e)
		{System.out.println("2: "+e.getMessage());
		}catch(java.security.NoSuchAlgorithmException e)
		{System.out.println("3: "+e.getMessage());
		}catch(java.security.cert.CertificateException e)
		{System.out.println("4: "+e.getMessage());}
		*/


		// TODO: use the keystoremanager
    		// public static void registerKeyStore(String name, String bundle, boolean removeable, String storePassword, KeyStoreType type) {
    		KeyStoreManager.registerKeyStore(KEYSTORE_NAME, "clientCerts", true, KEYSTORE_PASSWORD, KeyStoreManager.TYPE_JKS);
		KeyStoreManager km = KeyStoreManager.getInstance(KEYSTORE_NAME);
		clientKS = km.getKeyStore();

		
		// add the TrustManager to the SSL Listener
		clientCertTrustManager = new ClientCertTrustManager(clientKS);
		Context main = ContextHolder.getContext();
		main.setTrustManager((TrustManager)clientCertTrustManager, false);
		LOG.info("ClientCert plugin added TestTrustManager");
		ClientCertRequestHandler clientCertHandler = new ClientCertRequestHandler();
		main.registerRequestHandler(clientCertHandler);
		LOG.info("ClientCert plugin registered ClientCertHandler");
	}

	public void activatePlugin() throws ExtensionException {
		super.activatePlugin();
		try {
			initTableItemActions();
			// CoreUtil.updateEventsTable(TunnelPlugin.MESSAGE_RESOURCES_KEY, TunnelsEventConstants.class);
		} catch (Exception e) {
			throw new ExtensionException(ExtensionException.INTERNAL_ERROR, e, e.getLocalizedMessage());
		}
		LOG.info("ClientCert plugin activated");
	}
    
	private void initTableItemActions() throws Exception {
		MenuTree tree = NavigationManager.getMenuTree(TableItemActionMenuTree.MENU_TABLE_ITEM_ACTION_MENU_TREE);
		tree.addMenuItem("accounts", new CreateCertAction());
	}
}
