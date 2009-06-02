package net.openvpn.als.extensions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;

import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.core.CoreException;
import net.openvpn.als.core.stringreplacement.VariableReplacement;
import net.openvpn.als.extensions.actions.UndefinedParameterException;
import net.openvpn.als.extensions.store.ExtensionStore;
import net.openvpn.als.policyframework.LaunchSession;
import net.openvpn.als.security.SessionInfo;

/**
 * Utilities for processing extension descriptor XML for return to the agent
 * launcher or agent itself.
 */
public class ExtensionParser {

	/**
	 * Process an agent descriptor, replacing all replaceable content. No
	 * <i>Launch Session</i> is required for launching the agent.
	 * 
	 * @param des agent descriptor
	 * @param request request
	 * @param session session
	 * @param properties parameters
	 * @return processed descriptor XML
	 * @throws UndefinedParameterException
	 * @throws JDOMException
	 * @throws IOException
	 * @throws CoreException
	 */
	public static String processAgentParameters(ExtensionDescriptor des, HttpServletRequest request, SessionInfo session,
												Properties properties) throws UndefinedParameterException, JDOMException,
					IOException, CoreException {
		try {
			Element element = des.createProcessedDescriptorElement(session);
			VariableReplacement r = new VariableReplacement();
			r.setApplicationShortcut(des, null);
			r.setSession(session);
			r.setServletRequest(request);
			Map<String, String> parameterValues = new HashMap<String, String>();
			for(Iterator i = request.getParameterMap().keySet().iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				parameterValues.put(key, request.getParameter(key));
			}
			return getXML(element, parameterValues, r, des);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CoreException(0, "");
		}

	}

	/**
	 * Process an <i>Application Extension</i>'s descriptor , replacing all
	 * replaceable content. A <i>Launch Session</i> is required for launching
	 * any applications.
	 * 
	 * @param launchSession launch session
	 * @param properties parameters
	 * @param parameterValues parameter values
	 * @param applicationId
	 * @return processed descriptor XML
	 * @throws Exception 
	 * @throws Exception on any error
	 */
	public static String processApplicationParameters(LaunchSession launchSession, Properties properties, Map<String, String> parameterValues,
														String applicationId) throws Exception {
		ExtensionDescriptor des = ExtensionStore.getInstance().getExtensionDescriptor(applicationId);
		Element element = des.createProcessedDescriptorElement(launchSession.getSession());
		VariableReplacement r = new VariableReplacement();
		r.setApplicationShortcut(des, null);
		r.setLaunchSession(launchSession);
		// TODO different way of getting 'request' parameters when no servlet request is available
		return getXML(element, parameterValues, r, des);
	}

	static String getXML(Element element, Map<String, String> parameterValues, VariableReplacement replacer,
							ExtensionDescriptor des) throws UndefinedParameterException, IOException {

		List params = element.getChildren("parameter");

		for (Iterator it = params.iterator(); it.hasNext();) {
			Element p = (Element) it.next();

			String name = p.getAttribute("name").getValue();
			String value = (String) parameterValues.get(name);

			if (value == null) {
				value = des.getParameterDefinition(name).getDefaultValue();
				if (value == null || value.equals(PropertyDefinition.UNDEFINED_PARAMETER)) {
					throw new UndefinedParameterException("Parameter " + name + " is undefined");
				}
			}
			p.setAttribute("value", replacer.replace(value));
		}

		XMLOutputter output = new XMLOutputter();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		output.output(element, out);
		String xml = new String(out.toByteArray());

		params = element.getChildren("parameter");

		Map<String, String> parameters = new HashMap<String, String>();
		for (Iterator it = params.iterator(); it.hasNext();) {
			Element p = (Element) it.next();
			parameters.put(p.getAttributeValue("name"), p.getAttributeValue("value"));
		}
		replacer.setApplicationShortcut(des, parameters);
		return replacer.replace(xml);
	}
}
