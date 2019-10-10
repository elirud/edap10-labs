package lab;

import simulator.WashingSimulator;
import wash.WashingIO;

public class Wash {

	// simulation speed-up factor:
	// 50 means the simulation is 50 times faster than real time
	public static final int SPEEDUP = 50;

	public static void main(String[] args) throws InterruptedException {
		WashingSimulator sim = new WashingSimulator(SPEEDUP);

		WashingIO io = sim.startSimulation();

		TemperatureController temp = new TemperatureController(io);
		WaterController water = new WaterController(io);
		SpinController spin = new SpinController(io);

		temp.start();
		water.start();
		spin.start();

		Thread p3 = new WashingProgram3(io, temp, water, spin);
		Thread p1 = new WashingProgram1(io, temp, water, spin);
		Thread p2 = new WashingProgram2(io, temp, water, spin);
		
		Thread currentProgram = null;

		while (true) {
			int n = io.awaitButton();
			System.out.println("user selected program " + n);

			switch (n) {

				case 0:
					currentProgram.interrupt();
					break;
				case 1:
					p1.start();
					currentProgram = p1;
					break;
				case 2:
					p2.start();
					currentProgram = p2;
					break;
				case 3:
					p3.start();
					currentProgram = p3;
					break;

			}

			// TODO:
			// if the user presses buttons 1-3, start a washing program
			// if the user presses button 0, and a program has been started, stop it
		}
	}
};
