package in.co.rays.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.MarksheetBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.MarksheetModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * MarksheetMeritListCtl servlet handles requests for displaying the 
 * merit list of marksheets. It retrieves records from the model layer 
 * and forwards the result to the respective view for presentation.
 * <p>
 * It supports both GET and POST operations. 
 * <ul>
 *   <li>GET  Displays the merit list</li>
 *   <li>POST Handles navigation operations (e.g., Back)</li>
 * </ul>
 * </p>
 * 
 * This controller follows the MVC design pattern:
 * <ul>
 *   <li>Model: {@link MarksheetModel}</li>
 *   <li>View: Defined by {@link ORSView#MARKSHEET_MERIT_LIST_VIEW}</li>
 *   <li>Controller: This servlet</li>
 * </ul>
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "MarksheetMeritListCtl", urlPatterns = { "/ctl/MarksheetMeritListCtl" })
public class MarksheetMeritListCtl extends BaseCtl {

    /** Logger instance for debugging and error logging */
    private static final Logger log = Logger.getLogger(MarksheetMeritListCtl.class);

    /**
     * Handles HTTP GET requests to display the merit list of marksheets.
     * <p>
     * It retrieves the merit list from the {@link MarksheetModel}, 
     * sets it into the request scope, and forwards the request to the view.
     * </p>
     * 
     * @param request  the HttpServletRequest object containing client request
     * @param response the HttpServletResponse object for sending response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.debug("MarksheetMeritListCtl doGet started");

        int pageNo = 1;
        int pageSize = DataUtility.getInt(PropertyReader.getValue("page.size"));

        MarksheetModel model = new MarksheetModel();

        try {
            log.debug("Fetching merit list from model, pageNo=" + pageNo + ", pageSize=" + pageSize);

            List<MarksheetBean> list = model.getMeritList(pageNo, pageSize);

            if (list == null || list.isEmpty()) {
                log.debug("No records found for merit list");
                ServletUtility.setErrorMessage("No record found", request);
            } else {
                log.debug("Merit list retrieved successfully, size=" + list.size());
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);

            log.debug("Forwarding request to view: " + getView());
            ServletUtility.forward(getView(), request, response);

        } catch (ApplicationException e) {
            log.error("ApplicationException occurred in doGet: ", e);
            e.printStackTrace();
            ServletUtility.handleException(e, request, response);
            return;
        }

        log.debug("MarksheetMeritListCtl doGet ended");
    }

    /**
     * Handles HTTP POST requests. 
     * <p>
     * Specifically processes the "Back" operation to redirect 
     * users to the Welcome controller.
     * </p>
     * 
     * @param request  the HttpServletRequest object containing client request
     * @param response the HttpServletResponse object for sending response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.debug("MarksheetMeritListCtl doPost started");

        String op = DataUtility.getString(request.getParameter("operation"));
        log.debug("Operation received: " + op);

        if (OP_BACK.equalsIgnoreCase(op)) {
            log.debug("Back operation triggered, redirecting to Welcome Controller");
            ServletUtility.redirect(ORSView.WELCOME_CTL, request, response);
            return;
        }

        log.debug("MarksheetMeritListCtl doPost ended");
    }

    /**
     * Returns the view page for the merit list of marksheets.
     * 
     * @return the path of the merit list view JSP
     */
    @Override
    protected String getView() {
        return ORSView.MARKSHEET_MERIT_LIST_VIEW;
    }
}
