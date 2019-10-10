package lab;

import wash.WashingIO;

/**
 * Program 1 for washing machine. Serves as an example of how washing programs
 * are structured.
 * 
 * This short program stops all regulation of temperature and water levels,
 * stops the barrel from spinning, and drains the machine of water.
 * 
 * It is can be used after an emergency stop (program 0) or a power failure.
 */
class WashingProgram1 extends MessagingThread<WashingMessage> {

	private WashingIO io;
	private MessagingThread<WashingMessage> temp;
	private MessagingThread<WashingMessage> water;
	private MessagingThread<WashingMessage> spin;

	public WashingProgram1(WashingIO io, MessagingThread<WashingMessage> temp, MessagingThread<WashingMessage> water,
			MessagingThread<WashingMessage> spin) {
		this.io = io;
		this.temp = temp;
		this.water = water;
		this.spin = spin;
	}

	@Override
	public void run() {
		try {

			System.out.println("washing program 1 started");

			// Lock hatch
			io.lock(true);

			// Start the 30 minutes of washing in 40 degress
			water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
			spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
			temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 40));

			// spin for 30 simulated minutes (one minute == 60000 milliseconds)
			Thread.sleep(30 * 60000 / Wash.SPEEDUP);

			// Turn of heat and drain water to prepare for rinse
			temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
			water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
			System.out.println("got " + receive());

			// Rinse 5 times in cold water for 2 minutes each
			for (int i = 0; i < 5; i++) {
				
				water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
				Thread.sleep(2 * 60000 / Wash.SPEEDUP);
				water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
				System.out.println("got " + receive());
				
			}
			
			//Centrifuge for 5 minutes
			spin.send(new WashingMessage(this, WashingMessage.SPIN_FAST));
			Thread.sleep(5 * 60000 / Wash.SPEEDUP);

			// instruct SpinController to stop spin barrel spin
			spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
			
			io.lock(false);

			System.out.println("washing program 1 finished");
		} catch (InterruptedException e) {

			// if we end up here, it means the program was interrupt()'ed
			// set all controllers to idle

			try {

				temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
				water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
				spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
				System.out.println("washing program terminated");

			} catch (InterruptedException e1) {

				e1.printStackTrace();

			}
		}
	}
}
