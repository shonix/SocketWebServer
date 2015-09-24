import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class Webserver {
	//Fields

	public Webserver()throws Exception{
		//initialisere socket listener, og socket for users
		ServerSocket socketListener;
		Socket userSocket = null;
		boolean running = true;
		
		//Sætter listenerern til at lytte på port 5555
		socketListener = new ServerSocket(5557);
		
		while (running){
			//opretter user socket, ud fra accepteret socket listener
			//kalder processGET med userSocket
			userSocket = socketListener.accept();
			if(userSocket != null){
				processGET(userSocket);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void processGET(Socket userSocket) throws IOException{
		byte[] msgHandle;
		String msg;
		//Læser client input, altså input fra userSocket (det man skriver i adresse bar)
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
		String request = inFromClient.readLine();
		System.out.println("Fra socket: " + request);
		
		//opretter et string array hvori fil navn placeres, og sendes ind i en string (path)
		//Splitter string der fås fra inFromClient op ved mellemrum -> GET | path | Protocol
		String[] requestParam = request.split(" ");
		if(!requestParam[0].equals("GET")){
			userSocket.close();
		}
		if(requestParam[1].equals("/")){
			requestParam[1] = "/index.html";
		}
		String path = "F:\\Brugere\\Shonix\\Pictures\\Hjemmeside" + requestParam[1];
		System.out.println("We want file: " + path);
				
		//opretter ny fil, med path der blev modtaget fra client
		File file = new File(path);
		//Finder fil extension ud fra filens path, til brug ved HEADER
	    
		OutputStream userOutput = userSocket.getOutputStream();
		if(!file.exists()){
			msg = "HTTP/1.1 404 FileNotFound";
			msgHandle = msg.getBytes();
			userOutput.write(msgHandle);
			userSocket.close();
			return;
		}
		Path path2 = FileSystems.getDefault().getPath(path);
		StringBuilder header = new StringBuilder();
		addHeader(header, "HTTP/1.1 200 OK");
		addHeader(header, "Connection close");
		addHeader(header, "Content-Type: " + Files.probeContentType(path2));
		addHeader(header, "File size: " +file.length());
		addHeader(header,"");
		String strHead = header.toString();
		int transferedBytes = 0;
		byte[] byteBuf = new byte[1024]; //sætter filen i kø, for ikke at skulle sende hele fil på samme tid, sender den lidt ad gangen.. therefore buffer....
		while(transferedBytes < strHead.length()){ //Så længe al data fra strHead ikke er transfered, skal loop køre
			strHead.getBytes(transferedBytes, Math.min(byteBuf.length,strHead.length()-transferedBytes), byteBuf, 0); //Starter på transfered bytes pointer (0 til start da intet flyttet) flytter 1024 bytes ad gangen, flytter til byte buffer, fra index 0.
			userOutput.write(byteBuf,0,Math.min(strHead.length()-transferedBytes,byteBuf.length)); 	//vælger data der skal skrives til client socket. Skriver al data
			transferedBytes+=byteBuf.length;														//ud fra index 0 til, hvad enten der er mindst ad bytebufferen, eller Headeres resterende størrelse
		}
		
		InputStream outToClient = new FileInputStream(file);
		while((transferedBytes = outToClient.read(byteBuf))!= -1){ 	//Sender bytes til user socket, så der er noget i byteBuf at læse, så snart bufferen er færdig, stopper while loopet
			userOutput.write(byteBuf,0, transferedBytes);
		}
		//Lukker streams
		inFromClient.close();
		outToClient.close();
		//lukker socket
		userSocket.close();
	}
	
	//Bruges til at bygge Header op for return
	public void addHeader(StringBuilder header, String append){
		header.append(append);
		header.append("\r\n");
	}
}
