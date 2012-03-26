package se.space;


import java.util.ArrayList;
import java.util.List;

/**
 * A class thats used for countdowns using real time in the game.
 * Timers are created using the createTimer method to make sure they are updated each frame.
 */
public class Timer {
	private final int delay;
	private int timeLeft;
	private static List<Timer> timers = new ArrayList<Timer>();

	/**
	 * Create a new timer with a set delay.
	 * @param delay The number of millisecs this Timer should wait before it is done.
	 */
	private Timer(int delay) {
		setTimeLeft(this.delay = delay);
	}

	/**
	 * Decreases the time left for this Timer.
	 * @param change The number of millisecs that has passed since the last call to this method.
	 */
	private void tick(int change) {
		setTimeLeft(getTimeLeft() - change);

		if(getTimeLeft() < 0) {
			setTimeLeft(0);
		}
	}
	/**
	 * @return true if this Timer is done (it has counted down to zero), false otherwise.
	 */
	public boolean isDone() {
		return getTimeLeft() <= 0;
	}

	/**
	 * Resets this timer so that the time left is the same as the delay set in the constructor.
	 */
	public void reset() {
		setTimeLeft(delay);
	}

	/**
	 * Create a new timer with a set delay.
	 * @param delay The number of millisecs this Timer should wait before it is done.
	 * @return A Timer with a set delay.
	 */
	public static Timer createTimer(int delay) {
		Timer t = new Timer(delay);
		timers.add(t);
		return t;
	}

	/**
	 * Should be called by World each frame to decrease the time left.
	 * @param change The number of millisecs that has passed since the last call to this method.
	 */
	public static void updateTimers(int change) {
		for(Timer timer : timers) {
			timer.tick(change);
		}
	}

	public void setTimeLeft(int timeLeft) {
		this.timeLeft = timeLeft;
	}

	public int getTimeLeft() {
		return timeLeft;
	}
}
