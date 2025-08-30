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
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * SubjectListCtl handles listing, searching, and deletion of Subjects.
 * 
 * Supports pagination and preloads Subject and Course lists for filtering.
 * 
 * Author: Shad Khan
 * Version: 1.0
 */
@WebServlet(name = "SubjectListCtl", urlPatterns = { "/ctl/SubjectListCtl" })
public class SubjectListCtl extends BaseCtl {

    private static Logger log = Logger.getLogger(SubjectListCtl.class);

    /**
     * Preloads Subject and Course lists for filtering in the view.
     * 
     * @param request HttpServletRequest
     */
    @Override
    protected void preload(HttpServletRequest request) {
        log.debug("SubjectListCtl preload run");

        SubjectModel subjectModel = new SubjectModel();
        CourseModel courseModel = new CourseModel();

        try {
            List subjectList = subjectModel.list();
            request.setAttribute("subjectList", subjectList);
            log.debug("Preloaded subject list with size: " + subjectList.size());

            List courseList = courseModel.list();
            request.setAttribute("courseList", courseList);
            log.debug("Preloaded course list with size: " + courseList.size());
        } catch (ApplicationException e) {
            log.error("Error in preloading SubjectListCtl", e);
            e.printStackTrace();
        }
    }

    /**
     * Populates SubjectBean from request parameters.
     * 
     * @param request HttpServletRequest
     * @return BaseBean populated SubjectBean
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("SubjectListCtl populateBean run");

        SubjectBean bean = new SubjectBean();

        bean.setName(DataUtility.getString(request.getParameter("name")));
        bean.setCourseName(DataUtility.getString(request.getParameter("courseName")));
        bean.setDescription(DataUtility.getString(request.getParameter("description")));
        bean.setCourseId(DataUtility.getLong(request.getParameter("courseId")));
        bean.setId(DataUtility.getLong(request.getParameter("subjectId")));

        log.debug("Populated SubjectBean: " + bean);
        return bean;
    }

    /**
     * Handles GET request to display list of Subjects with pagination.
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.debug("SubjectListCtl doGet run");

        int pageNo = 1;
        int pageSize = DataUtility.getInt(PropertyReader.getValue("page.size"));

        SubjectBean bean = (SubjectBean) populateBean(request);
        SubjectModel model = new SubjectModel();

        try {
            List<SubjectBean> list = model.search(bean, pageNo, pageSize);
            List<SubjectBean> next = model.search(bean, pageNo + 1, pageSize);

            if (list == null || list.isEmpty()) {
                ServletUtility.setErrorMessage("No record found", request);
                log.debug("No record found");
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            ServletUtility.forward(getView(), request, response);
        } catch (ApplicationException e) {
            log.error("Error in doGet of SubjectListCtl", e);
            e.printStackTrace();
        }
    }

    /**
     * Handles POST request for search, pagination, delete, reset, and back operations.
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.debug("SubjectListCtl doPost run");

        List list = null;
        List next = null;

        int pageNo = DataUtility.getInt(request.getParameter("pageNo"));
        int pageSize = DataUtility.getInt(request.getParameter("pageSize"));

        pageNo = (pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == 0) ? DataUtility.getInt(PropertyReader.getValue("page.size")) : pageSize;

        SubjectBean bean = (SubjectBean) populateBean(request);
        SubjectModel model = new SubjectModel();

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

                log.debug("Pagination operation: " + op + ", pageNo: " + pageNo);
            } else if (OP_NEW.equalsIgnoreCase(op)) {
                ServletUtility.redirect(ORSView.SUBJECT_CTL, request, response);
                return;
            } else if (OP_DELETE.equalsIgnoreCase(op)) {
                pageNo = 1;
                if (ids != null && ids.length > 0) {
                    SubjectBean deletebean = new SubjectBean();
                    for (String id : ids) {
                        deletebean.setId(DataUtility.getInt(id));
                        model.delete(deletebean);
                        log.debug("Deleted Subject with ID: " + id);
                        ServletUtility.setSuccessMessage("Data is deleted successfully", request);
                    }
                } else {
                    ServletUtility.setErrorMessage("Select at least one record", request);
                    log.debug("No records selected for deletion");
                }
            } else if (OP_RESET.equalsIgnoreCase(op)) {
                ServletUtility.redirect(ORSView.SUBJECT_LIST_CTL, request, response);
                return;
            } else if (OP_BACK.equalsIgnoreCase(op)) {
                ServletUtility.redirect(ORSView.SUBJECT_LIST_CTL, request, response);
                return;
            }

            list = model.search(bean, pageNo, pageSize);
            next = model.search(bean, pageNo + 1, pageSize);

            if (!OP_DELETE.equalsIgnoreCase(op)) {
                if (list == null || list.size() == 0) {
                    ServletUtility.setErrorMessage("No record found ", request);
                    log.debug("No record found for current search criteria");
                }
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            ServletUtility.forward(getView(), request, response);
        } catch (ApplicationException e) {
            log.error("Error in doPost of SubjectListCtl", e);
            e.printStackTrace();
        }
    }

    /**
     * Returns Subject list view page.
     * 
     * @return String view page
     */
    @Override
    protected String getView() {
        return ORSView.SUBJECT_LIST_VIEW;
    }
}
