package in.co.rays.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.CollegeBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.CollegeModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * CollegeCtl Controller is responsible for handling operations related to 
 * College such as Add, Update, Reset, and Cancel. 
 * 
 * It validates input data, populates beans, and interacts with the 
 * CollegeModel for database operations.
 * 
 * URL Mapping: /CollegeCtl
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "/CollegeCtl", urlPatterns = {"/CollegeCtl"})
public class CollegeCtl extends BaseCtl {

    /** Logger instance for debug and error messages */
    private static Logger log = Logger.getLogger(CollegeCtl.class);

    /**
     * Validates input parameters for College form.
     * Ensures that name, address, state, city, and phone number are valid.
     * 
     * @param request the HttpServletRequest object
     * @return true if all fields are valid, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {
        log.debug("CollegeCtl validate run");

        boolean pass = true;

        if (DataValidator.isNull(request.getParameter("name"))) {
            request.setAttribute("name", PropertyReader.getValue("error.require", "Name"));
            pass = false;
        } else if (!DataValidator.isName(request.getParameter("name"))) {
            request.setAttribute("name", "Invalid Name");
            pass = false;
        }
        if (DataValidator.isNull(request.getParameter("address"))) {
            request.setAttribute("address", PropertyReader.getValue("error.require", "Address"));
            pass = false;
        }
        if (DataValidator.isNull(request.getParameter("state"))) {
            request.setAttribute("state", PropertyReader.getValue("error.require", "State"));
            pass = false;
        }
        if (DataValidator.isNull(request.getParameter("city"))) {
            request.setAttribute("city", PropertyReader.getValue("error.require", "City"));
            pass = false;
        }
        if (DataValidator.isNull(request.getParameter("phoneNo"))) {
            request.setAttribute("phoneNo", PropertyReader.getValue("error.require", "Phone No"));
            pass = false;
        } else if (!DataValidator.isPhoneLength(request.getParameter("phoneNo"))) {
            request.setAttribute("phoneNo", "Phone No must have 10 digits");
            pass = false;
        } else if (!DataValidator.isPhoneNo(request.getParameter("phoneNo"))) {
            request.setAttribute("phoneNo", "Invalid Phone No");
            pass = false;
        }

        return pass;
    }

    /**
     * Populates CollegeBean with request parameters.
     * 
     * @param request the HttpServletRequest object
     * @return CollegeBean populated with data
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("CollegeCtl populateBean run");

        CollegeBean bean = new CollegeBean();

        bean.setId(DataUtility.getLong(request.getParameter("id")));
        bean.setName(DataUtility.getString(request.getParameter("name")));
        bean.setAddress(DataUtility.getString(request.getParameter("address")));
        bean.setState(DataUtility.getString(request.getParameter("state")));
        bean.setCity(DataUtility.getString(request.getParameter("city")));
        bean.setPhoneNo(DataUtility.getString(request.getParameter("phoneNo")));

        populateDTO(bean, request);

        return bean;
    }

    /**
     * Handles HTTP GET request. 
     * Loads a college by ID (if available) and forwards to the College view.
     * 
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("CollegeCtl doGet run");

        String op = DataUtility.getString(request.getParameter("operation"));

        CollegeModel model = new CollegeModel();

        long id = DataUtility.getLong(request.getParameter("id"));

        if (id > 0) {
            CollegeBean bean;
            try {
                bean = model.findByPk(id);
                ServletUtility.setBean(bean, request);
            } catch (Exception e) {
                log.error("Error fetching college by id: " + id, e);
                e.printStackTrace();
                return;
            }
        }

        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Handles HTTP POST request. 
     * Performs Save, Update, Cancel, and Reset operations for College entity.
     * 
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("CollegeCtl doPost run");

        String op = DataUtility.getString(request.getParameter("operation"));

        CollegeModel model = new CollegeModel();

        long id = DataUtility.getLong(request.getParameter("id"));

        if (OP_SAVE.equalsIgnoreCase(op)) {

            CollegeBean bean = (CollegeBean) populateBean(request);

            try {
                long pk = model.add(bean);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("College is successfully saved", request);
                log.debug("College saved successfully: " + bean.getName());
            } catch (ApplicationException e) {
                log.error("ApplicationException while adding college", e);
                e.printStackTrace();
                return;
            } catch (Exception e) {
                log.error("Duplicate College Name: " + bean.getName(), e);
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("College Name already exists", request);
            }
        } else if (OP_UPDATE.equalsIgnoreCase(op)) {

            CollegeBean bean = (CollegeBean) populateBean(request);

            try {
                if (id > 0) {
                    model.update(bean);
                }
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("College is successfully updated", request);
                log.debug("College updated successfully: " + bean.getName());
            } catch (Exception e) {
                log.error("Duplicate College Name: " + bean.getName(), e);
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("College Name already exists", request);
                return;
            }

        } else if (OP_CANCEL.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.COLLEGE_LIST_CTL, request, response);
            return;
        } else if (OP_RESET.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.COLLEGE_CTL, request, response);
            return;
        }
        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Returns the view page for College form.
     * 
     * @return String representing College view path
     */
    @Override
    protected String getView() {
        return ORSView.COLLEGE_VIEW;
    }
}
