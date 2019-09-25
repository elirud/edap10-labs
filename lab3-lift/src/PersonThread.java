import java.util.Random;

import lift.Passenger;

public class PersonThread extends Thread {
	Monitor m;
	Passenger p;
	Random r = new Random();
	
	public PersonThread(Monitor m, Passenger p) {
		
		this.m = m;
		this.p = p;
		
	}


	public void run() {
		
		try {
			
			sleep(1000 * r.nextInt(46));
			
		} catch (InterruptedException e) {
		
			e.printStackTrace();
		
		}
		
		p.begin();
		m.waitForEntry(p.getStartFloor());
		p.enterLift();
		m.entered(p.getDestinationFloor());
		m.waitForExit(p.getDestinationFloor());
		p.exitLift();
		m.exited();
		p.end();
		
	}

	
	

}
