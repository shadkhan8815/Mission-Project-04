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
import in.co.rays.model.FacultyModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;
import org.apache.log4j.Logger;

/**
 * FacultyListCtl Servlet controller to handle listing, searching,
 * pagination and deletion of Faculty records.
 * <p>
 * This controller interacts with {@link FacultyModel} to fetch data
 * and forwards results to the Faculty List View.
 * </p>
 * 
 * Author: Shad Khan
 * Version: 1.0
 */
@WebServlet(name = "/FacultyListCtl", urlPatterns = {"/ctl/FacultyListCtl"})
public class FacultyListCtl extends BaseCtl {

    /** Logger for debug and error messages */
    private static Logger log = Logger.getLogger(FacultyListCtl.class);

    /**
     * Populates the FacultyBean from HTTP request parameters.
     *
     * @param request the HttpServletRequest object
     * @return populated FacultyBean object
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("FacultyListCtl populateBean run");

        FacultyBean bean = new FacultyBean();
        bean.setFirstName(DataUtility.getString(request.getParameter("firstName")));
        bean.setLastName(DataUtility.getString(request.getParameter("lastName")));
        bean.setEmail(DataUtility.getString(request.getParameter("email")));

        return bean;
    }

    /**
     * Handles GET requests. Displays the first page of Faculty list
     * and supports initial search operation.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("FacultyListCtl doGet run");

        int pageNo = 1;
        int pageSize = DataUtility.getInt(PropertyReader.getValue("page.size"));

        FacultyBean bean = (FacultyBean) populateBean(request);
        FacultyModel model = new FacultyModel();

        try {
            List<FacultyBean> list = model.search(bean, pageNo, pageSize);
            List<FacultyBean> next = model.search(bean, pageNo + 1, pageSize);

            if (list == null || list.isEmpty()) {
                ServletUtility.setErrorMessage("No record found", request);
                log.debug("No record found in FacultyListCtl doGet");
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            ServletUtility.forward(getView(), request, response);

        } catch (ApplicationException e) {
            log.error("Error in FacultyListCtl doGet", e);
            e.printStackTrace();
        }
    }

    /**
     * Handles POST requests. Supports operations like:
     * <ul>
     *   <li>Search</li>
     *   <li>Next/Previous page navigation</li>
     *   <li>New Faculty redirection</li>
     *   <li>Delete Faculty records</li>
     *   <li>Reset and Back operations</li>
     * </ul>
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("FacultyListCtl doPost run");

        List list = null;
        List next = null;

        int pageNo = DataUtility.getInt(request.getParameter("pageNo"));
        int pageSize = DataUtility.getInt(request.getParameter("pageSize"));

        pageNo = (pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == 0) ? DataUtility.getInt(PropertyReader.getValue("page.size")) : pageSize;

        FacultyBean bean = (FacultyBean) populateBean(request);
        FacultyModel model = new FacultyModel();

        String op = DataUtility.getString(request.getParameter("operation"));
        String[] ids = request.getParameterValues("ids");

        try {
            if (OP_SEARCH.equalsIgnoreCase(op) || "Next".equalsIgnoreCase(op) || "Previous".equalsIgnoreCase(op)) {

                if (OP_SEARCH.equalsIgnoreCase(op)) {
                    pageNo = 1;
                } else if (OP_NEXT.equalsIgnoreCase(op)) {
                    pageNo++;
                } else if (OP_PREVIOUS.equalsIgnoreCase(op) && pageNo > 1) {
                    pageNo--;
                }

            } else if (OP_NEW.equalsIgnoreCase(op)) {
                ServletUtility.redirect(ORSView.FACULTY_CTL, request, response);
                return;
            } else if (OP_DELETE.equalsIgnoreCase(op)) {
                pageNo = 1;
                if (ids != null && ids.length > 0) {
                    FacultyBean deletebean = new FacultyBean();
                    for (String id : ids) {
                        deletebean.setId(DataUtility.getInt(id));
                        model.delete(deletebean);
                        ServletUtility.setSuccessMessage("Faculty is deleted successfully", request);
                        log.debug("Deleted Faculty with id: " + id);
                    }
                } else {
                    ServletUtility.setErrorMessage("Select at least one record", request);
                    log.debug("No Faculty selected for deletion");
                }
            } else if (OP_RESET.equalsIgnoreCase(op) || OP_BACK.equalsIgnoreCase(op)) {
                ServletUtility.redirect(ORSView.FACULTY_LIST_CTL, request, response);
                return;
            }

            list = model.search(bean, pageNo, pageSize);
            next = model.search(bean, pageNo + 1, pageSize);

            if (!OP_DELETE.equalsIgnoreCase(op)) {
                if (list == null || list.size() == 0) {
                    ServletUtility.setErrorMessage("No record found", request);
                    log.debug("No record found after search or pagination in FacultyListCtl doPost");
                }
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            ServletUtility.forward(getView(), request, response);

        } catch (ApplicationException e) {
            log.error("Error in FacultyListCtl doPost", e);
            e.printStackTrace();
        }
    }

    /**
     * Returns the view path of the Faculty List page.
     *
     * @return Faculty List view constant
     */
    @Override
    protected String getView() {
        log.debug("FacultyListCtl getView run");
        return ORSView.FACULTY_LIST_VIEW;
    }
}
