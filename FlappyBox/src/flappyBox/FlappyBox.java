package flappyBox;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class FlappyBox implements ActionListener {

	public static FlappyBox flappyBox;
	public final int WIDTH = 800;
	public final int HEIGHT = 800;
	
	public int ticks;
	public int yMotion;
	
	public Renderer renderer;
	public Rectangle box;
	public ArrayList<Rectangle> columns;
	public Random random;
	
	
	public FlappyBox() {
		
		Timer timer = new Timer(20, this);
		JFrame jFrame = new JFrame();
		
		renderer = new Renderer();
		
		jFrame.setTitle("Flappy Box");
		jFrame.add(renderer);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setSize(WIDTH, HEIGHT);
		jFrame.setResizable(false);
		jFrame.setVisible(true);
		
		box = new Rectangle( (WIDTH / 2) - 10 , (HEIGHT / 2) - 10 , 20, 20);
		columns = new ArrayList<Rectangle>();
		
		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);
		
		
		timer.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		renderer.repaint();

	}
	
	public void addColumn(boolean start) {
		int space = 300;
		int width = 100;
		int height = 50 + random.nextInt(300);
		
		if (start) {
			columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(WIDTH + width + (columns.size() -1 ) * 300, 0, width, HEIGHT - height - space));
		}
		else {
			columns.add(new Rectangle(columns.get(columns.size() - 1).x +600, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(columns.get(columns.size() - 1).x , 0, width, HEIGHT - height - space));
		}
		
	}
	

	public void paintColumn(Graphics g, Rectangle column) {
		g.setColor(Color.BLACK);
		g.fillRect(column.x, column.y, column.width, column.height);
	}
	
	
	public void repaint(Graphics g) {

		// background color of entire frame
		g.setColor(Color.cyan);
		g.fillRect(0,0, WIDTH, HEIGHT);
		
		// color of dirt layer
		g.setColor(Color.pink);
		g.fillRect(0, HEIGHT - 120, WIDTH, 20);
		
		// color of ground
		g.setColor(Color.orange);
		g.fillRect(0, HEIGHT - 100, WIDTH, 120);
		
		// color of box
		g.setColor(Color.red);
		g.fillRect(box.x, box.y, box.width, box.height);
		
	}
	
	public static void main(String[] args) {
		
		flappyBox = new FlappyBox();
		
	}
	
	
	
}
