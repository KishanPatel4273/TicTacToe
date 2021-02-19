package Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JFrame;

public class Client {//implements Runnable
	
	private static Socket socket;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	
	private String ip = "localHost";
	private static int port = 7778;
	private boolean accepted = false;
	private Scanner scanner = new Scanner(System.in);
	
	//differentiating players
	public String clientID;
	
	//tags
	public static final String CLIENT_ID_TAG = Server.Server.CLIENT_ID_TAG;
	
	//input (should be json game type)
	public String inputData = "";
	
	public Client() {
		System.out.println("Please input the IP: ");
		ip = scanner.nextLine();
		System.out.println("Please input the Port: ");
		port = scanner.nextInt();
		//only allows valid port numbers
		while(port < 1 || port > 65535) {
			System.out.println("Please input another Port: ");
			port = scanner.nextInt();
		}
	
	}
	
	public boolean connect() {
		try {
			socket = new Socket("localHost", port);
			dos = new DataOutputStream(socket.getOutputStream());	
			dis = new DataInputStream(socket.getInputStream());
			accepted = true;
		} catch (IOException e) {
			System.out.println("Unable to connect to the address: " + ip + " + port: " + port);
			return false;
		}
		System.out.println("Successfully connected to the server");
		return true;
	}
	
	public void readInputData() {
		try {
			String input = dis.readUTF();
			//stores unique ID if its assigned and doesn't have one already
			if(clientID == null && input.contains("Assigned ClientID:")) {
				clientID = input.substring(input.indexOf(":")+1);
				System.out.println("TEST CLIENT ID" + clientID);
				return;
			}
			System.out.println(input);
			
		} catch (IOException e) {
			System.out.println("Unable to read data from server");
			e.printStackTrace();
		}
	}
	
	public void sendData() {
		try {
			dos.writeUTF(CLIENT_ID_TAG+clientID);
			System.out.println("sent data" + clientID);
			
			System.out.println("Data sent to the server");
		} catch (IOException e) {
			System.out.println("Unable to send data to the server");
			e.printStackTrace();
		}
	}
}