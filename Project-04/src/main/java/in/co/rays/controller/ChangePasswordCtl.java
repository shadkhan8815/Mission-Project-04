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
import in.co.rays.exception.RecordNotFoundException;
import in.co.rays.model.UserModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * ChangePasswordCtl Controller to handle Change Password functionality.
 * 
 * It provides validation for old password, new password, confirm password, 
 * and ensures password policies are met. It also updates the password 
 * in the system after validation.
 * 
 * URL Mapping: /ChangePasswordCtl
 * 
 * <p>
 * Provides GET and POST handling for password change operations.
 * </p>
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "ChangePasswordCtl", urlPatterns = { "/ctl/ChangePasswordCtl" })
public class ChangePasswordCtl extends BaseCtl {

    /** Logger instance for debug and error messages */
    private static Logger log = Logger.getLogger(ChangePasswordCtl.class);

    /** Constant for Change My Profile operation */
    public static final String OP_CHANGE_MY_PROFILE = "Change My Profile";

    /**
     * Validates the input data for Change Password functionality.
     * Ensures old password, new password, and confirm password are provided
     * and that they meet the required security policies.
     * 
     * @param request the HttpServletRequest object
     * @return true if input is valid, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {
        log.debug("ChangePasswordCtl validate run");

        boolean pass = true;
        String op = request.getParameter("operation");

        if (OP_CHANGE_MY_PROFILE.equalsIgnoreCase(op)) {
            return pass;
        }

        if (DataValidator.isNull(request.getParameter("oldPassword"))) {
            request.setAttribute("oldPassword", PropertyReader.getValue("error.require", "Old Password"));
            pass = false;
        } else if (request.getParameter("oldPassword").equals(request.getParameter("newPassword"))) {
            request.setAttribute("newPassword", "Old and New passwords should be different");
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("newPassword"))) {
            request.setAttribute("newPassword", PropertyReader.getValue("error.require", "New Password"));
            pass = false;
        } else if (!DataValidator.isPasswordLength(request.getParameter("newPassword"))) {
            request.setAttribute("newPassword", "Password should be 8 to 12 characters");
            pass = false;
        } else if (!DataValidator.isPassword(request.getParameter("newPassword"))) {
            request.setAttribute("newPassword", "Must contain uppercase, lowercase, digit & special character");
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("confirmPassword"))) {
            request.setAttribute("confirmPassword", PropertyReader.getValue("error.require", "Confirm Password"));
            pass = false;
        }

        if (!request.getParameter("newPassword").equals(request.getParameter("confirmPassword"))
                && !"".equals(request.getParameter("confirmPassword"))) {
            request.setAttribute("confirmPassword", "New and confirm passwords not matched");
            pass = false;
        }

        return pass;
    }

    /**
     * Populates UserBean with old and confirm password values.
     * 
     * @param request the HttpServletRequest object
     * @return UserBean populated with request data
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("ChangePasswordCtl populateBean run");

        UserBean bean = new UserBean();
        bean.setPassword(DataUtility.getString(request.getParameter("oldPassword")));
        bean.setConfirmPassword(DataUtility.getString(request.getParameter("confirmPassword")));
        populateDTO(bean, request);

        return bean;
    }

    /**
     * Handles HTTP GET request and forwards to Change Password view.
     * 
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("ChangePasswordCtl doGet run");
        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Handles HTTP POST request for Change Password functionality.
     * 
     * - Validates and updates user password
     * - Handles exceptions and provides appropriate error/success messages
     * - Supports "Change My Profile" operation redirection
     * 
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("ChangePasswordCtl doPost run");

        String op = DataUtility.getString(request.getParameter("operation"));
        String newPassword = (String) request.getParameter("newPassword");

        UserBean bean = (UserBean) populateBean(request);
        UserModel model = new UserModel();

        HttpSession session = request.getSession(true);
        UserBean user = (UserBean) session.getAttribute("user");
        long id = user.getId();

        if (OP_SAVE.equalsIgnoreCase(op)) {
            try {
                boolean flag = model.changePassword(id, bean.getPassword(), newPassword);
                if (flag == true) {
                    bean = model.findByLogin(user.getLogin());
                    session.setAttribute("user", bean);
                    ServletUtility.setBean(bean, request);
                    ServletUtility.setSuccessMessage("Password has been changed Successfully", request);
                    log.debug("Password changed successfully for user: " + user.getLogin());
                }
            } catch (RecordNotFoundException e) {
                ServletUtility.setErrorMessage("Old Password is Invalid", request);
                log.error("Old password is invalid for user: " + user.getLogin(), e);
            } catch (ApplicationException e) {
                log.error("ApplicationException in ChangePasswordCtl", e);
                e.printStackTrace();
                ServletUtility.handleException(e, request, response);
                return;
            }
        } else if (OP_CHANGE_MY_PROFILE.equalsIgnoreCase(op)) {
            log.debug("Redirecting to My Profile page");
            ServletUtility.redirect(ORSView.MY_PROFILE_CTL, request, response);
            return;
        }
        ServletUtility.forward(ORSView.CHANGE_PASSWORD_VIEW, request, response);
    }

    /**
     * Returns the view page for Change Password functionality.
     * 
     * @return String representing the view path
     */
    @Override
    protected String getView() {
        return ORSView.CHANGE_PASSWORD_VIEW;
    }
}
