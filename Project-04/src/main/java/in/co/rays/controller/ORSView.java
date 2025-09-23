package in.co.rays.controller;

/**
 * ORSView interface defines the constants for all view (JSP) file paths used 
 * in the Online Result System (ORS) project.
 * 
 * It centralizes the location of JSP pages so they can be easily managed 
 * and referenced throughout the application.
 * 
 * @author Shad khan
 * @version 1.0
 */
public interface ORSView {

    /** Application context path */
    public String APP_CONTEXT = "/Project-04";

    /** Base folder for JSP pages */
    public String PAGE_FOLDER = "/jsp";

    /** Path to Java documentation */
    public String JAVA_DOC = APP_CONTEXT + "/doc/index.html";
    
//--------------------------------------------------------------
    
    /** Error page view */
    public String ERROR_VIEW = PAGE_FOLDER + "/ErrorView.jsp";

    /** Marksheet form view */
    public String MARKSHEET_VIEW = PAGE_FOLDER + "/MarksheetView.jsp";

    /** Marksheet list view */
    public String MARKSHEET_LIST_VIEW = PAGE_FOLDER + "/MarksheetListView.jsp";

    /** Get Marksheet by Roll Number view */
    public String GET_MARKSHEET_VIEW = PAGE_FOLDER + "/GetMarksheetView.jsp";

    /** User form view */
    public String USER_VIEW = PAGE_FOLDER + "/UserView.jsp";

    /** User list view */
    public String USER_LIST_VIEW = PAGE_FOLDER + "/UserListView.jsp";

    /** College form view */
    public String COLLEGE_VIEW = PAGE_FOLDER + "/CollegeView.jsp";

    /** College list view */
    public String COLLEGE_LIST_VIEW = PAGE_FOLDER + "/CollegeListView.jsp";

    /** Student form view */
    public String STUDENT_VIEW = PAGE_FOLDER + "/StudentView.jsp";

    /** Student list view */
    public String STUDENT_LIST_VIEW = PAGE_FOLDER + "/StudentListView.jsp";

    /** Role form view */
    public String ROLE_VIEW = PAGE_FOLDER + "/RoleView.jsp";

    /** Role list view */
    public String ROLE_LIST_VIEW = PAGE_FOLDER + "/RoleListView.jsp";

    /** User registration form view */
    public String USER_REGISTRATION_VIEW = PAGE_FOLDER + "/UserRegistrationView.jsp";

    /** Login form view */
    public String LOGIN_VIEW = PAGE_FOLDER + "/LoginView.jsp";

    /** Welcome page view */
    public String WELCOME_VIEW = PAGE_FOLDER + "/Welcome.jsp";

    /** Change password form view */
    public String CHANGE_PASSWORD_VIEW = PAGE_FOLDER + "/ChangePasswordView.jsp";

    /** My profile page view */
    public String MY_PROFILE_VIEW = PAGE_FOLDER + "/MyProfileView.jsp";

    /** Forget password form view */
    public String FORGET_PASSWORD_VIEW = PAGE_FOLDER + "/ForgetPasswordView.jsp";

    /** Marksheet merit list view */
    public String MARKSHEET_MERIT_LIST_VIEW = PAGE_FOLDER + "/MarksheetMeritListView.jsp";

    /** Course form view */
    public String COURSE_VIEW = PAGE_FOLDER + "/CourseView.jsp";

    /** Course list view */
    public String COURSE_LIST_VIEW = PAGE_FOLDER + "/CourseListView.jsp";

    /** Subject form view */
    public String SUBJECT_VIEW = PAGE_FOLDER + "/SubjectView.jsp";

    /** Subject list view */
    public String SUBJECT_LIST_VIEW = PAGE_FOLDER + "/SubjectListView.jsp";

    /** Timetable form view */
    public String TIMETABLE_VIEW = PAGE_FOLDER + "/TimetableView.jsp";

    /** Timetable list view */
    public String TIMETABLE_LIST_VIEW = PAGE_FOLDER + "/TimetableListView.jsp";

    /** Faculty form view */
    public String FACULTY_VIEW = PAGE_FOLDER + "/FacultyView.jsp";

    /** Faculty list view */
    public String FACULTY_LIST_VIEW = PAGE_FOLDER + "/FacultyListView.jsp";
    
    public String PATIENT_VIEW = PAGE_FOLDER + "/PatientView.jsp";
    
    public String PATIENT_LIST_VIEW = PAGE_FOLDER + "/PatientListView.jsp";

//-------------------------------------------------------
    
    /** Controller for handling application errors */
    public String ERROR_CTL = APP_CONTEXT + "/ErrorCtl";

    /** Controller for handling new user registration */
    public String USER_REGISTRATION_CTL = APP_CONTEXT + "/UserRegistrationCtl";

    /** Controller for handling user login */
    public String LOGIN_CTL = APP_CONTEXT + "/LoginCtl";

    /** Controller for displaying the welcome page after login */
    public String WELCOME_CTL = APP_CONTEXT + "/WelcomeCtl";

    /** Controller for handling forget password requests */
    public String FORGET_PASSWORD_CTL = APP_CONTEXT + "/ForgetPasswordCtl";

 //---------------------------------------------------
    
    /** Controller for handling Marksheet operations */
    public String MARKSHEET_CTL = APP_CONTEXT + "/ctl/MarksheetCtl";

    /** Controller for displaying the list of Marksheets */
    public String MARKSHEET_LIST_CTL = APP_CONTEXT + "/ctl/MarksheetListCtl";

    /** Controller for handling User operations */
    public String USER_CTL = APP_CONTEXT + "/ctl/UserCtl";

    /** Controller for displaying the list of Users */
    public String USER_LIST_CTL = APP_CONTEXT + "/ctl/UserListCtl";

    /** Controller for handling College operations */
    public String COLLEGE_CTL = APP_CONTEXT + "/ctl/CollegeCtl";

    /** Controller for displaying the list of Colleges */
    public String COLLEGE_LIST_CTL = APP_CONTEXT + "/ctl/CollegeListCtl";

    /** Controller for handling Student operations */
    public String STUDENT_CTL = APP_CONTEXT + "/ctl/StudentCtl";

    /** Controller for displaying the list of Students */
    public String STUDENT_LIST_CTL = APP_CONTEXT + "/ctl/StudentListCtl";

    /** Controller for handling Role operations */
    public String ROLE_CTL = APP_CONTEXT + "/ctl/RoleCtl";

    /** Controller for displaying the list of Roles */
    public String ROLE_LIST_CTL = APP_CONTEXT + "/ctl/RoleListCtl";

    /** Controller for fetching a Marksheet by Roll Number */
    public String GET_MARKSHEET_CTL = APP_CONTEXT + "/ctl/GetMarksheetCtl";

    /** Controller for handling Change Password operations */
    public String CHANGE_PASSWORD_CTL = APP_CONTEXT + "/ctl/ChangePasswordCtl";

    /** Controller for displaying and updating My Profile page */
    public String MY_PROFILE_CTL = APP_CONTEXT + "/ctl/MyProfileCtl";

    /** Controller for displaying the Marksheet Merit List */
    public String MARKSHEET_MERIT_LIST_CTL = APP_CONTEXT + "/ctl/MarksheetMeritListCtl";

    /** Controller for handling Course operations */
    public String COURSE_CTL = APP_CONTEXT + "/ctl/CourseCtl";

    /** Controller for displaying the list of Courses */
    public String COURSE_LIST_CTL = APP_CONTEXT + "/ctl/CourseListCtl";

    /** Controller for handling Subject operations */
    public String SUBJECT_CTL = APP_CONTEXT + "/ctl/SubjectCtl";

    /** Controller for displaying the list of Subjects */
    public String SUBJECT_LIST_CTL = APP_CONTEXT + "/ctl/SubjectListCtl";

    /** Controller for handling Timetable operations */
    public String TIMETABLE_CTL = APP_CONTEXT + "/ctl/TimetableCtl";

    /** Controller for displaying the list of Timetables */
    public String TIMETABLE_LIST_CTL = APP_CONTEXT + "/ctl/TimetableListCtl";

    /** Controller for handling Faculty operations */
    public String FACULTY_CTL = APP_CONTEXT + "/ctl/FacultyCtl";

    /** Controller for displaying the list of Faculties */
    public String FACULTY_LIST_CTL = APP_CONTEXT + "/ctl/FacultyListCtl";
    
    public String PATIENT_CTL = APP_CONTEXT + "/ctl/PatientCtl";
    
    public String PATIENT_LIST_CTL = APP_CONTEXT + "/ctl/PatientListCtl";
}
