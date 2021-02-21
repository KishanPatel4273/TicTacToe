package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

	private Socket client;
	private DataOutputStream dos;
	private DataInputStream dis;
	
	private int IDuniqueness = Server.IDuniqueness;
	private String connectedTo;
	private String clientID;
	private boolean sendClientID;
	private final String CLIENT_ID_TAG = Server.CLIENT_ID_TAG;
	
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
	
	}
	
	/** Assigns the first and second unique clients (IP is ignored) to the two players
	 * @called in listenForServerRequest()
	 */
	public void clientIDAssigner() {
		//first player to join and will be under playerOneID
		if(Server.gameStateObj.getString("playerOneID").equals("")) {
			clientID = connectedTo + (int) (Math.random() * Math.pow(10, IDuniqueness));//generates ID
			Server.gameStateObj.put("playerOneID", clientID);//saves ID
			sendClientID = true;//will send the client its ID
								//^^^^^^^^^^^
			System.out.println(Server.gameStateObj.getString("playerOneID").length());
		} else if(Server.gameStateObj.getString("playerTwoID").equals("")) {
			clientID = connectedTo + (int) (Math.random() * Math.pow(10, IDuniqueness));
			Server.gameStateObj.put("playerTwoID", clientID);
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
		if(sendClientID && Server.gameStateObj.getString("playerOneID").equals(connectedID)) {
			Server.gameStateObj.put("playerTwoID", "");//corrects error
			sendClientID = false;	
		}
	}
	
	//accepts the players moves only
	public void readInputData() {
		try {
			String input = dis.readUTF();
			String clientID = input.substring(input.indexOf(CLIENT_ID_TAG)+CLIENT_ID_TAG.length()); 
	
			clientIDAssignerFix(clientID);//fixes json file if there is any problem in id storage
			
			//System.out.println(input);
			
		} catch (IOException e) {
			System.out.println("Unable to read data sent from: " + connectedTo);
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
			
			dos.writeUTF(Server.gameStateObj.toString());
			//System.out.println("Data sent to the Client");
		} catch (IOException e) {
			System.out.println("Unable to send data to the Client");
			e.printStackTrace();
		}
	}

	
	
}
