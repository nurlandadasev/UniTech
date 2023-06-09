package az.unibank.commons.exception;

public class PermissionDeniedException extends RuntimeException {

    public PermissionDeniedException() {
        this(null, null);
    }

    public PermissionDeniedException(String username, String path) {
        super("User " + username + ". Permission denied for path: " + path);
    }
}