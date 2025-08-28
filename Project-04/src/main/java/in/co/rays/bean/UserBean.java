package in.co.rays.bean;

import java.util.Date;

/**
 * UserBean is a JavaBean class that represents the user details.
 * It extends {@link BaseBean} and includes fields such as first name,
 * last name, login credentials, date of birth, mobile number, role, and gender.
 * 
 * This bean is commonly used for user management in an application.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class UserBean extends BaseBean {

    /**
     * First name of the user.
     */
    private String firstName;

    /**
     * Last name of the user.
     */
    private String lastName;

    /**
     * Login ID (username or email) of the user.
     */
    private String login;

    /**
     * Password of the user.
     */
    private String password;

    /**
     * Confirm password field to verify the password.
     */
    private String confirmPassword;

    /**
     * Date of birth of the user.
     */
    private Date dob;

    /**
     * Mobile number of the user.
     */
    private String mobileNo;

    /**
     * Role ID associated with the user.
     */
    private long roleId;

    /**
     * Gender of the user.
     */
    private String gender;

    /**
     * Gets the first name of the user.
     * 
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     * 
     * @param firstName user's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the user.
     * 
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     * 
     * @param lastName user's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the login ID of the user.
     * 
     * @return login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Sets the login ID of the user.
     * 
     * @param login user's login ID
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Gets the password of the user.
     * 
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     * 
     * @param password user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the confirm password field of the user.
     * 
     * @return confirmPassword
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * Sets the confirm password of the user.
     * 
     * @param confirmPassword confirm password
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * Gets the date of birth of the user.
     * 
     * @return dob
     */
    public Date getDob() {
        return dob;
    }

    /**
     * Sets the date of birth of the user.
     * 
     * @param dob date of birth
     */
    public void setDob(Date dob) {
        this.dob = dob;
    }

    /**
     * Gets the mobile number of the user.
     * 
     * @return mobileNo
     */
    public String getMobileNo() {
        return mobileNo;
    }

    /**
     * Sets the mobile number of the user.
     * 
     * @param mobileNo mobile number
     */
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    /**
     * Gets the role ID of the user.
     * 
     * @return roleId
     */
    public long getRoleId() {
        return roleId;
    }

    /**
     * Sets the role ID of the user.
     * 
     * @param roleId role ID
     */
    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets the gender of the user.
     * 
     * @return gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender of the user.
     * 
     * @param gender gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets the unique key of this bean (user ID).
     * 
     * @return key as String
     */
    @Override
    public String getKey() {
        return id + "";
    }

    /**
     * Gets the displayable value of this bean (user's full name).
     * 
     * @return firstName + " " + lastName
     */
    @Override
    public String getValue() {
        return firstName + " " + lastName;
    }
}
