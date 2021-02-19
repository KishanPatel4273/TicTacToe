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
	
	//DISCLAIMER platers need different IPS
	
	public static ServerSocket server;
	public static DataOutputStream dos;
	public static DataInputStream dis;
	public Socket socket = null;//client connected
	
	public static int port = 7778;
	private Scanner scanner = new Scanner(System.in);
	
	private Thread thread;
	private boolean running = false;
	private boolean accepted = false;//where server is connected to any client
	private String connectedTo = "";//which client is the server connected too
	
	
	private final static String NEW_GAME_DATA = "	 { \"playerOneID\": \"344\", "
													+ "\"playerTwoID\": \"\","
													+ "\"playerOnesTurn\": false,"
													+ "\"gameState\": [0, 0, 0, 0, 0, 0, 0, 0, 0],"
													+ "\"spectatorsID\" : [] }";
	
	public static JSONObject gameStateObj = new JSONObject(NEW_GAME_DATA);
	private String clientID = "";
	private int IDuniqueness = 5;// P(ERROR) = 10^(-IDuniqueness)
	private boolean sendClientID = false;
	
	//tags
	public static final String CLIENT_ID_TAG = "ClientID:";
	
	public Server() throws IOException, ClassNotFoundException {
		startGame();//sets the setting for the game
		System.out.println("Please input the Port: ");
		port = scanner.nextInt();
		//only allows valid port numbers
		while(port < 1 || port > 65535) {
			System.out.println("Please input another Port: ");
			port = scanner.nextInt();
		}
		
		server = new ServerSocket(port);
		
	
		
		//String testStr = gameStateObj.getString("playerOneID");
		//JSONArray arr = gameStateObj.getJSONArray("gameState");
		
		//System.out.println(gameStateObj.names());
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
			listenForServerRequest();
			if(accepted) {
				readInputData();
			}
			String str = "";//scanner.nextLine();
			if(!str.equals("")) {
				str = "";
			}
			//it waits for a response (the scanner)
			//System.out.println("test! Print");
			
			if(accepted) {
				sendData();
			}
		}
	
	}
	
	//runs the tictactoe game
	public void gameTick() {
		
	}
	
	/**
	 * resents game attributes;
	 */
	public void startGame() {
		gameStateObj = new JSONObject(NEW_GAME_DATA);
	
		//coin flip to see who goes first
		// 50/50 which player goes first
		if(Math.random() > 0.5) {
			gameStateObj.put("playerOnesTurn", false);
		}
	}
	
	public void listenForServerRequest() {
		try {
			socket = server.accept();
			dis = new DataInputStream(socket.getInputStream());		
			dos = new DataOutputStream(socket.getOutputStream());
			accepted = true;
			connectedTo = socket.getInetAddress().toString();
			System.out.println("Client request has came in, and has been accepted");
			
			
			clientIDAssigner();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Assigns the first and second unique clients (IP is ignored) to the two players
	 * @called in listenForServerRequest()
	 */
	public void clientIDAssigner() {
		//first player to join and will be under playerOneID
		if(gameStateObj.getString("playerOneID").equals("")) {
			clientID = connectedTo + (int) (Math.random() * Math.pow(10, IDuniqueness));//generates ID
			gameStateObj.put("playerOneID", clientID);//saves ID
			sendClientID = true;//will send the client its ID
								//^^^^^^^^^^^
		} else if(gameStateObj.getString("playerTwoID").equals("")) {
			clientID = connectedTo + (int) (Math.random() * Math.pow(10, IDuniqueness));
			gameStateObj.put("playerTwoID", clientID);
			sendClientID = true;
		} else {
			clientID = "spectator";
		}
	}
	
	/**
	 * @param connectedID
	 * Doesn't assign user one both player ID slots
	 * @called in readInputData()
	 */
	public void clientIDAssignerFix(String connectedID) {
		if(sendClientID && gameStateObj.getString("playerOneID").equals(connectedID)) {
			gameStateObj.put("playerTwoID", "");//corrects error
			sendClientID = false;	
		}
	}
	
	//accepts the players moves only
	public void readInputData() {
		//to read data
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		
		try {
			String input = dis.readUTF();
			String clientID = input.substring(input.indexOf(CLIENT_ID_TAG)+CLIENT_ID_TAG.length()); 
	
			clientIDAssignerFix(clientID);
			
			
		} catch (IOException e) {
			System.out.println("Unable to read data sent from: " + socket.getInetAddress());
			e.printStackTrace();	
		}
	}
	
	//sends the gameStateObj to client
	public void sendData() {
		try {
			if(sendClientID) {
				dos.writeUTF("Assigned " + CLIENT_ID_TAG + clientID);	
				clientID = "";
				sendClientID = false;
				return;
			}
			
			
			
			dos.writeUTF(gameStateObj.toString());
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
	
	
	/**}
	 * @param ID
	 * @return true if the given id is going 
	 */
	public static boolean turn(String ID) {
		//waiting for players
		if(gameStateObj.getString("playerOneID").equals("")
				|| gameStateObj.getString("playerTwoID").equals("")) {
			//return false;
		}
		
		//if its player ones turn and id is player one -> true 
		// Or then its player twos turn and if id is player two -> ture 
		return gameStateObj.getString("playerTwoID").equals(ID);
		//(gameStateObj.getBoolean("playerOnesTurn") && gameStateObj.getString("playerOneID").equals(ID))
	
	}

}