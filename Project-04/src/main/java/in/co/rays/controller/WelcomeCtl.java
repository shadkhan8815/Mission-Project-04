package in.co.rays.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.util.ServletUtility;
import org.apache.log4j.Logger;

/**
 * WelcomeCtl handles the welcome page display of the application.
 * It forwards requests to the welcome view.
 * 
 * Author: Shad Khan
 * Version: 1.0
 */
@WebServlet(name = "WelcomeCtl", urlPatterns = {"/WelcomeCtl"})
public class WelcomeCtl extends BaseCtl {

    private static Logger log = Logger.getLogger(WelcomeCtl.class);

    /**
     * Handles GET requests and forwards to the welcome view.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        log.debug("WelcomeCtl doGet run");
        ServletUtility.forward(getView(), request, response);
    }

    /**
     * Handles POST requests for the welcome page.
     * Currently logs the call and does not perform additional actions.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        log.debug("WelcomeCtl doPost run");
    }

    /**
     * Returns the view page for the welcome page.
     *
     * @return String welcome view page
     */
    @Override
    protected String getView() {
        return ORSView.WELCOME_VIEW;
    }
}
