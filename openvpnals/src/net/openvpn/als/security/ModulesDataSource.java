package net.openvpn.als.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.struts.util.LabelValueBean;
import org.apache.struts.util.MessageResources;

import net.openvpn.als.boot.SystemProperties;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.input.MultiSelectDataSource;

public class ModulesDataSource implements MultiSelectDataSource {

    String key;

    public ModulesDataSource(String key) {
        this.key = key;
    }

    public Collection<LabelValueBean> getValues(SessionInfo session) {
        List l = new ArrayList();
        for (Iterator i = AuthenticationModuleManager.getInstance().authenticationModuleDefinitions(); i.hasNext();) {
            AuthenticationModuleDefinition def = (AuthenticationModuleDefinition) i.next();
            /*
             * NOTE As from 0.1.12, HTTP Basic Authentication is still
             * registered (it is needed by WebDAV and Web Forwards) but it is
             * not a valid module to use for authentication.
             */
            if (!def.getSystem()
                            && !def.getName().equals(HTTPAuthenticationModule.MODULE_NAME)
                            || (def.getName().equals(HTTPAuthenticationModule.MODULE_NAME) && "true".equals(SystemProperties.get(
                                            "openvpnals.httpBasicAuthenticationModule.enabled", "false")))) {
                StringBuffer buf = new StringBuffer();
                if (def.getPrimary()) {
                    buf.append(" (");
                    buf.append(CoreUtil.getMessageResources(session.getHttpSession(), key).getMessage("authenticationModule.primary"));
                } else if(def.getPrimaryIfSecondardExists()) {
                    buf.append(" (");
                    buf.append(CoreUtil.getMessageResources(session.getHttpSession(), key).getMessage("authenticationModule.primaryIfSecondaryExists"));
                } else if (def.getSecondary()) {
                    buf.append(" (");
                    buf.append(CoreUtil.getMessageResources(session.getHttpSession(), key).getMessage("authenticationModule.secondary"));
                }
                
                if (buf.length() != 0) {
                    buf.append(")");
                }
                MessageResources mr = CoreUtil.getMessageResources(session.getHttpSession(), def.getMessageResourcesKey());
                l
                                .add(new LabelValueBean(
                                                mr == null ? ("!unknown bundle " + def.getMessageResourcesKey() + " in module definition")
                                                                : mr.getMessage("authenticationModule." + def.getName() + ".name")
                                                                                + buf.toString(), def.getName()));
            }
        }
        return l;
    }

}
