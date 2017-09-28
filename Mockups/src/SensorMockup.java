public class SensorMockup {

    private int startDist, frontSpeed, backSpeed;

    public SensorMockup(){
        init();
    }

    private void init(){
        startDist = 50;
    }

    public int getFrontWheelSpeed(){
        return 0;
    }

    public int getBackWheelSpeed(){
        return 0;
    }

    public double batteryOutput(){
        return 0;
    }

    public int getDist(){
        return 0;
    }

    public void setFrontSpeed(int speed){
        frontSpeed = speed;
    }

    public void setBackSpeed(int speed){
        backSpeed = speed;
    }
}