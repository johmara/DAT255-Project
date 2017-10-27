package absolut.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The handlers that will get the messages needs to implement this interface
 */
public interface IMessageHandler extends Remote {

    /**
     * Gets a message from a sender
     * @param message The message from the sender
     */
    void messageTask(String message) throws RemoteException;
}
