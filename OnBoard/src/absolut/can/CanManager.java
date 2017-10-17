package absolut.can;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author Gustaf Järgren
 * @author Arndt
 */
public class CanManager implements Runnable {

    private int channelNumber;
    private JavaCanLibrary javaCanLibrary;
    private HashMap<String, Integer> senders = new HashMap<String, Integer>();
    private ArrayList<ICanReceiver> canReceivers = new ArrayList<ICanReceiver>();

    CanManager(HashMap<String, Integer> senders, HashMap<Integer, String> receivers) {
        this.senders = senders;

        for (Entry<Integer, String> entry : receivers.entrySet()) {
            int id = entry.getKey();
            String function = entry.getValue();
            switch (function) {
                case "cantp": {
                    ICanReceiver r = new CanTPReceiver(id);
                    canReceivers.add(r);
                    break;
                }
                case "can": {
                    ICanReceiver r = new ClassicalCanReceiver(id);
                    canReceivers.add(r);
                    break;
                }
                default:
                    throw new RuntimeException("Error: there is wrong function name for receivers");
            }
        }

        javaCanLibrary = Native.loadLibrary("javaCanLib", JavaCanLibrary.class);
        channelNumber = javaCanLibrary.init_can();
    }

    @Override
    public void run() {
        while (true) {
            for (ICanReceiver receiver : canReceivers) {
                byte[] data = receiver.receive();
                if(data.length > 0)
                    try {
                        byte[] parsedData = parseByteData(data[0]);
                        switch (parsedData[0]) {
                            case 1:
                            case 2:
                                //Catch these, have no interest in them but would print errors if not catched
                                break;
                            case 3:
                                int index = 1;

                                /* Key - START */
                                byte[] buffer = new byte[4];
                                for(int i=0;i<4;i++) {
                                    buffer[i] = data[index++];
                                }
                                int keySize = byteArrayToInt(buffer);
                                //byte[] keyBytes = new byte[keySize];
                                for(int k=0;k<keySize;k++) {
                                    //keyBytes[k] = data[index++];
                                    index++;
                                }
                                //String keyStr = new String(keyBytes, Charset.forName("UTF-8"));
                                /* Key - STOP */

                                /* Data - START */
                                try {
                                    for(int i=0;i<4;i++) {
                                        buffer[i] = data[index++];
                                    }
                                } catch (Exception e) {
                                    continue;
                                }
                                int valueSize = byteArrayToInt(buffer);
                                if (valueSize > 512)
                                    break;
                                byte[] valueBytes = new byte[valueSize];
                                for(int v=0;v<valueSize;v++) {
                                    valueBytes[v] = data[index++];
                                }
                                String valueStr = new String(valueBytes, Charset.forName("UTF-8"));
                                /* Data - END */

                                CanReader.getInstance().setData(valueStr);
                                break;
                            default:
                                System.out.println("CanManager " + parsedData[0]);
                                System.out.println("Error: wrong message type from autosar");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    interface ICanReceiver {
        public byte[] receive();
    }

    class ClassicalCanReceiver implements ICanReceiver {
        private int canId;

        public ClassicalCanReceiver(int canId) {
            this.canId = canId;
        }

        @Override
        public byte[] receive() {
            Pointer p = javaCanLibrary.receiveByteData(channelNumber, canId);
            if(p != null) {
                byte len = p.getByte(0);
                if(len <= 0) {
                    System.out.println("It should not receive value less than 0 in normal can channel");
                    return new byte[0];
                }
                byte[] res = new byte[len];
                for (int i = 0; i < len; i++) {
                    res[i] = p.getByte(i + 1);
                }
                return res;
            } else
                return new byte[0];

        }
    }

    class CanTPReceiver implements ICanReceiver {
        private int canId;

        public CanTPReceiver(int canId) {
            this.canId = canId;
        }

        @Override
        public byte[] receive() {
            byte[] res = null;
            Pointer p = javaCanLibrary.receiveData(channelNumber, canId);
            int len = javaCanLibrary.getPackageSize();
            if (len > 0) {
                if (p == null) {
                    System.out.println("CAN receive null pointer");
                    return new byte[0];
                }
                res = new byte[len];
                for (int i = 0; i < len; i++) {
                    res[i] = p.getByte(i);
                }
                javaCanLibrary.resetPackageSize();
                return res;
            }
            return new byte[0];
        }

    }

    public void sendMessage(byte[] inData) {
        int ecuId = 2;
        int can_id;
        if (ecuId == 1) {
            can_id = 1124;
        } else {
            can_id = senders.get(ecuId + "-PWM");
        }
        System.out.println("Sent: " + Arrays.toString(inData));
        javaCanLibrary.sendData(channelNumber, can_id, inData);
    }

    private int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16
                | (b[0] & 0xFF) << 24;
    }

    private byte[] parseByteData(byte data) {
        // 2 bits: MessageType, 6 bits: plugin ID
        byte[] res = new byte[2];
        res[0] = (byte) ((int)(data & 0xFF) >> 6);
        res[1] = (byte) (0x3F & data);
        return res;
    }

}

