import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Start extends Canvas implements ActionListener, Runnable{
	

	private static final long serialVersionUID = 1L;

	private static int WIDTH = 400, HEIGHT = 400;
	private static JButton startServer, startTicTacToe;
	
	public static String MODE = "testing";//testing, complete
	
	private int buttonWidth = WIDTH/2;
	private int buttonHeight = HEIGHT/10;

	
	private Thread thread;
	private boolean running = false;
		
	public Start(){
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		
		
		//button creating and settings
		startServer = new JButton("Start Server");
		startServer.setBounds(WIDTH/2 - buttonWidth/2, 150+buttonHeight, buttonWidth, buttonHeight);
		startServer.setBackground(Color.decode("#E8A87C"));
		startServer.setFocusPainted(false);
		startServer.setBorder(null);
		startServer.setBorderPainted(false);
		
	
		startTicTacToe = new JButton("Launch Tic-Tac-Toe");
		startTicTacToe.setBounds(WIDTH/2 - buttonWidth/2, 100, buttonWidth, buttonHeight);
		startTicTacToe.setBackground(Color.decode("#E8A87C"));
		startTicTacToe.setFocusPainted(false);
		startTicTacToe.setBorder(null);
		startTicTacToe.setBorderPainted(false);

		
		startServer.addActionListener(this);
		startTicTacToe.addActionListener(this);		
	}
	
	private void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	/**
	 * stops the main loop
	 */
	private void stop() {
		if (!running)
			return;
		running = false;
		try {
			thread.join();//ends thread to close program correctly
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);//closes canvas
		}
	}
	
	
	private void render() {
		//has 3 images ready to display
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		//Initiates Graphics class using bufferStrategy
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.decode("#85DCBA"));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.dispose();//clears graphics
		bs.show();//shows graphics
	}
	
	public void run() {
		
		while(running){
			render();
		}

	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(startServer)){
			StartServer.main(new String[] {""});
			
			//closes the window after
			if(MODE.contains("complete")) {
				stop();
			}
			
		}
		if(e.getSource().equals(startTicTacToe)) {
			System.out.println("Game");
			
			//closes the window after
			if(MODE.contains("complete")) {
				stop();
			}
		}
	}
	
	public static void main(String[] args){
		Start start = new Start();
		JFrame frame = new JFrame("TicTacToe Launcher");
		//imports
		frame.add(startServer);
		frame.add(startTicTacToe);
		frame.add(start);
		
		frame.pack();
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		start.start();
		MODE = "complete";	
	}	
}