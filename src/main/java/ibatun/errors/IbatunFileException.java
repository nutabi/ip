package ibatun.errors;

/**
 * Thrown when an I/O error occurs in the Ibatun application.
 */
public class IbatunFileException extends IbatunException {
    /**
     * Constructs an IbatunFileException with the specified detail message.
     *
     * @param message The detail message
     */
    public IbatunFileException(String message) {
        super("IO Error: " + message);
    }

}
