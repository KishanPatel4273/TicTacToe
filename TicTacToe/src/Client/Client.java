package Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFrame;

import org.json.JSONObject;

import Server.ClientHandler;
import Server.Server;

public class Client {//implements Runnable
	
	private static Socket socket;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	
	private String ip = "localHost";
	private static int port = 22222;
	private boolean accepted = false;
	private Scanner scanner = new Scanner(System.in);
	
	//differentiating players
	public String clientID = "";
	
	//tags
	public static final String CLIENT_ID_TAG = Server.CLIENT_ID_TAG;
	
	//game state
	public static JSONObject gameStateObj = new JSONObject(Server.NEW_GAME_DATA);
	
	public String move = "";
	//if its this players turn
	public boolean turn = false;
	//game board
	public int[] gameBoard = new int[9];
	
	
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
			if(input.contains("Assigned " + Server.CLIENT_ID_TAG)) {
				clientID = input.substring(input.indexOf(":")+1);
				System.out.println("CLIENT ID IS SET");
				return;
			}
			if(input.contains("Game State:")) {
				String gameStateInput = input.substring(input.indexOf(":")+1);
				//gameStateObj = new JSONObject(gameStateInput);
			}
			if(input.contains("Turn:")) {
				turn = ClientHandler.getValue("Turn:", input, "|").equals("true");
				//if(turn)
				//	System.out.println("your turn from the handler -->" + turn+"|");	
				//else
				//System.out.println(clientID);
			
				//player1 is fucked
			}
			if(input.contains("Board:")) {
				gameBoard = ClientHandler.getArray((ClientHandler.getValue("Board:", input, "|")));
				//System.out.println("your Board from the handler -->" + Arrays.toString(gameBoard));
			}
		} catch (IOException e) {
			System.out.println("Unable to read data from server");
			e.printStackTrace();
		}
	}
	
	public void sendData() {
		try {
			if(move.contains("move:")) {
				dos.writeUTF(CLIENT_ID_TAG+clientID + "|" + move + "|");
				move = "";
				return;
			}

			dos.writeUTF(CLIENT_ID_TAG+clientID+"|");
			//System.out.println("sent data" + clientID);
			
			//System.out.println("Data sent to the server");
		
		
		} catch (IOException e) {
			System.out.println("Unable to send data to the server");
			e.printStackTrace();
		}
	}
}