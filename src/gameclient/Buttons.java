package gameclient;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class Buttons extends JComponent implements MouseListener {
	private BufferedImage imagePressed;
	private BufferedImage imageUnpressed;
	private States state = States.Unpressed;
	private enum States {
		Pressed, Unpressed;
	};

	public Buttons(String filenamePressed, String filenameUnpressed) {
		
		try {
			this.imagePressed = ImageIO.read(new File(filenamePressed));
			this.imageUnpressed = ImageIO.read(new File(filenameUnpressed));
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		this.addMouseListener(this);
	}
	
	
	public void mouseClicked(MouseEvent play) {
	
	}

	public void mouseEntered(MouseEvent play) {
		
	}

	public void mouseExited(MouseEvent play) {
		
	}

	public void mousePressed(MouseEvent play) {
		state = States.Pressed;
		repaint();
	}

	public void mouseReleased(MouseEvent play) {
		state = States.Unpressed;
		repaint();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		System.out.println(getSize());
		if(state == States.Pressed) {
			g.drawImage(imagePressed, 0, 0, 100, 50, null);
		} else if (state == States.Unpressed) {
			g.drawImage(imageUnpressed, 0, 0, 100, 50, null);
		}
		
	}
}
