package com.ovpnals.policyframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.LabelValueBean;

import com.ovpnals.input.MultiSelectDataSource;
import com.ovpnals.security.SessionInfo;

/**
 */
public class PolicyDataSource implements MultiSelectDataSource {
	private static Log log = LogFactory.getLog(PolicyDataSource.class);

	public Collection<LabelValueBean> getValues(SessionInfo session) {
		
		ArrayList l = new ArrayList();
		try {
		
			Policy policy;
			for(Iterator it = PolicyDatabaseFactory.getInstance().getPolicies(session.getUser().getRealm()).iterator(); it.hasNext();) {
				policy = (Policy)it.next();
                l.add(new LabelValueBean(policy.getResourceName(), String.valueOf(policy.getResourceId())));
			}
		} catch(Exception ex) {
			log.error("Error obtaining policies for data source", ex);
		}
		
		return l;
	}

}
