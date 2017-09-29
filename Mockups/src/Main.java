public class Main {

    public Main(){}

    public static void main(String[] args){
        int arg;
        arg = Integer.parseInt(args[0]);
        SensorMockup sm = new SensorMockup(arg);
        MopedMockup mmFollow = new MopedMockup(sm, 1);
        MopedMockup mmLeader = new MopedMockup(sm, 0);

        Thread tsm = new Thread(sm);
        Thread tmmFollow = new Thread(mmFollow);
        Thread tmmLeader = new Thread(mmLeader);

        try{
            tsm.start();
            tmmFollow.start();
            tmmLeader.start();

            /*sm.run();
            mmLeader.run();
            mmFollow.run();*/
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
