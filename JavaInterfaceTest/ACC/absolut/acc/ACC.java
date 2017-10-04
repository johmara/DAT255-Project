package absolut.acc;


import static java.lang.Thread.interrupted;

public class ACC implements Runnable {

    private CAN can;
    private Sensor sensor;

    public ACC(CAN can){
        this.can = can;
    }

    @Override
    public void run() {
        init();
        doFunction();
    }

    private void init() {
        sensor = new Sensor(can);
    }

    private void doFunction() {
        int dist = 0;
        while(true){
            try{
                dist = sensor.getDistance();
                if(dist < 100){
                    can.sendMotorValue((byte) 0);
                }else{
                    can.sendMotorValue((byte) 50);
                    Thread.sleep(1000);
                    can.sendMotorValue((byte) 0);
                }

            }catch(InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }
}