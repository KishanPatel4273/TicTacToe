package Client;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;

public class TicTacToe implements Runnable{

	private String ip = "localhost";
	private int port = 22222;
	private Scanner scanner = new Scanner(System.in);
	private JFrame jframe;
	private final int WIDTH = 506, HIEGHT = 527;
	private Thread thread;
	
	private Painter painter;
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	
	private ServerSocket serverSocket;
	
	private BufferedImage board;
	private BufferedImage redX;
	private BufferedImage blueX;
	private BufferedImage redCircle;
	private BufferedImage blueCircle;
	
	private int[] spaces = new int[9];
	
	private boolean yourTurn = false;
	private boolean cirlce = true;
	private boolean accepted = false;
	private boolean unableToCommunicateWithOpponet = false;
	private boolean won = false;
	private boolean enemyWon = false;
	
	private int lenghtOfSpace = 160;
	private int errors = 0;
	private int firstSpot = -1;
	private int secondSpot = -1;
	
	private Font font = new Font("Verdana", Font.BOLD, 32);
	
	
	public TicTacToe() {
		
	}
	
	public void run() {
		
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		TicTacToe game = new TicTacToe();
	}
	
	public class Painter {
		
	}
	
	public static String formatGameState(String playerOneID, String playerTwoID, boolean playerOnesTurn, int[] gameState) {
		return "{ \"playerOneID\": \"" + playerOneID + "\", "
				+ "\"playerTwoID\": \"" + playerTwoID + "\","
				+ "\"playerOnesTurn\":" + playerOnesTurn + ","
				+ "\"gameState\"" + gameState + ","
				+ "\"spectatorsID\" : [] }";
	}
	
}
