package in.co.rays.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.CourseBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.model.CourseModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * CourseCtl Controller servlet to handle add, update, and validation of courses.
 * It provides validation for course form and interacts with CourseModel 
 * for database operations.
 * 
 * URL Mapping: /CourseCtl
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "/CourseCtl", urlPatterns = { "/CourseCtl" })
public class CourseCtl extends BaseCtl {

    /** Logger instance for debug and error messages */
    private static Logger log = Logger.getLogger(CourseCtl.class);

    /**
     * Validates input data entered by the user in Course form.
     * Ensures name, duration, and description are valid.
     * 
     * @param request HttpServletRequest object containing form parameters
     * @return boolean true if input is valid, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {
        log.debug("CourseCtl validate run");

        boolean isValid = true;

        if (DataValidator.isNull(request.getParameter("name"))) {
            request.setAttribute("name", PropertyReader.getValue("error.require", "Course Name"));
            isValid = false;
        } else if (!DataValidator.isName(request.getParameter("name"))) {
            request.setAttribute("name", " Course Name contains alphabet only");
            isValid = false;
        }
        if (DataValidator.isNull(request.getParameter("duration"))) {
            request.setAttribute("duration", PropertyReader.getValue("error.require", "Duration"));
            isValid = false;
        }
        if (DataValidator.isNull(request.getParameter("description"))) {
            request.setAttribute("description", PropertyReader.getValue("error.require", "Description"));
            isValid = false;
        }
        return isValid;
    }

    /**
     * Populates CourseBean object from request parameters.
     * 
     * @param request HttpServletRequest object
     * @return BaseBean (CourseBean) populated with form data
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("CourseCtl populateBean run");

        CourseBean bean = new CourseBean();

        bean.setId(DataUtility.getLong(request.getParameter("id")));
        bean.setName(DataUtility.getString(request.getParameter("name")));
        bean.setDuration(DataUtility.getString(request.getParameter("duration")));
        bean.setDescription(DataUtility.getString(request.getParameter("description")));

        populateDTO(bean, request);
        return bean;
    }

    /**
     * Handles HTTP GET requests. 
     * Loads course data for editing if course ID is provided.
     * 
     * @param request HttpServletRequest object
     * @param response HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an input/output error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("CourseCtl doGet run");

        CourseModel model = new CourseModel();
        long id = DataUtility.getLong(request.getParameter("id"));

        if (id > 0) {
            try {
                CourseBean bean = model.findByPk(id);
                ServletUtility.setBean(bean, request);
            } catch (ApplicationException e) {
                log.error("Error in CourseCtl doGet", e);
                e.printStackTrace();
                return;
            }
        }
        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Handles HTTP POST requests. 
     * Performs SAVE, UPDATE, CANCEL, RESET operations for course data.
     * 
     * @param request HttpServletRequest object
     * @param response HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an input/output error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("CourseCtl doPost run");

        String op = DataUtility.getString(request.getParameter("operation"));
        CourseModel model = new CourseModel();

        if (OP_SAVE.equalsIgnoreCase(op)) {
            CourseBean bean = (CourseBean) populateBean(request);

            try {
                long pk = model.add(bean);
                bean.setId(pk);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Course Saved Successfully", request);
                log.debug("Course saved successfully, ID: " + pk);
            } catch (ApplicationException e) {
                log.error("Error in CourseCtl doPost SAVE", e);
                e.printStackTrace();
                return;
            } catch (DuplicateRecordException e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Course already exists", request);
                log.debug("Duplicate course record");
            }
        } else if (OP_UPDATE.equalsIgnoreCase(op)) {
            CourseBean bean = (CourseBean) populateBean(request);

            try {
                model.update(bean);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Course updated Successfully", request);
                log.debug("Course updated successfully, ID: " + bean.getId());
            } catch (ApplicationException e) {
                log.error("Error in CourseCtl doPost UPDATE", e);
                e.printStackTrace();
                return;
            } catch (DuplicateRecordException e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Course already exists", request);
                log.debug("Duplicate course record");
            }
        } else if (OP_CANCEL.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.COURSE_LIST_CTL, request, response);
            return;
        } else if (OP_RESET.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.COURSE_CTL, request, response);
            return;
        }
        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Returns the view page for Course form.
     * 
     * @return String constant of Course View
     */
    @Override
    protected String getView() {
        return ORSView.COURSE_VIEW;
    }
}
