package in.co.rays.controller;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import in.co.rays.util.ServletUtility;

/**
 * FrontController is a servlet filter that intercepts requests to the
 * application controllers. It checks whether a user session exists or not. If
 * no session is found, it redirects the user to the login page with an
 * appropriate error message.
 * <p>
 * It applies to all URLs under <code>/ctl/*</code> and <code>/doc/*</code>
 * paths.
 * </p>
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebFilter(filterName = "FrontCtl", urlPatterns = { "/ctl/*", "/doc/*" })
public class FrontController implements Filter {

	private static final Logger log = Logger.getLogger(FrontController.class);

	/**
	 * This method performs filtering of requests. It checks whether the user is
	 * logged in by verifying the session attribute. If the session does not contain
	 * a user, the request is forwarded to the login view with an error message.
	 *
	 * @param req   the ServletRequest object
	 * @param resp  the ServletResponse object
	 * @param chain the FilterChain to pass control to the next filter or resource
	 * @throws IOException      if an I/O error occurs
	 * @throws ServletException if a servlet error occurs
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.debug("FrontController doFilter() started");

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = req.getSession();

		String uri = req.getRequestURI();
		request.setAttribute("uri", uri);

		if (session.getAttribute("user") == null) {
			log.error("Session expired. Redirecting to login page.");
			request.setAttribute("error", "Your session is expired. Please login again!!");
			ServletUtility.forward(ORSView.LOGIN_VIEW, req, resp);
			return;
		} else {
			log.debug("Valid session found. Proceeding with filter chain.");
			chain.doFilter(request, response);
		}

		log.debug("FrontController doFilter() ended");
	}

	/**
	 * Initializes the filter. This implementation does not perform any specific
	 * initialization logic.
	 *
	 * @param conf the FilterConfig object containing configuration info
	 * @throws ServletException if an initialization error occurs
	 */
	public void init(FilterConfig conf) throws ServletException {
		log.debug("FrontController initialized");
	}

	/**
	 * Destroys the filter. This implementation does not perform any specific
	 * resource cleanup.
	 */
	public void destroy() {
		log.debug("FrontController destroyed");
	}
}
