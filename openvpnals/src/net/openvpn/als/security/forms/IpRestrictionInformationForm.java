package net.openvpn.als.security.forms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.core.forms.CoreForm;

public class IpRestrictionInformationForm extends CoreForm {
    
    final static Log log = LogFactory.getLog(IpRestrictionInformationForm.class);
    
    private Boolean allowed;
    private String ip;
    
    public void initialise(String allowed, String ip) {
        this.allowed = new Boolean(allowed);
        this.ip = ip;
    }
    
    public Boolean getAllowed() {
        return allowed;
    }

    public void setAllowed(Boolean allowd) {
        this.allowed = allowd;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
    

}
