package in.co.rays.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;   

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.MarksheetBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.MarksheetModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

/**
 * GetMarksheetCtl is a Controller to handle requests related to retrieving 
 * a student's marksheet by roll number. It validates the input, interacts 
 * with the MarksheetModel, and forwards the data to the appropriate view.
 * <p>
 * This controller follows the MVC pattern by working as the Controller 
 * between the View (JSP) and the Model (MarksheetModel).
 * </p>
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "GetMarksheetCtl", urlPatterns = { "/ctl/GetMarksheetCtl" })
public class GetMarksheetCtl extends BaseCtl {

    private static final Logger log = Logger.getLogger(GetMarksheetCtl.class);

    /**
     * Validates the request parameters. Ensures that the roll number is not null.
     *
     * @param request the HttpServletRequest containing user input
     * @return true if validation passes, false otherwise
     */
    @Override
    protected boolean validate(HttpServletRequest request) {

        log.debug("Validate method started");

        boolean pass = true;

        if (DataValidator.isNull(request.getParameter("rollNo"))) {
            request.setAttribute("rollNo", PropertyReader.getValue("error.require", "Roll Number"));
            log.error("Validation failed: Roll Number is required");
            pass = false;
        }

        log.debug("Validate method ended with pass=" + pass);
        return pass;
    }

    /**
     * Populates a MarksheetBean object with request parameters.
     *
     * @param request the HttpServletRequest containing form data
     * @return a populated MarksheetBean object
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {

        log.debug("Populating MarksheetBean with request parameters");

        MarksheetBean bean = new MarksheetBean();

        bean.setRollNo(DataUtility.getString(request.getParameter("rollNo")));

        log.debug("Populated bean: " + bean);

        return bean;
    }

    /**
     * Handles HTTP GET requests. Forwards the request to the view page.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        log.debug("doGet started");
        ServletUtility.forward(getView(), request, response);
        log.debug("doGet ended");
    }

    /**
     * Handles HTTP POST requests. Processes the form submission, retrieves 
     * the marksheet by roll number, and sets the data or error messages accordingly.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.debug("doPost started");

        String op = DataUtility.getString(request.getParameter("operation"));
        log.debug("Operation = " + op);

        MarksheetModel model = new MarksheetModel();

        MarksheetBean bean = (MarksheetBean) populateBean(request);

        if (OP_GO.equalsIgnoreCase(op)) {
            try {
                log.debug("Fetching marksheet for RollNo: " + bean.getRollNo());
                bean = model.findByRollNo(bean.getRollNo());
                if (bean != null) {
                    log.debug("Marksheet found for RollNo: " + bean.getRollNo());
                    ServletUtility.setBean(bean, request);
                } else {
                    log.error("No marksheet found for RollNo: " + bean.getRollNo());
                    ServletUtility.setErrorMessage("RollNo Does Not exists", request);
                }
            } catch (ApplicationException e) {
                log.error("ApplicationException in doPost", e);
                e.printStackTrace(); // existing code (unchanged)
                ServletUtility.handleException(e, request, response);
                return;
            }
        }

        ServletUtility.forward(getView(), request, response);
        log.debug("doPost ended");
    }

    /**
     * Returns the view page (JSP) associated with this controller.
     *
     * @return the path of the marksheet view JSP
     */
    @Override
    protected String getView() {
        return ORSView.GET_MARKSHEET_VIEW;
    }

}
