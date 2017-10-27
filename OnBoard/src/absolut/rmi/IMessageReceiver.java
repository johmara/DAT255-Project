package absolut.rmi;

/**
 * The end receivers should implement this
 */
public interface IMessageReceiver {

    /**
     * Receives the message that was sent to the handler
     * @param msg The message from an handler
     */
    void messageReceived(String msg);

}
