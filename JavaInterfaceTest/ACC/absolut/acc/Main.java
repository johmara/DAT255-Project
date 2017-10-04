package absolut.acc;

import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException {
        CAN can = CAN.getInstance();
        ACC acc = new ACC(can);
        Thread accThread = new Thread(acc);
        accThread.start();
    }
}