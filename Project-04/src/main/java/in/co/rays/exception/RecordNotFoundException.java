package in.co.rays.exception;

/**
 * RecordNotFoundException is a custom exception class that indicates 
 * the requested record was not found in the database or data source. 
 * <p>
 * It extends {@link Exception} and is typically thrown when an 
 * operation (like update, delete, or search) is attempted on a 
 * non-existing record.
 * </p>
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class RecordNotFoundException extends Exception {

    /**
     * Constructs a new RecordNotFoundException with the specified detail message.
     * 
     * @param msg the detail message that describes the cause of the exception
     */
    public RecordNotFoundException(String msg) {
        super(msg);

    }
}
