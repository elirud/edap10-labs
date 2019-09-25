import lift.LiftView;

public class Monitor {
	LiftView v;
	private int[] waitingForEntry = { 0, 0, 0, 0, 0, 0, 0 };
	private int[] waitingForExit = { 0, 0, 0, 0, 0, 0, 0 };
	private int currentFloor = 0, load = 0, entering = 0, exiting = 0;
	private boolean up = true, blocked = false;

	public Monitor(LiftView view) {

		this.v = view;

	}

	public synchronized void waitForEntry(int floor) {

		waitingForEntry[floor]++;
		while (floor != currentFloor || load + entering - exiting == 4 || !blocked) {

			try {

				wait();

			} catch (InterruptedException e) {

				e.printStackTrace();

			}

		}
		
		entering++;

	}

	public synchronized void waitForExit(int floor) {

		while (floor != currentFloor) {

			try {

				wait();

			} catch (InterruptedException e) {

				e.printStackTrace();

			}

		}
		
		exiting++;

	}

	public synchronized void waitToRun() {

		while (blocked) {

			try {

				wait();

			} catch (InterruptedException e) {

				e.printStackTrace();

			}

		}

	}

	public int getHere() {

		return currentFloor;

	}

	public int getNext() {

		if (up) {

			if (currentFloor < 6) {

				return currentFloor + 1;

			} else {

				up = false;
				return currentFloor - 1;

			}

		} else {

			if (currentFloor > 0) {

				return currentFloor - 1;

			} else {

				up = true;
				return currentFloor + 1;

			}

		}

	}

	public synchronized void updateFloor(int newFloor) {

		currentFloor = newFloor;
		
		if ((waitingForEntry[currentFloor] != 0 && load != 4) || waitingForExit[currentFloor] != 0) {
			
			blocked = true;
			
		}
		
		notifyAll();

	}

	public synchronized void entered(int destinationFloor) {

		waitingForEntry[currentFloor]--;
		entering--;
		load++;
		waitingForExit[destinationFloor]++;
		
		if (waitingForEntry[currentFloor] == 0 && waitingForExit[currentFloor] == 0) {
			
			blocked = false;
			
		}
		
		if (load == 4 && entering == 0 && exiting == 0) {
			
			blocked = false;
			
		}
		
		notifyAll();
		

	}

	public synchronized void exited() {

		waitingForExit[currentFloor]--;
		exiting--;
		load--;
		
		if (waitingForEntry[currentFloor] == 0 && waitingForExit[currentFloor] == 0) {
			
			blocked = false;
			
		}
		
		if (load == 4 && entering == 0 && exiting == 0) {
			
			blocked = false;
			
		}
		
		notifyAll();

	}

}
