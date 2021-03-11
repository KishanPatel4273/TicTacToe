import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Server.Server;

public class StartServer extends Canvas implements ActionListener, Runnable{
	

	private static final long serialVersionUID = 1L;

	private static int WIDTH = 400, HEIGHT = 400;
	private static JButton buttonStartServer;
	private static JLabel portLabel = new JLabel();
	private static JTextField portText;
	
	public static String MODE = "testing";//testing, complete
	
	private int buttonWidth = WIDTH/2;
	private int buttonHeight = HEIGHT/10;
	
	private Thread thread;
	private boolean running = false;
	//creates server once;
	private boolean latch = false;
	
	private String port = "22222";
	public String ip;
	
	public Color backgroundColor = Color.decode("#85DCBA");
	
	public StartServer(){
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		
		try {
			ip = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			ip = "/ERROR";
		}
		ip = ip.substring(ip.indexOf("/")+1);

		portLabel = new JLabel("Port:");
		portLabel.setBackground(backgroundColor);
		portLabel.setBounds(10, 10, 40, 30);
		portLabel.setBorder(null);

		portText = new JTextField();
		portText.setBounds(50, 30, 100, 30);
		portText.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
	    		port = portText.getText();
	    }
	});
		
		
		buttonStartServer = new JButton("Start Server");
		buttonStartServer.setBounds(WIDTH/2 - buttonWidth/2, 100, buttonWidth, buttonHeight);
		buttonStartServer.setBackground(Color.decode("#E8A87C"));
		buttonStartServer.setFocusPainted(false);
		buttonStartServer.setBorder(null);
		buttonStartServer.setBorderPainted(false);
		buttonStartServer.addActionListener(this);		
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
		System.exit(0);
	}
	
	
	private void render() throws UnknownHostException {
		//has 3 images ready to display
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		//Initiates Graphics class using bufferStrategy
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(backgroundColor);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		
		g.setColor(Color.BLACK);
		g.drawString("Server Address: " + ip, WIDTH/10, (int) (.5 * HEIGHT));
		
		g.dispose();//clears graphics
		bs.show();//shows graphics
	}
	
	public void run() {
		
		while(running){
			try {
				render();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(buttonStartServer) && !latch) {
			try {
				System.out.println("Port:" + port);
				Server.main(new String[] {"Port:" + port});
				latch = true;
			} catch (NumberFormatException | ClassNotFoundException | IOException e1) {
				latch = false;
				e1.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		StartServer startServer = new StartServer();
		JFrame frame = new JFrame("Start Server");
		//imports

		frame.add(portLabel);
		frame.add(portText);
		frame.add(buttonStartServer);
		frame.add(startServer);
		
		frame.pack();
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		startServer.start();
		MODE = "complete";	
	}	
}