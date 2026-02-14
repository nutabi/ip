package ibatun.errors;

/**
 * Custom exception class for Ibatun application errors.
 *
 * @author Binh
 * @version 1.0
 */
public class IbatunException extends Exception {
    /**
     * Constructs an IbatunException.
     *
     * @param message The error message
     */
    public IbatunException(String message) {
        super(String.format("Oopsie!\n%s", message));
    }
}
