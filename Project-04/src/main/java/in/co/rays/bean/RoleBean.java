package in.co.rays.bean;

/**
 * RoleBean is a JavaBean class that represents different roles in the system.
 * It extends {@link BaseBean} and stores role details such as role name and description.
 * <p>
 * It also defines constants for commonly used roles like ADMIN, STUDENT, COLLEGE, KIOSK, and FACULTY.
 * </p>
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class RoleBean extends BaseBean {

    /**
     * Role constant for Administrator.
     */
    public static final int ADMIN = 1;

    /**
     * Role constant for Student.
     */
    public static final int STUDENT = 2;

    /**
     * Role constant for College.
     */
    public static final int COLLEGE = 3;

    /**
     * Role constant for Kiosk.
     */
    public static final int KIOSK = 4;

    /**
     * Role constant for Faculty.
     */
    public static final int FACULTY = 5;

    /**
     * Name of the role.
     */
    private String name;

    /**
     * Description of the role.
     */
    private String description;

    /**
     * Gets the name of the role.
     * 
     * @return role name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the role.
     * 
     * @param name role name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the role.
     * 
     * @return role description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the role.
     * 
     * @param description role description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the unique key of this bean.
     * 
     * @return id of the bean as String
     */
    @Override
    public String getKey() {
        return id + "";
    }

    /**
     * Gets the displayable value of this bean.
     * 
     * @return role name
     */
    @Override
    public String getValue() {
        return name;
    }
}
