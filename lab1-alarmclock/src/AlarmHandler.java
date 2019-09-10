import clock.ClockOutput;

public class AlarmHandler {

	ClockOutput out;
	boolean alarm = false;
	int alarmTime;
	TimeHandler timeHandler;

	public AlarmHandler(ClockOutput out, TimeHandler timeHandler) {
		
		this.timeHandler = timeHandler;
		this.out = out;

	}

	public void toggleAlarm() {

		out.setAlarmIndicator(alarm = !alarm);

		Thread alarmThread = new Thread(() -> {
			
			while(!Thread.interrupted()) {
				
				if (timeHandler.getTime() == alarmTime) {
					
					for(int i = 0; i < 20; i++) {
						
						out.alarm();
						try {
							Thread.sleep(998);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					}
					
				}
				
			}

		});

		if (alarm)
			alarmThread.start();
		else
			alarmThread.interrupt();

	}

	public void setAlarm(int alarmTime) {
		
		this.alarmTime = alarmTime;

	}

}
