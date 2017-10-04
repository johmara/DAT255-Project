package absolut.can;

import java.util.concurrent.Semaphore;

public class CanReader {

    private static CanReader instance = null;
    private CanManager canManager;

    private String data;

    public static CanReader getInstance() {
        if (instance == null) {
            instance = new CanReader();
        }
        return instance;
    }

    private CanReader(){
        CanConfigParser.parseCanConfig("canConfig.xml");
        canManager = new CanManager(CanConfigParser.getSenders(), CanConfigParser.getReceivers());
        new Thread(canManager).start();
    }

    public CanManager getCanManager() {
        return canManager;
    }

    public synchronized String getData() {
        while (data == null) {
            try {
                wait();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        String tmp = data;
        data = null;
        return tmp;
    }

    public synchronized void setData(String s) {
        data = s;
        notify();
    }
}
