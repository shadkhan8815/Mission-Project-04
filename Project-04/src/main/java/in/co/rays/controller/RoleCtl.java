package in.co.rays.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.RoleBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.RoleModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * RoleCtl class performs role related operations.
 * It extends BaseCtl to handle common functionalities.
 * 
 * Operations: Add, Update, Reset, Back
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "/RoleCtl", urlPatterns = { "/ctl/RoleCtl" })
public class RoleCtl extends BaseCtl {

    private static Logger log = Logger.getLogger(RoleCtl.class);

    /**
     * Validates the Role form data.
     * 
     * @param request HttpServletRequest
     * @return boolean true if valid, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {
        log.debug("RoleCtl Validate run");

        boolean isValid = true;

        if (DataValidator.isNull(request.getParameter("name"))) {
            request.setAttribute("name", PropertyReader.getValue("error.require", "name"));
            isValid = false;
            log.error("Role name is required");
        } else if (!DataValidator.isName(request.getParameter("name"))) {
            request.setAttribute("name", "Invalid Name");
            isValid = false;
            log.error("Role name is invalid");
        }

        if (DataValidator.isNull(request.getParameter("description"))) {
            request.setAttribute("description", PropertyReader.getValue("error.require", "Description"));
            isValid = false;
            log.error("Role description is required");
        }

        return isValid;
    }

    /**
     * Populates RoleBean from HttpServletRequest parameters.
     * 
     * @param request HttpServletRequest
     * @return BaseBean populated bean
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("RoleCtl populateBean run");

        RoleBean bean = new RoleBean();

        bean.setId(DataUtility.getLong(request.getParameter("id")));
        bean.setName(DataUtility.getString(request.getParameter("name")));
        bean.setDescription(DataUtility.getString(request.getParameter("description")));

        populateDTO(bean, request);

        return bean;
    }

    /**
     * Handles GET request for RoleCtl.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("RoleCtl doGet run");

        String op = DataUtility.getString(request.getParameter("operation"));
        long id = DataUtility.getLong(request.getParameter("id"));

        RoleModel model = new RoleModel();

        if (id > 0 || op != null) {
            RoleBean bean;
            try {
                bean = model.findByPk(id);
                ServletUtility.setBean(bean, request);
            } catch (ApplicationException e) {
                log.error("Error in finding Role by ID", e);
                return;
            }
        }

        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Handles POST request for RoleCtl.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("RoleCtl doPost run");

        String op = DataUtility.getString(request.getParameter("operation"));
        long id = DataUtility.getLong(request.getParameter("id"));

        RoleModel model = new RoleModel();

        if (OP_SAVE.equalsIgnoreCase(op)) {

            RoleBean bean = (RoleBean) populateBean(request);

            try {
                model.add(bean);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Data Added Successfully", request);
                log.debug("Role added successfully: " + bean.getName());
            } catch (Exception e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Role Id Already Exist", request);
                log.error("Error adding Role", e);
                return;
            }

        } else if (OP_RESET.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.ROLE_CTL, request, response);
            return;
        }

        else if (OP_UPDATE.equalsIgnoreCase(op)) {

            RoleBean bean = (RoleBean) populateBean(request);

            try {
                if (id > 0 || op != null) {
                    model.update(bean);
                    ServletUtility.setBean(bean, request);
                    ServletUtility.setSuccessMessage("Data Updated Successfully", request);
                    log.debug("Role updated successfully: " + bean.getName());
                }

            } catch (Exception e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Role Id Already Exist", request);
                log.error("Error updating Role", e);
                return;
            }
        } else if (OP_BACK.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.ROLE_LIST_CTL, request, response);
            return;
        }
        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Returns Role view page.
     * 
     * @return String view page
     */
    @Override
    protected String getView() {
        return ORSView.ROLE_VIEW;
    }

}
