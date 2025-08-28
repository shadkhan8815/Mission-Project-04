package in.co.rays.util;

import java.util.HashMap;

/**
 * Utility class for building email messages in HTML format.
 * Provides methods to generate user registration, 
 * forgot password, and change password email messages.
 * 
 * Each method accepts a HashMap<String, String> containing user details.
 * 
 * Expected keys in the map:
 * <ul>
 *   <li>login - the user's login ID</li>
 *   <li>password - the user's password</li>
 *   <li>firstName - the user's first name (for personalized messages)</li>
 *   <li>lastName - the user's last name (for personalized messages)</li>
 * </ul>
 * 
 * @author 
 */
public class EmailBuilder {

    /**
     * Builds a user registration confirmation email message in HTML format.
     *
     * @param map a HashMap containing user details (login, password)
     * @return HTML email message as a string
     */
    public static String getUserRegistrationMessage(HashMap<String, String> map) {
        StringBuilder msg = new StringBuilder();
        msg.append("<HTML><BODY>");
        msg.append("<H1>Welcome to ORS, ").append(map.get("login")).append("!</H1>");
        msg.append("<P>Your registration is successful. You can now log in and manage your account.</P>");
        msg.append("<P><B>Login Id: ").append(map.get("login")).append("<BR>Password: ").append(map.get("password"))
                .append("</B></P>");
        msg.append("<P>Change your password after logging in for security reasons.</P>");
        msg.append("<P>For support, contact +91 98273 60504 or hrd@sunrays.co.in.</P>");
        msg.append("</BODY></HTML>");
        return msg.toString();
    }

    /**
     * Builds a forgot password recovery email message in HTML format.
     *
     * @param map a HashMap containing user details (firstName, lastName, login, password)
     * @return HTML email message as a string
     */
    public static String getForgetPasswordMessage(HashMap<String, String> map) {
        StringBuilder msg = new StringBuilder();
        msg.append("<HTML><BODY>");
        msg.append("<H1>Password Recovery</H1>");
        msg.append("<P>Hello, ").append(map.get("firstName")).append(" ").append(map.get("lastName")).append(".</P>");
        msg.append("<P>Your login details are:</P>");
        msg.append("<P><B>Login Id: ").append(map.get("login")).append("<BR>Password: ").append(map.get("password"))
                .append("</B></P>");
        msg.append("</BODY></HTML>");
        return msg.toString();
    }

    /**
     * Builds a change password confirmation email message in HTML format.
     *
     * @param map a HashMap containing user details (firstName, lastName, login, password)
     * @return HTML email message as a string
     */
    public static String getChangePasswordMessage(HashMap<String, String> map) {
        StringBuilder msg = new StringBuilder();
        msg.append("<HTML><BODY>");
        msg.append("<H1>Password Changed Successfully</H1>");
        msg.append("<P>Dear ").append(map.get("firstName")).append(" ").append(map.get("lastName"))
                .append(", your password has been updated.</P>");
        msg.append("<P>Your updated login details are:</P>");
        msg.append("<P><B>Login Id: ").append(map.get("login")).append("<BR>New Password: ").append(map.get("password"))
                .append("</B></P>");
        msg.append("</BODY></HTML>");
        return msg.toString();
    }
}
