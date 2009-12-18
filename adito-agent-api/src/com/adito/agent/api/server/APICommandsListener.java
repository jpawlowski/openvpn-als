/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adito.agent.api.server;

import com.adito.agent.api.objects.TunnelList;

/**
 *
 * @author Matthias Jansen / Jansen-Systems
 */
public interface APICommandsListener {

    public boolean shutdown();

    public boolean startTunnel(int id);

    public boolean stopTunnel(int id);

    public TunnelList getTunnelList();

}
