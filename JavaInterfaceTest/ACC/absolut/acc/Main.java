package absolut.acc;

import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException {
        ACC acc = new ACC();
        Thread accThread = new Thread(acc);
        accThread.start();
    }
}