package lab;

import wash.WashingIO;

public class WaterController extends MessagingThread<WashingMessage> {

	private final int IDLE = 0, FILLING = 1, DRAINING = 2;

	private WashingIO io;
	private int state = IDLE;
	private double setLevel = 0;
	private MessagingThread<WashingMessage> program;

	public WaterController(WashingIO io) {

		this.io = io;

	}

	@Override
	public void run() {

		try {

			while (true) {

				WashingMessage m = receiveWithTimeout(3000 / Wash.SPEEDUP);

				if (m != null) {

					System.out.println("got " + m);

					if (m.getCommand() == WashingMessage.WATER_FILL) {

						setLevel = m.getValue();
						//io.drain(false);
						io.fill(true);
						state = FILLING;

					} else if (m.getCommand() == WashingMessage.WATER_DRAIN) {

						program = m.getSender();
						//io.fill(false);
						io.drain(true);
						state = DRAINING;

					} else if (m.getCommand() == WashingMessage.WATER_IDLE) {

						switch (state) {

							case FILLING:
								io.fill(false);
								state = IDLE;
								break;
	
							case DRAINING:
								io.drain(false);
								state = IDLE;
								break;

						}

					}

				}
				
				if (state == FILLING && io.getWaterLevel() >= setLevel) {
					
					io.fill(false);
					state = IDLE;
					
				} else if (state == DRAINING && io.getWaterLevel() <= 0) {
					
					io.drain(false);
					program.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
					state = IDLE;
					
				}
				
			}

		} catch (InterruptedException unexpected) {

			// we don't expect this thread to be interrupted,
			// so throw an error if it happens anyway
			throw new Error(unexpected);

		}

	}

}
