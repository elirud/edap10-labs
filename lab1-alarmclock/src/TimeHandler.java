import java.util.concurrent.Semaphore;

import clock.ClockOutput;

public class TimeHandler {

	int hour, min, sec, time = 235958;
	String timeString, prevTimeString;
	Semaphore mutex = new Semaphore(1);
	ClockOutput out;

	public TimeHandler(ClockOutput out) {
		this.out = out;
	}

	private int formatTime(int currentTime) {
		
		timeString = Integer.toString(currentTime);
		
		while(timeString.length() < 6) {
			timeString = "0" + timeString;
		}
		
		this.hour = Integer.parseInt(timeString.substring(0, 2));
		this.min = Integer.parseInt(timeString.substring(2, 4));
		this.sec = Integer.parseInt(timeString.substring(4, 6));
		
		sec++;
		
		if (sec == 60) {
			sec = 0;
			min++;
			if (min == 60) {
				min = 0;
				hour++;
				if (hour == 24) {
					hour = 0;
				}
			}
		}
		
		timeString = "";
		if (hour < 10)
			timeString += "0";
		timeString += Integer.toString(hour);
		if (min < 10)
			timeString += "0";
		timeString += Integer.toString(min);
		if (sec < 10)
			timeString += "0";
		timeString += Integer.toString(sec);
		
		return Integer.parseInt(prevTimeString = timeString);
		
	}
	
	public int updateTime() throws InterruptedException {
		
		mutex.acquire();
		time = formatTime(time);
		mutex.release();
		return time;
		
	}
	
	public int setTime(int newTime) throws InterruptedException {
		
		mutex.acquire();		
		time = formatTime(newTime);
		mutex.release();
		return time;
		
	}
	
	public int getTime() {
		
		return Integer.parseInt(prevTimeString);
		
	}

	public void startTime() {

		Thread timeThread = new Thread(() -> {
			try {
				while (true) {
					out.displayTime(updateTime());
					Thread.sleep(998);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		timeThread.start();

	}

}
