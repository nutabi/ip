package ibatun.errors;

public class IbatunIOException extends IbatunException {
    /**
     * Constructs a new IbatunIOException with the specified detail message.
     *
     * @param message The detail message
     */
    public IbatunIOException(String message) {
        super("IO Error: " + message);
    }

}
