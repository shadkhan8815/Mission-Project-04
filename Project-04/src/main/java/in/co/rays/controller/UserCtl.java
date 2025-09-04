package in.co.rays.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.RoleBean;
import in.co.rays.bean.UserBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.model.RoleModel;
import in.co.rays.model.UserModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * UserCtl handles User add, update, and preloading role list.
 * 
 * Supports validation, populateBean, and CRUD operations.
 * 
 * Author: Shad Khan
 * Version: 1.0
 */
@WebServlet(name = "UserCtl", urlPatterns = { "/ctl/UserCtl" })
public class UserCtl extends BaseCtl {

    private static Logger log = Logger.getLogger(UserCtl.class);

    /**
     * Preloads role list for dropdown in User form.
     * 
     * @param request HttpServletRequest
     */
    @Override
    protected void preload(HttpServletRequest request) {
        log.debug("UserCtl preload run");

        RoleModel model = new RoleModel();
        try {
            List<RoleBean> roleList = model.list();
            request.setAttribute("roleList", roleList);
            log.debug("Preloaded role list size: " + roleList.size());
        } catch (Exception e) {
            log.error("Error preloading role list", e);
            e.printStackTrace();
        }
    }

    /**
     * Validates user input fields.
     * 
     * @param request HttpServletRequest
     * @return boolean true if valid, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {
        log.debug("UserCtl validate run");

        boolean isValid = true;

        String login = request.getParameter("login");
        String dob = request.getParameter("dob");
        String password = request.getParameter("password");

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

        if (DataValidator.isNull(login)) {
            request.setAttribute("login", PropertyReader.getValue("error.require", "Login Id"));
            isValid = false;
        } else if (!DataValidator.isEmail(login)) {
            request.setAttribute("login", PropertyReader.getValue("error.email", "Login "));
            isValid = false;
        }

        if (DataValidator.isNull(password)) {
            request.setAttribute("password", PropertyReader.getValue("error.require", "Password"));
            isValid = false;
        } else if (!DataValidator.isPasswordLength(password)) {
            request.setAttribute("password", "Password should be 8 to 12 characters");
            isValid = false;
        } else if (!DataValidator.isPassword(password)) {
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
        if (DataValidator.isNull(dob)) {
            request.setAttribute("dob", PropertyReader.getValue("error.require", "Date of Birth"));
            isValid = false;
        } else if (!DataValidator.isDate(dob)) {
            request.setAttribute("dob", PropertyReader.getValue("error.date", "Date of Birth"));
            isValid = false;
        }
        if (DataValidator.isNull(request.getParameter("roleId"))) {
            request.setAttribute("roleId", PropertyReader.getValue("error.require", "Role"));
            isValid = false;
        }
        if (DataValidator.isNull(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", PropertyReader.getValue("error.require", "MobileNo"));
            isValid = false;
        } else if (!DataValidator.isPhoneLength(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", "Mobile No must have 10 digits");
            isValid = false;
        } else if (!DataValidator.isPhoneNo(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", "Invalid Mobile No");
            isValid = false;
        }
        if (!request.getParameter("password").equals(request.getParameter("confirmPassword"))
                && !"".equals(request.getParameter("confirmPassword"))) {
            request.setAttribute("confirmPassword", "Password and Confirm Password must be Same!");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Populates UserBean from request parameters.
     * 
     * @param request HttpServletRequest
     * @return BaseBean populated UserBean
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("UserCtl populateBean run");

        UserBean bean = new UserBean();

        bean.setId(DataUtility.getLong(request.getParameter("id")));
        bean.setRoleId(DataUtility.getLong(request.getParameter("roleId")));
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
     * Handles GET request to display User form for add/edit.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("UserCtl doGet run");

        String op = DataUtility.getString(request.getParameter("operation"));
        UserModel model = new UserModel();
        long id = DataUtility.getLong(request.getParameter("id"));

        if (id > 0 || op != null) {
            try {
                UserBean bean = model.findByPk(id);
                ServletUtility.setBean(bean, request);
            } catch (ApplicationException e) {
                log.error("Error in doGet of UserCtl", e);
                e.printStackTrace();
                return;
            }
        }

        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Handles POST request for save, update, reset, and cancel operations.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("UserCtl doPost run");

        String op = DataUtility.getString(request.getParameter("operation"));
        UserModel model = new UserModel();

        if (OP_SAVE.equalsIgnoreCase(op)) {
            UserBean bean = (UserBean) populateBean(request);

            try {
                long pk = model.add(bean);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Data Saved Successfully", request);
                log.debug("User saved successfully with ID: " + pk);
            } catch (ApplicationException e) {
                log.error("ApplicationException in saving user", e);
                e.printStackTrace();
                return;
            } catch (DuplicateRecordException e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Login Id already exists", request);
                log.error("DuplicateRecordException: Login Id already exists");
            }

        }else if (OP_RESET.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.USER_CTL, request, response);
            return;
        }

        else if (OP_UPDATE.equalsIgnoreCase(op)) {
            UserBean bean = (UserBean) populateBean(request);

            try {
                model.update(bean);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Data updated Successfully", request);
                log.debug("User updated successfully with ID: " + bean.getId());
            } catch (ApplicationException e) {
                log.error("ApplicationException in updating user", e);
                e.printStackTrace();
                return;
            } catch (DuplicateRecordException e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Login Id already exists", request);
                log.error("DuplicateRecordException: Login Id already exists");
            }

        } else if (OP_CANCEL.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.USER_LIST_CTL, request, response);
            return;

        } 
        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Returns the User view page.
     * 
     * @return String view page
     */
    @Override
    protected String getView() {
        return ORSView.USER_VIEW;
    }
}
