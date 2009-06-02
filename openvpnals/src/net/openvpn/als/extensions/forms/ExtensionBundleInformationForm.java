package net.openvpn.als.extensions.forms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.core.forms.CoreForm;
import net.openvpn.als.extensions.ExtensionBundle;

public class ExtensionBundleInformationForm extends CoreForm {
    final static Log log = LogFactory.getLog(ExtensionBundleInformationForm.class);
    
    private ExtensionBundle bundle;
    
    public void initialise(ExtensionBundle bundle) {
    	this.bundle = bundle;
    }
    
    public ExtensionBundle getBundle() {
    	return bundle;
    }
}
