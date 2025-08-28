package in.co.rays.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.RoleBean;
import in.co.rays.model.RoleModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * RoleListCtl class provides functionality to list, search, delete and navigate Role records.
 * 
 * It extends BaseCtl for common controller operations.
 * 
 * Supported operations: Search, Next, Previous, New, Delete, Reset, Back
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "/RoleListCtl", urlPatterns = { "/RoleListCtl" })
public class RoleListCtl extends BaseCtl {

    private static Logger log = Logger.getLogger(RoleListCtl.class);

    /**
     * Preloads Role list for the view.
     * 
     * @param request HttpServletRequest
     */
    @Override
    protected void preload(HttpServletRequest request) {
        log.debug("RoleListCtl preload run");

        RoleModel model = new RoleModel();

        try {
            List<?> roleList = model.list();
            request.setAttribute("roleList", roleList);
            log.debug("Role list preloaded with size: " + roleList.size());
            return;
        } catch (Exception e) {
            log.error("Error in preloading role list", e);
            e.printStackTrace();
        }
    }

    /**
     * Populates RoleBean from request parameters.
     * 
     * @param request HttpServletRequest
     * @return BaseBean populated RoleBean
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("RoleListCtl populateBean run");

        RoleBean bean = new RoleBean();
        bean.setId(DataUtility.getLong(request.getParameter("roleId")));
        log.debug("Populated RoleBean with ID: " + bean.getId());

        return bean;
    }

    /**
     * Handles GET requests for listing roles.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("RoleListCtl doGet run");

        int pageNo = 1;
        int pageSize = DataUtility.getInt(PropertyReader.getValue("page.size"));

        RoleModel model = new RoleModel();
        RoleBean bean = (RoleBean) populateBean(request);

        try {
            List<?> list = model.search(bean, pageNo, pageSize);
            List<?> next = model.search(bean, pageNo + 1, pageSize);

            if (list == null || list.isEmpty()) {
                ServletUtility.setErrorMessage("Record Not Found", request);
                log.error("No roles found");
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());
            log.debug("Role list displayed with pageNo: " + pageNo + ", pageSize: " + pageSize);

        } catch (Exception e) {
            log.error("Error in RoleListCtl doGet", e);
            e.printStackTrace();
        }

        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Handles POST requests for listing roles with operations like search, pagination, delete, etc.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("RoleListCtl doPost run");

        List<?> list = null;
        List<?> next = null;

        int pageNo = DataUtility.getInt(request.getParameter("pageNo"));
        int pageSize = DataUtility.getInt(request.getParameter("pageSize"));

        pageNo = (pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == 0) ? DataUtility.getInt(PropertyReader.getValue("page.size")) : pageSize;

        RoleBean bean = (RoleBean) populateBean(request);
        RoleModel model = new RoleModel();

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
                ServletUtility.redirect(ORSView.USER_CTL, request, response);
                return;
            } else if (OP_DELETE.equalsIgnoreCase(op)) {
                pageNo = 1;
                if (ids != null && ids.length > 0) {
                    RoleBean deletebean = new RoleBean();
                    for (String id : ids) {
                        deletebean.setId(DataUtility.getInt(id));
                        model.delete(deletebean);
                        ServletUtility.setSuccessMessage("Data is deleted successfully", request);
                        log.debug("Deleted Role with ID: " + id);
                    }
                } else {
                    ServletUtility.setErrorMessage("Select at least one record", request);
                    log.error("No Role selected for deletion");
                }
            } else if (OP_RESET.equalsIgnoreCase(op)) {
                ServletUtility.redirect(ORSView.USER_LIST_CTL, request, response);
                return;
            } else if (OP_BACK.equalsIgnoreCase(op)) {
                ServletUtility.redirect(ORSView.USER_LIST_CTL, request, response);
                return;
            }

            list = model.search(bean, pageNo, pageSize);
            next = model.search(bean, pageNo + 1, pageSize);

            if (!OP_DELETE.equalsIgnoreCase(op)) {
                if (list == null || list.isEmpty()) {
                    ServletUtility.setErrorMessage("No record found", request);
                    log.error("No roles found for current search");
                }
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            ServletUtility.forward(getView(), request, response);
            log.debug("Role list displayed after doPost with pageNo: " + pageNo + ", pageSize: " + pageSize);

        } catch (Exception e) {
            log.error("Error in RoleListCtl doPost", e);
            e.printStackTrace();
            return;
        }
    }

    /**
     * Returns Role List view page.
     * 
     * @return String view page
     */
    @Override
    protected String getView() {
        return ORSView.ROLE_LIST_VIEW;
    }

}
