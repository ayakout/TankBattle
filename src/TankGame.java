import static java.lang.System.exit;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

class TankGame implements ActionListener {
	private HandleTwoPress handel2;
	private HandleTwoPress handel3;
	private HandleTwoPress handel4;
	private final Vector<HandleTwoPress> moveVector = new Vector<HandleTwoPress>();
	private final Vector<HandleTwoPress> rightVector = new Vector<HandleTwoPress>();
	private final Vector<HandleTwoPress> leftVector = new Vector<HandleTwoPress>();

	// GUI
	private final JFrame frame = new JFrame("Tank Game verion 0.1");
	private final ImageIcon background = new ImageIcon(this.getClass().getResource("logo.jpg"));
	private final JLabel firstBackground = new JLabel(this.background);
	private static JLabel inGameMsg = new JLabel("New Game",
			SwingConstants.CENTER);
	private final Container contentPane;
	private final ImageIcon icon = new ImageIcon("tankright.gif");

	// Menu
	private final JMenuBar menuBar = new JMenuBar();

	private final JMenu gameMenu = new JMenu("Game");
	private final JMenuItem singlePlayItem = new JMenuItem("Single player game");
	private final JMenuItem twoPlayItem = new JMenuItem("Two players game");
	private final JMenuItem exitItem = new JMenuItem("Exit");

	private final JMenu networkMenu = new JMenu("NetworkGame");
	private final JMenuItem serverItem = new JMenuItem(
			"Start new multiplayer game");
	private final JMenuItem clientItem = new JMenuItem("Join server");

	private final JMenu helpMenu = new JMenu("Help");
	private final JMenuItem aboutItem = new JMenuItem("About");

	// Game map
	private GameMaps newMap;
	private Vector<Rectangle> obstcalRect;

	// Winning sound
	private static AudioClip winSound;

	private TwoPlayer twoPlayer;
	private SinglePlayer singlePlayer;

	/**
	 * Players vector used to save players only Server and client add players ro
	 * this vector
	 */
	public static Vector<Player> pVector = new Vector<Player>();

	// #########################################################################
	/**
	 * Method to be called by bullet object at tank collision to remove the
	 * player from the vector
	 */
	public static void remove(final Tank tank) {
		// Search the vector for tank owner and remove him
		for (final Player p : pVector) {
			if (p.tank == tank) {
				pVector.remove(p);
				if (pVector.size() == 1) {
					winSound.play();
					inGameMsgThread = new ShowInGameMsg(
							pVector.firstElement().name + " has won the game");
					inGameMsgThread.start();
					pVector.removeAllElements();
				}
				break;
			}
		}
	}

	// #########################################################################
	/**
	 * updateLives update player live on screen upon change
	 */
	public static void updateLives(final Tank tank) {
		for (final Player p : pVector) {
			if (p.tank == tank) {
				p.livesLabel.setText(p.name + " lives: " + p.tank.tankLives);
				break;
			}
		}
	}

	// #########################################################################
	/**
	 * FlashTank used to flash player tank at game start
	 */
	class FlashTank extends Thread {
		private final Tank tank;

		FlashTank(final Tank tank) {
			this.tank = tank;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(500);
				this.tank.setVisible(false);
				Thread.sleep(500);
				this.tank.setVisible(true);
				Thread.sleep(500);
				this.tank.setVisible(false);
				Thread.sleep(500);
				this.tank.setVisible(true);
			} catch (final Exception ex) {
			}
		}
	}

	// #########################################################################
	/**
	 * Show in game messages
	 */
	static class ShowInGameMsg extends Thread {
		String msg;

		ShowInGameMsg(final String msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			inGameMsg.setText(this.msg);
			inGameMsg.setVisible(true);
			try {
				Thread.sleep(3000);
			} catch (final Exception ex) {
			}
			inGameMsg.setVisible(false);
		}
	}

	private static ShowInGameMsg inGameMsgThread;

	/**
	 * Server class handles network game
	 */
	class Server {
		// Network
		private static final int PORT = 2525;
		private ServerSocket sc;

		// Main player
		private Player me;
		private GameData gameData;

		/**
		 * Players sockets vector that will be used to send player move over the
		 * network to all players even if the die, he can still watch the game
		 */
		private final Vector<ObjectOutputStream> oosVector = new Vector<ObjectOutputStream>();

		/**
		 * Concurrent queue to handle player threads adding data at the same
		 * time
		 */
		private final ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();

		// #########################################################################
		/**
		 * Remote players accepting thread
		 */
		class AcceptThread extends Thread {
			private double rand;
			private int playerX, playerY;
			private GameControl gameControl;
			private Scanner scan;
			private ObjectOutputStream oos;
			private String playerName;
			private int livesPosition = 14;

			@Override
			public void run() {
				while (true) {
					try {
						final Socket socket = Server.this.sc.accept();
						// Further clients recieve must be blocked till this
						// completes
						// Finding unccupied place on the map
						do {
							this.rand = Math.random();
							this.playerX = (int) (this.rand * 750);
							this.playerY = (int) (this.rand * 500);
						} while (TankGame.this.contentPane.getComponentAt(
								this.playerX, this.playerY) instanceof Tank);

						// Recieve player name
						try {
							this.scan = new Scanner(socket.getInputStream());
							this.oos = new ObjectOutputStream(socket
									.getOutputStream());
						} catch (final Exception ex) {
							ex.printStackTrace();
						}

						// Checking name uniqueness
						nameLoop: while (true) {
							this.playerName = this.scan.nextLine();
							for (final Player p : pVector) {
								if (p.name.equals(this.playerName)) {
									this.oos
											.writeInt(GameControl.PLAYER_NAME_INUSE);
									this.oos.reset();
									continue nameLoop;
								}
							}
							this.oos.writeInt(GameControl.NEW_PLAYER_APPROVAL);
							break;
						}

						Player player = new Player(this.playerX, this.playerY,
								TankGame.this.frame, this.playerName,
								this.livesPosition, TankGame.this.obstcalRect,
								socket);
						this.livesPosition += 14;

						// Handshaking with client
						// Sending all in game players to new player
						for (final Player p : pVector) {
							this.gameControl = new GameControl(
									GameControl.LOAD_IN_GAME_PLAYERS, p);
							this.oos.writeObject(this.gameControl);
						}

						// Sending approval to new player
						this.gameControl = new GameControl(
								GameControl.NEW_PLAYER_APPROVAL, player);
						this.oos.writeObject(this.gameControl);

						// Notify all remote players that a new player has
						// entered the game
						this.gameControl = new GameControl(
								GameControl.NEW_PLAYER_ENTERED_THE_GAME, player);
						Server.this.queue.offer(this.gameControl);
						Server.this.sendAll();

						// Display new player entered the game messege
						inGameMsgThread = new ShowInGameMsg(player.name
								+ " has entered the game");
						inGameMsgThread.start();

						pVector.add(player);
						Server.this.oosVector.add(this.oos);
						final PlayerHandler pHandler = new PlayerHandler(
								player, this.scan);
						pHandler.start();
						player = null;
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		// #########################################################################
		/**
		 * Sends player moves to all remote players except server player no 2
		 * thread are allowed to call this method at the same time to avoid
		 * network stream corruption
		 */
		synchronized private void sendAll() {
			Object object;
			while ((object = this.queue.poll()) != null) {
				for (final ObjectOutputStream oos : this.oosVector) {
					try {
						oos.writeObject(object);
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		// #########################################################################
		/**
		 * Player controls recieve handler depends that the client sends only
		 * movement keys
		 */
		class PlayerHandler extends Thread {
			private Player player;
			private final Scanner scan;
			private int key;
			private GameData gameData;

			PlayerHandler(final Player player, final Scanner scan) {
				this.player = player;
				this.scan = scan;
			}

			@Override
			public void run() {
				while (true) {
					if (!pVector.contains(this.player)) {
						break;
					}
					try {
						this.key = this.scan.nextInt();
					} catch (final NoSuchElementException ex) {
						// Display player has left messege
						inGameMsgThread = new ShowInGameMsg(this.player.name
								+ " has left the game");
						inGameMsgThread.start();
						// Removing player
						this.player.tank.explode();
						TankGame.remove(this.player.tank);

						// Inform other players that this player has left the
						// game
						final GameControl gameControl = new GameControl(
								GameControl.PLAYER_HAS_LEFT, this.player);
						Server.this.queue.offer(gameControl);
						Server.this.sendAll();

						this.player = null;
						break;
					}
					switch (this.key) {
					case (KeyEvent.VK_SPACE):
						this.player.tank.fire();
						this.gameData = new GameData(this.player, this.key);
						Server.this.queue.offer(this.gameData);
						Server.this.sendAll();
						break;
					case (KeyEvent.VK_UP):
						// try {
						if (this.player.tank.move()) {
							this.gameData = new GameData(this.player, this.key);
							Server.this.queue.offer(this.gameData);
							Server.this.sendAll();
						}
						// }
						// catch (NullPointerException
						// ex){ex.printStackTrace();}
						break;
					case (KeyEvent.VK_RIGHT):
						this.player.tank.rotateRight();
						this.gameData = new GameData(this.player, this.key);
						Server.this.queue.offer(this.gameData);
						Server.this.sendAll();
						break;
					case (KeyEvent.VK_LEFT):
						this.player.tank.rotateLeft();
						this.gameData = new GameData(this.player, this.key);
						Server.this.queue.offer(this.gameData);
						Server.this.sendAll();
						break;
					}
				}
			}
		}

		// #########################################################################
		Server() throws Exception {
			// Key hanlder
			TankGame.this.frame.addKeyListener(new KeyAdapter() {
				private int key;

				@Override
				public void keyPressed(final KeyEvent e) {
					if (!pVector.contains(Server.this.me)) {
						Server.this.me = null;
						TankGame.this.frame.removeKeyListener(this);
						return;
					}
					this.key = e.getKeyCode();
					switch (this.key) {
					case (KeyEvent.VK_UP):
						if (Server.this.me.tank.move()) {
							Server.this.gameData = new GameData(Server.this.me,
									this.key);
							Server.this.queue.offer(Server.this.gameData);
							Server.this.sendAll();
						}
						break;
					case (KeyEvent.VK_RIGHT):
						Server.this.me.tank.rotateRight();
						Server.this.gameData = new GameData(Server.this.me,
								this.key);
						Server.this.queue.offer(Server.this.gameData);
						Server.this.sendAll();
						break;
					case (KeyEvent.VK_LEFT):
						Server.this.me.tank.rotateLeft();
						Server.this.gameData = new GameData(Server.this.me,
								this.key);
						Server.this.queue.offer(Server.this.gameData);
						Server.this.sendAll();
						break;
					}
				}

				@Override
				public void keyReleased(final KeyEvent e) {
					this.key = e.getKeyCode();
					if (this.key == KeyEvent.VK_SPACE) {
						Server.this.me.tank.fire();
						Server.this.gameData = new GameData(Server.this.me,
								this.key);
						Server.this.queue.offer(Server.this.gameData);
						Server.this.sendAll();
					}
				}
			});

			final String myName = JOptionPane.showInputDialog("Player name",
					"Anonymous");
			if (myName == null) {
				throw (new Exception("Player name canceled"));
			}
			// Game map
			TankGame.this.newMap = new GameMaps(TankGame.this.frame);
			TankGame.this.obstcalRect = TankGame.this.newMap
					.getObstcalRectArray();
			try {
				this.sc = new ServerSocket(PORT);
				this.me = new Player(0, 250, TankGame.this.frame, myName, 0,
						TankGame.this.obstcalRect);
				pVector.add(this.me);
				final AcceptThread aThread = new AcceptThread();
				aThread.start();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Client class handles network game
	 */
	class Client {
		// Network setup
		private static final int PORT = 2525;
		private final String ip;
		private Socket socket;
		private ObjectInputStream ois;
		private PrintStream ps;

		private GameControl gameControl;
		private GameData gameData;
		private Player player;
		private String playerName;
		private int livesPosition = 14;

		// #########################################################################
		/**
		 * Game data accept thread
		 */
		class AcceptThread extends Thread {
			private Object object;

			@Override
			public void run() {
				while (true) {
					try {
						this.object = Client.this.ois.readObject();
						if (this.object instanceof GameData) {
							// Recieving players moves including mine!!
							Client.this.gameData = (GameData) this.object;
							for (final Player p : pVector) {
								if (p.name.equals(Client.this.gameData.name)) {
									switch (Client.this.gameData.key) {
									case (KeyEvent.VK_SPACE):
										p.tank.fire();
										break;
									case (KeyEvent.VK_UP):
										/**
										 * Here we should use move method but
										 * after network testing tanks position
										 * went out of synchronization so we now
										 * set the location to insure that we
										 * have the same map as the server
										 */

										// p.tank.move();
										p.tank.setLocation(
												Client.this.gameData.tankX,
												Client.this.gameData.tankY);
										break;
									case (KeyEvent.VK_RIGHT):
										p.tank.rotateRight();
										break;
									case (KeyEvent.VK_LEFT):
										p.tank.rotateLeft();
										break;
									}
									break;
								}
							}
						} else if (this.object instanceof GameControl) {
							Client.this.gameControl = (GameControl) this.object;
							switch (Client.this.gameControl.code) {
							case (GameControl.NEW_PLAYER_ENTERED_THE_GAME):
								Client.this.player = new Player(
										Client.this.gameControl.tankX,
										Client.this.gameControl.tankY,
										TankGame.this.frame,
										Client.this.gameControl.name,
										Client.this.livesPosition,
										TankGame.this.obstcalRect);
								Client.this.livesPosition += 14;
								pVector.add(Client.this.player);
								// Display new player entered the game messege
								inGameMsgThread = new ShowInGameMsg(
										Client.this.player.name
												+ " has entered the game");
								inGameMsgThread.start();
								Client.this.player = null;
								break;
							case (GameControl.PLAYER_HAS_LEFT):
								// Display player has left messege
								inGameMsgThread = new ShowInGameMsg(
										Client.this.gameControl.name
												+ " has left the game");
								inGameMsgThread.start();
								for (final Player p : pVector) {
									if (p.name
											.equals(Client.this.gameControl.name)) {
										p.tank.explode();
										pVector.remove(p);
										break;
									}
								}
								break;
							}
						}
					} catch (final Exception ex) {
						JOptionPane.showMessageDialog(null, "Connection lost");
						break;
					}
				}
			}
		}

		// #########################################################################
		Client() throws Exception {
			// Key press handler
			TankGame.this.frame.addKeyListener(new KeyAdapter() {
				private int key;

				@Override
				public void keyPressed(final KeyEvent e) {
					this.key = e.getKeyCode();
					if ((this.key == KeyEvent.VK_UP)
							|| (this.key == KeyEvent.VK_LEFT)
							|| (this.key == KeyEvent.VK_RIGHT)) {
						Client.this.ps.println(this.key);
					}
				}

				@Override
				public void keyReleased(final KeyEvent e) {
					this.key = e.getKeyCode();
					if (this.key == KeyEvent.VK_SPACE) {
						Client.this.ps.println(this.key);
					}
				}
			});
			// Client setup
			this.ip = JOptionPane.showInputDialog("Connect to server",
					"127.0.0.1");
			if (this.ip == null) {
				throw (new Exception("Connection canceled"));
			}
			try {
				this.socket = new Socket(this.ip, PORT);
				this.ois = new ObjectInputStream(this.socket.getInputStream());
				this.ps = new PrintStream(this.socket.getOutputStream());
			} catch (final Exception ex) {
				JOptionPane.showMessageDialog(null,
						"Please check network connectivity");
				throw ex;
			}

			// Sending player name
			int result;
			while (true) {
				this.playerName = JOptionPane.showInputDialog("Player name",
						"Anonymous");
				if (this.playerName == null) {
					throw (new Exception("Player name canceled"));
				}
				this.ps.println(this.playerName);
				result = this.ois.readInt();
				if (result == GameControl.NEW_PLAYER_APPROVAL) {
					break;
				} else if (result == GameControl.PLAYER_NAME_INUSE) {
					JOptionPane
							.showMessageDialog(null,
									"Player name is already used, please choose another name");
				}
			}

			// Game map
			TankGame.this.newMap = new GameMaps(TankGame.this.frame);
			TankGame.this.obstcalRect = TankGame.this.newMap
					.getObstcalRectArray();

			// Handshaking with server
			// Recieving in game players
			while (true) {
				try {
					this.gameControl = (GameControl) this.ois.readObject();
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
				if (this.gameControl.code == GameControl.LOAD_IN_GAME_PLAYERS) {
					// Be sure that the only reference to player is in the
					// vector
					// so that we can remove player instance when it is
					// destructed
					this.player = new Player(this.gameControl.tankX,
							this.gameControl.tankY, TankGame.this.frame,
							this.gameControl.name, this.livesPosition,
							TankGame.this.obstcalRect);
					this.livesPosition += 14;
					// Adjusting tank live
					this.player.tank.tankLives = this.gameControl.tankLives;
					pVector.add(this.player);
					TankGame.updateLives(this.player.tank);
					// Adjusting tank position
					if (this.player.tank.tankPosition < this.gameControl.tankPosition) {
						for (int i = this.player.tank.tankPosition; i < this.gameControl.tankPosition; i++) {
							this.player.tank.rotateRight();
						}
					} else {
						for (int i = this.player.tank.tankPosition; i > this.gameControl.tankPosition; i--) {
							this.player.tank.rotateLeft();
						}
					}
					this.player = null;
				} else {
					// Approval
					break;
				}
			}
			// Client player must be at index 0 in the vector because we want to
			// recieve
			// controls from server
			Player me = new Player(this.gameControl.tankX,
					this.gameControl.tankY, TankGame.this.frame,
					this.gameControl.name, 0, TankGame.this.obstcalRect);
			final FlashTank flashTank = new FlashTank(me.tank);
			flashTank.start();
			pVector.add(0, me);
			me = null;
			final AcceptThread t = new AcceptThread();
			t.start();
		}
	}

	/**
	 * Single player game
	 */
	class SinglePlayer {
		private Player me;
		private final Player computer1;
		private final Player computer2;
		private final Player computer3;
		private ComputerMove compMove;
		private final JFrame frame;

		SinglePlayer(final JFrame xframe) throws Exception {
			final String myName = JOptionPane.showInputDialog("Player name",
					"Anonymous");
			if (myName == null) {
				throw (new Exception("Player name canceled"));
			}
			this.frame = xframe;
			// Game map
			TankGame.this.newMap = new GameMaps(this.frame);
			TankGame.this.obstcalRect = TankGame.this.newMap
					.getObstcalRectArray();

			this.me = new Player(0, 0, this.frame, myName, 0,
					TankGame.this.obstcalRect);
			final FlashTank flashTank = new FlashTank(this.me.tank);
			flashTank.start();
			pVector.add(this.me);
			this.computer1 = new Player(750, 0, this.frame, "Enemy 1", 14,
					TankGame.this.obstcalRect);
			pVector.add(this.computer1);
			this.computer2 = new Player(0, 500, this.frame, "Enemy 2", 28,
					TankGame.this.obstcalRect);
			pVector.add(this.computer2);
			this.computer3 = new Player(750, 500, this.frame, "Enemy 3", 42,
					TankGame.this.obstcalRect);
			pVector.add(this.computer3);
			this.compMove = new ComputerMove(this.computer1);
			this.compMove.start();
			this.compMove = new ComputerMove(this.computer2);
			this.compMove.start();
			this.compMove = new ComputerMove(this.computer3);
			this.compMove.start();

			/**
			 * Handle when the user release the click Every time the user press
			 * a button a thread is intiatiated and starts to do the required
			 * action (move,left,right,fire) then when the user release the
			 * button all the threads is killed. but to stop the thread we need
			 * to save the created thread in a vector then retrieve it and kill
			 * it.
			 */

			// --- implementing the key lisentner
			this.frame.addKeyListener(new KeyAdapter() {
				// when the user release the key
				@Override
				public void keyReleased(final KeyEvent e) {
					if (!pVector.contains(SinglePlayer.this.me)) {
						SinglePlayer.this.me = null;
						SinglePlayer.this.frame.removeKeyListener(this);
						return;
					}
					final int key = e.getKeyCode();
					// --------------- switch case to see which key the user
					// enter
					switch (key) {

					// ------------ the user wishes to stop fireing
					case (KeyEvent.VK_SPACE):
						SinglePlayer.this.me.tank.fire();
						break;

					// ------------ the user wishes to stop moving
					case (KeyEvent.VK_UP):
						for (int i = 0; i < TankGame.this.moveVector.size(); i++) {
							TankGame.this.handel2 = (HandleTwoPress) TankGame.this.moveVector
									.elementAt(i);
							TankGame.this.handel2.timer1.cancel();
							;
						}
						break;

					// ------------ the user wishes to stop going right
					case (KeyEvent.VK_RIGHT):
						for (int i = 0; i < TankGame.this.rightVector.size(); i++) {
							TankGame.this.handel3 = (HandleTwoPress) TankGame.this.rightVector
									.elementAt(i);
							TankGame.this.handel3.timer1.cancel();
							;
						}
						break;

					// ------------ the user wishes to stop going left
					case (KeyEvent.VK_LEFT):
						for (int i = 0; i < TankGame.this.leftVector.size(); i++) {
							TankGame.this.handel4 = (HandleTwoPress) TankGame.this.leftVector
									.elementAt(i);
							TankGame.this.handel4.timer1.cancel();
							;
						}
						break;
					}
				}

				// --------- hendel when the user press to take action
				@Override
				public void keyPressed(final KeyEvent e) {
					if (!pVector.contains(SinglePlayer.this.me)) {
						SinglePlayer.this.me = null;
						SinglePlayer.this.frame.removeKeyListener(this);
						return;
					}
					final int key = e.getKeyCode();
					switch (key) {
					// ----------------- the tank wishes to move
					case (KeyEvent.VK_UP):
						for (int i = 0; i < TankGame.this.moveVector.size(); i++) {
							TankGame.this.handel2 = (HandleTwoPress) TankGame.this.moveVector
									.elementAt(i);
							TankGame.this.handel2.timer1.cancel();
							;
						}
						TankGame.this.handel2 = new HandleTwoPress(
								SinglePlayer.this.me.tank, 0);
						TankGame.this.moveVector.add(TankGame.this.handel2);
						TankGame.this.handel2.start();
						break;

					// ----------------- the tank wishes to go right
					case (KeyEvent.VK_RIGHT):
						for (int i = 0; i < TankGame.this.rightVector.size(); i++) {
							TankGame.this.handel3 = (HandleTwoPress) TankGame.this.rightVector
									.elementAt(i);
							TankGame.this.handel3.timer1.cancel();
							;
						}
						TankGame.this.handel3 = new HandleTwoPress(
								SinglePlayer.this.me.tank, 2);
						TankGame.this.rightVector.add(TankGame.this.handel3);
						TankGame.this.handel3.start();
						break;

					// ----------------- the tank wishes to go left
					case (KeyEvent.VK_LEFT):
						for (int i = 0; i < TankGame.this.leftVector.size(); i++) {
							TankGame.this.handel4 = (HandleTwoPress) TankGame.this.leftVector
									.elementAt(i);
							TankGame.this.handel4.timer1.cancel();
							;
						}
						TankGame.this.handel4 = new HandleTwoPress(
								SinglePlayer.this.me.tank, 3);
						TankGame.this.leftVector.add(TankGame.this.handel4);
						TankGame.this.handel4.start();
						break;
					}
				}
			});
		}

		/**
		 * ComputerMove thread used to drive computer tanks moves
		 */
		class ComputerMove extends Thread {
			private double rand;
			private Tank tank;
			private Player player;

			ComputerMove(final Player computer) {
				this.tank = computer.tank;
				this.player = computer;
			}

			@Override
			public void run() {
				while (true) {
					if (!pVector.contains(this.player)) {
						this.player = null;
						this.tank = null;
						return;
					} else {
						this.rand = Math.random() * 100;
						if ((this.rand >= 0) && (this.rand <= 70)) {
							this.tank.move();
						} else if ((this.rand > 70) && (this.rand <= 80)) {
							this.tank.rotateLeft();
						} else if ((this.rand > 80) && (this.rand <= 90)) {
							this.tank.rotateRight();
						} else {
							this.tank.fire();
						}
					}
					try {
						Thread.sleep(100);
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	// #########################################################################
	// #########################################################################
	// #########################################################################
	public void actionPerformed(final ActionEvent e) {
		if (e.getActionCommand().equals("Exit")) {
			exit(0);
		} else if (e.getActionCommand().equals("Start new multiplayer game")) {
			// Cleaning frame
			this.contentPane.removeAll();
			this.frame.add(inGameMsg);
			pVector.removeAllElements();

			try {
				final Server server = new Server();
			} catch (final Exception ex) {
				this.frame.add(this.firstBackground);
				this.frame.repaint();
			}
		} else if (e.getActionCommand().equals("Join server")) {
			// Cleaning frame
			this.contentPane.removeAll();
			this.frame.add(inGameMsg);
			pVector.removeAllElements();

			try {
				final Client client = new Client();
			} catch (final Exception ex) {
				this.frame.add(this.firstBackground);
				this.frame.repaint();
			}
		} else if (e.getActionCommand().equals("Two players game")) {
			// Cleaning frame
			this.contentPane.removeAll();
			this.frame.add(inGameMsg);
			pVector.removeAllElements();
			// Game map
			// newMap = new GameMaps(frame);
			// obstcalRect = newMap.getObstcalRectArray();
			this.twoPlayer = new TwoPlayer(this.frame);
		} else if (e.getActionCommand().equals("Single player game")) {
			// Cleaning frame
			this.contentPane.removeAll();
			this.frame.add(inGameMsg);
			pVector.removeAllElements();

			try {
				this.singlePlayer = new SinglePlayer(this.frame);
			} catch (final Exception ex) {
				this.frame.add(this.firstBackground);
				this.frame.repaint();
			}
		} else if (e.getActionCommand().equals("About")) {
			JOptionPane
					.showMessageDialog(
							null,
							"TankGame 0.1\n\nAuthor:\nAli Yakout\nAyman Salama\n\niTi Alexandria, UNIX department 2006\n",
							"About", JOptionPane.PLAIN_MESSAGE, this.icon);
		}

	}

	// #########################################################################
	TankGame() {
		// Sound
		try {
			winSound = Applet.newAudioClip(this.getClass().getResource(
					"win.wav"));
		} catch (final Exception ex) {
		}

		// In game messages label
		inGameMsg.setForeground(Color.RED);
		inGameMsg.setFont(new Font("SansSerif", Font.BOLD, 16));
		inGameMsg.setLocation(0, 280);
		inGameMsg.setSize(790, 30);
		inGameMsg.setVisible(false);

		// Menu
		this.gameMenu.add(this.singlePlayItem);
		this.gameMenu.add(this.twoPlayItem);
		this.gameMenu.addSeparator();
		this.gameMenu.add(this.exitItem);
		this.networkMenu.add(this.serverItem);
		this.networkMenu.add(this.clientItem);
		this.helpMenu.add(this.aboutItem);
		this.menuBar.add(this.gameMenu);
		this.menuBar.add(this.networkMenu);
		this.menuBar.add(this.helpMenu);
		this.frame.setJMenuBar(this.menuBar);

		// Menu actions
		this.singlePlayItem.addActionListener(this);
		this.twoPlayItem.addActionListener(this);
		this.exitItem.addActionListener(this);
		this.serverItem.addActionListener(this);
		this.clientItem.addActionListener(this);
		this.aboutItem.addActionListener(this);

		// Setting first background
		this.firstBackground.setSize(800, 560);
		this.firstBackground.setLocation(0, 0);

		// GUI (frame must be visible before creating any tank)
		this.frame.setLayout(null);
		this.frame.setIconImage(this.icon.getImage());
		this.frame.setResizable(false);
		this.frame.setSize(800, 600);// Resolution [800 x 600]
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.contentPane = this.frame.getContentPane();
		this.frame.add(this.firstBackground);
		this.frame.setVisible(true);
	}

	// #########################################################################
	public static void main(final String[] args) {
		final TankGame game = new TankGame();
	}
}