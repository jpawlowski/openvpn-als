package net.openvpn.als.setup.forms;

import net.openvpn.als.core.forms.CoreForm;
import net.openvpn.als.security.SessionInfo;

/**
 */
public class SessionInformationForm extends CoreForm {
    private SessionInfo session;
    
    /**
     * @param session
     */
    public void initialise(SessionInfo session) {
        this.session = session;
    }
    
    /**
     * @return SessionInfo
     */ 
    public SessionInfo getSession() {
        return session;
    }
}
