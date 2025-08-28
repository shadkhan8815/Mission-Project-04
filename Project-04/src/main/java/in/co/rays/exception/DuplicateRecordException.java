package in.co.rays.exception;

/**
 * DuplicateRecordException is a custom exception class that indicates 
 * a record already exists in the database when attempting to create 
 * a duplicate entry. 
 * <p>
 * It extends {@link Exception} and is typically thrown when 
 * a unique constraint (like login ID, email, or primary key) 
 * is violated.
 * </p>
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class DuplicateRecordException extends Exception {

    /**
     * Constructs a new DuplicateRecordException with the specified detail message.
     * 
     * @param msg the detail message that explains the cause of the exception
     */
    public DuplicateRecordException(String msg) {
        super(msg);
    }

}
