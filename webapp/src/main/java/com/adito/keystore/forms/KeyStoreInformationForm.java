package com.adito.keystore.forms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.core.forms.CoreForm;

public class KeyStoreInformationForm extends CoreForm {
    
    final static Log log = LogFactory.getLog(KeyStoreInformationForm.class);
    
    private String alias;
    private String type;
    
    public void initialise(String alias, String type) {
        this.alias = alias;
        this.type = type;
    }
    
    public String getAlias() {
        return alias;
    }

    public void setAlias(String ip) {
        this.alias = ip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
