package Server;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server2 {
	
	public static ServerSocket server;
	public static DataOutputStream out;
	public static DataInputStream in;
	
	public static int portNumber = 7778;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		System.out.println("starting Sever");
		server = new ServerSocket(portNumber);
		System.out.println("server started");
		
		
		//waits for connection
		Socket socket = server.accept();
		System.out.println("Client connected from " + socket.getInetAddress());	
		
		//to send data
		PrintStream ps = new PrintStream(socket.getOutputStream());
		
		//to read data
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		//to read data from keyboard
		BufferedReader keyb = new BufferedReader( new InputerStreamReader(System.in));
		
		//Continiously lets you send messages
		boolean done = false;
		
		while (!done) {
			
			while((str = br.readLine()) != null) {
				System.out.println(str);
				str1 = keyb.readLine();
				
				ps.println(str1);
			}
			
			//to close all connections
			ps.close();
			br.close();
			keyb.close();
			socket.close();
			server.close();
			
			System.exit(0);
		}		
	}
}