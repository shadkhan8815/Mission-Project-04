package in.co.rays.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

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

/**
 * LoginCtl is a controller responsible for handling login, logout, and
 * redirection to user registration. It validates login credentials,
 * authenticates the user via UserModel, manages session data, and forwards or
 * redirects to appropriate views based on operations.
 * <p>
 * Supported operations:
 * <ul>
 * <li>Sign In</li>
 * <li>Sign Up</li>
 * <li>Logout</li>
 * </ul>
 * </p>
 * 
 * Logging:
 * <ul>
 * <li>DEBUG logs are used to trace method flow and data population.</li>
 * <li>ERROR logs are used to capture validation errors, failed authentication,
 * and exceptions.</li>
 * </ul>
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebServlet(name = "LoginCtl", urlPatterns = { "/LoginCtl" })
public class LoginCtl extends BaseCtl {

	/** Constant for Sign In operation */
	public static final String OP_SIGN_IN = "Sign In";
	/** Constant for Sign Up operation */
	public static final String OP_SIGN_UP = "Sign Up";
	/** Constant for Logout operation */
	public static final String OP_LOG_OUT = "Logout";

	/** Logger instance for LoginCtl */
	private static final Logger log = Logger.getLogger(LoginCtl.class);

	/**
	 * Validates user input during login. Ensures login ID and password are provided
	 * and that the login ID is a valid email format. Skip validation if the
	 * operation is Sign Up or Logout.
	 * 
	 * Logging:
	 * <ul>
	 * <li>DEBUG → when validation starts/ends.</li>
	 * <li>ERROR → when validation fails for login or password.</li>
	 * </ul>
	 *
	 * @param request the HttpServletRequest containing user input
	 * @return true if validation passes, false otherwise
	 */
	@Override
	protected boolean validate(HttpServletRequest request) {
		System.out.println("LoginCtl validate Run");
		log.debug("validate() started");

		boolean isValid = true;
		String op = DataUtility.getString(request.getParameter("operation"));

		if (OP_SIGN_UP.equalsIgnoreCase(op) || OP_LOG_OUT.equalsIgnoreCase(op)) {
			log.debug("Skipping validation for operation: " + op);
			return isValid;
		}

		if (DataValidator.isNull(request.getParameter("login"))) {
			System.out.println("DV isNull log");
			log.error("Validation failed: Login Id is required");
			request.setAttribute("login", PropertyReader.getValue("error.require", "Login Id"));
			isValid = false;
		} else if (!DataValidator.isEmail(request.getParameter("login"))) {
			System.out.println("DV isEmail");
			log.error("Validation failed: Invalid email format");
			request.setAttribute("login", PropertyReader.getValue("error.email", "login"));
			isValid = false;
		}

		if (DataValidator.isNull(request.getParameter("password"))) {
			System.out.println("DV isNull pass");
			log.error("Validation failed: Password is required");
			request.setAttribute("password", PropertyReader.getValue("error.require", "Password"));
			isValid = false;
		}

		log.debug("validate() ended with isValid=" + isValid);
		return isValid;
	}

	/**
	 * Populates a UserBean object with request parameters (id, login, password).
	 * 
	 * Logging:
	 * <ul>
	 * <li>DEBUG → when bean population starts and ends.</li>
	 * </ul>
	 *
	 * @param request the HttpServletRequest containing form data
	 * @return a populated UserBean object
	 */
	@Override
	protected BaseBean populateBean(HttpServletRequest request) {
		System.out.println("LoginCtl populateBean run");
		log.debug("Populating UserBean with request parameters");

		UserBean bean = new UserBean();
		bean.setId(DataUtility.getLong(request.getParameter("id")));
		bean.setLogin(DataUtility.getString(request.getParameter("login")));
		bean.setPassword(DataUtility.getString(request.getParameter("password")));

		log.debug("Populated bean: " + bean);
		return bean;
	}

	/**
	 * Handles HTTP GET requests. If the operation is Logout, invalidates the
	 * session and forwards to login view with a success message. Otherwise, simply
	 * forwards to login view.
	 * 
	 * Logging:
	 * <ul>
	 * <li>DEBUG → start/end of doGet, logout flow.</li>
	 * </ul>
	 *
	 * @param request  the HttpServletRequest object
	 * @param response the HttpServletResponse object
	 * @throws ServletException if a servlet error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.debug("doGet started");

		HttpSession session = request.getSession();
		String op = DataUtility.getString(request.getParameter("operation"));

		if (OP_LOG_OUT.equals(op)) {
			log.debug("Operation: Logout");
			session.invalidate();
			ServletUtility.setSuccessMessage("Logout Successfully!", request);
			ServletUtility.forward(getView(), request, response);
			log.debug("User logged out successfully");
			return;
		}
		ServletUtility.forward(getView(), request, response);
		log.debug("doGet ended");
	}

	/**
	 * Handles HTTP POST requests. Processes login authentication, user session
	 * creation, role assignment, and redirection. Also supports user registration
	 * redirection.
	 * 
	 * Logging:
	 * <ul>
	 * <li>DEBUG → method start/end, authentication flow, successful login, role
	 * assignment.</li>
	 * <li>ERROR → failed authentication or ApplicationException.</li>
	 * </ul>
	 *
	 * @param request  the HttpServletRequest object
	 * @param response the HttpServletResponse object
	 * @throws ServletException if a servlet error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.debug("doPost started");

		HttpSession session = request.getSession();
		String op = DataUtility.getString(request.getParameter("operation"));
		log.debug("Operation = " + op);

		UserModel model = new UserModel();
		RoleModel Rmodel = new RoleModel();

		if (OP_SIGN_IN.equalsIgnoreCase(op)) {
			UserBean bean = (UserBean) populateBean(request);

			String str = request.getParameter("uri");

			try {
				log.debug("Authenticating user: " + bean.getLogin());
				bean = model.authenticate(bean.getLogin(), bean.getPassword());

				if (bean != null) {
					log.debug("Authentication successful for user: " + bean.getLogin());
					session.setAttribute("user", bean);

					RoleBean Rbean = Rmodel.findByPk(bean.getRoleId());
					if (Rbean != null) {
						log.debug("Role found: " + Rbean.getName());
						session.setAttribute("role", Rbean.getName());
					}
					if ("null".equalsIgnoreCase(str)) {
						ServletUtility.redirect(ORSView.WELCOME_CTL, request, response);
						return;
					} else {
						ServletUtility.redirect(str, request, response);
						return;
					}
				} else {
					log.error("Authentication failed for login: " + request.getParameter("login"));
					bean = (UserBean) populateBean(request);
					ServletUtility.setBean(bean, request);
					ServletUtility.setErrorMessage("Invalid LoginId and Password", request);
				}
			} catch (ApplicationException e) {
				log.error("ApplicationException in doPost", e);
				return;
			}

		} else if (OP_SIGN_UP.equalsIgnoreCase(op)) {
			log.debug("Redirecting to User Registration Controller");
			ServletUtility.redirect(ORSView.USER_REGISTRATION_CTL, request, response);
			return;
		}
		ServletUtility.forward(getView(), request, response);
		log.debug("doPost ended");
	}

	/**
	 * Returns the path of the login view (JSP).
	 *
	 * @return the login view path as String
	 */
	@Override
	protected String getView() {
		return ORSView.LOGIN_VIEW;
	}

}
