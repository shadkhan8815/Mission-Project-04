package in.co.rays.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.CollegeBean;
import in.co.rays.bean.StudentBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.CollegeModel;
import in.co.rays.model.StudentModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * StudentCtl class handles Student form operations.
 * 
 * Operations include: Add, Update, Reset, Cancel
 * 
 * Preloads College list for selection in Student form.
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "/StudentCtl", urlPatterns = { "/StudentCtl" })
public class StudentCtl extends BaseCtl {

    private static Logger log = Logger.getLogger(StudentCtl.class);

    /**
     * Preloads college list for Student form.
     * 
     * @param request HttpServletRequest
     */
    @Override
    protected void preload(HttpServletRequest request) {
        log.debug("StudentCtl preload run");

        CollegeModel model = new CollegeModel();

        try {
            List<CollegeBean> collegeList = model.list();
            request.setAttribute("collegeList", collegeList);
            log.debug("College list preloaded with size: " + collegeList.size());
        } catch (Exception e) {
            log.error("Error in preloading college list", e);
            e.printStackTrace();
        }
    }

    /**
     * Validates Student form input.
     * 
     * @param request HttpServletRequest
     * @return boolean true if valid, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {
        log.debug("StudentCtl validate run");

        boolean isValid = true;

        if (DataValidator.isNull(request.getParameter("firstName"))) {
            request.setAttribute("firstName", PropertyReader.getValue("error.require", "FirstName"));
            isValid = false;
            log.error("First Name is required");
        } else if (!DataValidator.isName(request.getParameter("firstName"))) {
            request.setAttribute("firstName", "Invalid FirstName");
            isValid = false;
            log.error("First Name is invalid");
        }

        if (DataValidator.isNull(request.getParameter("lastName"))) {
            request.setAttribute("lastName", PropertyReader.getValue("error.require", "LastName"));
            isValid = false;
            log.error("Last Name is required");
        } else if (!DataValidator.isName(request.getParameter("lastName"))) {
            request.setAttribute("lastName", "Invalid LastName");
            isValid = false;
            log.error("Last Name is invalid");
        }

        if (DataValidator.isNull(request.getParameter("dob"))) {
            request.setAttribute("dob", PropertyReader.getValue("error.require", "dob"));
            isValid = false;
            log.error("DOB is required");
        } else if (!DataValidator.isDate(request.getParameter("dob"))) {
            request.setAttribute("dob", "Invalid dob");
            isValid = false;
            log.error("DOB is invalid");
        }

        if (DataValidator.isNull(request.getParameter("gender"))) {
            request.setAttribute("gender", PropertyReader.getValue("error.require", "Gender"));
            isValid = false;
            log.error("Gender is required");
        }

        if (DataValidator.isNull(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", PropertyReader.getValue("error.require", "MobileNo"));
            isValid = false;
            log.error("MobileNo is required");
        } else if (!DataValidator.isPhoneNo(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", "MobileNo must be start with 6-9 digit");
            isValid = false;
            log.error("MobileNo format is invalid");
        } else if (!DataValidator.isPhoneLength(request.getParameter("mobileNo"))) {
            request.setAttribute("mobileNo", "MobileNo must be 10 digit");
            isValid = false;
            log.error("MobileNo length is invalid");
        }

        if (DataValidator.isNull(request.getParameter("email"))) {
            request.setAttribute("email", PropertyReader.getValue("error.require", "Email"));
            isValid = false;
            log.error("Email is required");
        } else if (!DataValidator.isEmail(request.getParameter("email"))) {
            request.setAttribute("email", PropertyReader.getValue("error.email", "Email"));
            isValid = false;
            log.error("Email format is invalid");
        }

        if (DataValidator.isNull(request.getParameter("collegeId"))) {
            request.setAttribute("collegeId", PropertyReader.getValue("error.require", "collegeId"));
            isValid = false;
            log.error("College ID is required");
        } else if (!DataValidator.isLong(request.getParameter("collegeId"))) {
            request.setAttribute("collegeId", "Invalid College id");
            isValid = false;
            log.error("College ID format is invalid");
        }

        return isValid;
    }

    /**
     * Populates StudentBean from request parameters.
     * 
     * @param request HttpServletRequest
     * @return BaseBean populated StudentBean
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("StudentCtl populateBean run");

        StudentBean bean = new StudentBean();

        bean.setId(DataUtility.getLong(request.getParameter("id")));
        bean.setFirstName(DataUtility.getString(request.getParameter("firstName")));
        bean.setLastName(DataUtility.getString(request.getParameter("lastName")));
        bean.setDob(DataUtility.getDate(request.getParameter("dob")));
        bean.setGender(DataUtility.getString(request.getParameter("gender")));
        bean.setMobileNo(DataUtility.getString(request.getParameter("mobileNo")));
        bean.setEmail(DataUtility.getString(request.getParameter("email")));
        bean.setCollegeId(DataUtility.getLong(request.getParameter("collegeId")));

        populateDTO(bean, request);
        log.debug("Populated StudentBean with ID: " + bean.getId());
        return bean;
    }

    /**
     * Handles GET requests to load Student form.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("StudentCtl doGet run");

        StudentModel model = new StudentModel();
        long id = DataUtility.getLong(request.getParameter("id"));

        if (id > 0) {
            try {
                StudentBean bean = model.findByPk(id);
                ServletUtility.setBean(bean, request);
                log.debug("Loaded StudentBean with ID: " + id);
            } catch (ApplicationException e) {
                log.error("Error in finding Student by ID: " + id, e);
                e.printStackTrace();
                return;
            }
        }

        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Handles POST requests for add/update/reset/cancel operations.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("StudentCtl doPost run");

        String op = DataUtility.getString(request.getParameter("operation"));
        StudentModel model = new StudentModel();

        if (OP_SAVE.equalsIgnoreCase(op)) {
            StudentBean bean = (StudentBean) populateBean(request);
            try {
                long pk = model.add(bean);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Student Saved Successfully", request);
                log.debug("Student saved successfully with ID: " + pk);
            } catch (Exception e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Student Id already exists", request);
                log.error("Error saving Student", e);
                return;
            }

        } else if (OP_RESET.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.STUDENT_CTL, request, response);
            return;
        } else if (OP_UPDATE.equalsIgnoreCase(op)) {
            StudentBean bean = (StudentBean) populateBean(request);
            try {
                model.update(bean);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Student updated Successfully", request);
                log.debug("Student updated successfully with ID: " + bean.getId());
            } catch (Exception e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Student Id already exists", request);
                log.error("Error updating Student", e);
            }

        } else if (OP_CANCEL.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.STUDENT_LIST_CTL, request, response);
            return;
        }

        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Returns Student view page.
     * 
     * @return String view page
     */
    @Override
    protected String getView() {
        return ORSView.STUDENT_VIEW;
    }
}
