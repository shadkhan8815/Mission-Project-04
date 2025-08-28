package in.co.rays.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * ForgetPasswordCtl Servlet class is responsible for handling the "Forget Password"
 * functionality. It validates user input, processes requests to reset password, 
 * and interacts with the UserModel to send the password to the user's email.
 * 
 * Functionality:
 * <ul>
 *   <li>Validates email input format.</li>
 *   <li>Populates UserBean with login details.</li>
 *   <li>Handles GET and POST requests for password recovery.</li>
 *   <li>Calls UserModel to perform forget password logic.</li>
 * </ul>
 * 
 * Author: Shad Khan
 * Version: 1.0
 * Since: 2025
 */
@WebServlet(name = "ForgetPasswordCtl", urlPatterns = { "/ForgetPasswordCtl" })
public class ForgetPasswordCtl extends BaseCtl {

    /** Logger for debug and error messages */
    private static Logger log = Logger.getLogger(ForgetPasswordCtl.class);

    /**
     * Validates the email ID entered by the user.
     * 
     * @param request HttpServletRequest object containing client request
     * @return boolean true if validation passes, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {
        log.debug("ForgetPasswordCtl validate run");

        boolean pass = true;

        if (DataValidator.isNull(request.getParameter("login"))) {
            request.setAttribute("login", PropertyReader.getValue("error.require", "Email Id"));
            pass = false;
            log.debug("Email ID is null");
        } else if (!DataValidator.isEmail(request.getParameter("login"))) {
            request.setAttribute("login", PropertyReader.getValue("error.email", "Login "));
            pass = false;
            log.debug("Email ID format is invalid");
        }

        return pass;
    }

    /**
     * Populates UserBean with login email from request parameter.
     * 
     * @param request HttpServletRequest object containing client request
     * @return BaseBean populated with login data
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("ForgetPasswordCtl populateBean run");

        UserBean bean = new UserBean();
        bean.setLogin(DataUtility.getString(request.getParameter("login")));

        return bean;
    }

    /**
     * Handles GET request and forwards to Forget Password view.
     * 
     * @param request  HttpServletRequest object containing client request
     * @param response HttpServletResponse object for sending response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("ForgetPasswordCtl doGet run");
        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Handles POST request. If operation is "GO", attempts to send the password 
     * to user's email by calling UserModel.
     * 
     * @param request  HttpServletRequest object containing client request
     * @param response HttpServletResponse object for sending response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("ForgetPasswordCtl doPost run");

        String op = DataUtility.getString(request.getParameter("operation"));
        UserBean bean = (UserBean) populateBean(request);
        UserModel model = new UserModel();

        if (OP_GO.equalsIgnoreCase(op)) {
            try {
                boolean flag = model.forgetPassword(bean.getLogin());
                if (flag) {
                    ServletUtility.setSuccessMessage("Password has been sent to your email id", request);
                    log.debug("Password sent successfully to email: " + bean.getLogin());
                }
            } catch (RecordNotFoundException e) {
                ServletUtility.setErrorMessage(e.getMessage(), request);
                log.error("Record not found for email: " + bean.getLogin(), e);
            } catch (ApplicationException e) {
                e.printStackTrace();
                ServletUtility.setErrorMessage("Please check your internet connection..!!", request);
                log.error("ApplicationException in ForgetPasswordCtl doPost", e);
            }
            ServletUtility.forward(getView(), request, response);
        }
    }

    /**
     * Returns the view page for Forget Password functionality.
     * 
     * @return String representing the view path
     */
    @Override
    protected String getView() {
        log.debug("ForgetPasswordCtl getView run");
        return ORSView.FORGET_PASSWORD_VIEW;
    }
}
