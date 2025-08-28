package in.co.rays.exception;

/**
 * DatabaseException is a custom exception class that indicates 
 * errors related to database operations. 
 * <p>
 * It extends {@link Exception} and is typically thrown when any 
 * database connectivity issue, query failure, or SQL-related 
 * problem occurs.
 * </p>
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class DatabaseException extends Exception {

    /**
     * Constructs a new DatabaseException with the specified detail message.
     * 
     * @param msg the detail message that provides more information about the exception
     */
    public DatabaseException(String msg) {
        super(msg);
    }
}
