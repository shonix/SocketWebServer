import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class Webserver {
	public Webserver(){
		ServerSocket socketListener;
		Socket userSocket = null;
		try {
			socketListener = new ServerSocket(5559);
			userSocket = socketListener.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	if(userSocket != null){
		try {
			processGET(userSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	}
	public void processGET(Socket userSocket) throws IOException{
		//Læser client input, altså input fra userSocket (det man skriver i adresse bar)
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
		String request = inFromClient.readLine();
		System.out.println("Fra socket: " + request);
		
		//opretter et string array hvori fil navn placeres, og sendes ind i en string (path)
		//Splitter string der fås fra inFromClient op ved mellemrum -> GET | path | Protocol
		String[] requestParam = request.split(" ");
		String path = "F:\\Brugere\\Shonix\\Pictures" + requestParam[1];
		System.out.println("We want file: " + path);
		
		//Printwriter, bruges til output til client
		PrintWriter out = new PrintWriter(userSocket.getOutputStream(),true);
		
		//opretter ny fil, med path der blev modtaget fra client
		File file = new File(path);
		InputStream outToClient = new FileInputStream(file);
		if(!file.exists()){
			System.out.println("shit: ");
			out.write("HTTP 404 shit \r\n\r\n");
		}
		int len = (int) file.length();
		byte[] byteBuf = new byte[8000];
		OutputStream fileOutput = userSocket.getOutputStream();
		while((len = outToClient.read(byteBuf))!= -1){
			fileOutput.write(byteBuf,0, len);
		}
		System.out.println(byteBuf);
		inFromClient.close();
		out.close();
	}
}
