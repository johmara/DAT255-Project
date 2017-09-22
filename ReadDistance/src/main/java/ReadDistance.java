package main.java;

import com.sun.squawk.VM;
import sics.plugin.PlugInComponent;
import sics.port.PluginPPort;
import sics.port.PluginRPort;

public class ReadDistance extends PlugInComponent {
	public PluginRPort sensor;
	public PluginPPort S2Vw;
	
	public ReadDistance(String args[]) {
		super(args);
	}
	public ReadDistance(){}
	
	public static void main(String args[]) {
		VM.println("ReadDistance.main()\r\n");
		ReadDistance ap = new ReadDistance(args);
		ap.run();
	}
	
	 public void init() {
			// Initiate PluginPPort
			VM.println("init ports");
			sensor = new PluginRPort(this, "se");
			S2Vw = new PluginPPort(this, "write");
		    }
	 
	 public void doFunction() throws InterruptedException {
		 while(true){
			 S2Vw.write("Distance:" + sensor.readInt());
		 }
		
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
}