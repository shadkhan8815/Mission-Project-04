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

@WebServlet(name = "/UserListCtl", urlPatterns = { "/UserListCtl" })
public class UserListCtl extends BaseCtl {
	
	@Override
	protected void preload(HttpServletRequest request) {
		System.out.println("UserListCtl preload run");
		
		RoleModel model = new RoleModel() ;
		
	try {
		List roleList = model.list();
		request.setAttribute("roleList", roleList);
	
	} catch (Exception e) {
		
		e.printStackTrace();
	}
	}

	@Override
	protected BaseBean populateBean(HttpServletRequest request) {
		System.out.println("UserListCtl populateBean run");
		
		UserBean bean = new UserBean() ;
		
		    bean.setFirstName(DataUtility.getString(request.getParameter("firstName")));
	        bean.setLogin(DataUtility.getString(request.getParameter("login")));
	        bean.setRoleId(DataUtility.getLong(request.getParameter("roleId")));

		return bean;

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("UserListCtl doGet run");
		
		 int pageNo = 1;
	        int pageSize = DataUtility.getInt(PropertyReader.getValue("page.size"));

	        UserBean bean = (UserBean) populateBean(request);
	        UserModel model = new UserModel();

	        try {
	            List<UserBean> list = model.search(bean, pageNo, pageSize);
	            List<UserBean> next = model.search(bean, pageNo + 1, pageSize);

	            if (list == null || list.isEmpty()) {
	                ServletUtility.setErrorMessage("No record found", request);
	            }

	            ServletUtility.setList(list, request);
	            ServletUtility.setPageNo(pageNo, request);
	            ServletUtility.setPageSize(pageSize, request);
	            ServletUtility.setBean(bean, request);
	            request.setAttribute("nextListSize", next.size());

	            ServletUtility.forward(getView(), request, response);

	        } catch (ApplicationException e) {
	            e.printStackTrace();
	        }
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("UserListCtl doPost run");
	}

	@Override
	protected String getView() {
		return ORSView.USER_LIST_VIEW ;
	}

}
