import java.util.Timer;
import java.util.TimerTask;

//#############################################################################
//----------------------class to handle two player at the same time
//#############################################################################
class HandleTwoPress extends Thread {
	private Tank tank;
	private int action;
	public Timer timer1;
	private int time;

	HandleTwoPress() {
	}

	HandleTwoPress(final Tank tank, final int action) {
		this.action = action;
		this.tank = tank;
		// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ specify the time for the timer of
		// the thread
		// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ in order to move fast delay is 50 ms
		// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ in order to rotate left and right
		// slow delay is 150ms
		// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ in order to fire slow, delay is
		// 100ms
		switch (action) {
		case 0:
			this.time = 50;
			break;
		case 1:
			this.time = 100;
			break;
		case 2:
			this.time = 150;
			break;
		case 3:
			this.time = 150;
			break;
		}
	}

	@Override
	public void run() {
		this.timer1 = new Timer();
		final TimerTask task = new TimerTask() {
			@Override
			public void run() {
				switch (HandleTwoPress.this.action) {
				case 0:
					HandleTwoPress.this.tank.move();
					break;
				case 1:
					HandleTwoPress.this.tank.fire();
					break;
				case 2:
					HandleTwoPress.this.tank.rotateRight();
					break;
				case 3:
					HandleTwoPress.this.tank.rotateLeft();
					break;
				}
			}
		};
		this.timer1.scheduleAtFixedRate(task, 1, this.time);
	}
}
