package lab;

import wash.WashingIO;

public class TemperatureController extends MessagingThread<WashingMessage> {

	private final int dt = 10;
	private final double mu = dt * 0.0478;
	private final double ml = dt * 0.000952;
	private double setTemp = 0;
	

	private WashingIO io;

	public TemperatureController(WashingIO io) {

		this.io = io;

	}

	@Override
	public void run() {

		try {

			while (true) {

				WashingMessage m = receiveWithTimeout(dt * 1000 / Wash.SPEEDUP);

				// if m is null, it means a minute passed and no message was received
				if (m != null) {

					System.out.println("got " + m);

					if (m.getCommand() == WashingMessage.TEMP_SET) {
						
						setTemp = m.getValue();
						
					} else if (m.getCommand() == WashingMessage.TEMP_IDLE) {
						
						setTemp = 0;
						
					}

				}
				
				if (io.getTemperature() < setTemp - mu || io.getTemperature() < setTemp - 2 + ml && setTemp != 0) {
					
					io.heat(true);
					
				} else {
					
					io.heat(false);
					
				}

			}

		} catch (InterruptedException unexpected) {

			// we don't expect this thread to be interrupted,
			// so throw an error if it happens anyway
			throw new Error(unexpected);

		}

	}
}
