/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adito.agent.api.server.resources;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 *
 * @author Matthias Jansen / Jansen-Systems
 */
public class ShutdownResource extends Resource {

    public ShutdownResource(Context context, Request request, Response response) {
	super(context, request, response);
	// getVariants().add(new Variant(MediaType.TEXT_XML));
	getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }
    
    @Override
    public Representation represent() throws ResourceException {

	Representation representation = new StringRepresentation(
                "hello, world", MediaType.TEXT_PLAIN);
        return representation;

	/* Representation r = new XmlRepresentation(MediaType.TEXT_XML) {

	    @Override
	    public Object evaluate(String string, QName qname) throws Exception {
		// throw new UnsupportedOperationException("Not supported yet.");
		System.out.println("String: "+string);
		System.out.println("qname: "+qname.toString());
		return null;
	    }

	    @Override
	    public void write(OutputStream out) throws IOException {
		// throw new UnsupportedOperationException("Not supported yet.");
		Shutdown s = new Shutdown();
		s.setSuccess(Boolean.TRUE);
		try {
		    APIApplication.getInstance().getMarshaller().marshal(s, out);
		} catch (JAXBException ex) {
		    // Logger.getLogger(ShutdownResource.class.getName()).log(Level.SEVERE, null, ex);
		    ex.printStackTrace();
		}
	    }
	};
	return r; */
    }
    
}
