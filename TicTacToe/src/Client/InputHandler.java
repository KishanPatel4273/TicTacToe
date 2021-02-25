package Client;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputHandler implements KeyListener, FocusListener, MouseListener, MouseMotionListener {

	public static boolean[] key = new boolean[68836];
	public static int mouseX;
	public static int mouseY;
	public boolean mouseClicked;
	
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	//gets x and y of the mouse on the canvas
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	//sees if mouse is clicked
	public void mouseClicked(MouseEvent e) {
		mouseClicked = true;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseClicked = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseClicked = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub

	}

	public void focusLost(FocusEvent e) {
		for (int i = 0; i < key.length; i++) {
			key[i] = false;
		}
	}

	//what key is pressed
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode > 0 && keyCode < key.length) {
			key[keyCode] = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		int keyCode =e.getKeyCode();
		if(keyCode > 0 && keyCode < key.length){
			key[keyCode] = false;
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	
	public String getMousePos() {
		return "(" + mouseX + ", " + mouseY + ")";
	}
}