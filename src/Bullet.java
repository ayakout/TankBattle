import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class Bullet// extends JPanel
{
	int xPosBullet = 0;
	int yPosBullet = 0;
	static Image test;
	JFrame frame = new JFrame("Bullet");

	Bullet() {
		// validate();
		this.frame.setLayout(null);
		System.out.println("iam trying cc");
		final JLabel label1 = new JLabel("");
		final Toolkit kit = Toolkit.getDefaultToolkit();
		final Dimension d = kit.getScreenSize();
		final double width = d.getWidth();
		final double height = d.getHeight();

		this.frame.setLocation((int) (width / 4), (int) (height / 4));
		this.frame.setSize(500, 500);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Image img1 = kit.getImage("bullet.jpg");
		img1 = img1.getScaledInstance(10, 10, Image.SCALE_DEFAULT);
		final ImageIcon icon1 = new ImageIcon(img1);
		// test = test.getScaledInstance(70,70,Image.SCALE_DEFAULT);
		// icon1.paintIcon(img1,null,50,50);
		label1.setSize(10, 10);
		label1.setIcon(icon1);

		this.frame.add(label1);

		this.frame.setVisible(true);
		for (int i = 1; i < 9; i++) {
			final Move moveBullet = new Move(200, 200, i);
			moveBullet.start();
		}

		// testGraph.repaint();
	}

	Bullet(final int tankWidth, final int xPosTank, final int yPosTank,
			final double RotangleTank) {
		final double widthTemp = tankWidth;
		this.xPosBullet = (int) (widthTemp * Math.cos(RotangleTank));
		this.yPosBullet = (int) (widthTemp * Math.sin(RotangleTank));
	}

	public static void main(final String args[]) {
		final Bullet newBullet = new Bullet();
	}

	class MyGraphics extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void paintComponent(final Graphics g) {
			super.paintComponent(g);
			final Graphics2D g2 = (Graphics2D) g;
			System.out.println("iam trying");
			g2.drawImage(test, 50, 50, null);
		}
	}

	// #############################################################################################
	// ___________________________________ class thread to move objects
	// ___________________________
	// #############################################################################################
	class Move extends Thread {
		JLabel label;
		private int bulletX;
		private int bulletY;
		private final int tankWidth = 70;
		private final int dir;
		private int newX = 0;
		private int newY = 0;

		Move(final int xpos, final int ypos, final int dir) {
			this.label = new JLabel();
			final Toolkit kit = Toolkit.getDefaultToolkit();
			Image img1 = kit.getImage("reddot1.gif");
			img1 = img1.getScaledInstance(10, 10, Image.SCALE_DEFAULT);
			final ImageIcon icon1 = new ImageIcon(img1);
			this.label.setSize(10, 10);
			this.label.setIcon(icon1);
			Bullet.this.frame.add(this.label);
			this.dir = dir;
			this.bulletX = xpos;
			this.bulletY = ypos;
			Dimension screenSize;
			screenSize = Bullet.this.frame.getSize();
			this.newX = (int) screenSize.getHeight();
			this.newY = (int) screenSize.getWidth();
			System.out.println("hieght is " + this.newX + "width is "
					+ this.newY);
			// this.label=l;
			switch (dir) {
			case 1: {
				this.bulletX = xpos + (this.tankWidth / 2);
				this.bulletY = ypos;
				break;
			}
			case 2: {
				this.bulletX = xpos + this.tankWidth;
				this.bulletY = ypos;
				break;
			}
			case 3: {
				this.bulletX = xpos + this.tankWidth;
				this.bulletY = ypos + (this.tankWidth / 2);
				break;
			}
			case 4: {
				this.bulletX = xpos + this.tankWidth;
				this.bulletY = ypos + this.tankWidth;
				break;
			}
			case 5: {
				this.bulletX = xpos + (this.tankWidth / 2);
				this.bulletY = ypos + this.tankWidth;
				break;
			}
			case 6: {
				this.bulletX = xpos;
				this.bulletY = ypos + this.tankWidth;
				break;
			}
			case 7: {
				this.bulletX = xpos;
				this.bulletY = ypos + (this.tankWidth / 2);
				break;
			}
			case 8: {
				this.bulletX = xpos;
				this.bulletY = ypos;
				break;
			}
			default: {
				break;
			}
			}
		}

		// ******-> get the x position for the bulllet
		public int getBulletX() {
			return this.bulletX;
		}

		// ******-> get the y position for the bulllet
		public int getBulletY() {
			return this.bulletY;
		}

		// *****-> what will be excuted at the calling of the start in the
		// thread.
		@Override
		public void run() {
			final Timer timer1 = new Timer();
			final TimerTask task = new TimerTask() {
				@Override
				public void run() {
					switch (Move.this.dir) {
					case 1: {
						Move.this.bulletY--;
						break;
					}
					case 2: {
						Move.this.bulletX++;
						Move.this.bulletY--;
						break;
					}
					case 3: {
						Move.this.bulletX++;
						break;
					}
					case 4: {
						Move.this.bulletX++;
						Move.this.bulletY++;
						break;
					}
					case 5: {
						Move.this.bulletY++;
						break;
					}
					case 6: {
						Move.this.bulletX--;
						Move.this.bulletY++;
						break;
					}
					case 7: {
						Move.this.bulletX--;
						break;
					}
					case 8: {
						Move.this.bulletX--;
						Move.this.bulletY--;
						break;
					}
					default: {
						break;
					}
					}
					// if (bulletX<newX) { bulletX++; }
					// if (bulletX>newX) { bulletX--; }
					// if (bulletY<newY) { bulletY++; }
					// if (bulletY>newY) { bulletY--; }
					Move.this.label.setLocation(Move.this.bulletX,
							Move.this.bulletY);
					System.out.println("label 1 xpos= " + Move.this.bulletX
							+ " ypos = " + Move.this.bulletY + " x = "
							+ Move.this.newX + " y = " + Move.this.newY);
					if ((Move.this.newX == Move.this.bulletX)
							|| (Move.this.newY == Move.this.bulletY)
							|| (Move.this.bulletY == 0)
							|| (Move.this.bulletX == 0)) {
						timer1.cancel();
						Move.this.label.setIcon(null);
					}
				}
			};
			timer1.scheduleAtFixedRate(task, 1, 1000);
		}
	}
}
