import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import clock.ClockOutput;

public class TimeHandler implements Runnable {

	boolean alarm = false, alarming = false;
	int hour, min, sec, time = 235958, alarmTime, alarmCount;
	String timeString, prevTimeString;
	Semaphore mutex = new Semaphore(1);
	ClockOutput out;
	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public TimeHandler(ClockOutput out) {
		this.out = out;
	}

	private int formatTime(int currentTime) {

		timeString = Integer.toString(currentTime);

		while (timeString.length() < 6) {
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

		scheduler.scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);

//		Thread timeThread = new Thread(() -> {
//
//			long t0 = System.currentTimeMillis();
//
//			while (true) {
//
//				try {
//
//					out.displayTime(updateTime());
//					if (time == alarmTime && alarm) {
//
//						alarming = true;
//						
//					}
//					
//					if (alarming) {
//						
//						if (alarmCount < 20) {
//
//							out.alarm();
//							alarmCount++;
//
//						} else {
//
//							alarmCount = 0;
//							alarming = false;
//
//						}
//						
//					}
//					
//					long time = -((System.currentTimeMillis() - t0) % 1000) + 1000;
//					System.out.println(time);
//					Thread.sleep(-((System.currentTimeMillis() - t0) % 1000) + 1000);
//
//				} catch (InterruptedException e) {
//
//					e.printStackTrace();
//
//				}
//
//			}
//
//		});
//		timeThread.start();

	}

	@Override
	public void run() {

		try {
			
			out.displayTime(updateTime());
			if (time == alarmTime && alarm) {

				alarming = true;
				
			}
			
			if (alarming) {
				
				if (alarmCount < 20) {

					out.alarm();
					alarmCount++;

				} else {

					alarmCount = 0;
					alarming = false;

				}
				
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setAlarm(int alarmTime) {

		this.alarmTime = alarmTime;

	}

	public void stopAlarm() {

		if (alarming) {
			
			alarmCount = 0;
			alarming = false;
			
		}

	}

	public void toggleAlarm() {

		out.setAlarmIndicator(alarm = !alarm);

	}

}
