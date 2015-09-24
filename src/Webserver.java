import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Webserver {
	public Webserver()throws Exception{
		//initialisere socket listener, og socket for users
		ServerSocket socketListener;
		Socket userSocket = null;
		
		//tilknytter port til socket listener
		socketListener = new ServerSocket(5559);
		//opretter user socket, ud fra accepteret socket listener
		//kalder processGET med userSocket
		userSocket = socketListener.accept();
		if(userSocket != null){
				processGET(userSocket);
			}
		socketListener.close(); //når operation er færdig, lukkes socketListener
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
				
		//opretter ny fil, med path der blev modtaget fra client
		File file = new File(path);
		InputStream outToClient = new FileInputStream(file);
		int len = (int) file.length();
		
		//sætter filen i kø, for ikke at skulle sende hele fil på samme tid, sender den lidt ad gangen.. therefore buffer....
		byte[] byteBuf = new byte[1024];
		
		//sætter det output, som den der er connected med socket får, til at være lig med en outputstream. I denne output stream, bliver der skrevet
		//bytes, til, som kommer fra filen der ønskes at sendes. byteBuf står for at buffe bytesne der udgør filen, som der bliver sendt
		//igennem socketed til client.
		OutputStream fileOutput = userSocket.getOutputStream();
		while((len = outToClient.read(byteBuf))!= -1){
			fileOutput.write(byteBuf,0, len);
		}
		System.out.println(byteBuf);
		inFromClient.close();
		outToClient.close();
	}
}
