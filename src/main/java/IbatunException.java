public class IbatunException extends Exception {
    public IbatunException(String message) {
        super(String.format("Oopsie! %s", message));
    }   
}
