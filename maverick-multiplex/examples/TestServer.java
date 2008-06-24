/*
 * Created on 08-May-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
*/


import com.maverick.http.GetMethod;
import com.maverick.http.HttpClient;
import com.maverick.http.HttpResponse;
import com.maverick.http.PasswordCredentials;
import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelFactory;
import com.maverick.multiplex.MultiplexedConnection;
import com.maverick.multiplex.SocketChannel;

/**
 * @author lee
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestServer {


    public void processRequest(String requestName, byte[] requestData) {
		
    	if(requestName.equals("authorizationTicket")) {
    		System.out.println("Received authorization ticket " + new String(requestData));
    	}
	}

	MultiplexedConnection c;


    public TestServer() {

		
		
    	try {
        
    		System.setProperty("com.maverick.ssl.allowUntrustedCertificates", "true");
    		System.setProperty("com.maverick.ssl.allowInvalidCertificates", "true");
    		
    		HttpClient client = new HttpClient("127.0.0.1", 443, true);
    		GetMethod get = new GetMethod("AGENT", "/");
    		client.setCredentials(new PasswordCredentials("lee", "xxxxxxxxxx"));
    		client.setPreemtiveAuthentication(true);
    		
    		HttpResponse response = client.execute(get);
    		
    		c = new MultiplexedConnection(new SocketChannelFactory());

            c.startProtocol(response.getConnection().getInputStream(), response.getConnection().getOutputStream(), false);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public static void main(String[] args) {
        TestServer t = new TestServer();
    }

    class SocketChannelFactory implements ChannelFactory {

            public Channel createChannel(MultiplexedConnection connection, String type) {
            	
            	if(type.equals(SocketChannel.CHANNEL_TYPE))
                    return new SocketChannel();
            	else 
            		return null;
            }
    }
}
