package Server;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
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
		
		out = new DataOutputStream(socket.getOutputStream());
		out.writeUTF("Did you get the code");		
		
		System.out.println("data sent");
		
		//Receiving
		in = new DataInputStream(socket.getInputStream());		
		String message_received = in.readUTF();
		System.out.println(message_received);
		
	}

}