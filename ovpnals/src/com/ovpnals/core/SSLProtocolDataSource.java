package com.ovpnals.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.util.LabelValueBean;

import com.ovpnals.input.MultiSelectDataSource;
import com.ovpnals.security.SessionInfo;

public class SSLProtocolDataSource implements MultiSelectDataSource {

	private static ArrayList<LabelValueBean> list;
	
	static {
		list = new ArrayList<LabelValueBean>();
		list.add(new LabelValueBean("SSLv2Hello","SSLv2Hello"));
		list.add(new LabelValueBean("SSLv3","SSLv3"));
		list.add(new LabelValueBean("TLSv1","TLSv1"));
	}
	
	public List getValues(SessionInfo sessionInfo) {
		return list;
	}
}
