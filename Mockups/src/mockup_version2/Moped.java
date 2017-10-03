package mockup_version2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.Semaphore;

public class Moped implements Runnable{

    private int follow;
    private Sensor sensor;
    private Moped leader;

    private int lxPos, lControlSignal;
    private int fxPos, fControlSignal;

    private Semaphore sem;

    private int preferedDistance;

    /**
     *  @param follow if follow = 0 this moped is the leader
     * @param sm
     * @param leader
     */
    public Moped(int follow, Sensor sm, Moped leader){
        this.follow = follow;
        this.sensor = sm;
        this.leader = leader;
        preferedDistance = 20;

        sem = new Semaphore(1);

    }

    /**
     * TODO:
     * @param i
     */
    public void speed(int i){
        if (follow == 0){
            lControlSignal = i;
        }else {
            fControlSignal = i;
        }
    }


    private void leader() {
        /*
        Velocity är i cm/s
        Controll signal är i -100 till 100
        xpos är en punkt på x-axeln
        */
        int counter = 0;
        while (true){
            if (counter == 39){
                if(sem.tryAcquire()) {
                    lxPos = lxPos + lControlSignal;
                }
                sem.release();
                counter =0;
            }
            counter++;
            //System.out.println("velocity Leader: " + lControlSignal + " xPos Leader: " + lxPos);
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * TODO:
     */
    private void follow() {
        int counter = 0;
        int lxPos = 0;
        while (true) {
            int dist = 0;
            if(sensor.sensorSem.tryAcquire()){
                dist = sensor.getDistance();
            }
            sensor.sensorSem.release();

            Acc(dist);

            if (counter == 39){ //bör hända 1 gång per sekund
                fxPos = fxPos + fControlSignal;
                //System.out.println("velocity Follower: " + fControlSignal + " xPos Follower: " + fxPos);
                counter = 0;
            }
            counter++;

            if (sem.tryAcquire()) {
                lxPos = leader.lxPos;
            }
            sem.release();
            if (sensor.sensorSem.tryAcquire()) {
                sensor.setDistance(lxPos - fxPos);
            }
            sensor.sensorSem.release();
        }
    }


    private void Acc(int dist) {
        //helllo

        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (follow == 0) {
            initGUI("LEADER");
            leader();
        }else {
            initGUI("FOLLOWER");
            follow();
        }
    }

    private void initGUI(String windowTitle) {
        JFrame window = new JFrame(windowTitle);
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
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) okBtn.doClick();
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        contentPane.setLayout(new GridLayout(1,2));
        contentPane.add(inputSpeed);
        contentPane.add(okBtn);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }

}
