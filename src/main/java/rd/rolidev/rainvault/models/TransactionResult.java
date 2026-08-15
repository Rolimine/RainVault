package rd.rolidev.rainvault.models;

public class TransactionResult {
    private final boolean success;
    private final String message;
    private final Transaction transaction;

    public TransactionResult(boolean success, String message, Transaction transaction) {
        this.success = success;
        this.message = message;
        this.transaction = transaction;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
