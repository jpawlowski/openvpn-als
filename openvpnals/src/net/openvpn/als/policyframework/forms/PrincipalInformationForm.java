package net.openvpn.als.policyframework.forms;

import net.openvpn.als.core.forms.CoreForm;
import net.openvpn.als.policyframework.Principal;

/**
 */
public class PrincipalInformationForm extends CoreForm {
    private Principal principal;
    
    /**
     * @param principal
     */
    public void initialise(Principal principal) {
        this.principal = principal;
    }
    
    /**
     * @return Principal
     */ 
    public Principal getPrincipal() {
        return principal;
    }
}