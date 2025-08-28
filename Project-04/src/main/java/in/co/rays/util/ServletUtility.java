package in.co.rays.util;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import in.co.rays.bean.BaseBean;
import in.co.rays.controller.BaseCtl;
import in.co.rays.controller.ORSView;

/**
 * The {@code ServletUtility} class provides a set of helper methods to simplify 
 * common servlet operations such as forwarding, redirection, setting and getting 
 * request attributes, handling messages, pagination, and exception management.
 * <p>
 * It acts as a utility layer for controllers and servlets in the application.
 * </p>
 * 
 * <p>Example usage:</p>
 * <pre>
 *     ServletUtility.forward("LoginView.jsp", request, response);
 *     ServletUtility.setErrorMessage("Invalid Login", request);
 *     String msg = ServletUtility.getErrorMessage(request);
 * </pre>
 * 
 * @author  
 * @version 1.0
 */
public class ServletUtility {

    /**
     * Forwards the request to the specified page.
     *
     * @param page     the target JSP or controller page
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws IOException
     * @throws ServletException
     */
    public static void forward(String page, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        RequestDispatcher rd = request.getRequestDispatcher(page);
        rd.forward(request, response);
    }

    /**
     * Redirects the request to the specified page.
     *
     * @param page     the target JSP or controller page
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws IOException
     * @throws ServletException
     */
    public static void redirect(String page, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.sendRedirect(page);
    }

    /**
     * Retrieves an error message stored as a request attribute by key.
     *
     * @param property the attribute key
     * @param request  the HttpServletRequest object
     * @return the error message or an empty string if not found
     */
    public static String getErrorMessage(String property, HttpServletRequest request) {
        String val = (String) request.getAttribute(property);
        if (val == null) {
            return "";
        } else {
            return val;
        }
    }

    /**
     * Retrieves a message stored as a request attribute by key.
     *
     * @param property the attribute key
     * @param request  the HttpServletRequest object
     * @return the message or an empty string if not found
     */
    public static String getMessage(String property, HttpServletRequest request) {
        String val = (String) request.getAttribute(property);
        if (val == null) {
            return "";
        } else {
            return val;
        }
    }

    /**
     * Sets an error message in the request scope.
     *
     * @param msg     the error message to set
     * @param request the HttpServletRequest object
     */
    public static void setErrorMessage(String msg, HttpServletRequest request) {
        request.setAttribute(BaseCtl.MSG_ERROR, msg);
    }

    /**
     * Retrieves the error message stored in the request scope.
     *
     * @param request the HttpServletRequest object
     * @return the error message or an empty string if not found
     */
    public static String getErrorMessage(HttpServletRequest request) {
        String val = (String) request.getAttribute(BaseCtl.MSG_ERROR);
        if (val == null) {
            return "";
        } else {
            return val;
        }
    }

    /**
     * Sets a success message in the request scope.
     *
     * @param msg     the success message to set
     * @param request the HttpServletRequest object
     */
    public static void setSuccessMessage(String msg, HttpServletRequest request) {
        request.setAttribute(BaseCtl.MSG_SUCCESS, msg);
    }

    /**
     * Retrieves the success message stored in the request scope.
     *
     * @param request the HttpServletRequest object
     * @return the success message or an empty string if not found
     */
    public static String getSuccessMessage(HttpServletRequest request) {
        String val = (String) request.getAttribute(BaseCtl.MSG_SUCCESS);
        if (val == null) {
            return "";
        } else {
            return val;
        }
    }

    /**
     * Stores a bean object in the request scope.
     *
     * @param bean    the BaseBean object to store
     * @param request the HttpServletRequest object
     */
    public static void setBean(BaseBean bean, HttpServletRequest request) {
        request.setAttribute("bean", bean);
    }

    /**
     * Retrieves a bean object from the request scope.
     *
     * @param request the HttpServletRequest object
     * @return the BaseBean object or null if not found
     */
    public static BaseBean getBean(HttpServletRequest request) {
        return (BaseBean) request.getAttribute("bean");
    }

    /**
     * Retrieves a request parameter by key.
     *
     * @param property the request parameter name
     * @param request  the HttpServletRequest object
     * @return the parameter value or an empty string if not found
     */
    public static String getParameter(String property, HttpServletRequest request) {
        String val = (String) request.getParameter(property);
        if (val == null) {
            return "";
        } else {
            return val;
        }
    }

    /**
     * Stores a list of objects in the request scope.
     *
     * @param list    the list of objects
     * @param request the HttpServletRequest object
     */
    public static void setList(List list, HttpServletRequest request) {
        request.setAttribute("list", list);
    }

    /**
     * Retrieves a list of objects from the request scope.
     *
     * @param request the HttpServletRequest object
     * @return the list of objects or null if not found
     */
    public static List getList(HttpServletRequest request) {
        return (List) request.getAttribute("list");
    }

    /**
     * Stores the current page number for pagination in the request scope.
     *
     * @param pageNo  the page number
     * @param request the HttpServletRequest object
     */
    public static void setPageNo(int pageNo, HttpServletRequest request) {
        request.setAttribute("pageNo", pageNo);
    }

    /**
     * Retrieves the current page number for pagination from the request scope.
     *
     * @param request the HttpServletRequest object
     * @return the page number
     */
    public static int getPageNo(HttpServletRequest request) {
        return (Integer) request.getAttribute("pageNo");
    }

    /**
     * Stores the page size for pagination in the request scope.
     *
     * @param pageSize the number of records per page
     * @param request  the HttpServletRequest object
     */
    public static void setPageSize(int pageSize, HttpServletRequest request) {
        request.setAttribute("pageSize", pageSize);
    }

    /**
     * Retrieves the page size for pagination from the request scope.
     *
     * @param request the HttpServletRequest object
     * @return the page size
     */
    public static int getPageSize(HttpServletRequest request) {
        return (Integer) request.getAttribute("pageSize");
    }
    
    /**
     * Handles an exception by storing it in the request scope and redirecting 
     * the user to the error controller view.
     *
     * @param e        the exception to handle
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws IOException
     * @throws ServletException
     */
    public static void handleException(Exception e, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.setAttribute("exception", e);
        response.sendRedirect(ORSView.ERROR_CTL);
    }
}
