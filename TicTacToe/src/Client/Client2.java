package Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client2 {
	
	public static Socket socket;
	public static DataInputStream in;
	public static DataOutputStream out;
	
	public static int portNumber = 7778;
	
	public static void main(String[] args) throws IOException {
		
		System.out.println("Connecting...");
		socket = new Socket("143.229.228.123", portNumber);
		System.out.println("connected");
		
		//to send data
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		
		//to read data
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		//to read data from keyboard
		BufferedReader keyb = new BufferedReader( new InputStreamReader(System.in));
		String str, str1;
		
		// will keep running until "exit" is typed
		while(!(str = keyb.readLine()).equals("exit")) {
			
			dos.writeBytes(str + "\n");
			
			str1= br.readLine();
			
			System.out.println(str1);
		}
		
		//close all connections
		dos.close();
		br.close();
		keyb.close();
		socket.close();
		
	}
}