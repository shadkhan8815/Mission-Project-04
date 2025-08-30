package in.co.rays.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.UserBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.model.UserModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;
import org.apache.log4j.Logger;

/**
 * MyProfileCtl Controller manages the user profile functionalities such as 
 * viewing and updating user details. 
 * 
 * Functionalities:
 * <ul>
 *   <li>Displays the current logged-in user profile</li>
 *   <li>Validates profile update form fields</li>
 *   <li>Allows user to update their profile</li>
 *   <li>Provides navigation to change password functionality</li>
 * </ul>
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "MyProfileCtl", urlPatterns = { "/ctl/MyProfileCtl" })
public class MyProfileCtl extends BaseCtl {

    /** Logger instance for debugging and error logging */
    private static Logger log = Logger.getLogger(MyProfileCtl.class);

    public static final String OP_CHANGE_MY_PASSWORD = "Change Password";

    /**
     * Validates the user profile form input.
     * 
     * @param request HttpServletRequest object containing client request parameters
     * @return boolean true if validation passes, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {
        log.debug("MyProfileCtl validate started");

        boolean pass = true;
        String op = DataUtility.getString(request.getParameter("operation"));

        if (OP_CHANGE_MY_PASSWORD.equalsIgnoreCase(op) || op == null) {
            return pass;
        }

        if (DataValidator.isNull(request.getParameter("firstName"))) {
            request.setAttribute("firstName", PropertyReader.getValue("error.require", "First Name"));
            pass = false;
        } else if (!DataValidator.isName(request.getParameter("firstName"))) {
            request.setAttribute("firstName", "Invalid First Name");
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("lastName"))) {
            request.setAttribute("lastName", PropertyReader.getValue("error.require", "Last Name"));
            pass = false;
        } else if (!DataValidator.isName(request.getParameter("lastName"))) {
            request.setAttribute("lastName", "Invalid Last Name");
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("gender"))) {
            request.setAttribute("gender", PropertyReader.getValue("error.require", "Gender"));
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", PropertyReader.getValue("error.require", "MobileNo"));
            pass = false;
        } else if (!DataValidator.isPhoneLength(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", "Mobile No must have 10 digits");
            pass = false;
        } else if (!DataValidator.isPhoneNo(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", "Invalid Mobile No");
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("dob"))) {
            request.setAttribute("dob", PropertyReader.getValue("error.require", "Date Of Birth"));
            pass = false;
        }

        log.debug("MyProfileCtl validate ended with result: " + pass);
        return pass;
    }

    /**
     * Populates the UserBean from the request parameters.
     * 
     * @param request HttpServletRequest object
     * @return populated UserBean instance
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("MyProfileCtl populateBean started");

        UserBean bean = new UserBean();
        bean.setId(DataUtility.getLong(request.getParameter("id")));
        bean.setLogin(DataUtility.getString(request.getParameter("login")));
        bean.setFirstName(DataUtility.getString(request.getParameter("firstName")));
        bean.setLastName(DataUtility.getString(request.getParameter("lastName")));
        bean.setMobileNo(DataUtility.getString(request.getParameter("mobileNo")));
        bean.setGender(DataUtility.getString(request.getParameter("gender")));
        bean.setDob(DataUtility.getDate(request.getParameter("dob")));

        populateDTO(bean, request);

        log.debug("MyProfileCtl populateBean ended with bean: " + bean);
        return bean;
    }

    /**
     * Handles GET requests to load the user profile.
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("MyProfileCtl doGet started");

        HttpSession session = request.getSession(true);
        UserBean user = (UserBean) session.getAttribute("user");
        long id = user.getId();

        UserModel model = new UserModel();

        if (id > 0) {
            try {
                UserBean bean = model.findByPk(id);
                ServletUtility.setBean(bean, request);
            } catch (ApplicationException e) {
                log.error("ApplicationException in MyProfileCtl doGet: ", e);
                ServletUtility.handleException(e, request, response);
                return;
            }
        }
        ServletUtility.forward(getView(), request, response);

        log.debug("MyProfileCtl doGet ended");
    }

    /**
     * Handles POST requests for profile update or change password navigation.
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("MyProfileCtl doPost started");

        HttpSession session = request.getSession(true);
        UserBean user = (UserBean) session.getAttribute("user");
        long id = user.getId();
        String op = DataUtility.getString(request.getParameter("operation"));
        UserModel model = new UserModel();

        if (OP_SAVE.equalsIgnoreCase(op)) {
            UserBean bean = (UserBean) populateBean(request);
            try {
                if (id > 0) {
                    user.setFirstName(bean.getFirstName());
                    user.setLastName(bean.getLastName());
                    user.setGender(bean.getGender());
                    user.setMobileNo(bean.getMobileNo());
                    user.setDob(bean.getDob());
                    model.update(user);
                }
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Profile has been updated Successfully. ", request);
            } catch (DuplicateRecordException e) {
                log.error("DuplicateRecordException in MyProfileCtl doPost: ", e);
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Login id already exists", request);
            } catch (ApplicationException e) {
                log.error("ApplicationException in MyProfileCtl doPost: ", e);
                ServletUtility.handleException(e, request, response);
                return;
            }
        } else if (OP_CHANGE_MY_PASSWORD.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.CHANGE_PASSWORD_CTL, request, response);
            return;
        }
        ServletUtility.forward(getView(), request, response);

        log.debug("MyProfileCtl doPost ended");
    }

    /**
     * Returns the view page for profile screen.
     * 
     * @return MY_PROFILE_VIEW constant
     */
    @Override
    protected String getView() {
        return ORSView.MY_PROFILE_VIEW;
    }
}
