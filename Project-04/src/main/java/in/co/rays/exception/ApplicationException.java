package in.co.rays.exception;

/**
 * ApplicationException is a custom exception class used to represent
 * application-level exceptions in the project. 
 * <p>
 * It extends {@link Exception} and is typically thrown when any 
 * unexpected situation occurs in the application logic.
 * </p>
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class ApplicationException extends Exception {

    /**
     * Constructs a new ApplicationException with the specified detail message.
     * 
     * @param msg the detail message that provides more information about the exception
     */
    public ApplicationException(String msg) {
        super(msg);
    }
}
