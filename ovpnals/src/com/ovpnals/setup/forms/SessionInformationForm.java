package com.ovpnals.setup.forms;

import com.ovpnals.core.forms.CoreForm;
import com.ovpnals.security.SessionInfo;

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
