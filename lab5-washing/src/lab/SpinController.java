package lab;

import wash.WashingIO;

public class SpinController extends MessagingThread<WashingMessage> {

	private final int SPIN_OFF = 1;
	private final int SPINNING_SLOW_RIGHT = 2;
	private final int SPINNING_SLOW_LEFT = 3;
	private final int SPINNING_FAST = 4;

	private WashingIO io;
	private int state = SPIN_OFF;
	private long prevTime = 0;

	public SpinController(WashingIO io) {

		this.io = io;

	}

	@Override
	public void run() {
		try {

			while (true) {

				// wait for up to a (simulated) minute for a WashingMessage
				WashingMessage m = receiveWithTimeout(60000 / Wash.SPEEDUP);

				// if m is null, it means a minute passed and no message was received
				if (m != null) {

					System.out.println("got " + m);

					if (m.getCommand() == WashingMessage.SPIN_SLOW) {

						state = SPINNING_SLOW_LEFT;
						io.setSpinMode(WashingIO.SPIN_LEFT);
						prevTime = System.currentTimeMillis();

					} else if (m.getCommand() == WashingMessage.SPIN_FAST) {
						
						state = SPINNING_FAST;
						io.setSpinMode(WashingIO.SPIN_FAST);
						
					} else if (m.getCommand() == WashingMessage.SPIN_OFF) {
						
						io.setSpinMode(WashingIO.SPIN_IDLE);
						state = SPIN_OFF;
						
					}

				}
				
				if (state == SPINNING_SLOW_LEFT && (System.currentTimeMillis() - prevTime) > (60000 / Wash.SPEEDUP)) {
					
					state = SPINNING_SLOW_RIGHT;
					io.setSpinMode(WashingIO.SPIN_RIGHT);
					
				} else if (state == SPINNING_SLOW_RIGHT && (System.currentTimeMillis() - prevTime) > (60000 / Wash.SPEEDUP)) {
					
					state = SPINNING_SLOW_LEFT;
					io.setSpinMode(WashingIO.SPIN_LEFT);
					
				}
				
				prevTime = System.currentTimeMillis();
				
			}
		} catch (InterruptedException unexpected) {

			// we don't expect this thread to be interrupted,
			// so throw an error if it happens anyway
			throw new Error(unexpected);

		}
	}
}
