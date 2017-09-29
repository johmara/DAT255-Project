public class SensorMockup implements Runnable{

    private int leadPos, followPos, diffPos , fSpeed, bSpeed, mockupCase;


    public SensorMockup(int mockupCase){
        this.mockupCase = mockupCase;
        init();
    }

    /**
     * speeds are given in cm/s, 
     * standard speed is 30 cm/s
     */
    private void init(){
        switch (mockupCase) {
            case 1://from standing still too far away
                leadPos = 50;
                fSpeed = 0;
                bSpeed = 0;
                followPos =0;
                break;
            case 2: //from standing still too close
                leadPos = 10;
                fSpeed = 0;
                bSpeed = 0;
                break;
            case 3: //from standing still at desired distance 
                leadPos = 15;
                fSpeed = 0;
                bSpeed = 0;
                break;
            case 4: //speed too low too far away
                leadPos = 50;
                fSpeed = 10;
                bSpeed = 10;
                break;
            case 5: //speed too high too close
                leadPos = 10;
                fSpeed = 40;
                bSpeed = 40;
                break;
        }
    }

    public int getFrontWheelSpeed(){
        return fSpeed;
    }

    public int getBackWheelSpeed(){
        return bSpeed;
    }

    public double batteryOutput(){
        return 100;
    }

    public int getDist(){

        return diffPos;
    }

    public void setDist(int dist) {
        this.diffPos = dist;
    }

    public void setFollowPos(int followPos){
        this.followPos = followPos;
    }

    public void setLeadPos(int leadPos){
        this.leadPos = leadPos;
    }

    public void setfSpeed(int speed){
        fSpeed = speed;
    }

    public void setbSpeed(int speed){
        bSpeed = speed;
    }

    @Override
    public void run() {
        int counter = 0;
        while(true) {
            if(counter == 39){
                setDist(leadPos - followPos);
                counter = 0;
            }
            counter++;
            try {
                Thread.sleep(25);// => 40 ggr per sec
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}