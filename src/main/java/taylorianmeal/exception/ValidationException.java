package taylorianmeal.exception;

/**
 * ValidationException
 * -------------------
 * Custom exception for input validation errors in the application.
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 