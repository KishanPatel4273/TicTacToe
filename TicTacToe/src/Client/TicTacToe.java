package Client;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;

public class TicTacToe extends Canvas implements Runnable{
	
	public static final int WIDTH = 500;
	public static final int HEIGHT = 600;
	public static final String TITLE = "TicTacTow";
	
	private boolean running = false;
	private Thread thread;
	
	private Client client;
	
	
	private Font font = new Font("Verdana", Font.BOLD, 32);
	
	public TicTacToe() {
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		
		client = new Client();
		client.connect();
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
			
			if(xx > -1) {
			
				tick();
				render();
				client.sendData();
				client.readInputData();
				xx++;
			}
	
		}
		
	}
	
	public void tick() {
		//check if its this clients turn
		System.out.println("turn return:" + Server.Server.turn(client.clientID) + " " + client.clientID);
		System.out.println(Server.Server.turn(client.clientID));
		if(Server.Server.turn(client.clientID)) {
			System.out.println("--------------- Your Turn -------------------");
		}
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		
		//g.drawImage(img, 0, 0, null);

		
		g.setFont(new Font("Arial", 0, 10));
		g.setColor(Color.WHITE);
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) throws IOException {
		TicTacToe game = new TicTacToe();
		
		JFrame frame = new JFrame("TicTacToe");
		frame.add(game);
		frame.pack();
		frame.setResizable(true);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		game.start();
		
	}
	
	public static String formatGameState(String playerOneID, String playerTwoID, boolean playerOnesTurn, int[] gameState) {
		return "{ \"playerOneID\": \"" + playerOneID + "\", "
				+ "\"playerTwoID\": \"" + playerTwoID + "\","
				+ "\"playerOnesTurn\":" + playerOnesTurn + ","
				+ "\"gameState\"" + gameState + ","
				+ "\"spectatorsID\" : [] }";
	}
	
	
	/**
	 * 
	 * @param board ex: [0, 0, 0, 0, 0, 0, 0, 0, 0] left to right
	 * @param piece -1 or 1 (player 1 or 2)
	 * @return true of the piece makes a 3 in a row
	 */
	public static boolean gameStatus(int[] board, int piece) {
		int check = 3*piece;
		int[] hv = {0, 0, 0, 0, 0, 0};
		for(int i = 0; i < 3; i++) {
		//			hv[0] += board[3*i]; //0 3 6
		//			hv[1] += board[3*i+1]; //1 4 7
		//			hv[2] += board[3*i+2]; //2 5 8
		//			hv[3] += board[i]; //0 1 2
		//			hv[4] += board[3+i]; //3 4 5
		//			hv[5] += board[6+i]; //6 7 8
			for(int j = 0; j < 3; j++) {
				hv[i] += board[3*i+j]; 
				hv[i+3] = board[3*j+i];	
			}	
		}
		for(int total: hv) {
			if(total == check) {
				return true;
			}
		}
		//diagonal
		return (board[0] + board[4] + board[8]) == check ||
				(board[2] + board[4] + board[6]) == check;
	}
}
