package az.unibank.commons.enums;

public enum ResponseCode {

    OK(200, "OK"),
    CREATED(201, "Created"),
    DUPLICATE(300, "Duplicate"),
    UNAUTHORIZED(401, "Unauthorized"),
    PERMISSION_DENIED(403, "Permission Denied"),
    NOT_FOUND(404, "Not Found"),
    INVALID_VALUE(405, "Invalid Value"),
    INCORRECT_VALUE(406, "Incorrect Value"),
    INTEGRITY_ERROR(408, "Integrity Error"),
    NOT_ENOUGH_BALANCE(409, "Not Enough Balance"),
    MAX_VALUE_EXCEEDED(410, "Max Value Exceeded"),
    INVALID_RANGE(411, "Invalid range"),
    ACCOUNT_ALREADY_REGISTERED(413, "Account already registered"),
    ACCOUNT_HAS_ACTIVE_SYNC(431, "Account has active sync"),
    ACCOUNT_NOT_ACTIVE(444, "Account not active"),
    EXPIRED(445, "Expired"),
    ACCOUNT_BLOCKED(446, "Account blocked"),
    SERVER_ERROR(500, "Server Error"),
    SELLER_ACCOUNT_ERROR(505, "Seller Account Error"),
    API_LIMIT_EXCEEDED(506, "Api limit exceeded"),
    UNKNOWN(0, "Unknown");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseCode valueOf(int code) {
        for (ResponseCode responseCode : ResponseCode.values())
            if (responseCode.code == code) {
                return responseCode;
            }
        return UNKNOWN;
    }

    public int value() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.message;
    }

    public boolean isSuccess() {
        return this.code == OK.value();
    }
}