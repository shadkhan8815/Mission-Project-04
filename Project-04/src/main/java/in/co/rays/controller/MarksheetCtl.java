package in.co.rays.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger; 

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.MarksheetBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.model.MarksheetModel;
import in.co.rays.model.StudentModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

/**
 * MarksheetCtl is a Controller that handles operations related to adding,
 * updating, validating, and retrieving Marksheet records. It interacts with
 * MarksheetModel and StudentModel to perform database operations and forwards
 * the data to the corresponding JSP views.
 * <p>
 * Supported operations:
 * <ul>
 *   <li>Save Marksheet</li>
 *   <li>Update Marksheet</li>
 *   <li>Cancel (redirect to list)</li>
 *   <li>Reset (reload form)</li>
 * </ul>
 * </p>
 *
 * <h3>Logging</h3>
 * <ul>
 *   <li>DEBUG: lifecycle start/end, request parameters, model calls, forwards/redirects</li>
 *   <li>ERROR: validation failures and exceptions (Application/Duplicate)</li>
 * </ul>
 * 
 * All existing code (prints, logic, returns) remains unchanged.
 * 
 * author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "MarksheetCtl", urlPatterns = { "/ctl/MarksheetCtl" })
public class MarksheetCtl extends BaseCtl {

    /** Logger for this controller (added) */
    private static final Logger log = Logger.getLogger(MarksheetCtl.class);

    /**
     * Preloads the list of students to populate dropdowns in the view.
     * <p><b>Logging:</b> DEBUG on start/end and list size; ERROR on exceptions.</p>
     *
     * @param request the HttpServletRequest object
     */
    @Override
    protected void preload(HttpServletRequest request) {
        System.out.println("MarksheetCtl preload run");
        log.debug("preload() started");

        StudentModel model = new StudentModel();
        try {
            List studentList = model.list();
            request.setAttribute("studentList", studentList);
            log.debug("preload() loaded studentList size=" + (studentList != null ? studentList.size() : 0));
        } catch (ApplicationException e) {
            log.error("preload() ApplicationException", e);
            e.printStackTrace();
            return;
        }

        log.debug("preload() ended");
    }

    /**
     * Validates the input parameters for Marksheet form. Ensures required fields
     * are provided and marks are within valid range (0–100).
     * <p><b>Logging:</b> DEBUG on start/end; ERROR entries set for each failing field.</p>
     *
     * @param request the HttpServletRequest containing form inputs
     * @return true if validation passes, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {
        System.out.println("MarksheetCtl validate run");
        log.debug("validate() started");

        boolean pass = true;

        if (DataValidator.isNull(request.getParameter("studentId"))) {
            request.setAttribute("studentId", PropertyReader.getValue("error.require", "Student Name"));
            log.error("Validation: studentId required");
            pass = false;

            if (DataValidator.isNull(request.getParameter("rollNo"))) {
                request.setAttribute("rollNo", PropertyReader.getValue("error.require", "Roll Number"));
                log.error("Validation: rollNo required (inside studentId null branch)");
                pass = false;
            }
        } else if (!DataValidator.isRollNo(request.getParameter("rollNo"))) {
            request.setAttribute("rollNo", "Roll No is invalid");
            log.error("Validation: rollNo invalid format");
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("physics"))) {
            request.setAttribute("physics", PropertyReader.getValue("error.require", "Marks"));
            log.error("Validation: physics required");
            pass = false;
        } else if (DataValidator.isNotNull(request.getParameter("physics"))
                && !DataValidator.isInteger(request.getParameter("physics"))) {
            request.setAttribute("physics", PropertyReader.getValue("error.integer", "Marks"));
            log.error("Validation: physics not integer");
            pass = false;
        } else if (DataUtility.getInt(request.getParameter("physics")) > 100
                || DataUtility.getInt(request.getParameter("physics")) < 0) {
            request.setAttribute("physics", "Marks should be in 0 to 100");
            log.error("Validation: physics out of range");
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("chemistry"))) {
            request.setAttribute("chemistry", PropertyReader.getValue("error.require", "Marks"));
            log.error("Validation: chemistry required");
            pass = false;
        } else if (DataValidator.isNotNull(request.getParameter("chemistry"))
                && !DataValidator.isInteger(request.getParameter("chemistry"))) {
            request.setAttribute("chemistry", PropertyReader.getValue("error.integer", "Marks"));
            log.error("Validation: chemistry not integer");
            pass = false;
        } else if (DataUtility.getInt(request.getParameter("chemistry")) > 100
                || DataUtility.getInt(request.getParameter("chemistry")) < 0) {
            request.setAttribute("chemistry", "Marks should be in 0 to 100");
            log.error("Validation: chemistry out of range");
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("maths"))) {
            request.setAttribute("maths", PropertyReader.getValue("error.require", "Marks"));
            log.error("Validation: maths required");
            pass = false;
        } else if (DataValidator.isNotNull(request.getParameter("maths"))
                && !DataValidator.isInteger(request.getParameter("maths"))) {
            request.setAttribute("maths", PropertyReader.getValue("error.integer", "Marks"));
            log.error("Validation: maths not integer");
            pass = false;
        } else if (DataUtility.getInt(request.getParameter("maths")) > 100
                || DataUtility.getInt(request.getParameter("maths")) < 0) {
            request.setAttribute("maths", "Marks should be in 0 to 100");
            log.error("Validation: maths out of range");
            pass = false;
        }

        if (DataValidator.isNull(request.getParameter("studentId"))) {
            request.setAttribute("studentId", PropertyReader.getValue("error.require", "Student Name"));
            log.error("Validation (end check): studentId required");
            pass = false;
        }

        log.debug("validate() ended with pass=" + pass);
        return pass;
    }

    /**
     * Populates a MarksheetBean object from the request parameters.
     * <p><b>Logging:</b> DEBUG on start, populated values, and end.</p>
     *
     * @param request the HttpServletRequest containing form inputs
     * @return a populated MarksheetBean
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        System.out.println("MarksheetCtl populateBean run");
        log.debug("populateBean() started");

        MarksheetBean bean = new MarksheetBean();

        bean.setId(DataUtility.getLong(request.getParameter("id")));
        bean.setRollNo(DataUtility.getString(request.getParameter("rollNo")));
        bean.setName(DataUtility.getString(request.getParameter("name")));

        if (request.getParameter("physics") != null && request.getParameter("physics").length() != 0) {
            bean.setPhysics(DataUtility.getInt(request.getParameter("physics")));
        }
        if (request.getParameter("chemistry") != null && request.getParameter("chemistry").length() != 0) {
            bean.setChemistry(DataUtility.getInt(request.getParameter("chemistry")));
        }
        if (request.getParameter("maths") != null && request.getParameter("maths").length() != 0) {
            bean.setMaths(DataUtility.getInt(request.getParameter("maths")));
        }

        bean.setStudentId(DataUtility.getLong(request.getParameter("studentId")));

        populateDTO(bean, request);

        log.debug("populateBean() populated: id=" + bean.getId()
                + ", rollNo=" + bean.getRollNo()
                + ", name=" + bean.getName()
                + ", physics=" + bean.getPhysics()
                + ", chemistry=" + bean.getChemistry()
                + ", maths=" + bean.getMaths()
                + ", studentId=" + bean.getStudentId());
        log.debug("populateBean() ended");
        return bean;
    }

    /**
     * Handles HTTP GET requests. Loads an existing Marksheet by ID (if provided)
     * and forwards to the view.
     * <p><b>Logging:</b> DEBUG on start/end, parameters, model fetch; ERROR on exception.</p>
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("MarksheetCtl doGet run");
        log.debug("doGet() started");

        String op = DataUtility.getString(request.getParameter("operation"));
        long id = DataUtility.getLong(request.getParameter("id"));
        log.debug("doGet() params: op=" + op + ", id=" + id);

        MarksheetModel model = new MarksheetModel();

        if (id > 0 || op != null) {
            MarksheetBean bean;
            try {
                log.debug("Fetching Marksheet by PK: " + id);
                bean = model.findByPk(id);
                ServletUtility.setBean(bean, request);
                log.debug("Marksheet fetched " + (bean != null ? "successfully" : "but was null"));
            } catch (ApplicationException e) {
                log.error("doGet() ApplicationException", e);
                e.printStackTrace();
                return;
            }
        }
        log.debug("Forwarding to view: " + getView());
        ServletUtility.forward(getView(), request, response);
        log.debug("doGet() ended");
    }

    /**
     * Handles HTTP POST requests. Performs Save, Update, Cancel, and Reset
     * operations on Marksheet data based on the user action.
     * <p><b>Logging:</b> DEBUG on start/end, op routing, model calls; ERROR on exceptions.</p>
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("MarksheetCtl doPost run");
        log.debug("doPost() started");

        String op = DataUtility.getString(request.getParameter("operation"));
        log.debug("doPost() op=" + op);

        MarksheetModel model = new MarksheetModel();

        if (OP_SAVE.equalsIgnoreCase(op)) {
            log.debug("Operation: SAVE");
            MarksheetBean bean = (MarksheetBean) populateBean(request);
            try {
                long pk = model.add(bean);
                log.debug("Marksheet added with PK=" + pk);
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Marksheet added successfully", request);
            } catch (ApplicationException e) {
                log.error("doPost() SAVE ApplicationException", e);
                e.printStackTrace();
                return;
            } catch (DuplicateRecordException e) {
                log.error("doPost() SAVE DuplicateRecordException (Roll No exists)", e);
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Roll No already exists", request);
            }
        } else if (OP_UPDATE.equalsIgnoreCase(op)) {
            log.debug("Operation: UPDATE");
            MarksheetBean bean = (MarksheetBean) populateBean(request);
            try {
                if (bean.getId() > 0) {
                    log.debug("Updating Marksheet id=" + bean.getId());
                    model.update(bean);
                }
                ServletUtility.setBean(bean, request);
                ServletUtility.setSuccessMessage("Marksheet updated successfully", request);
            } catch (ApplicationException e) {
                log.error("doPost() UPDATE ApplicationException", e);
                e.printStackTrace();
                return;
            } catch (DuplicateRecordException e) {
                log.error("doPost() UPDATE DuplicateRecordException (Roll No exists)", e);
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Roll No already exists", request);
            }
        } else if (OP_CANCEL.equalsIgnoreCase(op)) {
            log.debug("Operation: CANCEL -> redirect to list");
            ServletUtility.redirect(ORSView.MARKSHEET_LIST_CTL, request, response);
            return;
        } else if (OP_RESET.equalsIgnoreCase(op)) {
            log.debug("Operation: RESET -> reload form");
            ServletUtility.redirect(ORSView.MARKSHEET_CTL, request, response);
            return;
        }

        log.debug("Forwarding to view: " + getView());
        ServletUtility.forward(getView(), request, response);
        log.debug("doPost() ended");
    }

    /**
     * Returns the view page (JSP) associated with MarksheetCtl.
     * <p><b>Logging:</b> DEBUG on return.</p>
     *
     * @return the JSP path for Marksheet view
     */
    @Override
    protected String getView() {
        log.debug("getView() -> " + ORSView.MARKSHEET_VIEW);
        return ORSView.MARKSHEET_VIEW;
    }
}
