package az.unibank.commons.enums;

public enum Role {

    SUPER_ADMIN(1),
    USER(2),
    UNKNOWN(0);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    public static Role from(int value) {
        for (Role role : Role.values()) {
            if (role.value == value) {
                return role;
            }
        }
        return UNKNOWN;
    }

    public int value() {
        return this.value;
    }
}