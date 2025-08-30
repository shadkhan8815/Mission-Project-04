package in.co.rays.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.CollegeBean;
import in.co.rays.model.CollegeModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * CollegeListCtl Controller class to handle operations related to 
 * listing, searching, deleting, and paginating college records.
 * 
 * It uses CollegeModel to perform database operations and ServletUtility 
 * to manage request, response, and navigation.
 * 
 * URL Mapping: /CollegeListCtl
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "CollegeListCtl", urlPatterns = { "/ctl/CollegeListCtl" })
public class CollegeListCtl extends BaseCtl {

    /** Logger instance for debug and error messages */
    private static Logger log = Logger.getLogger(CollegeListCtl.class);

    /**
     * Preloads the list of all colleges to be used in views (like dropdowns etc.).
     * 
     * @param request HttpServletRequest object
     */
    @Override
    protected void preload(HttpServletRequest request) {
        log.debug("CollegeListCtl preload run");

        CollegeModel collegeModel = new CollegeModel();

        try {
            List<?> collegeList = collegeModel.list();
            request.setAttribute("collegeList", collegeList);
        } catch (Exception e) {
            log.error("Error in CollegeListCtl preload", e);
            e.printStackTrace();
        }
    }

    /**
     * Populates CollegeBean object from request parameters.
     * 
     * @param request HttpServletRequest object
     * @return CollegeBean populated with request data
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("CollegeListCtl populateBean run");

        CollegeBean bean = new CollegeBean();

        bean.setName(DataUtility.getString(request.getParameter("name")));
        bean.setCity(DataUtility.getString(request.getParameter("city")));
        bean.setId(DataUtility.getLong(request.getParameter("collegeId")));

        return bean;
    }

    /**
     * Handles GET requests for displaying the list of colleges with pagination.
     * 
     * @param request HttpServletRequest object
     * @param response HttpServletResponse object
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("CollegeListCtl doGet run");

        int pageNo = 1;
        int pageSize = DataUtility.getInt(PropertyReader.getValue("page.size"));

        CollegeBean bean = (CollegeBean) populateBean(request);
        CollegeModel model = new CollegeModel();

        try {
            List<CollegeBean> list = model.search(bean, pageNo, pageSize);
            List<CollegeBean> next = model.search(bean, pageNo + 1, pageSize);

            if (list == null || list.isEmpty()) {
                ServletUtility.setErrorMessage("No record found", request);
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            ServletUtility.forward(getView(), request, response);

        } catch (Exception e) {
            log.error("Error in CollegeListCtl doGet", e);
            e.printStackTrace();
        }
    }

    /**
     * Handles POST requests for operations like search, next, previous, delete, reset, etc.
     * 
     * @param request HttpServletRequest object
     * @param response HttpServletResponse object
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("CollegeListCtl doPost run");

        List list = null;
        List next = null;

        int pageNo = DataUtility.getInt(request.getParameter("pageNo"));
        int pageSize = DataUtility.getInt(request.getParameter("pageSize"));

        pageNo = (pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == 0) ? DataUtility.getInt(PropertyReader.getValue("page.size")) : pageSize;

        CollegeBean bean = (CollegeBean) populateBean(request);
        CollegeModel model = new CollegeModel();

        String op = DataUtility.getString(request.getParameter("operation"));
        String[] ids = request.getParameterValues("ids");

        try {

            if (OP_SEARCH.equalsIgnoreCase(op) || OP_NEXT.equalsIgnoreCase(op) || OP_PREVIOUS.equalsIgnoreCase(op)) {

                if (OP_SEARCH.equalsIgnoreCase(op)) {
                    pageNo = 1;
                } else if (OP_NEXT.equalsIgnoreCase(op)) {
                    pageNo++;
                } else if (OP_PREVIOUS.equalsIgnoreCase(op) && pageNo > 1) {
                    pageNo--;
                }

            } else if (OP_NEW.equalsIgnoreCase(op)) {
                ServletUtility.redirect(ORSView.COLLEGE_CTL, request, response);
                return;
            } else if (OP_DELETE.equalsIgnoreCase(op)) {
                pageNo = 1;
                if (ids != null && ids.length > 0) {
                    CollegeBean deletebean = new CollegeBean();
                    for (String id : ids) {
                        deletebean.setId(DataUtility.getInt(id));
                        model.delete(deletebean);
                        ServletUtility.setSuccessMessage("College is deleted successfully", request);
                        log.debug("College deleted successfully, ID: " + id);
                    }
                } else {
                    ServletUtility.setErrorMessage("Select at least one record", request);
                    log.debug("No record selected for deletion");
                }
            } else if (OP_RESET.equalsIgnoreCase(op) || OP_BACK.equalsIgnoreCase(op)) {
                ServletUtility.redirect(ORSView.COLLEGE_LIST_CTL, request, response);
                return;
            }

            list = model.search(bean, pageNo, pageSize);
            next = model.search(bean, pageNo + 1, pageSize);

            if (!OP_DELETE.equalsIgnoreCase(op)) {
                if (list == null || list.size() == 0) {
                    ServletUtility.setErrorMessage("No record found", request);
                }
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            ServletUtility.forward(getView(), request, response);
        } catch (Exception e) {
            log.error("Error in CollegeListCtl doPost", e);
            e.printStackTrace();
        }
    }

    /**
     * Returns the view path for the college list page.
     * 
     * @return String representing the path of the view
     */
    @Override
    protected String getView() {
        return ORSView.COLLEGE_LIST_VIEW;
    }
}
