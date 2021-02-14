package Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client implements Runnable{
	
	private static Socket socket;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	
	private String ip = "localHost";
	private static int port = 7778;
	private boolean accepted = false;
	private Scanner scanner = new Scanner(System.in);
	
	private boolean running = false;
	private Thread thread;
	
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
			if( xx < 4) {
				connect();
				
				sendData();
				readInputData();
				
				xx++;
				System.out.println(xx);
			}
			
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
			System.out.println(dis.readUTF());
		} catch (IOException e) {
			System.out.println("Unable to read data from server");
			e.printStackTrace();
		}
	}
	
	public void sendData() {
		try {
			dos.writeUTF("Test"+xx);
			System.out.println("Data sent to the server");
		} catch (IOException e) {
			System.out.println("Unable to send data to the server");
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) throws IOException {
		Client client = new Client();
		client.start();
	}
}