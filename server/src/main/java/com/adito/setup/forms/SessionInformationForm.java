package com.adito.setup.forms;

import com.adito.core.forms.CoreForm;
import com.adito.security.SessionInfo;

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
