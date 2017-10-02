import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MopedMockup implements Runnable{

    private int speedF, speedR, dist, sA, setSpeed;
    private double sAdouble, battery;
    private int follow;

    private int speedLead, leaderPos;

    private SensorMockup sm;

    public MopedMockup(SensorMockup sm, int follow){
        this.sm = sm;
        this.follow = follow;
    }

    private void ACC(){
        int count = 0;
        while (true) {

            speedF = sm.getFrontWheelSpeed();
            speedR = sm.getBackWheelSpeed();
            dist = sm.getDist();
            battery = sm.batteryOutput();

            sAdouble = (speedF + speedR) / 2;
            sA = round(sAdouble);


            if (count == 39) {
                System.out.println(dist);
                System.out.println(sA);
                count = 0;
            }

            if (dist < desiredDist()) {
                setSpeed = maintainSpeed(sA) - aquireControllSignal(sA, 0); //aquire a negative control signal
            } else if (dist > desiredDist()) {
                setSpeed = aquireControllSignal(sA, 1); //aquire a positive control signal
            } else {
                setSpeed = maintainSpeed(sA);
            }
            speed(setSpeed);
            count++;
            try{
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void speed(int setSpeed){
        if(follow == 1){

        } else {
            System.out.println(setSpeed);
            speedLead = setSpeed;
            sm.setfSpeed((int) ((int) (setSpeed+22.3)/0.98));
            sm.setbSpeed((int) ((int) (setSpeed+22.3)/0.98));
        }
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
            return (int) ((speed*0.94)-10.2);
        }
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
    @Override
    public void run() {
        if (follow == 1) {
            try {
                System.out.println("Following moped starting ACC()\n");
                ACC();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Leader moped in 'manual' mode\n");
            //GUI or no?
            /*
            * easy to use understand
            * but harder to implement
            *
            * command line makes it harder to controll
            * and is still quite hard to implement because of the nature of the function
            *
            * GUI should be able to change the speed of the car which is leading th coloumn
            */
            initGUI();
            mopedLeader();

        }
    }

    private void mopedLeader() {

        while(true){
            leaderPos += speedLead;
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initGUI() {

        JFrame window = new JFrame("LEADER MOPED");
        JPanel contentPane = (JPanel) window.getContentPane();
        JTextField inputSpeed = new JTextField();
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e ->{
            String s;
            int i;
            s = inputSpeed.getText();
            s.trim();
            i = Integer.parseInt(s);
            speed(i);
        });
        inputSpeed.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) okBtn.doClick();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        contentPane.setLayout(new GridLayout(1,2));
        contentPane.add(inputSpeed);
        contentPane.add(okBtn);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }

    public void setDist(int dist){
        this.dist = dist;
    }
}



