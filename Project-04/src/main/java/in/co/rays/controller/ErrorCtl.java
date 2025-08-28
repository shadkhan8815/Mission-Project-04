package in.co.rays.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.util.ServletUtility;
import org.apache.log4j.Logger;

/**
 * ErrorCtl servlet class to handle application error page navigation.
 * <p>
 * This controller forwards the request to the error view whenever an error occurs.
 * </p>
 * 
 * Author: Shad Khan
 * Version: 1.0
 */
@WebServlet("/ErrorCtl")
public class ErrorCtl extends BaseCtl {

    /** Logger for debug and error messages */
    private static Logger log = Logger.getLogger(ErrorCtl.class);

    /**
     * Handles HTTP GET requests and forwards them to the error view.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("ErrorCtl doGet run");
        try {
            ServletUtility.forward(getView(), request, response);
        } catch (Exception e) {
            log.error("Error in ErrorCtl doGet", e);
        }
    }

    /**
     * Handles HTTP POST requests and forwards them to the error view.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("ErrorCtl doPost run");
        try {
            ServletUtility.forward(getView(), request, response);
        } catch (Exception e) {
            log.error("Error in ErrorCtl doPost", e);
        }
    }

    /**
     * Returns the view path of the error page.
     *
     * @return String constant of error view
     */
    @Override
    protected String getView() {
        log.debug("ErrorCtl getView run");
        return ORSView.ERROR_VIEW;
    }
}
