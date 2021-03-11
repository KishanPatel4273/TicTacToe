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
	
	
	public final static String NEW_GAME_DATA = "{ \"playerOneID\": \"\", "
													+ "\"playerTwoID\": \"\","
													+ "\"playerOnesTurn\":true,"
													+ "\"gameState\": [0, 0, 0, 0, 0, 0, 0, 0, 0],"
													+ "\"spectatorsID\" : [] }";
	
	public static JSONObject gameState = new JSONObject(NEW_GAME_DATA);
	public static String test = "";
	
	
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
	}
	
	public Server(int port) throws IOException, ClassNotFoundException {
		startGame();//sets the setting for the game
		this.port = port;
		serverListener = new ServerSocket(port);
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
	
	/**
	 * resents game attributes;
	 */
	public void startGame() {
		ClientHandler.gameStateObj = new JSONObject(NEW_GAME_DATA);
	
		//coin flip to see who goes first
		// 50/50 which player goes first
		if(Math.random() > 0.5) {
			ClientHandler.gameStateObj.put("playerOnesTurn", false);
		}
	}

	/**
	 * keeps Client IDs
	 * Doesn't work
	 */
	public static void restartGame() {
		ClientHandler.gameStateObj.put("playerOnesTurn", true);
		ClientHandler.gameStateObj.put("gameState", new JSONArray(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0}));
		if(Math.random() > 0.5) {
			ClientHandler.gameStateObj.put("playerOnesTurn", false);
		}
	}
	
	public static void main(String[] args)  throws IOException, ClassNotFoundException {
		System.out.println("starting Sever");
		
		int port = 0;
		for(int i = 0; i < args.length; i++) {
			if(args[i].contains("Port:")) {
				port = Integer.valueOf(args[i].substring(args[i].indexOf(":") + 1));
			}
		}
		
		Server serverC;
		if(port != 0) {
			serverC = new Server(port);
		} else {
			serverC = new Server();
		}
		System.out.println("server started");
		serverC.start();
	}
}