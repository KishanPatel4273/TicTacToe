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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.*; //https://github.com/stleary/JSON-java

public class Server implements Runnable{
	
	//DISCLAIMER platers need different IPS
	
	public static ServerSocket serverListener;
	public static DataOutputStream dos;
	public static DataInputStream dis;
	public Socket socket = null;//client connected
	
	public static int port = 7778;
	private Scanner scanner = new Scanner(System.in);
	
	private Thread thread;
	private boolean running = false;
	private boolean accepted = false;//where server is connected to any client
	private String connectedTo = "";//which client is the server connected too
	
	
	private final static String NEW_GAME_DATA = "	 { \"playerOneID\": \"\", "
													+ "\"playerTwoID\": \"\","
													+ "\"playerOnesTurn\":true,"
													+ "\"gameState\": [0, 0, 0, 0, 0, 0, 0, 0, 0],"
													+ "\"spectatorsID\" : [] }";
	
	public static JSONObject gameStateObj = new JSONObject(NEW_GAME_DATA);
	private String clientID = "";
	public static int IDuniqueness = 5;// P(ERROR) = 10^(-IDuniqueness)
	private boolean sendClientID = false;
	
	//tags
	public static final String CLIENT_ID_TAG = "ClientID:";
	
	private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
	private static ExecutorService pool = Executors.newFixedThreadPool(4);
	
	
	public Server() throws IOException, ClassNotFoundException {
		startGame();//sets the setting for the game
		System.out.println("Please input the Port: ");
		port = scanner.nextInt();
		//only allows valid port numbers
		while(port < 1 || port > 65535) {
			System.out.println("Please input another Port: ");
			port = scanner.nextInt();
		}
		
		serverListener = new ServerSocket(port);
		
	
		
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
			
			try {
				System.out.println("SERVER Waiting for client connections...");
				Socket socket = serverListener.accept();
				System.out.println("SERVER Connected to client");
				ClientHandler clientThread = new ClientHandler(socket);
				clients.add(clientThread);
				pool.execute(clientThread);				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	public void run2() {
	
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
		System.out.println("//////////////////////////////////////////////////////////////////");
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
		//waiting for both players
		if(gameStateObj.getString("playerOneID").equals("")
				|| gameStateObj.getString("playerTwoID").equals("")) {
			//return false;
		}
		
		//if its player ones turn and id is player one -> true 
		// Or then its player twos turn and if id is player two -> ture 
		//return gameStateObj.getString("playerTwoID").equals(ID);
		System.out.println("------------------------------" );
		if(gameStateObj.getBoolean("playerOnesTurn")) {
			System.out.println("_------------------------------------_-TRUE");
		}
		System.out.println(gameStateObj.toString());//getString("playerOneID"));
		System.out.println(ID);
		
		if(gameStateObj.getString("playerOneID").equals(ID)) {
			return gameStateObj.getBoolean("playerOnesTurn");
		} else {//its player two
			return !gameStateObj.getBoolean("playerOnesTurn");
		}	
	}

}