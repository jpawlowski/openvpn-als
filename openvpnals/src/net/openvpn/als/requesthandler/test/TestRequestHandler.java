package net.openvpn.als.requesthandler.test;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.HttpConstants;
import net.openvpn.als.boot.RequestHandler;
import net.openvpn.als.boot.RequestHandlerException;
import net.openvpn.als.boot.RequestHandlerRequest;
import net.openvpn.als.boot.RequestHandlerResponse;

public class TestRequestHandler implements RequestHandler {

    public static Log log = LogFactory.getLog(TestRequestHandler.class);
    
    public TestRequestHandler() {
        super();
    }

    public boolean handle(String pathInContext, String pathParams, RequestHandlerRequest request, RequestHandlerResponse response)
                    throws IOException, RequestHandlerException {
        
        
	        if(request.getMethod().equals("TEST")) {
	            
	            if(log.isDebugEnabled())
	                log.debug("Starting TEST request");
	            
	            
            request.setTunnel(new TestFullDuplexTunnel());     
            
            // Set the response status
            response.setStatus(HttpConstants.RESP_200_OK);

            return true;
        } else
            return false;
        
    }

}
