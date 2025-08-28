package in.co.rays.bean;

/**
 * CourseBean is a JavaBean class that represents the details of a course.
 * It extends {@link BaseBean} and holds information such as name, duration,
 * and description of the course.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class CourseBean extends BaseBean {

    /**
     * Name of the course.
     */
    private String name;

    /**
     * Duration of the course.
     */
    private String duration;

    /**
     * Description of the course.
     */
    private String description;

    /**
     * Gets the name of the course.
     * 
     * @return name of the course
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the course.
     * 
     * @param name name of the course
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the duration of the course.
     * 
     * @return duration of the course
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the course.
     * 
     * @param duration duration of the course
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Gets the description of the course.
     * 
     * @return description of the course
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the course.
     * 
     * @param description description of the course
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
     * @return name of the course
     */
    @Override
    public String getValue() {
        return name;
    }
}
