package in.co.rays.util;

import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import in.co.rays.exception.ApplicationException;

/**
 * The {@code EmailUtility} class provides utility methods for sending emails
 * using the JavaMail API.
 * 
 * <p>
 * It reads SMTP server configuration from a resource bundle
 * {@code in.co.rays.resourceBundle.system}, including:
 * </p>
 * <ul>
 *   <li>{@code smtp.server} - SMTP server host</li>
 *   <li>{@code smtp.port} - SMTP server port</li>
 *   <li>{@code email.login} - sender email address</li>
 *   <li>{@code email.pwd} - sender email password</li>
 * </ul>
 * 
 * <p>
 * The class supports sending both HTML and plain text messages.
 * </p>
 */
public class EmailUtility {

    /** Resource bundle for loading SMTP configuration */
    static ResourceBundle rb = ResourceBundle.getBundle("in.co.rays.resourceBundle.system");

    /** SMTP server host name */
    private static final String SMTP_HOST_NAME = rb.getString("smtp.server");

    /** SMTP server port */
    private static final String SMTP_PORT = rb.getString("smtp.port");

    /** Sender email address (login username) */
    private static final String emailFromAddress = rb.getString("email.login");

    /** Sender email password */
    private static final String emailPassword = rb.getString("email.pwd");

    /** Properties object containing mail session configuration */
    private static Properties props = new Properties();

    // Static block to initialize mail server properties
    static {
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.debug", "true");
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
    }

    /**
     * Sends an email using the details provided in the given {@link EmailMessage}.
     * 
     * @param emailMessageDTO the {@code EmailMessage} object containing
     *                        recipient(s), subject, message, and message type
     * @throws ApplicationException if there is an error while sending the email
     */
    public static void sendMail(EmailMessage emailMessageDTO) throws ApplicationException {
        try {
            // Setup mail session
            Session session = Session.getDefaultInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailFromAddress, emailPassword);
                }
            });

            // Create and setup the email message
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(emailFromAddress));
            msg.setRecipients(Message.RecipientType.TO, getInternetAddresses(emailMessageDTO.getTo()));
            msg.setSubject(emailMessageDTO.getSubject());

            // Set content type based on message type
            String contentType = emailMessageDTO.getMessageType() == EmailMessage.HTML_MSG ? "text/html" : "text/plain";
            msg.setContent(emailMessageDTO.getMessage(), contentType);

            // Send the email
            Transport.send(msg);

        } catch (Exception ex) {
            throw new ApplicationException("Email Error: " + ex.getMessage());
        }
    }

    /**
     * Converts a comma-separated string of email addresses into an array of
     * {@link InternetAddress} objects.
     * 
     * @param emails comma-separated email addresses
     * @return array of {@code InternetAddress} objects
     * @throws Exception if an email address is invalid
     */
    private static InternetAddress[] getInternetAddresses(String emails) throws Exception {
        if (emails == null || emails.isEmpty()) {
            return new InternetAddress[0];
        }
        String[] emailArray = emails.split(",");
        InternetAddress[] addresses = new InternetAddress[emailArray.length];
        for (int i = 0; i < emailArray.length; i++) {
            addresses[i] = new InternetAddress(emailArray[i].trim());
        }
        return addresses;
    }
}
