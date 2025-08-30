package in.co.rays.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.StudentBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.StudentModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * StudentListCtl handles listing, searching, pagination, deletion, and navigation for Student records.
 * 
 * Supported operations: Search, Next, Previous, New, Delete, Reset, Back
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "StudentListCtl", urlPatterns = { "/ctl/StudentListCtl" })
public class StudentListCtl extends BaseCtl {

    private static Logger log = Logger.getLogger(StudentListCtl.class);

    /**
     * Populates StudentBean from request parameters.
     * 
     * @param request HttpServletRequest
     * @return BaseBean populated StudentBean
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("StudentListCtl populateBean run");

        StudentBean bean = new StudentBean();

        bean.setFirstName(DataUtility.getString(request.getParameter("firstName")));
        bean.setLastName(DataUtility.getString(request.getParameter("lastName")));
        bean.setEmail(DataUtility.getString(request.getParameter("email")));

        log.debug("Populated StudentBean: firstName=" + bean.getFirstName() + ", lastName=" + bean.getLastName() + ", email=" + bean.getEmail());

        return bean;
    }

    /**
     * Handles GET request to display list of students with pagination.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("StudentListCtl doGet run");

        int pageNo = 1;
        int pageSize = DataUtility.getInt(PropertyReader.getValue("page.size"));

        StudentBean bean = (StudentBean) populateBean(request);
        StudentModel model = new StudentModel();

        try {
            List<StudentBean> list = model.search(bean, pageNo, pageSize);
            List<StudentBean> next = model.search(bean, pageNo + 1, pageSize);

            if (list == null || list.isEmpty()) {
                ServletUtility.setErrorMessage("No record found", request);
                log.error("No student records found for current criteria");
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            ServletUtility.forward(getView(), request, response);
            log.debug("Student list displayed with pageNo=" + pageNo + ", pageSize=" + pageSize);

        } catch (ApplicationException e) {
            log.error("Error in StudentListCtl doGet", e);
            e.printStackTrace();
            ServletUtility.handleException(e, request, response);
        }
    }

    /**
     * Handles POST request for searching, pagination, deletion, and navigation.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("StudentListCtl doPost run");

        List<?> list = null;
        List<?> next = null;

        int pageNo = DataUtility.getInt(request.getParameter("pageNo"));
        int pageSize = DataUtility.getInt(request.getParameter("pageSize"));

        pageNo = (pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == 0) ? DataUtility.getInt(PropertyReader.getValue("page.size")) : pageSize;

        StudentBean bean = (StudentBean) populateBean(request);
        StudentModel model = new StudentModel();

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
                ServletUtility.redirect(ORSView.STUDENT_CTL, request, response);
                return;

            } else if (OP_DELETE.equalsIgnoreCase(op)) {
                pageNo = 1;
                if (ids != null && ids.length > 0) {
                    StudentBean deletebean = new StudentBean();
                    for (String id : ids) {
                        deletebean.setId(DataUtility.getInt(id));
                        model.delete(deletebean);
                        ServletUtility.setSuccessMessage("Student is deleted successfully", request);
                        log.debug("Deleted Student with ID=" + id);
                    }
                } else {
                    ServletUtility.setErrorMessage("Select at least one record", request);
                    log.error("No student selected for deletion");
                }

            } else if (OP_RESET.equalsIgnoreCase(op)) {
                ServletUtility.redirect(ORSView.STUDENT_LIST_CTL, request, response);
                return;

            } else if (OP_BACK.equalsIgnoreCase(op)) {
                ServletUtility.redirect(ORSView.STUDENT_LIST_CTL, request, response);
                return;
            }

            list = model.search(bean, pageNo, pageSize);
            next = model.search(bean, pageNo + 1, pageSize);

            if (list == null || list.isEmpty()) {
                ServletUtility.setErrorMessage("No record found", request);
                log.error("No student records found for current search");
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            ServletUtility.forward(getView(), request, response);
            log.debug("Student list displayed after doPost with pageNo=" + pageNo + ", pageSize=" + pageSize);

        } catch (ApplicationException e) {
            log.error("Error in StudentListCtl doPost", e);
            e.printStackTrace();
            ServletUtility.handleException(e, request, response);
        }
    }

    /**
     * Returns Student List view page.
     * 
     * @return String view page
     */
    @Override
    protected String getView() {
        return ORSView.STUDENT_LIST_VIEW;
    }
}
