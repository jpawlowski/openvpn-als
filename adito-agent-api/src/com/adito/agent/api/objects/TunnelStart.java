/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adito.agent.api.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Matthias Jansen / Jansen-Systems
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "TunnelStart")
public class TunnelStart {
    private Boolean success = null;

    /**
     * @return the success
     */
    public Boolean getSuccess() {
	return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(Boolean success) {
	this.success = success;
    }
}
