/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adito.agent.api.server;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.Router;

/**
 *
 * @author Matthias Jansen / Jansen-Systems
 */
public class APIApplication extends Application {
    private static APIApplication instance = null;
    private JAXBContext jc = null;
    private Marshaller marshaller = null;
    private Unmarshaller unmarshaller = null;

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of HelloWorldResource.
        Router router = new Router(getContext());

        // Defines only one route
        // router.attachDefault(ShutdownResource.class);

        return router;
    }


    public static APIApplication getInstance() throws JAXBException {
	if (instance == null)
	    instance = new APIApplication();

	return instance;
    }

    private APIApplication() throws JAXBException  {
	jc = JAXBContext.newInstance("com.adito.agent.api.objects");
	marshaller = jc.createMarshaller();
	unmarshaller = jc.createUnmarshaller();
    }

    /**
     * @return the marshaller
     */
    public Marshaller getMarshaller() {
	return marshaller;
    }

    /**
     * @return the unmarshaller
     */
    public Unmarshaller getUnmarshaller() {
	return unmarshaller;
    }

}
