package plugins;

import sics.plugin.PlugInComponent;
import sics.port.PluginPPort;
import sics.port.PluginRPort;

public class pluginOne  extends PlugInComponent{

        public PluginPPort pport;
        public PluginRPort rport;

        public pluginOne() {}

        public pluginOne(String[] args) {
            super(args);
        }

        public static void main(String[] args) {
            pluginOne instance = new pluginOne(args);
            instance.run();
        }

        public void init() {
            // Initiate PluginPPort
            pport = new PluginPPort(this, "pport");
            rport = new PluginRPort(this, "rport");
        }

        public void run() {
            init();
            // do functions, for example, read front wheel speed value from sensor and then publish through MQTT
            while(true) {

                pport.write(50);

                // read front wheel speed value
               // Integer frontWheelData = (Integer)rport.read();
                // Prepare published data, which is packaged in the format “key|value”
                //String pubData = “fs|” + String.valueof(frontWheelData);
                // Publish data
                //pport.write(pubData);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }

                pport.write(-10);

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {

                }
            }
        }



    }
