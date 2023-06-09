package az.unibank.commons.exception;

public class RemoteApiException extends RuntimeException {

    public RemoteApiException() {
        this(null);
    }

    public RemoteApiException(String api) {
        super("Error while accessing remote api" + (api != null ? ": " + api : ""));
    }
}