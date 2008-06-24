/*
 * Created on 08-May-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.net.Socket;

import com.maverick.multiplex.MultiplexedConnection;
import com.maverick.multiplex.SocketChannel;

/**
 * @author lee
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestClient {

	public static void main(String[] args) {

		try {
			

			
		Socket socket = new Socket("127.0.0.1", 10000);

		MultiplexedConnection c = new MultiplexedConnection(null);

		SocketChannel channel = new SocketChannel(socket, "cvs.3sp.co.uk", 22);

		c.openChannel(channel);

		channel.getOutputStream().write("SSH-2.0-foobar\r\n".getBytes());

		byte[] id = new byte[255];

		int read = channel.getInputStream().read(id);

		System.out.write(id, 0, read);

		} catch(Throwable t) {
			t.printStackTrace();
		}

	}
}
