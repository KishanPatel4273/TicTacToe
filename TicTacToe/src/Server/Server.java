package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import org.json.*; //https://github.com/stleary/JSON-java

public class Server implements Runnable{
	
	public static ServerSocket server;
	public static DataOutputStream dos;
	public static DataInputStream dis;
	public Socket socket = null;//client connected
	
	public static int port = 7778;
	private Scanner scanner = new Scanner(System.in);
	
	private Thread thread;
	private boolean running = false;
	private boolean accepted = false;
	
	private String gameData = "	{ \"playerOneID\": \"148.34.31\", "
			+ "\"playerTwoID\": \"\","
			+ "\"playerOnesTurn\": true,"
			+ "\"gameState\": [0, 0, 0, 0, 0, 0, 0, 0, 0],"
			+ "\"spectatorsID\" : [] }"; 
	
	public Server() throws IOException, ClassNotFoundException {
		System.out.println("Please input the Port: ");
		port = scanner.nextInt();
		//only allows valid port numbers
		while(port < 1 || port > 65535) {
			System.out.println("Please input another Port: ");
			port = scanner.nextInt();
		}
		
		server = new ServerSocket(port);
		JSONObject obj = new JSONObject(gameData);
		String testStr = obj.getString("playerOneID");
		JSONArray arr = obj.getJSONArray("gameState");
		
		System.out.println(obj.names());
	}
	
	private void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	private void stop() {
		if (!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	int xx = 0;
	public void run() {
	
		while(running) {
			//listenForServerRequest();
			if(accepted) {
				readInputData();
			}
			String str = scanner.nextLine();
			if(!str.equals("")) {
				System.out.println(str);
				str = "";
			}
			//it waits for a response (the scanner)
			//System.out.println("test! Print");
			
			if(xx < 2  && accepted) {
				sendData();
			}
		}
	
	}
	
	
	public void listenForServerRequest() {

		try {
			socket = server.accept();
			dis = new DataInputStream(socket.getInputStream());		
			dos = new DataOutputStream(socket.getOutputStream());
			accepted = true;
			System.out.println("Client request has came in, and has been accepted");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readInputData() {
		//to read data
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		
		try {
			System.out.println(dis.readUTF());
		} catch (IOException e) {
			System.out.println("Unable to read data sent from: " + socket.getInetAddress());
			e.printStackTrace();	
		}
	}
	
	public void sendData() {
		try {
			dos.writeUTF("Test"+ (xx*10 + 10));
			System.out.println("Data sent to the Client");
		} catch (IOException e) {
			System.out.println("Unable to send data to the Client");
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args)  throws IOException, ClassNotFoundException {
		System.out.println("starting Sever");
		
		Server serverC = new Server();
		
		
		System.out.println("server started");
		

		serverC.start();
	}

}