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

/**
 * MarksheetMeritListCtl servlet handles requests for displaying the 
 * merit list of marksheets. It retrieves records from the model layer 
 * and forwards the result to the respective view for presentation.
 * <p>
 * It supports both GET and POST operations. The GET method displays 
 * the merit list, while the POST method handles navigation operations.
 * </p>
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "MarksheetMeritListCtl", urlPatterns = { "/MarksheetMeritListCtl" })
public class MarksheetMeritListCtl extends BaseCtl {

    /**
     * Handles HTTP GET requests to display the merit list of marksheets.
     * 
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int pageNo = 1;
        int pageSize = DataUtility.getInt(PropertyReader.getValue("page.size"));

        MarksheetModel model = new MarksheetModel();

        try {
            List<MarksheetBean> list = model.getMeritList(pageNo, pageSize);

            if (list == null || list.isEmpty()) {
                ServletUtility.setErrorMessage("No record found", request);
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);

            ServletUtility.forward(getView(), request, response);

        } catch (ApplicationException e) {
            e.printStackTrace();
            ServletUtility.handleException(e, request, response);
            return;
        }
    }

    /**
     * Handles HTTP POST requests. Specifically processes the 
     * "Back" operation to redirect users to the Welcome controller.
     * 
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String op = DataUtility.getString(request.getParameter("operation"));

        if (OP_BACK.equalsIgnoreCase(op)) {
            ServletUtility.redirect(ORSView.WELCOME_CTL, request, response);
            return;
        }
    }

    /**
     * Returns the view page for the merit list of marksheets.
     * 
     * @return the path of the merit list view
     */
    @Override
    protected String getView() {
        return ORSView.MARKSHEET_MERIT_LIST_VIEW;
    }
}