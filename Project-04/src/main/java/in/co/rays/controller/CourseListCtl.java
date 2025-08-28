package in.co.rays.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.CourseBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.CourseModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

import org.apache.log4j.Logger;

/**
 * Controller to handle Course List operations such as search, pagination,
 * delete, reset, back, and preload of course data.
 * 
 * URL Mapping: /CourseListCtl
 * 
 * Provides interaction with CourseModel for database operations and uses
 * ServletUtility for request/response handling.
 * 
 * Author: Shad Khan Version: 1.0
 */
@WebServlet(name = "/CourseListCtl", urlPatterns = { "/CourseListCtl" })
public class CourseListCtl extends BaseCtl {

	/** Logger for debug and error messages */
	private static Logger log = Logger.getLogger(CourseListCtl.class);

	/**
	 * Preloads the course list and sets it as a request attribute. This is used for
	 * dropdowns or selection lists in the view.
	 * 
	 * @param request HttpServletRequest object
	 */
	@Override
	protected void preload(HttpServletRequest request) {
		log.debug("CourseListCtl preload run");

		CourseModel courseModel = new CourseModel();

		try {
			List<CourseBean> courseList = courseModel.list();
			request.setAttribute("courseList", courseList);
		} catch (ApplicationException e) {
			log.error("Error in CourseListCtl preload", e);
			e.printStackTrace();
		}
	}

	/**
	 * Populates the CourseBean with request parameters.
	 * 
	 * @param request HttpServletRequest object
	 * @return CourseBean instance populated with request data
	 */
	@Override
	protected BaseBean populateBean(HttpServletRequest request) {
		log.debug("CourseListCtl populateBean run");

		CourseBean bean = new CourseBean();
		 bean.setId(DataUtility.getLong(request.getParameter("courseId")));
		 System.out.println(request.getParameter("courseId"));
		 bean.setName(DataUtility.getString(request.getParameter("name")));
		
		return bean;
	}

	/**
	 * Handles HTTP GET requests. Performs search and pagination for course list.
	 * 
	 * @param request  HttpServletRequest object
	 * @param response HttpServletResponse object
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.debug("CourseListCtl doGet run");

		int pageNo = 1;
		int pageSize = DataUtility.getInt(PropertyReader.getValue("page.size"));

		CourseModel model = new CourseModel();
		CourseBean bean = (CourseBean) populateBean(request);

		try {
			List<CourseBean> list = model.search(bean, pageNo, pageSize);
			List<CourseBean> next = model.search(bean, pageNo + 1, pageSize);

			if (list == null || list.isEmpty()) {
				ServletUtility.setErrorMessage("Record Not Found", request);
			}

			ServletUtility.setList(list, request);
			ServletUtility.setPageNo(pageNo, request);
			ServletUtility.setPageSize(pageSize, request);
			ServletUtility.setBean(bean, request);
			request.setAttribute("nextListSize", next.size());

			ServletUtility.forward(getView(), request, response);
		} catch (ApplicationException e) {
			log.error("Error in CourseListCtl doGet", e);
			e.printStackTrace();
		}
	}

	/**
	 * Handles HTTP POST requests. Performs operations like Search, Next, Previous,
	 * New, Delete, Reset, and Back on course list.
	 * 
	 * @param request  HttpServletRequest object
	 * @param response HttpServletResponse object
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.debug("CourseListCtl doPost run");

		List list = null;
		List next = null;

		int pageNo = DataUtility.getInt(request.getParameter("pageNo"));
		int pageSize = DataUtility.getInt(request.getParameter("pageSize"));

		pageNo = (pageNo == 0) ? 1 : pageNo;
		pageSize = (pageSize == 0) ? DataUtility.getInt(PropertyReader.getValue("page.size")) : pageSize;

		CourseBean bean = (CourseBean) populateBean(request);
		CourseModel model = new CourseModel();

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
					CourseBean deletebean = new CourseBean();
					for (String id : ids) {
						deletebean.setId(DataUtility.getInt(id));
						model.delete(deletebean);
						ServletUtility.setSuccessMessage("Course is deleted successfully", request);
					}
				} else {
					ServletUtility.setErrorMessage("Select at least one record", request);
				}
			} else if (OP_RESET.equalsIgnoreCase(op) || OP_BACK.equalsIgnoreCase(op)) {
				ServletUtility.redirect(ORSView.COURSE_LIST_CTL, request, response);
				return;
			}

			list = model.search(bean, pageNo, pageSize);
			next = model.search(bean, pageNo + 1, pageSize);

			if (!OP_DELETE.equalsIgnoreCase(op) && (list == null || list.isEmpty())) {
				ServletUtility.setErrorMessage("No record found ", request);
			}

			ServletUtility.setList(list, request);
			ServletUtility.setPageNo(pageNo, request);
			ServletUtility.setPageSize(pageSize, request);
			ServletUtility.setBean(bean, request);
			request.setAttribute("nextListSize", next.size());

			ServletUtility.forward(getView(), request, response);
		} catch (Exception e) {
			log.error("Error in CourseListCtl doPost", e);
			e.printStackTrace();
		}
	}

	/**
	 * Returns the view page for course list.
	 * 
	 * @return String constant of view path
	 */
	@Override
	protected String getView() {
		return ORSView.COURSE_LIST_VIEW;
	}

}
