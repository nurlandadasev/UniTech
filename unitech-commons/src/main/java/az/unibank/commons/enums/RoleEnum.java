package az.unibank.commons.enums;

public enum RoleEnum {

    ADMIN(1),
    USER(2),
    UNKNOWN(0);

    private final int value;

    RoleEnum(int value) {
        this.value = value;
    }

    public static RoleEnum from(int value) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.value == value) {
                return roleEnum;
            }
        }
        return UNKNOWN;
    }

    public int value() {
        return this.value;
    }
}