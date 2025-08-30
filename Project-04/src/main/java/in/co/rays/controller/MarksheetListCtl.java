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
import in.co.rays.model.MarksheetModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

/**
 * Marksheet List Controller class to handle operations related to 
 * displaying and managing the list of marksheets.
 * 
 * This controller provides pagination, search, navigation (Next/Previous),
 * addition of new marksheets, deletion of existing marksheets, and 
 * reset/back functionality.
 *
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "MarksheetListCtl", urlPatterns = { "/ctl/MarksheetListCtl" })
public class MarksheetListCtl extends BaseCtl {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The log object for logging debug and error messages. */
    private static Logger log = Logger.getLogger(MarksheetListCtl.class);

    /**
     * Populates the {@link MarksheetBean} with request parameters.
     * 
     * @param request the HttpServletRequest object
     * @return populated {@link MarksheetBean}
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        System.out.println("MarksheetListCtl populateBean run");
        log.debug("MarksheetListCtl populateBean started");

        MarksheetBean bean = new MarksheetBean();

        bean.setRollNo(DataUtility.getString(request.getParameter("rollNo")));
        bean.setName(DataUtility.getString(request.getParameter("name")));

        log.debug("MarksheetListCtl populateBean populated with RollNo=" 
                + bean.getRollNo() + ", Name=" + bean.getName());
        log.debug("MarksheetListCtl populateBean ended");
        return bean;
    }

    /**
     * Handles GET requests to display the list of marksheets.
     * It initializes page number and size, retrieves the list from the model,
     * and forwards the data to the view.
     *
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if servlet error occurs
     * @throws IOException if an input or output error is detected
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("MarksheetListCtl doGet run");
        log.debug("MarksheetListCtl doGet started");

        int pageNo = 1;
        int pageSize = DataUtility.getInt(PropertyReader.getValue("page.size"));

        MarksheetBean bean = (MarksheetBean) populateBean(request);
        MarksheetModel model = new MarksheetModel();

        try {
            List<MarksheetBean> list = model.search(bean, pageNo, pageSize);
            List<MarksheetBean> next = model.search(bean, pageNo + 1, pageSize);

            if (list == null || list.isEmpty()) {
                log.debug("No record found for given search criteria");
                ServletUtility.setErrorMessage("No record found", request);
            } else {
                log.debug("Records fetched successfully, size: " + list.size());
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            log.debug("Forwarding to view: " + getView());
            ServletUtility.forward(getView(), request, response);

        } catch (ApplicationException e) {
            log.error("ApplicationException in doGet: ", e);
            e.printStackTrace();
        }

        log.debug("MarksheetListCtl doGet ended");
    }

    /**
     * Handles POST requests for performing search, navigation (Next/Previous),
     * add new marksheet, delete records, reset, and back operations.
     *
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if servlet error occurs
     * @throws IOException if an input or output error is detected
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("MarksheetListCtl doPost run");
        log.debug("MarksheetListCtl doPost started");

        List list = null;
        List next = null;

        int pageNo = DataUtility.getInt(request.getParameter("pageNo"));
        int pageSize = DataUtility.getInt(request.getParameter("pageSize"));

        pageNo = (pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == 0) ? DataUtility.getInt(PropertyReader.getValue("page.size")) : pageSize;

        MarksheetBean bean = (MarksheetBean) populateBean(request);
        MarksheetModel model = new MarksheetModel();

        String op = DataUtility.getString(request.getParameter("operation"));
        String[] ids = request.getParameterValues("ids");

        try {
            log.debug("Operation received: " + op);

            if (OP_SEARCH.equalsIgnoreCase(op) || "Next".equalsIgnoreCase(op) || "Previous".equalsIgnoreCase(op)) {

                if (OP_SEARCH.equalsIgnoreCase(op)) {
                    pageNo = 1;
                } else if (OP_NEXT.equalsIgnoreCase(op)) {
                    pageNo++;
                } else if (OP_PREVIOUS.equalsIgnoreCase(op) && pageNo > 1) {
                    pageNo--;
                }
            } else if (OP_NEW.equalsIgnoreCase(op)) {
                log.debug("Redirecting to new Marksheet form");
                ServletUtility.redirect(ORSView.MARKSHEET_CTL, request, response);
                return;
            } else if (OP_DELETE.equalsIgnoreCase(op)) {
                pageNo = 1;
                if (ids != null && ids.length > 0) {
                    MarksheetBean deletebean = new MarksheetBean();
                    for (String id : ids) {
                        deletebean.setId(DataUtility.getInt(id));
                        model.delete(deletebean);
                        log.debug("Deleted Marksheet ID: " + id);
                        ServletUtility.setSuccessMessage("Marksheet is deleted successfully", request);
                    }
                } else {
                    log.debug("Delete operation failed: No record selected");
                    ServletUtility.setErrorMessage("Select at least one record", request);
                }
            } else if (OP_RESET.equalsIgnoreCase(op)) {
                log.debug("Reset operation performed");
                ServletUtility.redirect(ORSView.MARKSHEET_LIST_CTL, request, response);
                return;
            } else if (OP_BACK.equalsIgnoreCase(op)) {
                log.debug("Back operation performed");
                ServletUtility.redirect(ORSView.MARKSHEET_LIST_CTL, request, response);
                return;
            }

            list = model.search(bean, pageNo, pageSize);
            next = model.search(bean, pageNo + 1, pageSize);

            if (!OP_DELETE.equalsIgnoreCase(op)) {
                if (list == null || list.size() == 0) {
                    log.debug("No record found for given search in doPost");
                    ServletUtility.setErrorMessage("No record found ", request);
                }
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            log.debug("Forwarding to view: " + getView());
            ServletUtility.forward(getView(), request, response);

        } catch (ApplicationException e) {
            log.error("ApplicationException in doPost: ", e);
            e.printStackTrace();
            return;
        }

        log.debug("MarksheetListCtl doPost ended");
    }

    /**
     * Returns the view page for the marksheet list.
     * 
     * @return the constant view name of marksheet list
     */
    @Override
    protected String getView() {
        log.debug("Returning view: " + ORSView.MARKSHEET_LIST_VIEW);
        return ORSView.MARKSHEET_LIST_VIEW;
    }  
}
