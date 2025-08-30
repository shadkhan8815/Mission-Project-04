package in.co.rays.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.FacultyBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.model.CollegeModel;
import in.co.rays.model.CourseModel;
import in.co.rays.model.FacultyModel;
import in.co.rays.model.SubjectModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;
import org.apache.log4j.Logger;

/**
 * FacultyCtl Controller is responsible for handling all requests related to Faculty operations.
 * It performs actions like preload data, validation, populating FacultyBean, and handling CRUD operations.
 * 
 * Author: Shad Khan
 * Version: 1.0
 */
@WebServlet(name = "/FacultyCtl", urlPatterns = {"/ctl/FacultyCtl"})
public class FacultyCtl extends BaseCtl {

    /** Logger for debug and error messages */
    private static Logger log = Logger.getLogger(FacultyCtl.class);

    /**
     * Preloads data required for Faculty form such as College, Subject, and Course lists.
     * 
     * @param request HttpServletRequest object to set attributes
     */
    @Override
    protected void preload(HttpServletRequest request) {
        log.debug("FacultyCtl preload run");

        CollegeModel collegeModel = new CollegeModel();
        SubjectModel subjectModel = new SubjectModel();
        CourseModel courseModel = new CourseModel();

        try {
            List collegeList = collegeModel.list();
            request.setAttribute("collegeList", collegeList);

            List subjectList = subjectModel.list();
            request.setAttribute("subjectList", subjectList);

            List courseList = courseModel.list();
            request.setAttribute("courseList", courseList);

        } catch (ApplicationException e) {
            log.error("Error in FacultyCtl preload", e);
            e.printStackTrace();
        }
    }

    /**
     * Validates input fields for Faculty form.
     * Ensures data such as names, email, mobile number, and IDs are correct and not null.
     * 
     * @param request HttpServletRequest object containing form data
     * @return boolean true if data is valid, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {
        log.debug("FacultyCtl validate run");

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

        if (DataValidator.isNull(request.getParameter("email"))) {
            request.setAttribute("email", PropertyReader.getValue("error.require", "Email "));
            isValid = false;
        } else if (!DataValidator.isEmail(request.getParameter("email"))) {
            request.setAttribute("email", PropertyReader.getValue("error.email", "Email "));
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

        if (DataValidator.isNull(request.getParameter("collegeId"))) {
            request.setAttribute("collegeId", PropertyReader.getValue("error.require", "College Name"));
            isValid = false;
        }
        if (DataValidator.isNull(request.getParameter("courseId"))) {
            request.setAttribute("courseId", PropertyReader.getValue("error.require", "Course Name"));
            isValid = false;
        }
        if (DataValidator.isNull(request.getParameter("subjectId"))) {
            request.setAttribute("subjectId", PropertyReader.getValue("error.require", "Subject Name"));
            isValid = false;
        }

        return isValid;
    }

    /**
     * Populates FacultyBean object with request parameters.
     * 
     * @param request HttpServletRequest object containing form data
     * @return BaseBean (FacultyBean) populated with request data
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("FacultyCtl populateBean run");

        FacultyBean bean = new FacultyBean();

        bean.setId(DataUtility.getLong(request.getParameter("id")));
        bean.setFirstName(DataUtility.getString(request.getParameter("firstName")));
        bean.setLastName(DataUtility.getString(request.getParameter("lastName")));
        bean.setGender(DataUtility.getString(request.getParameter("gender")));
        bean.setDob(DataUtility.getDate(request.getParameter("dob")));
        bean.setMobileNo(DataUtility.getString(request.getParameter("mobileNo")));
        bean.setEmail(DataUtility.getString(request.getParameter("email")));
        bean.setCollegeId(DataUtility.getLong(request.getParameter("collegeId")));
        bean.setCourseId(DataUtility.getLong(request.getParameter("courseId")));
        bean.setSubjectId(DataUtility.getLong(request.getParameter("subjectId")));

        populateDTO(bean, request);

        return bean;
    }

    /**
     * Handles GET requests for Faculty form.
     * Fetches Faculty data by ID if available and forwards to view.
     * 
     * @param request HttpServletRequest object
     * @param response HttpServletResponse object
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("FacultyCtl doGet run");

        long id = DataUtility.getLong(request.getParameter("id"));
        FacultyModel model = new FacultyModel();

        if (id > 0) {
            try {
                FacultyBean bean = model.findByPk(id);
                ServletUtility.setBean(bean, request);
            } catch (ApplicationException e) {
                log.error("Error in FacultyCtl doGet", e);
                e.printStackTrace();
                return;
            }
        }
        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Handles POST requests for Faculty form.
     * Performs operations like Save, Update, Reset, and Cancel.
     * 
     * @param request HttpServletRequest object
     * @param response HttpServletResponse object
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("FacultyCtl doPost run");

        String op = DataUtility.getString(request.getParameter("operation"));
        FacultyModel model = new FacultyModel();

        if (OP_SAVE.equalsIgnoreCase(op)) {
            FacultyBean bean = (FacultyBean) populateBean(request);
            try {
                long pk = model.add(bean);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Faculty added successfully", request);
            } catch (ApplicationException e) {
                log.error("Error adding faculty", e);
                e.printStackTrace();
                return;
            } catch (DuplicateRecordException e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Email already exists", request);
            }
        } else if (OP_UPDATE.equalsIgnoreCase(op)) {
            FacultyBean bean = (FacultyBean) populateBean(request);
            try {
                if (bean.getId() > 0) {
                    model.update(bean);
                }
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Faculty updated successfully", request);
            } catch (ApplicationException e) {
                log.error("Error updating faculty", e);
                e.printStackTrace();
                return;
            } catch (DuplicateRecordException e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Email already exists", request);
            }
        } else if (OP_CANCEL.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.FACULTY_LIST_CTL, request, response);
            return;
        } else if (OP_RESET.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.FACULTY_CTL, request, response);
            return;
        }
        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Returns the view page for Faculty form.
     * 
     * @return String Faculty View JSP
     */
    @Override
    protected String getView() {
        log.debug("FacultyCtl getView run");
        return ORSView.FACULTY_VIEW;
    }
}
