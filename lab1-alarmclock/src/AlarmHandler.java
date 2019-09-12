import clock.ClockOutput;

public class AlarmHandler {

	ClockOutput out;
	boolean alarm = false, alarming = false;
	int alarmTime, alarmCount;
	TimeHandler timeHandler;
	Thread alarmThread;

	public AlarmHandler(ClockOutput out, TimeHandler timeHandler) {
		
		this.timeHandler = timeHandler;
		this.out = out;

	}

	public void toggleAlarm() {

		out.setAlarmIndicator(alarm = !alarm);

		alarmThread = new Thread(() -> {
			
			while(!Thread.interrupted()) {
				
				if (timeHandler.getTime() == alarmTime) {
					
					for(alarmCount = 0; alarmCount < 20; alarmCount++) {
						
						alarming = true;
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
	
	public void stopAlarm() {
		
		if(alarming) {
			alarmCount = 20;
		}
			
		
	}

}
