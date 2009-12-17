/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adito.agent.api.objects;

import javax.xml.bind.annotation.XmlRegistry;

/**
 *
 * @author Matthias Jansen / Jansen-Systems
 */
@XmlRegistry
public class ObjectFactory {

    public Shutdown createShutdown() {
	return new Shutdown();
    }

    public TunnelList createTunnelList() {
	return new TunnelList();
    }
}
