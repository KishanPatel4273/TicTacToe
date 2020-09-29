package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
	
	public static Socket socket;
	public static DataInputStream in;
	public static DataOutputStream out;
	
	public static int portNumber = 7778;
	
	public static void main(String[] args) throws IOException {
		System.out.println("Connecting...");
		socket = new Socket("143.229.228.123", portNumber);
		System.out.println("connected");
		
		in = new DataInputStream(socket.getInputStream());


		System.out.println("receiving info");
		
		String test = in.readUTF();
		System.out.println("Message recived :" + test);
		
		out = new DataOutputStream(socket.getOutputStream());	
		
		out.writeUTF("Penis");
		
		
	}
}