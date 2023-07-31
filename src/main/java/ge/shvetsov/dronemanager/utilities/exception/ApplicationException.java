package ge.shvetsov.dronemanager.utilities.exception;

public class ApplicationException extends RuntimeException {

    private final String code;

    public ApplicationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return String.format("Application exception: {\"code\": %s, \"message\": %s}", code, getMessage());
    }
}
