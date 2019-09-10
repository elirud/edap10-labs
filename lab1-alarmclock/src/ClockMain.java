import java.util.concurrent.Semaphore;

import clock.ClockInput;
import clock.ClockInput.UserInput;
import clock.ClockOutput;
import emulator.AlarmClockEmulator;

public class ClockMain {
	


	public static void main(String[] args) throws InterruptedException {
		AlarmClockEmulator emulator = new AlarmClockEmulator();

		ClockInput in = emulator.getInput();
		ClockOutput out = emulator.getOutput();

		TimeHandler timeHandler = new TimeHandler(out);
		AlarmHandler alarmHandler = new AlarmHandler(out, timeHandler);
		
		timeHandler.startTime();

		Semaphore sem = in.getSemaphore();

		while (true) {
			sem.acquire(); // wait for user input

			UserInput userInput = in.getUserInput();
			int choice = userInput.getChoice();
			int value = userInput.getValue();

			switch (choice) {
				case 1:
					timeHandler.setTime(value);
					break;
				case 2:
					alarmHandler.setAlarm(value);
					break;
				case 3:
					alarmHandler.toggleAlarm();
					break;
				default:
			}

			System.out.println("choice = " + choice + "  value=" + value);
		}
	}
}
