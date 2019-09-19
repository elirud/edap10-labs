package factory.controller;

import factory.model.DigitalSignal;
import factory.model.WidgetKind;
import factory.swingview.Factory;

public class ToolController {
	private final DigitalSignal conveyor, press, paint;
	private final long pressingMillis, paintingMillis;
	private boolean painting = false, pressing = false;

	public ToolController(DigitalSignal conveyor, DigitalSignal press, DigitalSignal paint, long pressingMillis,
			long paintingMillis) {
		this.conveyor = conveyor;
		this.press = press;
		this.paint = paint;
		this.pressingMillis = pressingMillis;
		this.paintingMillis = paintingMillis;
	}

	public synchronized void onPressSensorHigh(WidgetKind widgetKind) throws InterruptedException {
		//
		// TODO: you will need to modify this method
		//
		if (widgetKind == WidgetKind.BLUE_RECTANGULAR_WIDGET) {
			stopConveyor();
			pressing = true;
			press.on();
			waitOutside(pressingMillis);
			press.off();
			waitOutside(pressingMillis);
			pressing = false;
			startConveyor();
		}
	}

	public synchronized void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException {
		//
		// TODO: you will need to modify this method
		//
		if (widgetKind == WidgetKind.ORANGE_ROUND_WIDGET) {
			stopConveyor();
			painting = true;
			paint.on();
			waitOutside(paintingMillis);
			paint.off();
			painting = false;
			startConveyor();
		}
	}

	/** Helper method: sleep outside of monitor for ’millis’ milliseconds. */
	private synchronized void waitOutside(long millis) throws InterruptedException {
		long timeToWakeUp = System.currentTimeMillis() + millis;
		// ...
		while (System.currentTimeMillis() < timeToWakeUp) {
			long dt = timeToWakeUp - System.currentTimeMillis();
			wait(dt);
			// ...
		}
	}
    
    private synchronized void startConveyor() throws InterruptedException {
    	
    	if(!pressing && !painting) {
    		
    		conveyor.on();
    		
    	}
    	
    	
    }
    
    private synchronized void stopConveyor() throws InterruptedException {
    	
    	conveyor.off();
    	
    }

	// -----------------------------------------------------------------------

	public static void main(String[] args) {
		Factory factory = new Factory();
		factory.startSimulation();
	}
}
