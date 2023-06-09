package az.unibank.commons.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ShipmentException extends RuntimeException {
    public ShipmentException(String message) {
        super(message);
    }
}