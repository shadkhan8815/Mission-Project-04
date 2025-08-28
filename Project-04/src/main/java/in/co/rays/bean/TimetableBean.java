package in.co.rays.bean;

import java.util.Date;

/**
 * TimetableBean is a JavaBean class that represents the timetable details
 * of examinations. It extends {@link BaseBean} and contains information
 * such as semester, description, exam date, exam time, course, and subject.
 * 
 * This bean can be used to store and retrieve timetable-related data 
 * in an application.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class TimetableBean extends BaseBean {

    /**
     * Semester for which the timetable is scheduled.
     */
    private String semester;

    /**
     * Description of the timetable or exam.
     */
    private String description;

    /**
     * Date of the examination.
     */
    private Date examDate;

    /**
     * Time of the examination.
     */
    private String examTime;

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
     * Gets the semester of the timetable.
     * 
     * @return semester
     */
    public String getSemester() {
        return semester;
    }

    /**
     * Sets the semester of the timetable.
     * 
     * @param semester semester
     */
    public void setSemester(String semester) {
        this.semester = semester;
    }

    /**
     * Gets the description of the timetable.
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the timetable.
     * 
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the exam date.
     * 
     * @return exam date
     */
    public Date getExamDate() {
        return examDate;
    }

    /**
     * Sets the exam date.
     * 
     * @param examDate exam date
     */
    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    /**
     * Gets the exam time.
     * 
     * @return exam time
     */
    public String getExamTime() {
        return examTime;
    }

    /**
     * Sets the exam time.
     * 
     * @param examTime exam time
     */
    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    /**
     * Gets the course ID associated with the timetable.
     * 
     * @return course ID
     */
    public long getCourseId() {
        return courseId;
    }

    /**
     * Sets the course ID associated with the timetable.
     * 
     * @param courseId course ID
     */
    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    /**
     * Gets the course name associated with the timetable.
     * 
     * @return course name
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Sets the course name associated with the timetable.
     * 
     * @param courseName course name
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * Gets the subject ID associated with the timetable.
     * 
     * @return subject ID
     */
    public long getSubjectId() {
        return subjectId;
    }

    /**
     * Sets the subject ID associated with the timetable.
     * 
     * @param subjectId subject ID
     */
    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    /**
     * Gets the subject name associated with the timetable.
     * 
     * @return subject name
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Sets the subject name associated with the timetable.
     * 
     * @param subjectName subject name
     */
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    /**
     * Gets the unique key of this bean.
     * 
     * @return key as String (currently returns null)
     */
    @Override
    public String getKey() {
        return id + " ";
    }

    /**
     * Gets the displayable value of this bean.
     * 
     * @return value as String (currently returns null)
     */
    @Override
    public String getValue() {
        return semester;
    }
}
