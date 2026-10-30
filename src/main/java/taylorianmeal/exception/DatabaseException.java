package taylorianmeal.exception;

/**
 * DatabaseException
 * -----------------
 * Custom exception for database-related errors in the application.
 */
public class DatabaseException extends RuntimeException {
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DatabaseException(Throwable cause) {
        super(cause);
    }
} 