/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adito.agent.api.server.resources.restlets;

import com.adito.agent.api.server.APICommandsListener;
import org.restlet.Restlet;

/**
 *
 * @author Matthias Jansen / Jansen-Systems
 */
public class BaseRestlet extends Restlet {
    protected APICommandsListener apiCommandListener = null;
    
    public BaseRestlet(APICommandsListener l) {
	this.apiCommandListener = l;
    }

}
