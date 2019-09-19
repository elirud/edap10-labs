package factory.controller;

import factory.model.DigitalSignal;
import factory.model.WidgetKind;
import factory.swingview.Factory;

public class ToolController2 {
    private final DigitalSignal conveyor, press, paint;
    private final long pressingMillis, paintingMillis;
    private boolean painting = false, pressing = false;
    
    public ToolController2(DigitalSignal conveyor,
                          DigitalSignal press,
                          DigitalSignal paint,
                          long pressingMillis,
                          long paintingMillis)
    {
        this.conveyor = conveyor;
        this.press = press;
        this.paint = paint;
        this.pressingMillis = pressingMillis;
        this.paintingMillis = paintingMillis;
    }
    
    private synchronized void startPress() throws InterruptedException {
    	
    	press.on();
    	
    }
    
    private synchronized void stopPress() throws InterruptedException {
    	
    	press.off();
    	
    }
    
    private synchronized void startPaint() throws InterruptedException {
    	
    	paint.on();
    	
    }
    
    private synchronized void stopPaint() throws InterruptedException {
    	
    	paint.off();
    	
    }
    
    private synchronized void startConveyor() throws InterruptedException {
    	
    	if(!pressing && !painting) {
    		
    		conveyor.on();
    		
    	}
    	
    	
    }
    
    private synchronized void stopConveyor() throws InterruptedException {
    	
    	conveyor.off();
    	
    }

    public void onPressSensorHigh(WidgetKind widgetKind) throws InterruptedException {
        //
        // TODO: you will need to modify this method
        //
        if (widgetKind == WidgetKind.BLUE_RECTANGULAR_WIDGET) {
        	stopConveyor();
        	pressing = true;
            startPress();
            Thread.sleep(pressingMillis);
            stopPress();
            Thread.sleep(pressingMillis);
        	pressing = false;
            startConveyor();
        }
    }

    public void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException {
        //
        // TODO: you will need to modify this method
        //
        if (widgetKind == WidgetKind.ORANGE_ROUND_WIDGET) {
        	stopConveyor();
        	painting = true;
        	startPaint();
            Thread.sleep(paintingMillis);
        	stopPaint();
        	painting = false;
            startConveyor();
        }
    }
    
    // -----------------------------------------------------------------------
    
    public static void main(String[] args) {
        Factory factory = new Factory();
        factory.startSimulation();
    }
}