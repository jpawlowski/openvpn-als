/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adito.agent.api.server.resources.restlets;

import com.adito.agent.api.objects.TunnelStart;
import com.adito.agent.api.server.APIApplication;
import com.adito.agent.api.server.APICommandsListener;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.XmlRepresentation;

/**
 *
 * @author Matthias jansen / Jansen-Systems
 */
public class TunnelStartRestlet extends BaseRestlet {

    public TunnelStartRestlet(APICommandsListener l) {
	super(l);
    }

    @Override
    public void handle(Request request, Response response) {
	int id = 0;
	boolean ok;
	try {
	    id = Integer.parseInt(request.getAttributes().get("id").toString());
	    ok = apiCommandListener.startTunnel(id);
	} catch (Exception e) {
	    // unknown id
	    e.printStackTrace();
	    ok = false;
	}

	final boolean fok = ok;

	Representation r = new XmlRepresentation(MediaType.TEXT_XML) {

	    @Override
	    public Object evaluate(String string, QName qname) throws Exception {
		// throw new UnsupportedOperationException("Not supported yet.");
		return null;
	    }

	    @Override
	    public void write(OutputStream out) throws IOException {
		// throw new UnsupportedOperationException("Not supported yet.");

		TunnelStart s = new TunnelStart();
		s.setSuccess(fok);
		try {
		    APIApplication.getInstance().getMarshaller().marshal(s, out);
		} catch (JAXBException ex) {
		    // Logger.getLogger(ShutdownResource.class.getName()).log(Level.SEVERE, null, ex);
		    ex.printStackTrace();
		}
	    }
	};
	response.setEntity(r);
    }


}
