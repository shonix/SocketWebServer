import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class Webserver {
	public Webserver(){
		ServerSocket socketListener;
		Socket userSocket = null;
		try {
			socketListener = new ServerSocket(7778);
			userSocket = socketListener.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
	if(userSocket != null){
		try {
			processGET(userSocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	public void processGET(Socket userSocket) throws IOException{
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
		String request = inFromClient.readLine();
		System.out.println("Fra socket: " + request);
		String[] requestParam = request.split(" ");
		String path = "C:\\Users\\peter_000\\Pictures\\Billeder til eksamen\\" + requestParam[1];
		System.out.println("We want file: " + path);
		PrintWriter out = new PrintWriter(userSocket.getOutputStream(),true);
		File file = new File(path);
		if(!file.exists()){
			System.out.println("shit: ");
			out.write("HTTP 404 shit \r\n\r\n"); //File b gon yo
		}
		
		inFromClient.close();
		out.close();
	}
}
