import java.awt.Color;
import java.awt.Rectangle;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;

/*******************************************************************************
 * * Player class contains player information **
 ******************************************************************************/
class Player {
	public Tank tank;
	public Socket socket;
	public String name;
	public JLabel livesLabel;

	// Client player
	Player(final int x, final int y, final JFrame frame, final String name,
			final int livesPosition, final Vector<Rectangle> obstcalRect,
			final Socket socket) {

		this.tank = new Tank(x, y, frame, obstcalRect);

		this.socket = socket;
		this.name = name;
		this.livesLabel = new JLabel(this.name + " lives: "
				+ this.tank.tankLives);
		this.livesLabel.setSize(400, 12);
		this.livesLabel.setForeground(Color.RED);
		this.livesLabel.setLocation(0, livesPosition);
		frame.add(this.livesLabel, 10);
	}

	// Server player
	Player(final int x, final int y, final JFrame frame, final String name,
			final int livesPosition, final Vector<Rectangle> obstcalRect) {
		this.tank = new Tank(x, y, frame, obstcalRect);
		this.name = name;
		this.livesLabel = new JLabel(this.name + " lives: "
				+ this.tank.tankLives);
		this.livesLabel.setSize(400, 12);
		this.livesLabel.setForeground(Color.RED);
		this.livesLabel.setLocation(0, livesPosition);
		frame.add(this.livesLabel, 10);
	}
}