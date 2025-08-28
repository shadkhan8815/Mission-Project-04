package in.co.rays.bean;

/**
 * DropdownListBean is an interface that provides a contract 
 * for objects that can be represented in a dropdown list.
 * <p>
 * It defines methods to get a unique key and a displayable value.
 * </p>
 * 
 * @author Shad Khan
 * @version 1.0
 */
public interface DropdownListBean {

    /**
     * Returns the unique key of the object.
     * This is generally used as the hidden value of a dropdown option.
     * 
     * @return key as a String
     */
    public String getKey();

    /**
     * Returns the displayable value of the object.
     * This is generally used as the visible text of a dropdown option.
     * 
     * @return value as a String
     */
    public String getValue();

}
