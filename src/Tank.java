import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

//#############################################################################
class Tank extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final ImageIcon[] iconArr = {
			new ImageIcon(this.getClass().getResource("tankup.gif")),
			new ImageIcon(this.getClass().getResource("tankupright.gif")), new ImageIcon(this.getClass().getResource("tankright.gif")),
			new ImageIcon(this.getClass().getResource("tankdownright.gif")), new ImageIcon(this.getClass().getResource("tankdown.gif")),
			new ImageIcon(this.getClass().getResource("tankdownleft.gif")), new ImageIcon(this.getClass().getResource("tankleft.gif")),
			new ImageIcon(this.getClass().getResource("tankupleft.gif")) };

	private final ImageIcon[] iconExplode = { new ImageIcon(this.getClass().getResource("exp5.gif")),
			new ImageIcon(this.getClass().getResource("exp4.gif")), new ImageIcon(this.getClass().getResource("exp3.gif")),
			new ImageIcon(this.getClass().getResource("exp2.gif")), new ImageIcon(this.getClass().getResource("exp1.gif")) };
	public int tankWidth, tankHeight, tankFrontX, tankFrontY, anchorX, anchorY;
	public int maxX, maxY, tankPosition = 2;
	public int tankLives = 5;
	private final Insets insets;
	private final JFrame frame;
	private final Container contentPane;
	private AudioClip fireSound;
	private AudioClip explodeSound;
	private final URL fireSoundUrl = this.getClass().getResource("tank_fire_rocket.wav");
	private final URL explodeSoundUrl = this.getClass().getResource("tank_explode.wav");
	private final Vector<Rectangle> obstcalRect;

	Tank(final int x, final int y, final JFrame frame, final Vector<Rectangle> obstcalRect) {
		this.obstcalRect = obstcalRect;
		this.setName("tanker");
		this.frame = frame; 
		try {
			this.fireSound = Applet.newAudioClip(fireSoundUrl);
			this.explodeSound = Applet.newAudioClip(explodeSoundUrl);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		this.contentPane = frame.getContentPane();
		this.tankWidth = this.iconArr[0].getIconWidth();
		this.tankHeight = this.iconArr[0].getIconHeight();
		this.tankFrontX = x + this.tankWidth;
		this.tankFrontY = y + this.tankHeight / 2;
		this.anchorX = x;
		this.anchorY = y;
		this.insets = frame.getInsets();
		this.maxX = frame.getWidth() - this.tankWidth - this.insets.left
				- this.insets.right;
		this.maxY = frame.getHeight() - this.tankHeight - this.insets.top
				- this.insets.bottom - /* Menubar */30;
		this.setIcon(this.iconArr[this.tankPosition]);
		this.setSize(this.tankWidth, this.tankHeight);
		this.setLocation(x, y);
		frame.add(this, 1);// tank in level 0 and that is high level on the
		// bullet and that enable the bullet to see it
		frame.repaint();
	}

	// #############################################################################################
	// ___________________________________ class thread to move bullets
	// ___________________________
	// #############################################################################################
	class Bullet extends Thread {
		JLabel label;
		Tank destroyTank;
		Tank myTank;
		Component comp;
		private int bulletX;
		private int bulletY;
		// private int tankWidth;
		private final int dir;
		private final int newX = Tank.this.maxX + Tank.this.tankWidth;
		private final int newY = Tank.this.maxY + Tank.this.tankHeight;
		private final Vector<Rectangle> obstcalRect;

		Bullet(final int xpos, final int ypos, final int dir,
				final Tank myTank, final Vector<Rectangle> obstcalRect) {
			this.obstcalRect = obstcalRect;
			this.myTank = myTank;
			this.label = new JLabel();
			final Toolkit kit = Toolkit.getDefaultToolkit();
			Image img1 = kit.getImage(this.getClass().getResource("reddot1.gif"));
			img1 = img1.getScaledInstance(10, 10, Image.SCALE_DEFAULT);
			final ImageIcon icon1 = new ImageIcon(img1);
			this.label.setSize(10, 10);
			this.label.setIcon(icon1);
			Tank.this.frame.add(this.label, 10); // bullet on level 1 and
			// that a lower level from
			// the tank and it can see
			// it
			Tank.this.frame.validate();
			this.dir = dir;
			this.bulletX = Tank.this.tankFrontX;
			this.bulletY = Tank.this.tankFrontY;
			Tank.this.fireSound.play();
		}

		// *****-> what will be executed at the calling of the start in the
		// thread.
		@Override
		public void run() {
			final Timer timer1 = new Timer();
			final TimerTask task = new TimerTask() {
				@Override
				public void run() {
					switch (Bullet.this.dir) {
					case 1: {
						Bullet.this.bulletY -= 5;
						break;
					}
					case 2: {
						Bullet.this.bulletX += 5;
						Bullet.this.bulletY -= 5;
						break;
					}
					case 3: {
						Bullet.this.bulletX += 5;
						break;
					}
					case 4: {
						Bullet.this.bulletX += 5;
						Bullet.this.bulletY += 5;
						break;
					}
					case 5: {
						Bullet.this.bulletY += 5;
						break;
					}
					case 6: {
						Bullet.this.bulletX -= 5;
						Bullet.this.bulletY += 5;
						break;
					}
					case 7: {
						Bullet.this.bulletX -= 5;
						break;
					}
					case 8: {
						Bullet.this.bulletX -= 5;
						Bullet.this.bulletY -= 5;
						break;
					}
					default: {
						break;
					}
					}
					Bullet.this.label.setLocation(Bullet.this.bulletX,
							Bullet.this.bulletY);
					Bullet.this.comp = Tank.this.contentPane.getComponentAt(
							Bullet.this.bulletX, Bullet.this.bulletY);
					Rectangle bulletRect = Bullet.this.label.getBounds();
					for (int i = 0; i < Bullet.this.obstcalRect.size(); i++) {
						if (bulletRect
								.intersects((Rectangle) Bullet.this.obstcalRect
										.elementAt(i))) {
							Bullet.this.label.setIcon(null);
							timer1.cancel();
						}
					}
					// if (comp != myTank) added to solve the problem of self
					// exploding
					// when too many bullet threads and the tank become faster
					// than the bullet
					if ((Bullet.this.comp instanceof Tank)
							&& (Bullet.this.comp != Bullet.this.myTank)) {
						Bullet.this.destroyTank = (Tank) Bullet.this.comp;
						Bullet.this.label.setIcon(null);
						if (Bullet.this.destroyTank.tankLives > 0) {
							Bullet.this.destroyTank.tankLives--;
							TankGame.updateLives(Bullet.this.destroyTank);
						}
						if (Bullet.this.destroyTank.tankLives == 0) {
							Bullet.this.destroyTank.explode();
							TankGame.remove(Bullet.this.destroyTank);
						}
						timer1.cancel();
					}
					if ((Bullet.this.newX < Bullet.this.bulletX)
							|| (Bullet.this.newY < Bullet.this.bulletY)
							|| (Bullet.this.bulletY < 0)
							|| (Bullet.this.bulletX < 0)) {
						timer1.cancel();
						Bullet.this.label.setIcon(null);
					}
				}
			};
			timer1.scheduleAtFixedRate(task, 1, 10);
		}
	}

	// #########################################################################
	@Override
	public void setLocation(final int x, final int y) {
		super.setLocation(x, y);
		this.anchorX = x;
		this.anchorY = y;
		this.updateFrontPoint();
	}

	// #########################################################################
	public boolean move() {
		boolean moved = false;
		switch (this.tankPosition) {
		case (0):
			if ((this.anchorY > 5)
					&& !(this.contentPane.getComponentAt(this.tankFrontX,
							this.tankFrontY - 5) instanceof Tank)
					&& !(this.contentPane.getComponentAt(this.tankFrontX,
							this.tankFrontY - 5) instanceof Obstcal)) {
				this.setLocation(this.anchorX, this.anchorY -= 5);
				moved = true;
			}
			break;
		case (1):
			if ((this.anchorY > 5)
					&& (this.anchorX < this.maxX)
					&& !(this.contentPane.getComponentAt(this.tankFrontX + 5,
							this.tankFrontY - 5) instanceof Tank)
					&& !(this.contentPane.getComponentAt(this.tankFrontX + 5,
							this.tankFrontY - 5) instanceof Obstcal)) {
				this.setLocation(this.anchorX += 5, this.anchorY -= 5);
				moved = true;
			}
			break;
		case (2):
			if ((this.anchorX < this.maxX)
					&& !(this.contentPane.getComponentAt(this.tankFrontX + 5,
							this.tankFrontY) instanceof Tank)
					&& !(this.contentPane.getComponentAt(this.tankFrontX + 5,
							this.tankFrontY) instanceof Obstcal)) {
				this.setLocation(this.anchorX += 5, this.anchorY);
				moved = true;
			}
			break;
		case (3):
			if ((this.anchorX < this.maxX)
					&& (this.anchorY < this.maxY)
					&& !(this.contentPane.getComponentAt(this.tankFrontX + 5,
							this.tankFrontY + 5) instanceof Tank)
					&& !(this.contentPane.getComponentAt(this.tankFrontX + 5,
							this.tankFrontY + 5) instanceof Obstcal)) {
				this.setLocation(this.anchorX += 5, this.anchorY += 5);
				moved = true;
			}
			break;
		case (4):
			if ((this.anchorY < this.maxY)
					&& !(this.contentPane.getComponentAt(this.tankFrontX,
							this.tankFrontY + 5) instanceof Tank)
					&& !(this.contentPane.getComponentAt(this.tankFrontX,
							this.tankFrontY + 5) instanceof Obstcal)) {
				this.setLocation(this.anchorX, this.anchorY += 5);
				moved = true;
			}
			break;
		case (5):
			if ((this.anchorY < this.maxY)
					&& (this.anchorX > 5)
					&& !(this.contentPane.getComponentAt(this.tankFrontX - 5,
							this.tankFrontY + 5) instanceof Tank)
					&& !(this.contentPane.getComponentAt(this.tankFrontX - 5,
							this.tankFrontY + 5) instanceof Obstcal)) {
				this.setLocation(this.anchorX -= 5, this.anchorY += 5);
				moved = true;
			}
			break;
		case (6):
			if ((this.anchorX > 5)
					&& !(this.contentPane.getComponentAt(this.tankFrontX - 5,
							this.tankFrontY) instanceof Tank)
					&& !(this.contentPane.getComponentAt(this.tankFrontX - 5,
							this.tankFrontY) instanceof Obstcal)) {
				this.setLocation(this.anchorX -= 5, this.anchorY);
				moved = true;
			}
			break;
		case (7):
			if ((this.anchorX > 5)
					&& (this.anchorY > 5)
					&& !(this.contentPane.getComponentAt(this.tankFrontX - 5,
							this.tankFrontY - 5) instanceof Tank)
					&& !(this.contentPane.getComponentAt(this.tankFrontX - 5,
							this.tankFrontY - 5) instanceof Obstcal)) {
				this.setLocation(this.anchorX -= 5, this.anchorY -= 5);
				moved = true;
			}
			break;
		}
		this.updateFrontPoint();
		return moved;
	}

	// #########################################################################
	public void rotateRight() {
		this.tankPosition++;
		this.tankPosition %= 8;
		this.setIcon(this.iconArr[this.tankPosition]);
		this.updateFrontPoint();
	}

	// #########################################################################
	public void rotateLeft() {
		this.tankPosition += 7;
		this.tankPosition %= 8;
		this.setIcon(this.iconArr[this.tankPosition]);
		this.updateFrontPoint();
	}

	// #########################################################################
	// Update tankFrontX and tankFrontY every key move
	private void updateFrontPoint() {
		switch (this.tankPosition) {
		case (0):
			this.tankFrontX = this.anchorX + (this.tankWidth / 2);
			this.tankFrontY = this.anchorY;
			break;
		case (1):
			this.tankFrontX = this.anchorX + this.tankWidth;
			this.tankFrontY = this.anchorY;
			break;
		case (2):
			this.tankFrontX = this.anchorX + this.tankWidth;
			this.tankFrontY = this.anchorY + (this.tankHeight / 2);
			break;
		case (3):
			this.tankFrontX = this.anchorX + this.tankWidth;
			this.tankFrontY = this.anchorY + this.tankHeight;
			break;
		case (4):
			this.tankFrontX = this.anchorX + (this.tankWidth / 2);
			this.tankFrontY = this.anchorY + this.tankHeight;
			break;
		case (5):
			this.tankFrontX = this.anchorX;
			this.tankFrontY = this.anchorY + this.tankHeight;
			break;
		case (6):
			this.tankFrontX = this.anchorX;
			this.tankFrontY = this.anchorY + (this.tankHeight / 2);
			break;
		case (7):
			this.tankFrontX = this.anchorX;
			this.tankFrontY = this.anchorY;
			break;
		}
	}

	// #########################################################################
	public void explode() {
		this.explodeSound.play();
		for (final ImageIcon i : this.iconExplode) {
			this.setIcon(i);
			try {
				Thread.sleep(100);
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
		this.frame.remove(this);
		this.frame.repaint();
	}

	// #########################################################################
	public void fire() {
		final Bullet bullet = new Bullet(this.anchorX, this.anchorY,
				this.tankPosition + 1, this, this.obstcalRect);
		bullet.start();
	}
}