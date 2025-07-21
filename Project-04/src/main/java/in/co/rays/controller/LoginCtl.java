package in.co.rays.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import in.co.rays.bean.BaseBean;
import in.co.rays.bean.RoleBean;
import in.co.rays.bean.UserBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.model.RoleModel;
import in.co.rays.model.UserModel;
import in.co.rays.util.DataUtility;
import in.co.rays.util.DataValidator;
import in.co.rays.util.PropertyReader;
import in.co.rays.util.ServletUtility;

@WebServlet(name = "LoginCtl", urlPatterns = {"/LoginCtl"})
public class LoginCtl extends BaseCtl {
	
	public static final String OP_SIGN_IN = "Sign In";
	public static final String OP_SIGN_UP = "Sign Up";
	public static final String OP_LOG_OUT = "Logout";
	
	@Override
	protected boolean validate(HttpServletRequest request) {
		System.out.println("LoginCtl validate Run");
		
		boolean isValid = true ;
		
		String op = DataUtility.getString(request.getParameter("operation"));
		
		if (OP_SIGN_UP.equalsIgnoreCase(op) || OP_LOG_OUT.equalsIgnoreCase(op)) {
			
			return isValid ;
			
		}
		
		if (DataValidator.isNull(request.getParameter("login"))) {
			System.out.println("DV isNull log");
			request.setAttribute("login", PropertyReader.getValue("error.require", "Login Id"));
			isValid = false ;
		}
		else if (!DataValidator.isEmail(request.getParameter("login"))) {
			System.out.println("DV isEmail");
			request.setAttribute("login",PropertyReader.getValue("error.email", "login"));
			isValid = false ;
		}
		
		if (DataValidator.isNull(request.getParameter("password"))) {
			System.out.println("DV isNull pass");
			request.setAttribute("password", PropertyReader.getValue("error.require", "Password"));
			isValid = false ;
		}
		
		return isValid ;
	}
	
	@Override
	protected BaseBean populateBean(HttpServletRequest request) {
		System.out.println("LoginCtl populateBean run");
		
		UserBean bean = new UserBean() ;
		
		bean.setId(DataUtility.getLong(request.getParameter("id")));
		bean.setLogin(DataUtility.getString(request.getParameter("login")));
		bean.setPassword(DataUtility.getString(request.getParameter("password")));
		
		return bean ;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("LoginCtl doGet Run");
		
		HttpSession session = request.getSession();

		String op = DataUtility.getString(request.getParameter("operation"));

		if (OP_LOG_OUT.equals(op)) {
			session.invalidate();
			ServletUtility.setSuccessMessage("Logout Successfully!", request);
			ServletUtility.forward(getView(), request, response);
			return;
		}
		ServletUtility.forward(getView(), request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("LoginCtl doPost Run");

		HttpSession session = request.getSession();
		
		String op = DataUtility.getString(request.getParameter("operation"));
		
		UserModel model = new UserModel() ;
		RoleModel Rmodel = new RoleModel();
		
		if (OP_SIGN_IN.equalsIgnoreCase(op)) {
			
			UserBean bean = (UserBean) populateBean(request);
			
			try {
				bean = model.authenticate(bean.getLogin(), bean.getPassword());
				
			     if (bean != null) {
			    	 session.setAttribute("user", bean);
			    	 
			    	RoleBean Rbean = Rmodel.findByPk(bean.getRoleId());
			     
			    	if (Rbean != null) {
			    		session.setAttribute("role", Rbean.getName());
			    	}
			    	ServletUtility.redirect(ORSView.WELCOME_CTL, request, response);
			    	return ;
			     }else {
					bean = (UserBean)populateBean(request);
					ServletUtility.setBean(bean, request);
					ServletUtility.setErrorMessage("Invalid LoginId and Password", request);
				}
			} catch (ApplicationException e) {
				e.printStackTrace();
				return ;
			}
			
		}else if (OP_SIGN_UP.equalsIgnoreCase(op)) {
			ServletUtility.redirect(ORSView.USER_REGISTRATION_CTL, request, response);
			return;
		}
		ServletUtility.forward(getView(), request, response);
		
	}
	
	@Override
	protected String getView() {
		return ORSView.LOGIN_VIEW;
	}

}
