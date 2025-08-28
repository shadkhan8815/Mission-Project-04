package in.co.rays.util;

/**
 * The {@code EmailMessage} class represents an email message object
 * that contains recipient address, subject, message body, and type of message
 * (either HTML or plain text).
 * 
 * <p>It provides getter and setter methods for managing email details.
 * The class can be used in email sending utilities.</p>
 * 
 * <p>Message types:</p>
 * <ul>
 *   <li>{@link #HTML_MSG} - HTML formatted email</li>
 *   <li>{@link #TEXT_MSG} - Plain text email</li>
 * </ul>
 * 
 * Default message type is {@code TEXT_MSG}.
 * 
 */
public class EmailMessage {

    /** Recipient email address */
    private String to;

    /** Subject of the email */
    private String subject;

    /** Message content of the email */
    private String message;

    /** Type of the message (HTML or Text). Default is TEXT_MSG */
    private int messageType = TEXT_MSG;

    /** Constant for HTML message type */
    public static final int HTML_MSG = 1;

    /** Constant for plain text message type */
    public static final int TEXT_MSG = 2;

    /**
     * Default constructor for {@code EmailMessage}.
     */
    public EmailMessage() {
    }

    /**
     * Parameterized constructor to initialize email details.
     *
     * @param to      recipient email address
     * @param subject subject of the email
     * @param message content of the email
     */
    public EmailMessage(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    /**
     * Sets the recipient email address.
     *
     * @param to recipient email address
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Gets the recipient email address.
     *
     * @return recipient email address
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the subject of the email.
     *
     * @param subject subject line
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the subject of the email.
     *
     * @return subject line
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the content of the email message.
     *
     * @param message email body
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the content of the email message.
     *
     * @return email body
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the type of the email message.
     *
     * @param messageType message type (use {@link #HTML_MSG} or {@link #TEXT_MSG})
     */
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    /**
     * Gets the type of the email message.
     *
     * @return message type
     */
    public int getMessageType() {
        return messageType;
    }
}
