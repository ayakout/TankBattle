import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.JFrame;

//#############################################################################
//------------------------------2 Player---------------------------------------
//#############################################################################
class TwoPlayer {
	private HandleTwoPress handel2;
	private HandleTwoPress handel3;
	private HandleTwoPress handel4;
	private HandleTwoPress handel5;
	private HandleTwoPress handel6;
	private HandleTwoPress handel7;
	private Vector<HandleTwoPress> moveVector = new Vector<HandleTwoPress>();
	private Vector<HandleTwoPress> fireVector = new Vector<HandleTwoPress>();
	private Vector<HandleTwoPress> rightVector = new Vector<HandleTwoPress>();
	private Vector<HandleTwoPress> leftVector = new Vector<HandleTwoPress>();
	private Vector<HandleTwoPress> moveVector2 = new Vector<HandleTwoPress>();
	private Vector<HandleTwoPress> fireVector2 = new Vector<HandleTwoPress>();
	private Vector<HandleTwoPress> rightVector2 = new Vector<HandleTwoPress>();
	private Vector<HandleTwoPress> leftVector2 = new Vector<HandleTwoPress>();
	JFrame frame;
	private final Vector<Rectangle> obstcalRect;
	private Player player1;
	private Player player2;

	TwoPlayer(final JFrame xframe) {
		this.frame = xframe;
		final GameMaps newMap = new GameMaps(this.frame);
		this.obstcalRect = newMap.getObstcalRectArray();
		this.player1 = new Player(0, 28, this.frame, "Player 1", 0,
				this.obstcalRect);
		this.player2 = new Player(750, 500, this.frame, "Player 2", 14,
				this.obstcalRect);
		TankGame.pVector.add(this.player1);
		TankGame.pVector.add(this.player2);
		// final Tank tank1 = player1.tank;//new Tank(0,0,frame,obstcalRect);
		// final Tank tank2 = player2.tank;//new
		// Tank(100,100,frame,obstcalRect);

		// --------------- handel when the user release the click
		/*------------ every time the user press a button a thread is intiatiated and starts to do
		the required action (move,left,right,fire) then when the user release the button all the threads is
		killed. but to stop the thread we need to save the created thread in a vector then retrieve it and kill it. */
		// --- implementing the key lisentner
		this.frame.addKeyListener(new KeyAdapter() {
			Tank tank1 = TwoPlayer.this.player1.tank;// new
			// Tank(0,0,frame,obstcalRect);
			Tank tank2 = TwoPlayer.this.player2.tank;// new

			// Tank(100,100,frame,obstcalRect);
			// when the user release the key
			@Override
			public void keyReleased(final KeyEvent e) {
				if ((!TankGame.pVector.contains(TwoPlayer.this.player1))
						|| (!TankGame.pVector.contains(TwoPlayer.this.player2))) {
					TwoPlayer.this.player1 = null;
					TwoPlayer.this.player2 = null;
					this.tank1 = null;
					this.tank2 = null;
					TwoPlayer.this.handel2 = null;
					TwoPlayer.this.handel3 = null;
					TwoPlayer.this.handel4 = null;
					TwoPlayer.this.handel5 = null;
					TwoPlayer.this.handel6 = null;
					TwoPlayer.this.handel7 = null;
					TwoPlayer.this.moveVector = null;
					TwoPlayer.this.fireVector = null;
					TwoPlayer.this.rightVector = null;
					TwoPlayer.this.leftVector = null;
					TwoPlayer.this.moveVector2 = null;
					TwoPlayer.this.fireVector2 = null;
					TwoPlayer.this.rightVector2 = null;
					TwoPlayer.this.leftVector2 = null;
					TwoPlayer.this.frame.removeKeyListener(this);
					return;
				}

				final int key = e.getKeyCode();
				// --------------- switch case to see which key the user enter
				switch (key) {

				/* HANDEL THE FIRST TANK */
				// ------------ the user 1 wishes to stop fireing
				case (KeyEvent.VK_ENTER):
					// looping to stop all threads
					for (int i = 0; i < TwoPlayer.this.fireVector.size(); i++) {
						// handel1=(HandleTwoPress)fireVector.elementAt(i);
						// handel1.timer1.cancel();;
					}
					this.tank1.fire();
					break;

				// ------------ the user 1 wishes to stop moving
				case (KeyEvent.VK_UP):
					for (int i = 0; i < TwoPlayer.this.moveVector.size(); i++) {
						TwoPlayer.this.handel2 = (HandleTwoPress) TwoPlayer.this.moveVector
								.elementAt(i);
						TwoPlayer.this.handel2.timer1.cancel();
						;
					}
					TwoPlayer.this.moveVector.removeAllElements();
					break;

				// ------------ the user 1 wishes to stop going right
				case (KeyEvent.VK_RIGHT):
					for (int i = 0; i < TwoPlayer.this.rightVector.size(); i++) {
						TwoPlayer.this.handel3 = (HandleTwoPress) TwoPlayer.this.rightVector
								.elementAt(i);
						TwoPlayer.this.handel3.timer1.cancel();
						;
					}
					TwoPlayer.this.rightVector.removeAllElements();
					break;

				// ------------ the user 1 wishes to stop going left
				case (KeyEvent.VK_LEFT):
					for (int i = 0; i < TwoPlayer.this.leftVector.size(); i++) {
						TwoPlayer.this.handel4 = (HandleTwoPress) TwoPlayer.this.leftVector
								.elementAt(i);
						TwoPlayer.this.handel4.timer1.cancel();
						;
					}
					TwoPlayer.this.leftVector.removeAllElements();
					break;

				/* HANDEL THE SECOND TANK */
				// ------------ the user 2 wishes to stop moveing
				case (KeyEvent.VK_W):
					for (int i = 0; i < TwoPlayer.this.moveVector2.size(); i++) {
						TwoPlayer.this.handel5 = (HandleTwoPress) TwoPlayer.this.moveVector2
								.elementAt(i);
						TwoPlayer.this.handel5.timer1.cancel();
						;
					}
					TwoPlayer.this.moveVector2.removeAllElements();
					break;

				// ------------ the user 2 wishes to stop going left
				case (KeyEvent.VK_A):
					for (int i = 0; i < TwoPlayer.this.leftVector2.size(); i++) {
						TwoPlayer.this.handel6 = (HandleTwoPress) TwoPlayer.this.leftVector2
								.elementAt(i);
						TwoPlayer.this.handel6.timer1.cancel();
						;
					}
					TwoPlayer.this.leftVector2.removeAllElements();
					break;

				// ------------ the user 2 wishes to stop going right
				case (KeyEvent.VK_D):
					for (int i = 0; i < TwoPlayer.this.rightVector2.size(); i++) {
						TwoPlayer.this.handel7 = (HandleTwoPress) TwoPlayer.this.rightVector2
								.elementAt(i);
						TwoPlayer.this.handel7.timer1.cancel();
						;
					}
					TwoPlayer.this.rightVector2.removeAllElements();
					break;

				// ------------ the user 2 wishes to stop fireing
				case (KeyEvent.VK_SPACE):
					for (int i = 0; i < TwoPlayer.this.fireVector2.size(); i++) {
						// handel8=(HandleTwoPress)fireVector2.elementAt(i);
						// handel8.timer1.cancel();;
					}
					this.tank2.fire();
					break;
				}
			}

			// --------- hendel when the user press to take action
			@Override
			public void keyPressed(final KeyEvent e) {
				if ((!TankGame.pVector.contains(TwoPlayer.this.player1))
						|| (!TankGame.pVector.contains(TwoPlayer.this.player2))) {
					TwoPlayer.this.player1 = null;
					TwoPlayer.this.player2 = null;
					this.tank1 = null;
					this.tank2 = null;
					TwoPlayer.this.handel2 = null;
					TwoPlayer.this.handel3 = null;
					TwoPlayer.this.handel4 = null;
					TwoPlayer.this.handel5 = null;
					TwoPlayer.this.handel6 = null;
					TwoPlayer.this.handel7 = null;
					TwoPlayer.this.moveVector = null;
					TwoPlayer.this.fireVector = null;
					TwoPlayer.this.rightVector = null;
					TwoPlayer.this.leftVector = null;
					TwoPlayer.this.moveVector2 = null;
					TwoPlayer.this.fireVector2 = null;
					TwoPlayer.this.rightVector2 = null;
					TwoPlayer.this.leftVector2 = null;
					TwoPlayer.this.frame.removeKeyListener(this);
					return;
				}
				final int key = e.getKeyCode();
				switch (key) {

				/* HANDEL THE FIRST TANK */
				// ----------------- the first tank wishes to fire
				case (KeyEvent.VK_ENTER):
					// before starting a new thread, all thread should be
					// killed.looping to stop all threads.
					for (int i = 0; i < TwoPlayer.this.fireVector.size(); i++) {
						// handel1=(HandleTwoPress)fireVector.elementAt(i);
						// handel1.timer1.cancel();;
					}

					// start the
					// handel1 = new HandleTwoPress(tank1,1);
					// fireVector.add(handel1);
					// handel1.start();
					break;

				// ----------------- the first tank wishes to move
				case (KeyEvent.VK_UP):
					for (int i = 0; i < TwoPlayer.this.moveVector.size(); i++) {
						TwoPlayer.this.handel2 = (HandleTwoPress) TwoPlayer.this.moveVector
								.elementAt(i);
						TwoPlayer.this.handel2.timer1.cancel();
						;
					}
					TwoPlayer.this.moveVector.removeAllElements();
					TwoPlayer.this.handel2 = new HandleTwoPress(this.tank1, 0);
					TwoPlayer.this.moveVector.add(TwoPlayer.this.handel2);
					TwoPlayer.this.handel2.start();
					break;

				// ----------------- the first tank wishes to go right
				case (KeyEvent.VK_RIGHT):
					for (int i = 0; i < TwoPlayer.this.rightVector.size(); i++) {
						TwoPlayer.this.handel3 = (HandleTwoPress) TwoPlayer.this.rightVector
								.elementAt(i);
						TwoPlayer.this.handel3.timer1.cancel();
						;
					}
					TwoPlayer.this.rightVector.removeAllElements();
					TwoPlayer.this.handel3 = new HandleTwoPress(this.tank1, 2);
					TwoPlayer.this.rightVector.add(TwoPlayer.this.handel3);
					TwoPlayer.this.handel3.start();
					break;

				// ----------------- the first tank wishes to go left
				case (KeyEvent.VK_LEFT):
					for (int i = 0; i < TwoPlayer.this.leftVector.size(); i++) {
						TwoPlayer.this.handel4 = (HandleTwoPress) TwoPlayer.this.leftVector
								.elementAt(i);
						TwoPlayer.this.handel4.timer1.cancel();
						;
					}
					TwoPlayer.this.leftVector.removeAllElements();
					TwoPlayer.this.handel4 = new HandleTwoPress(this.tank1, 3);
					TwoPlayer.this.leftVector.add(TwoPlayer.this.handel4);
					TwoPlayer.this.handel4.start();
					break;

				/* HANDEL THE FIRST TANK */
				// ----------------- the second tank wishes to move
				case (KeyEvent.VK_W):
					for (int i = 0; i < TwoPlayer.this.moveVector2.size(); i++) {
						TwoPlayer.this.handel5 = (HandleTwoPress) TwoPlayer.this.moveVector2
								.elementAt(i);
						TwoPlayer.this.handel5.timer1.cancel();
						;
					}
					TwoPlayer.this.moveVector2.removeAllElements();
					TwoPlayer.this.handel5 = new HandleTwoPress(this.tank2, 0);
					TwoPlayer.this.moveVector2.add(TwoPlayer.this.handel5);
					TwoPlayer.this.handel5.start();
					break;

				// ----------------- the second tank wishes to go left
				case (KeyEvent.VK_A):
					for (int i = 0; i < TwoPlayer.this.leftVector2.size(); i++) {
						TwoPlayer.this.handel6 = (HandleTwoPress) TwoPlayer.this.leftVector2
								.elementAt(i);
						TwoPlayer.this.handel6.timer1.cancel();
						;
					}
					TwoPlayer.this.leftVector2.removeAllElements();
					TwoPlayer.this.handel6 = new HandleTwoPress(this.tank2, 3);
					TwoPlayer.this.leftVector2.add(TwoPlayer.this.handel6);
					TwoPlayer.this.handel6.start();
					break;

				// ----------------- the second tank wishes to go right
				case (KeyEvent.VK_D):
					for (int i = 0; i < TwoPlayer.this.rightVector2.size(); i++) {
						TwoPlayer.this.handel7 = (HandleTwoPress) TwoPlayer.this.rightVector2
								.elementAt(i);
						TwoPlayer.this.handel7.timer1.cancel();
						;
					}
					TwoPlayer.this.rightVector2.removeAllElements();
					TwoPlayer.this.handel7 = new HandleTwoPress(this.tank2, 2);
					TwoPlayer.this.rightVector2.add(TwoPlayer.this.handel7);
					TwoPlayer.this.handel7.start();
					break;

				// ----------------- the second tank wishes to fire
				case (KeyEvent.VK_SPACE):
					for (int i = 0; i < TwoPlayer.this.fireVector2.size(); i++) {
						// handel8=(HandleTwoPress)fireVector2.elementAt(i);
						// handel8.timer1.cancel();;
					}
					// handel8 = new HandleTwoPress(tank2,1);
					// fireVector2.add(handel8);
					// handel8.start();
					break;
				}
			}
		});
	}
}
