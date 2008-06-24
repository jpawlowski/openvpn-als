package com.adito.policyframework.forms;

import com.adito.core.forms.CoreForm;
import com.adito.policyframework.Principal;

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