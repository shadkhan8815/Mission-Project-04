package in.co.rays.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.SubjectBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.CourseModel;
import in.co.rays.model.SubjectModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * SubjectCtl handles CRUD operations for Subject entity.
 * 
 * Preloads Course list for selection and supports Add, Update, Reset, and Cancel operations.
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "/SubjectCtl", urlPatterns = { "/SubjectCtl" })
public class SubjectCtl extends BaseCtl {

    private static Logger log = Logger.getLogger(SubjectCtl.class);

    /**
     * Preloads course list for subject form.
     * 
     * @param request HttpServletRequest
     */
    @Override
    protected void preload(HttpServletRequest request) {
        log.debug("SubjectCtl preload run");

        CourseModel model = new CourseModel();
        try {
            List<?> courseList = model.list();
            request.setAttribute("courseList", courseList);
            log.debug("Course list preloaded with size: " + courseList.size());
        } catch (ApplicationException e) {
            log.error("Error in preloading course list", e);
            e.printStackTrace();
        }
    }

    /**
     * Validates Subject form input.
     * 
     * @param request HttpServletRequest
     * @return boolean true if valid, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {
        log.debug("SubjectCtl validate run");

        boolean isValid = true;

        if (DataValidator.isNull(request.getParameter("name"))) {
            request.setAttribute("name", PropertyReader.getValue("error.require", "Subject Name"));
            isValid = false;
            log.error("Subject Name is required");
        }
        if (DataValidator.isNull(request.getParameter("courseId"))) {
            request.setAttribute("courseId", PropertyReader.getValue("error.require", "Course Name"));
            isValid = false;
            log.error("Course Name is required");
        }
        if (DataValidator.isNull(request.getParameter("description"))) {
            request.setAttribute("description", PropertyReader.getValue("error.require", "Description"));
            isValid = false;
            log.error("Description is required");
        }

        return isValid;
    }

    /**
     * Populates SubjectBean from request parameters.
     * 
     * @param request HttpServletRequest
     * @return BaseBean populated SubjectBean
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("SubjectCtl populateBean run");

        SubjectBean bean = new SubjectBean();

        bean.setId(DataUtility.getLong(request.getParameter("id")));
        bean.setName(DataUtility.getString(request.getParameter("name")));
        bean.setCourseId(DataUtility.getLong(request.getParameter("courseId")));
        bean.setDescription(DataUtility.getString(request.getParameter("description")));

        populateDTO(bean, request);
        log.debug("Populated SubjectBean with ID: " + bean.getId());
        return bean;
    }

    /**
     * Handles GET request to load subject form.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("SubjectCtl doGet run");

        SubjectModel model = new SubjectModel();
        long id = DataUtility.getLong(request.getParameter("id"));

        if (id > 0) {
            try {
                SubjectBean bean = model.findByPk(id);
                ServletUtility.setBean(bean, request);
                log.debug("Loaded SubjectBean with ID: " + id);
            } catch (Exception e) {
                log.error("Error in finding Subject by ID: " + id, e);
                e.printStackTrace();
                return;
            }
        }

        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Handles POST request for add, update, reset, and cancel operations.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("SubjectCtl doPost run");

        String op = DataUtility.getString(request.getParameter("operation"));
        SubjectModel model = new SubjectModel();
        long id = DataUtility.getLong(request.getParameter("id"));

        if (OP_SAVE.equalsIgnoreCase(op)) {
            SubjectBean bean = (SubjectBean) populateBean(request);

            try {
                long pk = model.add(bean);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Subject added successfully", request);
                log.debug("Subject added successfully with ID: " + pk);
            } catch (ApplicationException e) {
                log.error("Error in adding Subject", e);
                e.printStackTrace();
                return;
            } catch (Exception e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Subject Name already exists", request);
                log.error("Subject Name already exists", e);
            }

        } else if (OP_RESET.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.SUBJECT_CTL, request, response);
            return;

        } else if (OP_UPDATE.equalsIgnoreCase(op)) {
            SubjectBean bean = (SubjectBean) populateBean(request);

            try {
                if (id > 0) {
                    model.update(bean);
                }
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Subject updated successfully", request);
                log.debug("Subject updated successfully with ID: " + bean.getId());
            } catch (ApplicationException e) {
                log.error("Error in updating Subject", e);
                e.printStackTrace();
                return;
            } catch (Exception e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Subject Name already exists", request);
                log.error("Subject Name already exists", e);
            }

        } else if (OP_CANCEL.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.SUBJECT_LIST_CTL, request, response);
            return;
        }

        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Returns Subject view page.
     * 
     * @return String view page
     */
    @Override
    protected String getView() {
        return ORSView.SUBJECT_VIEW;
    }
}
