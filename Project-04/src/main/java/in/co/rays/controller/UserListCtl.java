package in.co.rays.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.UserBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.RoleModel;
import in.co.rays.model.UserModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * UserListCtl handles listing, searching, pagination, and deletion of users.
 * Supports preloading of roles for search filter dropdowns.
 * 
 * Author: Shad Khan
 * Version: 1.0
 */
@WebServlet(name = "/UserListCtl", urlPatterns = { "/ctl/UserListCtl" })
public class UserListCtl extends BaseCtl {

    private static Logger log = Logger.getLogger(UserListCtl.class);

    /**
     * Preloads the role list into the request for dropdowns.
     * 
     * @param request HttpServletRequest
     */
    @Override
    protected void preload(HttpServletRequest request) {
        log.debug("UserListCtl preload run");
        RoleModel roleModel = new RoleModel();
        try {
            List<?> roleList = roleModel.list();
            request.setAttribute("roleList", roleList);
            log.debug("Preloaded role list size: " + roleList.size());
        } catch (Exception e) {
            log.error("Error preloading role list", e);
            e.printStackTrace();
        }
    }

    /**
     * Populates UserBean from request parameters for search/filter.
     * 
     * @param request HttpServletRequest
     * @return BaseBean populated UserBean
     */
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
        log.debug("UserListCtl populateBean run");
        UserBean bean = new UserBean();
        bean.setFirstName(DataUtility.getString(request.getParameter("firstName")));
        bean.setLogin(DataUtility.getString(request.getParameter("login")));
        bean.setRoleId(DataUtility.getLong(request.getParameter("roleId")));
        return bean;
    }

    /**
     * Handles GET request for displaying paginated user list.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.debug("UserListCtl doGet run");

        int pageNo = 1;
        int pageSize = DataUtility.getInt(PropertyReader.getValue("page.size"));

        UserBean bean = (UserBean) populateBean(request);
        UserModel model = new UserModel();

        try {
            List<UserBean> list = model.search(bean, pageNo, pageSize);
            List<UserBean> next = model.search(bean, pageNo + 1, pageSize);

            if (list == null || list.isEmpty()) {
                ServletUtility.setErrorMessage("No record found", request);
                log.debug("No record found for the search criteria");
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            ServletUtility.forward(getView(), request, response);

        } catch (ApplicationException e) {
            log.error("ApplicationException in doGet", e);
            e.printStackTrace();
        }
    }

    /**
     * Handles POST request for search, pagination, delete, reset, and back operations.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.debug("UserListCtl doPost run");

        List list = null;
        List next = null;

        int pageNo = DataUtility.getInt(request.getParameter("pageNo"));
        int pageSize = DataUtility.getInt(request.getParameter("pageSize"));

        pageNo = (pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == 0) ? DataUtility.getInt(PropertyReader.getValue("page.size")) : pageSize;

        UserBean bean = (UserBean) populateBean(request);
        UserModel model = new UserModel();

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
                ServletUtility.redirect(ORSView.USER_CTL, request, response);
                return;
            } else if (OP_DELETE.equalsIgnoreCase(op)) {
                pageNo = 1;
                if (ids != null && ids.length > 0) {
                    UserBean deletebean = new UserBean();
                    for (String id : ids) {
                        deletebean.setId(DataUtility.getInt(id));
                        model.delete(deletebean);
                        log.debug("Deleted user with ID: " + id);
                    }
                    ServletUtility.setSuccessMessage("Data is deleted successfully", request);
                } else {
                    ServletUtility.setErrorMessage("Select at least one record", request);
                    log.debug("No record selected for deletion");
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
                if (list == null || list.size() == 0) {
                    ServletUtility.setErrorMessage("No record found ", request);
                    log.debug("No record found for search after operation: " + op);
                }
            }

            ServletUtility.setList(list, request);
            ServletUtility.setPageNo(pageNo, request);
            ServletUtility.setPageSize(pageSize, request);
            ServletUtility.setBean(bean, request);
            request.setAttribute("nextListSize", next.size());

            ServletUtility.forward(getView(), request, response);

        } catch (ApplicationException e) {
            log.error("ApplicationException in doPost", e);
            e.printStackTrace();
        }
    }

    /**
     * Returns the view page for User List.
     * 
     * @return String view page
     */
    @Override
    protected String getView() {
        return ORSView.USER_LIST_VIEW;
    }
}
