package Client;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;

import org.json.JSONArray;
import org.json.JSONObject;

public class TicTacToe extends Canvas implements Runnable{
	
	public static final int WIDTH = 301;
	public static final int HEIGHT = 600;
	public static final String TITLE = "TicTacTow";
	public final int tileSize = WIDTH/3;
	
	private boolean running = false;
	private Thread thread;
	
	private Client client;
	private InputHandler input;
		
	private Font font = new Font("Verdana", Font.BOLD, 14);
	
	public TicTacToe() {
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		
		client = new Client();
		client.connect();
		
		input = new InputHandler();
		addKeyListener(input);
		addFocusListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
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
			//System.out.println(client.clientID);
			//System.out.println("(" + input.mouseX + ", " + input.mouseY + ")");
			//ID and input work
			tick();
			render();
			client.sendData();
			client.readInputData();
			
		}
		
	}
	
	public void tick() {
		// move is valid if its your turn
		if(!client.turn && !client.move.equals("")) {
			client.move = "";
			input.mouseClicked = false;
		}
		upadateBoard();
		
		if(input.key[KeyEvent.VK_R]) {
			Server.Server.restartGame();
		}
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		
		g.clearRect(0, 0, WIDTH, HEIGHT);
		
		
		renderBoard(g, client.gameBoard);
		
		
		//render state
		String state;
		if(client.turn) {
			state = "It is your turn.";
		} else {
			state = "It is the opponents turn.";
		}
		
		System.out.println(client.gamePiece);
		if(gameStatus(client.gameBoard, client.gamePiece)) {
			state = "You won, congratulations!";
		} else if(gameStatus(client.gameBoard, -1 * client.gamePiece)) {
			state = "Your opponent won.";
		}
		g.setFont(font);
		g.drawString(state, tileSize/10, (int) (3.5 * tileSize));
		
		
		g.dispose();
		bs.show();
	}
	
	
	/**
	 *
	 * @param g
	 * @param board int[] of size 9
	 */
	public void renderBoard(Graphics g, int[] board) {
		for(int i = 0; i < 4; i++) {
			g.drawLine(0, i*tileSize, WIDTH, i*tileSize);
			g.drawLine(i*tileSize, 0, i*tileSize, WIDTH);
		}
		
		for(int i = 0; i < board.length; i++) {
			if(board[i] == -1) {//X
				renderX(g, i%3*tileSize, (int) Math.floor(i/3)*tileSize);
			}
			if(board[i] == 1) {//X
				renderO(g, i%3*tileSize, (int) Math.floor(i/3)*tileSize);
			}
		}
	}
	
	public void renderX(Graphics g, int x, int y) {
		int padding = (int) (tileSize * .10);
		g.drawLine(x+padding, y+padding, x+tileSize-padding, y+tileSize-padding);
		g.drawLine(x+tileSize-padding, y+padding, x+padding, y+tileSize-padding);
	}
	
	public void renderO(Graphics g, int x, int y) {
		int padding = (int) (tileSize * .10);
		g.drawOval(x+padding, y+padding, tileSize-2*padding, tileSize-2*padding);
	}
	
	/**
	 * @returns updates gameStateObj if its this players turn and makes a move
	 */
	public void upadateBoard() {
		if(client.turn && input.mouseClicked) {//your turn and u made a move
			//System.out.println("(" + input.mouseX + ", " + input.mouseY + ")");
			int i = getBoardIndex(input.mouseX, input.mouseY);
			if(i != -1) {//stop is open
				if(client.move.equals("")) {
					client.move = "move:" + i;
					input.mouseClicked = false;
					input.mouseX = -WIDTH; 
					input.mouseY = -HEIGHT;
				}
				
				//int[] tempBoard = getBoard(client.gameStateObj);	
				//tempBoard[i] = gamePiece(client.clientID, client.gameStateObj);//updates board
				//client.gameStateObj.put("gameState", new JSONArray(tempBoard));
				//client.gameStateObj.put("playerOnesTurn", !client.gameStateObj.getBoolean("playerOnesTurn"));//turn is flipped
				//input.mouseClicked = false;
				//System.out.println("flipped");
			}
		}
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
	 * @param x position of mouse
	 * @param y position of mouse
	 * @return tile clicked on 3x3 grid
	 */
	public int getBoardIndex(int x, int y) {
		for(int i = 0; i < 9; i++) {
			int xx = i%3 + 1;
			int yy = (int) Math.floor(i/3) + 1;
			if(0 <= x && x < xx*tileSize && 0 < y && y < yy*tileSize) {
				return i;
			}
		}
		return -1;
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
					hv[0] += board[3*i]; //0 3 6
					hv[1] += board[3*i+1]; //1 4 7
					hv[2] += board[3*i+2]; //2 5 8
					hv[3] += board[i]; //0 1 2
					hv[4] += board[3+i]; //3 4 5
					hv[5] += board[6+i]; //6 7 8
			//for(int j = 0; j < 3; j++) {
			//	hv[i] += board[3*i+j]; 
			//	hv[i+3] = board[3*j+i];	
			//}	
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
	
	/**
	 * 
	 * @param board int[] of game board
	 * @return if any player won
	 */
	public static boolean gameStatus(int[] board) {
		return gameStatus(board, -1) || gameStatus(board, 1);
	}

}
