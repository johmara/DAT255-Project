package main.java;

import com.sun.squawk.VM;
import sics.plugin.PlugInComponent;
import sics.port.PluginPPort;
import sics.port.PluginRPort;


public class DistanceLEDs extends PlugInComponent{
	
	public PluginRPort S2Vr; 
	public PluginPPort LED;
	
	public DistanceLEDs(String args[]) {
		super(args);
	}
	public DistanceLEDs(){}
	
	public static void main(String args[]) {
		VM.println("ReadDistance.main()\r\n");
		DistanceLEDs ap = new DistanceLEDs(args);
		ap.run();
	}
	

	@Override
	public void run() {
		init();
		try {
			doFunction();
		} catch (InterruptedException e) {
			VM.println("**************** Interrupted.");
			return;
		}
		
	}

	@Override
	public void init() {
		S2Vr = new PluginRPort(this, "read");
		LED = new PluginPPort(this, "led");
	}
	
	public void doFunction() throws InterruptedException {
		int distance = getDistance();
		if (distance == -1) {}
		
		else if(distance >= 100) {
			LED.write("1|0");
			LED.write("2|1");
			LED.write("3|1");
		}
		else if (distance >= 50) {
			LED.write("1|1");
			LED.write("2|0");
			LED.write("3|1");
		}
		else {
			LED.write("1|1");
			LED.write("2|1");
			LED.write("3|0");
		}
		
	}
	
	public int getDistance() {
		String temp = S2Vr.readString();
		if(temp.contains("Distance:")) {
			return Integer.parseInt(temp.replaceAll("\\D", ""));
		}
		return -1;
		
	}
	
}
