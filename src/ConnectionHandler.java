import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler {
			ServerSocket socketListener = new ServerSocket(5555);
			
			public ConnectionHandler()throws Exception{
			while (true){
				Socket userSocket = socketListener.accept();
				if(userSocket != null){
					Webserver ws = new Webserver(userSocket);
					ws.start();
				}
			}
			}
}