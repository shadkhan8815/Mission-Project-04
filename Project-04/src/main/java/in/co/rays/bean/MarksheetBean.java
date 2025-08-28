package in.co.rays.bean;

/**
 * MarksheetBean is a JavaBean class that represents the marksheet details of a student.
 * It extends {@link BaseBean} and stores information such as roll number, student ID,
 * student name, and marks in different subjects.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class MarksheetBean extends BaseBean {

    /**
     * Roll number of the student.
     */
    private String rollNo;

    /**
     * ID of the student.
     */
    private long studentId;

    /**
     * Name of the student.
     */
    private String name;

    /**
     * Marks obtained in Physics.
     */
    private Integer physics;

    /**
     * Marks obtained in Chemistry.
     */
    private Integer chemistry;

    /**
     * Marks obtained in Mathematics.
     */
    private Integer maths;

    /**
     * Gets the roll number of the student.
     * 
     * @return roll number
     */
    public String getRollNo() {
        return rollNo;
    }

    /**
     * Sets the roll number of the student.
     * 
     * @param rollNo roll number
     */
    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    /**
     * Gets the student ID.
     * 
     * @return student ID
     */
    public long getStudentId() {
        return studentId;
    }

    /**
     * Sets the student ID.
     * 
     * @param studentId student ID
     */
    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    /**
     * Gets the name of the student.
     * 
     * @return student name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the student.
     * 
     * @param name student name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the marks obtained in Physics.
     * 
     * @return marks in Physics
     */
    public Integer getPhysics() {
        return physics;
    }

    /**
     * Sets the marks obtained in Physics.
     * 
     * @param physics marks in Physics
     */
    public void setPhysics(Integer physics) {
        this.physics = physics;
    }

    /**
     * Gets the marks obtained in Chemistry.
     * 
     * @return marks in Chemistry
     */
    public Integer getChemistry() {
        return chemistry;
    }

    /**
     * Sets the marks obtained in Chemistry.
     * 
     * @param chemistry marks in Chemistry
     */
    public void setChemistry(Integer chemistry) {
        this.chemistry = chemistry;
    }

    /**
     * Gets the marks obtained in Mathematics.
     * 
     * @return marks in Mathematics
     */
    public Integer getMaths() {
        return maths;
    }

    /**
     * Sets the marks obtained in Mathematics.
     * 
     * @param maths marks in Mathematics
     */
    public void setMaths(Integer maths) {
        this.maths = maths;
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
     * It combines the student's name and roll number.
     * 
     * @return name + roll number
     */
    @Override
    public String getValue() {
        return name + rollNo;
    }
}
