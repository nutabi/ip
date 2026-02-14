package ibatun.errors;

/**
 * Thrown when stored data cannot be parsed due to corruption.
 */
public class IbatunCorruptedDataException extends IbatunException {
    /**
     * Constructs an IbatunCorruptedDataException with the specified detail message.
     *
     * @param message The detail message
     */
    public IbatunCorruptedDataException(String message) {
        super(message);
    }
}
