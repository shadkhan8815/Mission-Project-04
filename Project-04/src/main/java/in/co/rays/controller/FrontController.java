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
import in.co.rays.util.ServletUtility;

/**
 * FrontController is a servlet filter that intercepts requests to the 
 * application controllers. It checks whether a user session exists or not. 
 * If no session is found, it redirects the user to the login page 
 * with an appropriate error message.
 * <p>
 * It applies to all URLs under <code>/ctl/*</code> and <code>/doc/*</code> paths.
 * </p>
 * 
 * @author Shad Khan
 * @version 1.0
 */
@WebFilter(filterName = "FrontCtl", urlPatterns = { "/ctl/*", "/doc/*" })
public class FrontController implements Filter {

    /**
     * This method performs filtering of requests. 
     * It checks whether the user is logged in by verifying 
     * the session attribute. If the session does not contain a user, 
     * the request is forwarded to the login view with an error message.
     *
     * @param req   the ServletRequest object
     * @param resp  the ServletResponse object
     * @param chain the FilterChain to pass control to the next filter or resource
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("Fctl Do filter");

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession();

        if (session.getAttribute("user") == null) {
            // request.setAttribute("message", " Your Session has been Expired... Please
            // Login Again");
            ServletUtility.setErrorMessage(" Your Session has been Expired... Please Login Again", request);
            // Set the URI
            String str = request.getRequestURI();

            request.setAttribute("uri", str);
            System.out.println("URI" + str);
            ServletUtility.forward(ORSView.LOGIN_VIEW, request, response);
            return;
        } else {
            chain.doFilter(req, resp);
        }
    }

    /**
     * Initializes the filter. This implementation does not perform 
     * any specific initialization logic.
     *
     * @param conf the FilterConfig object containing configuration info
     * @throws ServletException if an initialization error occurs
     */
    public void init(FilterConfig conf) throws ServletException {
    }

    /**
     * Destroys the filter. This implementation does not perform 
     * any specific resource cleanup.
     */
    public void destroy() {
    }
}
