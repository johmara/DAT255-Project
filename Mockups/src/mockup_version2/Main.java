package mockup_version2;

public class Main {

    public Main(){}

    public static void main(String[] args){
        //int arg;
        //arg = Integer.parseInt(args[0]);
        Sensor sm = new Sensor();
        Moped mmLeader = new Moped(0, sm, null);
        Moped mmFollow = new Moped(1,sm, mmLeader);


        Thread tsm = new Thread(sm);
        Thread tmmLeader = new Thread(mmLeader);
        Thread tmmFollow = new Thread(mmFollow);


        try{
            tsm.start();
            tmmLeader.start();
            tmmFollow.start();

            /*sm.run();
            mmLeader.run();
            mmFollow.run();*/
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
