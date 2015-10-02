import java.io.BufferedReader;
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

	public void startServer()throws Exception{
		
		Runnable serverTask = new Runnable(){
			public void run(){
			try{
			//Sætter listenerern til at lytte på port 5555
			ServerSocket socketListener = new ServerSocket(5558);
			
			while (true){
				//opretter user socket, ud fra accepteret socket listener
				//kalder processGET med userSocket
				Socket userSocket = socketListener.accept();
				if(userSocket != null){
					processGET(userSocket);
				}
			}
	            } catch (IOException e) {
	                System.err.println("Unable to process client request");
	                e.printStackTrace();
	            }
			}
		};
		Thread serverThread = new Thread(serverTask);
		serverThread.start();
	}
	
	@SuppressWarnings("deprecation")
	public void processGET(Socket userSocket) throws IOException{
		int transferedBytes = 0;
		byte[] byteBuf = new byte[1024]; 
		//sætter filen i kø, for ikke at skulle sende hele fil på samme tid, sender den lidt ad gangen.. therefore buffer....
		//Læser client input, altså input fra userSocket (det man skriver i adresse bar)
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
		OutputStream userOutput = userSocket.getOutputStream();
		String request = inFromClient.readLine();
		System.out.println("Fra socket: " + request);
		if(request.length() < 1 ){
			return;
		}
		//opretter et string array hvori fil navn placeres, og sendes ind i en string (path)
		//Splitter string der fås fra inFromClient op ved mellemrum -> GET | path | Protocol
		String[] requestParam = request.split(" ");
		if(!requestParam[0].equals("GET")){
			userSocket.close();
			return;
		}
		if(requestParam[1].equals("/")){
			requestParam[1] = "/index.html";
		}
		
		String path = "C:\\Users\\peter_000\\Pictures\\Hjemmeside" + requestParam[1];
		System.out.println("We want file: " + path);
				
		//opretter ny fil, med path der blev modtaget fra client
		File file = new File(path);
		Path filePath = FileSystems.getDefault().getPath(path);


		if(!file.exists()){
			/* msg = "HTTP/1.1 404 FileNotFound";
			 * msgHandle = msg.getBytes();
			 * userOutput.write(msgHandle);
			 */
			
			path = "C:\\Users\\peter_000\\Pictures\\Hjemmeside\\404.html";
			file = new File(path);
			//Finder fil extension ud fra filens path, til brug ved HEADER
			StringBuilder header = new StringBuilder();
			addHeader(header, "HTTP/1.1 404 Not Found");
			addHeader(header, "Connection close");
			addHeader(header, "Content-Type: " + Files.probeContentType(filePath));
			addHeader(header,"");
			String strHead = header.toString();
			
			//Så længe al data fra strHead ikke er transfered, skal loop køre
			while(transferedBytes < strHead.length()){ 
				//Starter på transfered bytes pointer (0 til start da intet flyttet) flytter 1024 bytes ad gangen, flytter til byte buffer, fra index 0.
				strHead.getBytes(transferedBytes, Math.min(byteBuf.length,strHead.length()-transferedBytes), byteBuf, 0); 
				//vælger data der skal skrives til client socket. Skriver al data
				userOutput.write(byteBuf,0,Math.min(strHead.length()-transferedBytes,byteBuf.length)); 	
				transferedBytes+=byteBuf.length;
		}
		}

		else if(file.exists()){
		//Finder fil extension ud fra filens path, til brug ved HEADER
		Path path2 = FileSystems.getDefault().getPath(path);

		StringBuilder header = new StringBuilder();
		addHeader(header, "HTTP/1.1 200 OK");
		addHeader(header, "Connection close");
		addHeader(header, "Content-Type: " + Files.probeContentType(filePath));
		addHeader(header, "File size: " +file.length());
		addHeader(header,"");
		String strHead = header.toString();
		
		//Så længe al data fra strHead ikke er transfered, skal loop køre
		while(transferedBytes < strHead.length()){ 
			//Starter på transfered bytes pointer (0 til start da intet flyttet) flytter 1024 bytes ad gangen, flytter til byte buffer, fra index 0.
			strHead.getBytes(transferedBytes, Math.min(byteBuf.length,strHead.length()-transferedBytes), byteBuf, 0); 
			//vælger data der skal skrives til client socket. Skriver al data
			//ud fra index 0 til, hvad enten der er mindst ad bytebufferen, eller Headeres resterende størrelse
			userOutput.write(byteBuf,0,Math.min(strHead.length()-transferedBytes,byteBuf.length)); 	
			transferedBytes+=byteBuf.length;														
		}
		}
		
		InputStream outToClient = new FileInputStream(file); //Åbner filen, så den er klar til at blive læst/omdannet til bytes.
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
