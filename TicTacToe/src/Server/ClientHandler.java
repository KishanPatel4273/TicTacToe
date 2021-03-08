package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

public class ClientHandler implements Runnable {

	private Socket client;
	private DataOutputStream dos;
	private DataInputStream dis;
	
	private int IDuniqueness = Server.IDuniqueness;
	private String connectedTo;
	private String clientID;
	private boolean sendClientID;
	private final String CLIENT_ID_TAG = Server.CLIENT_ID_TAG;
	
	//data to sent to the client
	private String sendingData = "";
	
	public ClientHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		dis = new DataInputStream(client.getInputStream());		
		dos = new DataOutputStream(client.getOutputStream());
		
		connectedTo = client.getInetAddress().toString();//IP address of the given client
		clientIDAssigner();
	}
	
	public void run() {
		while(true) {
			readInputData();
			tick();
			sendData();
		}
		 
	}
	
	public void tick() {
		gameStateUpdatelocaly();
		//System.out.println(Client.Client.gameStateObj.toString());
		//the access here has stores the id 
	}
	
	
	/**
	 * updates the data to send the client
	 */
	public synchronized void gameStateUpdatelocaly() {
		//System.out.println("TEST:" + clientID);
		//System.out.println("TEST:" + Client.Client.gameStateObj);
		sendingData = "Turn:" + turn(clientID, Client.Client.gameStateObj) + "|"
				    + "Board:" + Arrays.toString(getBoard(Client.Client.gameStateObj)) + "|";
	}
	
	/** Assigns the first and second unique clients (IP is ignored) to the two players
	 * @called in listenForServerRequest()
	 */
	public synchronized void clientIDAssigner() {
		//first player to join and will be under playerOneID
		if(Client.Client.gameStateObj.getString("playerOneID").equals("")) {
			clientID = connectedTo + (int) (Math.random() * Math.pow(10, IDuniqueness));//generates ID
			Client.Client.gameStateObj.put("playerOneID", clientID);//saves ID
			sendClientID = true;//will send the client its ID
								//^^^^^^^^^^^
		} else if(Client.Client.gameStateObj.getString("playerTwoID").equals("")) {
			clientID = connectedTo + (int) (Math.random() * Math.pow(10, IDuniqueness));
			Client.Client.gameStateObj.put("playerTwoID", clientID);
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
	public synchronized void clientIDAssignerFix(String connectedID) {
		if(sendClientID && Client.Client.gameStateObj.getString("playerOneID").equals(connectedID)) {
			Client.Client.gameStateObj.put("playerTwoID", "");//corrects error
			sendClientID = false;	
		}
	}
	
	//accepts the players moves only
	public void readInputData() {
		try {
			String input = dis.readUTF();
		  
			//constant update to the id makes it empty because client has some error
			//clientID = getValue(CLIENT_ID_TAG, input, "|");
				
			//need this to keep the clientID 
			clientIDAssignerFix(clientID);//fixes json file if there is any problem in id storage
			//its main function is pointless now but it turns sendClientID 
			
			
			if(input.contains("move:")) {
				int move = Integer.valueOf(getValue("move:", input, "|"));
				move(move);
			}			
			//System.out.println("---------------" + turn(clientID, Client.Client.gameStateObj));
			//System.out.println(input);
			
		} catch (IOException e) {
			System.out.println("Unable to read data sent from: " + connectedTo);
			e.printStackTrace();	
		}
	}
	
	/**
	 * updates the game board given a move by the client
	 * @param move integer value of tile clicked
	 */
	public synchronized void move(int move) {
		if(turn(clientID, Client.Client.gameStateObj)) {
			int[] board = getBoard(Client.Client.gameStateObj);
			if(board[move] == 0) {//spot is open
				//updates the game board
				board[move] = gamePiece(clientID, Client.Client.gameStateObj);
				//updates main game state json
				Client.Client.gameStateObj.put("gameState", new JSONArray(board));
				//turn is over
				Client.Client.gameStateObj.put("playerOnesTurn", !Client.Client.gameStateObj.getBoolean("playerOnesTurn"));
				System.out.println(Client.Client.gameStateObj);
			}
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
			
			if(!sendingData.equals("")) {
				dos.writeUTF(sendingData);
			}
			
			dos.writeUTF("Game State:");
			//System.out.println("Data sent to the Client");
		} catch (IOException e) {
			System.out.println("Unable to send data to the Client");
			e.printStackTrace();
		}
	}

	
	/**}
	 * @param ID
	 * @return true if the given id is going 
	 */
	public static boolean turn(String ID, JSONObject gameState) {
		String p1 = gameState.getString("playerOneID");
		String p2 = gameState.getString("playerTwoID");
		boolean pt = gameState.getBoolean("playerOnesTurn");
		//System.out.println(p1+"-"+p2 +"-"+pt);
		//System.out.println(ID + "----" + ID.contains(p1));
		//waiting for both players
		if(p1.equals("")
				|| p2.equals("")) {
			//return false;
		}
		//System.out.println(state.getString("playerOneID"));
		//System.out.println("ID : " + ID);
		if(p1.equals(ID)) {
			return pt;
		} else if(p2.equals(ID)) {
			return !pt;
		} else {
			return false;
		}
	}
	
	/**
	 * @param gameState 
	 * @return converts a JSONArray to an array
	 */
	public static int[] getBoard(JSONObject gameState) {
		int[] y = new int[gameState.getJSONArray("gameState").length()];
		for(int i = 0; i < y.length; i++) {
			y[i] = gameState.getJSONArray("gameState").getInt(i);
		}
		return y;
	}
	
	
	/**
	 * 
	 * @param ID player id
	 * @param gameState current game state
	 * @return -1, 0, 1 depending on the player and their piece
	 */
	public static int gamePiece(String ID, JSONObject gameState) {
		String p1 = gameState.getString("playerOneID");
		String p2 = gameState.getString("playerTwoID");
		if(p1.equals(ID)) {
			return -1;
		} else if(p2.equals(ID)) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * @param key sequence in front of data
	 * @param data 
	 * @param end a character at end of data
	 * @return the value between the key and end
	 */
	public static String getValue(String key, String data, String end) {
		String newStart = data.substring(data.indexOf(key)+key.length());
		return newStart.substring(0, newStart.indexOf(end));
	}
	
	
	/**
	 * 
	 * @param data "[1, 2, 3]"
	 * @return [1, 2, 3]
	 */
	public static int[] getArray(String data) {
		String[] s = data.substring(1, data.length()-1).split(", "); 
		int[] x = new int[s.length];
		for(int i = 0; i < x.length; i++) {
			x[i] = Integer.valueOf(s[i]);
		}
		return x;
	}
	
	//	/127.0.0.116014
	public static void main(String[] args) {
		if("true".equals("true")) {
			System.out.println("works");
		}
		System.out.println("----");
	}
}
