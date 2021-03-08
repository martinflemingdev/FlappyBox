package flappyBox;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class FlappyBox implements ActionListener, MouseListener {

	public static FlappyBox flappyBox;
	public final int GAME_WIDTH = 800;
	public final int GAME_HEIGHT = 800;
	public double score;

	public int ticks;
	public int yMotion;
	public boolean gameOver;
	public boolean started;
	public int speed;

	public Renderer renderer;
	public Rectangle box;
	public ArrayList<Rectangle> columns;
	public Random random;

	public static void main(String[] args) {
		flappyBox = new FlappyBox();
	}
	
	public FlappyBox() {

		// 20 is delay in ms, "this" is the actionListener that FlappyBox is implementing
		Timer timer = new Timer(20, this);

		renderer = new Renderer();
		random = new Random();

		JFrame jFrame = new JFrame();

		jFrame.add(renderer);
		jFrame.setTitle("Flappy Box");
		jFrame.addMouseListener(this);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setSize(GAME_WIDTH, GAME_HEIGHT);
		jFrame.setResizable(false);
		jFrame.setVisible(true);

		// sets starting coordinates for box: x and y are centered, dimensions are 20x20
		box = new Rectangle( (GAME_WIDTH / 2 ) - 10 , (GAME_HEIGHT / 2) - 10 , 20, 20);
		columns = new ArrayList<Rectangle>();

		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);

		// starts actionPerformed inside timer instance
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		speed = 10;
		ticks++;

		if (started) {

			// every other tick, yMotion variable is increased up to max 14
			if (ticks % 2 == 0 && yMotion < 15) {
				yMotion += 2;
			}

			// the columns created are moved into frame
			for (int i = 0; i < columns.size(); i++) {
				Rectangle column = columns.get(i);

				//decreases column.x by speed so every column moves from right to left on x axis
				column.x -= speed;
			}

			// removes the columns once they cross the screen so they don't go forever
			for (int i = 0; i < columns.size(); i++) {
				Rectangle column = columns.get(i);

				if (column.x + column.width < 0) {
					columns.remove(column);

					if (column.y == 0) {
						// adds more columns in endless loop
						addColumn(false);
					}
				}
			}

			// yMotion is always going up to 15 which means the box's y axis will always be "falling"
			box.y += yMotion;

			// scoring and collision
			for (Rectangle column : columns) {

				// if the center point of the box and any columns pass each other, increase the score
				if (box.x + box.width / 2 > column.x + column.width / 2 - 10 && 
					box.x + box.width / 2 < column.x + column.width / 2 + 10) {
					score += .5; // counts twice, once for each column (top and bottom)
				}

				// if the box and any column touch, game over
				if (column.intersects(box)) {
					gameOver = true;

					// sets box's x coordinates to columns', meaning it appears the 
					// columns will push the box from right to left off screen
					box.x = column.x - box.width;
				}	
			}
			
			// if the box hits the ground or goes up off screen...game over
			if (box.y > GAME_HEIGHT - 120 || box.y < 0) {
				gameOver = true;
			}

			// if the box hits the ground (GAME_HEIGHT-120) it stays there, can't fall lower
			if (box.y + yMotion >= GAME_HEIGHT - 120) {
				box.y = GAME_HEIGHT - 120 - box.height;
			}
		}
		// renders all colors 
		renderer.repaint();
	}

	/**
	 * @param start
	 */
	public void addColumn(boolean start) {

		int gap = 300;

		// makes the gap smaller over time
		if (ticks > 200) {
			gap -= 25;
		}
		if (ticks > 400) {
			gap -= 25;
		}
		if (ticks > 600) {
			gap -= 25;
		}
		if (ticks > 800) {
			gap -= 25;
		}

		int columnWidth = 100;
		int columnHeight = 50 + random.nextInt(300);

		// adds a top and bottom part of column to create a "single" column with gap in between
		// spreads out the columns in 300 pixel intervals, called 4 times in flappyBox class
		if (start) {
			columns.add(new Rectangle(GAME_WIDTH + columnWidth + columns.size() * 300, GAME_HEIGHT - columnHeight - 120, columnWidth, columnHeight));
			columns.add(new Rectangle(GAME_WIDTH + columnWidth + (columns.size() -1 ) * 300, 0, columnWidth, GAME_HEIGHT - columnHeight - gap));
		}
		// looks at last column and takes it's x position + 600 pixels, continues to create columns
		else {
			columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, GAME_HEIGHT - columnHeight - 120, columnWidth, columnHeight));
			columns.add(new Rectangle(columns.get(columns.size() - 1).x , 0, columnWidth , GAME_HEIGHT - columnHeight - gap));
		}
	}

	public void repaint(Graphics g) {

		// background color of entire frame
		g.setColor(Color.cyan);
		g.fillRect(0,0, GAME_WIDTH, GAME_HEIGHT);

		// color of dirt layer
		g.setColor(Color.pink);
		g.fillRect(0, GAME_HEIGHT - 120, GAME_WIDTH, 20);

		// color of ground
		g.setColor(Color.orange);
		g.fillRect(0, GAME_HEIGHT - 100, GAME_WIDTH, 120);

		// color of box
		g.setColor(Color.red);
		g.fillRect(box.x, box.y, box.width, box.height);

		// color the columns
		for (Rectangle column : columns) {
			paintColumn(g, column);
		}

		// color for screen text
		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, 100));

		if (!started) {
		// sets starting point at x=100 and y=800/2 (so centers) and - 50 to compensate for size
			g.drawString("Click to Start!", 75, (GAME_HEIGHT / 2) - (100/2)) ;
		}

		if (gameOver) {
			g.drawString("GAME OVER!", 75 , GAME_HEIGHT/2 - 50);
		}

		// displays the score once the game starts
		if (!gameOver && started) {
			g.drawString((String.valueOf((int)score)), GAME_WIDTH / 2 -25 , 100);
		}
	}
	
	public void paintColumn(Graphics g, Rectangle column) {

		g.setColor(Color.green.darker().darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}

	public void action() {

		if (gameOver) {

			// resets all variables
			box = new Rectangle(GAME_WIDTH / 2 - 10, GAME_HEIGHT / 2 - 10, 20, 20);
			columns.clear();
			yMotion = 0;
			score = 0;
			ticks = 0;

			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);

			gameOver = false;
		}

		if (!started) {

			started = true;
		} 
		else if (!gameOver) {
			// stops box from falling so it can jump instead of fighting descent and just falling less
			if (yMotion > 0) {
				yMotion = 0;
			}
			// goes up 10 pixels when mouse pressed
			yMotion -= 10;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		action();
	}
	
	// empty methods here because of interface requirements \/\/
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}