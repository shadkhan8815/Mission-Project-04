package in.co.rays.bean;

/**
 * SubjectBean is a JavaBean class that represents the details of a subject.
 * It extends {@link BaseBean} and stores information such as subject name,
 * course ID, course name, and description.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class SubjectBean extends BaseBean {

    /**
     * Name of the subject.
     */
    private String name;

    /**
     * ID of the associated course.
     */
    private long courseId;

    /**
     * Name of the associated course.
     */
    private String courseName;

    /**
     * Description of the subject.
     */
    private String description;

    /**
     * Gets the name of the subject.
     * 
     * @return subject name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the subject.
     * 
     * @param name subject name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the course ID associated with the subject.
     * 
     * @return course ID
     */
    public long getCourseId() {
        return courseId;
    }

    /**
     * Sets the course ID associated with the subject.
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
     * Gets the description of the subject.
     * 
     * @return subject description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the subject.
     * 
     * @param description subject description
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
     * @return subject name
     */
    @Override
    public String getValue() {
        return name;
    }
}
