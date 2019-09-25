import lift.LiftView;

public class LiftThread extends Thread {
	Monitor m;
	LiftView v;
	boolean top = false;
	private int here;
	private int next;

	public LiftThread(Monitor m, LiftView v) {

		this.m = m;
		this.v = v;

	}

	public void run() {

		while (true) {

			m.waitToRun();
			here = m.getHere();
			next = m.getNext();
			v.moveLift(here, next);
			m.updateFloor(next);

		}

	}
}
