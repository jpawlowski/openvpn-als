/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adito.agent.api.objects;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Matthias Jansen / Jansen-Systems
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "TunnelList")
public class TunnelList {
    private ArrayList<String> tunnels = new ArrayList<String>();

    /**
     * @return the tunnels
     */
    public ArrayList<String> getTunnels() {
	return tunnels;
    }

    /**
     * @param tunnels the tunnels to set
     */
    public void setTunnels(ArrayList<String> tunnels) {
	this.tunnels = tunnels;
    }
}
