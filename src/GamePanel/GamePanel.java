package GamePanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
	private static final int PWIDTH = 500;
	private static final int PHEIGHT = 400;
	
	private volatile boolean running;

	private Thread animator;
	private int period = 10;
	private int fps;
	
	private Graphics bg;
	private Image bufferImage = null;

	public GamePanel() {
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
	}

	public void addNotify() {
		super.addNotify();
		startGame();
	}

	private void startGame() {
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}

	public void stopGame() {
		running = false;
	}

	public void run() {
		long beforeTime, diffTime, sleepTime;
		beforeTime = System.currentTimeMillis();
		
		running = true;
		while(running) {
			gameUpdate();
			gameRender();
			paintScreen();
			
			diffTime = System.currentTimeMillis() - beforeTime;
			sleepTime = period - diffTime;
			
			if(sleepTime <= 0) {
				sleepTime = 5;
			}
			
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			fps = (int) (sleepTime * 10);
			beforeTime = System.currentTimeMillis();
		}
		System.exit(0);
	}
	
	private void paintScreen() {
		Graphics g;
		try {
			g = this.getGraphics();
			if (g != null && bufferImage != null) 
				g.drawImage(bufferImage, 0, 0, null);
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		} catch (Exception e) {
			System.err.println("Graphics context error: " + e);
			e.printStackTrace();
		}
	}

	private void gameUpdate() {
		
	}
	
	private void gameRender() {
		if(bufferImage == null) {
			bufferImage = createImage(PWIDTH, PHEIGHT);
			if(bufferImage == null) {
				System.err.println("renderImage is null");
				return;
			} else {
				bg = bufferImage.getGraphics();
			}
			//clear
			bg.setColor(Color.white);
			bg.fillRect(0, 0, PWIDTH, PHEIGHT);
		}
	}
	
	public int getWidth() {
		return PWIDTH;
	}

	public int getHeight() {
		return PHEIGHT;
	}
}
