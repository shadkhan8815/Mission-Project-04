package in.co.rays.bean;

import java.util.Date;

/**
 * StudentBean is a JavaBean class that represents the details of a student.
 * It extends {@link BaseBean} and stores information such as name, date of birth,
 * gender, contact details, and associated college information.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class StudentBean extends BaseBean {

    /**
     * First name of the student.
     */
    private String firstName;

    /**
     * Last name of the student.
     */
    private String lastName;

    /**
     * Date of birth of the student.
     */
    private Date dob;

    /**
     * Gender of the student.
     */
    private String gender;

    /**
     * Mobile number of the student.
     */
    private String mobileNo;

    /**
     * Email address of the student.
     */
    private String email;

    /**
     * ID of the associated college.
     */
    private long collegeId;

    /**
     * Name of the associated college.
     */
    private String collegeName;

    /**
     * Gets the first name of the student.
     * 
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the student.
     * 
     * @param firstName first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the student.
     * 
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the student.
     * 
     * @param lastName last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the date of birth of the student.
     * 
     * @return date of birth
     */
    public Date getDob() {
        return dob;
    }

    /**
     * Sets the date of birth of the student.
     * 
     * @param dob date of birth
     */
    public void setDob(Date dob) {
        this.dob = dob;
    }

    /**
     * Gets the gender of the student.
     * 
     * @return gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender of the student.
     * 
     * @param gender gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets the mobile number of the student.
     * 
     * @return mobile number
     */
    public String getMobileNo() {
        return mobileNo;
    }

    /**
     * Sets the mobile number of the student.
     * 
     * @param mobileNo mobile number
     */
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    /**
     * Gets the email address of the student.
     * 
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the student.
     * 
     * @param email email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the college ID associated with the student.
     * 
     * @return college ID
     */
    public long getCollegeId() {
        return collegeId;
    }

    /**
     * Sets the college ID associated with the student.
     * 
     * @param collegeId college ID
     */
    public void setCollegeId(long collegeId) {
        this.collegeId = collegeId;
    }

    /**
     * Gets the name of the associated college.
     * 
     * @return college name
     */
    public String getCollegeName() {
        return collegeName;
    }

    /**
     * Sets the name of the associated college.
     * 
     * @param collegeName college name
     */
    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
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
     * Gets the displayable value of this bean (student full name).
     * 
     * @return full name (first name + last name)
     */
    @Override
    public String getValue() {
        return firstName + "" + lastName;
    }
}
