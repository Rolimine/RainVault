package rd.rolidev.rainvault.exceptions;

public class EconomyException extends Exception {
    private final ErrorType errorType;

    public EconomyException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public EconomyException(String message, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public enum ErrorType {
        INVALID_AMOUNT,
        INSUFFICIENT_FUNDS,
        MAX_BALANCE_EXCEEDED,
        PLAYER_NOT_FOUND,
        DATABASE_ERROR,
        TRANSACTION_FAILED,
        PERMISSION_DENIED,
        INVALID_OPERATION
    }
}
