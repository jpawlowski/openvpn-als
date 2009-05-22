package com.ovpnals.replacementproxy;

import java.util.StringTokenizer;

import com.ovpnals.core.stringreplacement.SessionInfoReplacer;
import com.ovpnals.webforwards.AbstractAuthenticatingWebForward;
import com.ovpnals.webforwards.WebForwardTypes;

public class ReplacementProxyAuthenticationFormMethod extends ReplacementProxyHttpMethod {

    public ReplacementProxyAuthenticationFormMethod(RequestProcessor requestProcessor, AbstractAuthenticatingWebForward webForward) throws Exception {
        super(webForward.getFormType(), requestProcessor);
        if(webForward.getFormType().equals(WebForwardTypes.FORM_SUBMIT_POST)) {
            clearParameters();
        }
        StringTokenizer tokens = new StringTokenizer(webForward.getFormParameters(), "\n");
        int idx;
        String param;
        while (tokens.hasMoreTokens()) {
            param = SessionInfoReplacer.replace(requestProcessor.getLaunchSession().getSession(), tokens.nextToken().trim());
            idx = param.indexOf('=');
            if (idx > -1 && idx < param.length()-1) {
                addParameter(param.substring(0, idx), param.substring(idx + 1));
            } else
                addParameter(param, "");
        }
    }

}
