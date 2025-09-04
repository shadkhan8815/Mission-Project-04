package in.co.rays.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.RoleBean;
import in.co.rays.bean.UserBean;
import in.co.rays.model.UserModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * UserRegistrationCtl handles student user registration functionality.
 * Performs form validation, bean population, and registration logic.
 * 
 * Author: Shad Khan
 * Version: 1.0
 */
@WebServlet(name = "UserRegistrationCtl", urlPatterns = { "/UserRegistrationCtl" })
public class UserRegistrationCtl extends BaseCtl {

    private static Logger log = Logger.getLogger(UserRegistrationCtl.class);

    public static final String OP_SIGN_UP = "Sign Up";

    /**
     * Validates the registration form inputs.
     * 
     * @param request HttpServletRequest
     * @return boolean true if all fields are valid, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {
        log.debug("UserRegistrationCtl validate run");

        boolean isValid = true;

        if (DataValidator.isNull(request.getParameter("firstName"))) {
            request.setAttribute("firstName", PropertyReader.getValue("error.require", "First Name"));
            isValid = false;
        } else if (!DataValidator.isName(request.getParameter("firstName"))) {
            request.setAttribute("firstName", "Invalid First Name");
            isValid = false;
        }

        if (DataValidator.isNull(request.getParameter("lastName"))) {
            request.setAttribute("lastName", PropertyReader.getValue("error.require", "Last Name"));
            isValid = false;
        } else if (!DataValidator.isName(request.getParameter("lastName"))) {
            request.setAttribute("lastName", "Invalid Last Name");
            isValid = false;
        }

        if (DataValidator.isNull(request.getParameter("login"))) {
            request.setAttribute("login", PropertyReader.getValue("error.require", "Login Id"));
            isValid = false;
        } else if (!DataValidator.isEmail(request.getParameter("login"))) {
            request.setAttribute("login", PropertyReader.getValue("error.email", "Login"));
            isValid = false;
        }

        if (DataValidator.isNull(request.getParameter("password"))) {
            request.setAttribute("password", PropertyReader.getValue("error.require", "Password"));
            isValid = false;
        } else if (!DataValidator.isPasswordLength(request.getParameter("password"))) {
            request.setAttribute("password", "Password should be 8 to 12 characters");
            isValid = false;
        } else if (!DataValidator.isPassword(request.getParameter("password"))) {
            request.setAttribute("password", "Must contain uppercase, lowercase, digit & special character");
            isValid = false;
        }

        if (DataValidator.isNull(request.getParameter("confirmPassword"))) {
            request.setAttribute("confirmPassword", PropertyReader.getValue("error.require", "Confirm Password"));
            isValid = false;
        }

        if (DataValidator.isNull(request.getParameter("gender"))) {
            request.setAttribute("gender", PropertyReader.getValue("error.require", "Gender"));
            isValid = false;
        }

        if (DataValidator.isNull(request.getParameter("dob"))) {
            request.setAttribute("dob", PropertyReader.getValue("error.require", "Date of Birth"));
            isValid = false;
        } else if (!DataValidator.isDate(request.getParameter("dob"))) {
            request.setAttribute("dob", PropertyReader.getValue("error.date", "Date of Birth"));
            isValid = false;
        }

        if (!request.getParameter("password").equals(request.getParameter("confirmPassword"))
                && !"".equals(request.getParameter("confirmPassword"))) {
            request.setAttribute("confirmPassword", "Password and Confirm Password must be Same!");
            isValid = false;
        }

        if (DataValidator.isNull(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", PropertyReader.getValue("error.require", "Mobile No"));
            isValid = false;
        } else if (!DataValidator.isPhoneLength(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", "Mobile No must have 10 digits");
            isValid = false;
        } else if (!DataValidator.isPhoneNo(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", "Invalid Mobile No");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Populates a UserBean from the registration form inputs.
     * 
     * @param request HttpServletRequest
     * @return BaseBean populated UserBean
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("UserRegistrationCtl populateBean run");

        UserBean bean = new UserBean();

        bean.setId(DataUtility.getLong(request.getParameter("id")));
        bean.setRoleId(RoleBean.STUDENT);
        bean.setFirstName(DataUtility.getString(request.getParameter("firstName")));
        bean.setLastName(DataUtility.getString(request.getParameter("lastName")));
        bean.setLogin(DataUtility.getString(request.getParameter("login")));
        bean.setPassword(DataUtility.getString(request.getParameter("password")));
        bean.setConfirmPassword(DataUtility.getString(request.getParameter("confirmPassword")));
        bean.setGender(DataUtility.getString(request.getParameter("gender")));
        bean.setDob(DataUtility.getDate(request.getParameter("dob")));
        bean.setMobileNo(DataUtility.getString(request.getParameter("mobileNo")));

        populateDTO(bean, request);

        return bean;
    }

    /**
     * Displays the registration page.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("UserRegistrationCtl doGet run");
        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Handles registration form submission.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("UserRegistrationCtl doPost run");

        String op = DataUtility.getString(request.getParameter("operation"));

        UserModel model = new UserModel();

        if (OP_SIGN_UP.equalsIgnoreCase(op)) {
            UserBean bean = (UserBean) populateBean(request);
            try {
                model.registerUser(bean);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Registration successful!", request);
                log.debug("User registered successfully: " + bean.getLogin());
                ServletUtility.forward(getView(), request, response);
                return;
            } catch (Exception e) {
                log.error("Error during registration for login: " + bean.getLogin(), e);
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Login id already exists", request);
                ServletUtility.forward(getView(), request, response);
                return;
            }
        } else if (OP_RESET.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.USER_REGISTRATION_CTL, request, response);
        }
    }

    /**
     * Returns the view page for user registration.
     * 
     * @return String view page
     */
    @Override
    protected String getView() {
        return ORSView.USER_REGISTRATION_VIEW;
    }
}
