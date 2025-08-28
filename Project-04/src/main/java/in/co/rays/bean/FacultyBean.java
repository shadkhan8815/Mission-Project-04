package in.co.rays.bean;

import java.util.Date;

/**
 * FacultyBean is a JavaBean class that represents details of a faculty member.
 * It extends {@link BaseBean} and stores information such as personal details,
 * contact details, and associations with college, course, and subject.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class FacultyBean extends BaseBean {

    /**
     * First name of the faculty.
     */
    private String firstName;

    /**
     * Last name of the faculty.
     */
    private String lastName;

    /**
     * Date of birth of the faculty.
     */
    private Date dob;

    /**
     * Gender of the faculty.
     */
    private String gender;

    /**
     * Mobile number of the faculty.
     */
    private String mobileNo;

    /**
     * Email address of the faculty.
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
     * ID of the associated course.
     */
    private long courseId;

    /**
     * Name of the associated course.
     */
    private String courseName;

    /**
     * ID of the associated subject.
     */
    private long subjectId;

    /**
     * Name of the associated subject.
     */
    private String subjectName;

    /**
     * Gets the first name of the faculty.
     * 
     * @return first name of the faculty
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the faculty.
     * 
     * @param firstName first name of the faculty
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the faculty.
     * 
     * @return last name of the faculty
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the faculty.
     * 
     * @param lastName last name of the faculty
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the date of birth of the faculty.
     * 
     * @return date of birth
     */
    public Date getDob() {
        return dob;
    }

    /**
     * Sets the date of birth of the faculty.
     * 
     * @param dob date of birth
     */
    public void setDob(Date dob) {
        this.dob = dob;
    }

    /**
     * Gets the gender of the faculty.
     * 
     * @return gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender of the faculty.
     * 
     * @param gender gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets the mobile number of the faculty.
     * 
     * @return mobile number
     */
    public String getMobileNo() {
        return mobileNo;
    }

    /**
     * Sets the mobile number of the faculty.
     * 
     * @param mobileNo mobile number
     */
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    /**
     * Gets the email address of the faculty.
     * 
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the faculty.
     * 
     * @param email email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the college ID associated with the faculty.
     * 
     * @return college ID
     */
    public long getCollegeId() {
        return collegeId;
    }

    /**
     * Sets the college ID associated with the faculty.
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
     * Gets the course ID associated with the faculty.
     * 
     * @return course ID
     */
    public long getCourseId() {
        return courseId;
    }

    /**
     * Sets the course ID associated with the faculty.
     * 
     * @param courseId course ID
     */
    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    /**
     * Gets the name of the associated course.
     * 
     * @return course name
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Sets the name of the associated course.
     * 
     * @param courseName course name
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * Gets the subject ID associated with the faculty.
     * 
     * @return subject ID
     */
    public long getSubjectId() {
        return subjectId;
    }

    /**
     * Sets the subject ID associated with the faculty.
     * 
     * @param subjectId subject ID
     */
    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    /**
     * Gets the name of the associated subject.
     * 
     * @return subject name
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Sets the name of the associated subject.
     * 
     * @param subjectName subject name
     */
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
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
     * Gets the displayable value of this bean (faculty full name).
     * 
     * @return full name (first name + last name)
     */
    @Override
    public String getValue() {
        return firstName + " " + lastName;
    }
}
