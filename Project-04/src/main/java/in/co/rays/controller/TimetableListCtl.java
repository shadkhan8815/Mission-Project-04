package in.co.rays.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.TimetableBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.CourseModel;
import in.co.rays.model.SubjectModel;
import in.co.rays.model.TimetableModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * TimetableListCtl handles operations related to Timetable list view.
 * 
 * Supports search, pagination, delete, and preloading of Course and Subject lists.
 * 
 * Author: Shad Khan
 * Version: 1.0
 */
@WebServlet(name = "TimetableListCtl", urlPatterns = { "/ctl/TimetableListCtl" })
public class TimetableListCtl extends BaseCtl {

    private static Logger log = Logger.getLogger(TimetableListCtl.class);

    /**
     * Preloads Subject and Course lists to populate dropdowns in the list view.
     * 
     * @param request HttpServletRequest
     */
    @Override
    protected void preload(HttpServletRequest request) {
        log.debug("TimetableListCtl preload run");

        SubjectModel subjectModel = new SubjectModel();
        CourseModel courseModel = new CourseModel();

        try {
            List subjectList = subjectModel.list();
            request.setAttribute("subjectList", subjectList);
            log.debug("Preloaded subject list size: " + subjectList.size());

            List courseList = courseModel.list();
            request.setAttribute("courseList", courseList);
            log.debug("Preloaded course list size: " + courseList.size());
        } catch (ApplicationException e) {
            log.error("Error preloading TimetableListCtl", e);
            e.printStackTrace();
        }
    }

    /**
     * Populates TimetableBean from request parameters.
     * 
     * @param request HttpServletRequest
     * @return BaseBean populated TimetableBean
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("TimetableListCtl populateBean run");

        TimetableBean bean = new TimetableBean();

        bean.setCourseId(DataUtility.getLong(request.getParameter("courseId")));
        bean.setSubjectId(DataUtility.getLong(request.getParameter("subjectId")));
        bean.setExamDate(DataUtility.getDate(request.getParameter("examDate")));

        return bean;
    }

    /**
     * Handles GET request to display Timetable list with pagination.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.debug("TimetableListCtl doGet run");

        int pageNo = 1;
        int pageSize = DataUtility.getInt(PropertyReader.getValue("page.size"));

        TimetableBean bean = (TimetableBean) populateBean(request);
        TimetableModel model = new TimetableModel();

        try {
            List<TimetableBean> list = model.search(bean, pageNo, pageSize);
            List<TimetableBean> next = model.search(bean, pageNo + 1, pageSize);

            if (list == null || list.isEmpty()) {
                ServletUtility.setErrorMessage("No record found", request);
                log.debug("No timetable records found on doGet");
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            ServletUtility.forward(getView(), request, response);

        } catch (ApplicationException e) {
            log.error("Error in doGet of TimetableListCtl", e);
            e.printStackTrace();
            ServletUtility.handleException(e, request, response);
            return;
        }
    }

    /**
     * Handles POST request to search, delete, or paginate Timetable records.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.debug("TimetableListCtl doPost run");

        List list = null;
        List next = null;

        int pageNo = DataUtility.getInt(request.getParameter("pageNo"));
        int pageSize = DataUtility.getInt(request.getParameter("pageSize"));

        pageNo = (pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == 0) ? DataUtility.getInt(PropertyReader.getValue("page.size")) : pageSize;

        TimetableBean bean = (TimetableBean) populateBean(request);
        TimetableModel model = new TimetableModel();

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
                ServletUtility.redirect(ORSView.TIMETABLE_CTL, request, response);
                return;

            } else if (OP_DELETE.equalsIgnoreCase(op)) {
                pageNo = 1;
                if (ids != null && ids.length > 0) {
                    TimetableBean deletebean = new TimetableBean();
                    for (String id : ids) {
                        deletebean.setId(DataUtility.getInt(id));
                        model.delete(deletebean);
                        ServletUtility.setSuccessMessage("Data is deleted successfully", request);
                        log.debug("Deleted Timetable ID: " + id);
                    }
                } else {
                    ServletUtility.setErrorMessage("Select at least one record", request);
                    log.debug("No record selected for deletion");
                }

            } else if (OP_RESET.equalsIgnoreCase(op)) {
                ServletUtility.redirect(ORSView.TIMETABLE_LIST_CTL, request, response);
                return;

            } else if (OP_BACK.equalsIgnoreCase(op)) {
                ServletUtility.redirect(ORSView.TIMETABLE_LIST_CTL, request, response);
                return;
            }

            list = model.search(bean, pageNo, pageSize);
            next = model.search(bean, pageNo + 1, pageSize);

            if (list == null || list.size() == 0) {
                ServletUtility.setErrorMessage("No record found ", request);
                log.debug("No timetable records found on doPost");
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            ServletUtility.forward(getView(), request, response);
        } catch (ApplicationException e) {
            log.error("Error in doPost of TimetableListCtl", e);
            e.printStackTrace();
            ServletUtility.handleException(e, request, response);
            return;
        }
    }

    /**
     * Returns Timetable list view page.
     * 
     * @return String view page
     */
    @Override
    protected String getView() {
        return ORSView.TIMETABLE_LIST_VIEW;
    }
}
