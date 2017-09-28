public class MopedMockup {

    private int speedF, speedR, dist, sA, setSpeed;
    private double sAdouble, battery;

    private SensorMockup sm;

    public MopedMockup(SensorMockup sm){
        this.sm = sm;
        ACC();

    }

    private void ACC(){
        while (true) {

            speedF = sm.getFrontWheelSpeed();
            speedR = sm.getBackWheelSpeed();
            dist = sm.getDist();
            battery = sm.batteryOutput();

            sAdouble = (speedF + speedR) / 2;
            sA = round(sAdouble);

            if (dist < desiredDist()) {
                setSpeed = maintainSpeed(sA) - aquireControllSignal(sA,
                        0); //aquire a negative control signal
            } else if (dist > desiredDist()) {
                setSpeed = aquireControllSignal(sA, 1); //aquire a positive control signal
            } else {
                setSpeed = maintainSpeed(sA);
            }

            speed(setSpeed);


        }
    }

    private void speed(int setSpeed){

    }

    private int desiredDist() {
        return 15;
    }

    /**
     *
     * @return a controllsignal
     */
    private int maintainSpeed(int speed) {
        // f(x) = 0.98x - 22.3
        if (battery > battery * 0.5){
            return (int) ((speed*0.98)-22.3);
        }else {
            return (int) ((speed*0.94)-10.2); //satte bara ett värde för retur satsen
        }
        //return 300; //standard värde för fel eftersom skalan går från -100 till 100
    }

    /**
     *
     * @return a controllsignal
     */
    private int aquireControllSignal(int speed, int index) {
        // f(x) = 0.98x - 22.3
        int speedtmp;
        if (battery > battery * 0.5){
            /*decrease speed*/
            if (index == 0){
                speedtmp = (int) (speed-0.98);
                return round((speedtmp*0.98)-22.3);
            }else if(index == 1){/*increase speed*/
                speedtmp = (int)(speed+0.98);
                return round((speedtmp*0.98)-22.3);
            }
        }else{
            if (index == 0){
                speedtmp = (int)(speed-0.94);
                return round((speedtmp*0.94)-10.2);
            }else if(index == 1){
                speedtmp = (int)(speed+0.94);
                return round((speedtmp*0.94)-10.2);
            }
        }

        return 300;
    }

    private int round(double num){

        double tmp;
        tmp = (num%10)%1;
        if(tmp >= 0.5){
            num -= tmp;
            tmp = 1;
            num += tmp;
            return (int)num;

        }else{
            num -= tmp;
            tmp = 1;
            num -= tmp;
            return (int)num;
        }
    }
}



