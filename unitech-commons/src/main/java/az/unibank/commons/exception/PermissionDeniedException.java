package az.unibank.commons.exception;

public class PermissionDeniedException extends RuntimeException {

    public PermissionDeniedException() {
        this(null, null);
    }

    public PermissionDeniedException(String pin, String path) {
        super("User " + pin + ". Permission denied for path: " + path);
    }
}