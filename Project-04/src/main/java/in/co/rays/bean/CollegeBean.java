package in.co.rays.bean;

/**
 * CollegeBean is a JavaBean class that represents the details of a college.
 * It extends {@link BaseBean} and holds information such as name, address,
 * state, city, and phone number.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class CollegeBean extends BaseBean {

    /**
     * Name of the college.
     */
    private String name;

    /**
     * Address of the college.
     */
    private String address;

    /**
     * State in which the college is located.
     */
    private String state;

    /**
     * City in which the college is located.
     */
    private String city;

    /**
     * Contact phone number of the college.
     */
    private String phoneNo;

    /**
     * Gets the name of the college.
     * 
     * @return name of the college
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the college.
     * 
     * @param name name of the college
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the address of the college.
     * 
     * @return address of the college
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the college.
     * 
     * @param address address of the college
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the state of the college.
     * 
     * @return state of the college
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state of the college.
     * 
     * @param state state of the college
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the city of the college.
     * 
     * @return city of the college
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city of the college.
     * 
     * @param city city of the college
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the phone number of the college.
     * 
     * @return phone number of the college
     */
    public String getPhoneNo() {
        return phoneNo;
    }

    /**
     * Sets the phone number of the college.
     * 
     * @param phoneNo phone number of the college
     */
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
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
     * @return name of the college
     */
    @Override
    public String getValue() {
        return name;
    }
}
