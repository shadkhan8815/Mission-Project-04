package in.co.rays.util;

import java.util.ResourceBundle;

/**
 * The {@code PropertyReader} class is a utility class that reads values from 
 * a resource bundle file. It supports fetching simple key values as well as 
 * formatted messages with single or multiple parameter replacements.
 * <p>
 * The resource bundle file is located at: {@code in.co.rays.resourceBundle.system}.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     String value = PropertyReader.getValue("error.require");
 *     String msg = PropertyReader.getValue("error.require", "loginId");
 *     String[] params = {"Roll No", "Student Name"};
 *     String multiMsg = PropertyReader.getValue("error.multipleFields", params);
 * </pre>
 * 
 * This allows configuration and error messages to be externalized in 
 * a properties file for better internationalization and maintainability.
 * 
 * @author  
 * @version 1.0
 */
public class PropertyReader {

	private static ResourceBundle rb = ResourceBundle.getBundle("in.co.rays.resourceBundle.system");

	/**
	 * Returns the value associated with the given key from the resource bundle.
	 * If the key is not found, the key itself is returned as the value.
	 *
	 * @param key the key to look up in the resource bundle
	 * @return the value associated with the given key, or the key itself if not found
	 */
	public static String getValue(String key) {

		String val = null;

		try {
			val = rb.getString(key); // {0} is required
		} catch (Exception e) {
			val = key;
		}
		return val;
	}

	/**
	 * Returns the value associated with the given key, replacing 
	 * the placeholder "{0}" with the provided parameter.
	 *
	 * @param key   the key to look up in the resource bundle
	 * @param param the parameter value to replace the placeholder "{0}"
	 * @return the formatted message string
	 */
	public static String getValue(String key, String param) {
		String msg = getValue(key); // {0} is required
		msg = msg.replace("{0}", param);
		return msg;
	}

	/**
	 * Returns the value associated with the given key, replacing 
	 * multiple placeholders ("{0}", "{1}", etc.) with the provided parameters.
	 *
	 * @param key    the key to look up in the resource bundle
	 * @param params an array of parameter values to replace placeholders
	 * @return the formatted message string
	 */
	public static String getValue(String key, String[] params) {
		String msg = getValue(key);
		for (int i = 0; i < params.length; i++) {
			msg = msg.replace("{" + i + "}", params[i]);
		}
		return msg;
	}

	/**
	 * Main method for testing the {@code PropertyReader} functionality.
	 * Demonstrates retrieving values and replacing parameters in messages.
	 *
	 * @param args command line arguments (not used)
	 */
	public static void main(String[] args) {

		System.out.println("Single key example:");
		System.out.println(PropertyReader.getValue("error.require"));

		System.out.println("\nSingle parameter replacement example:");
		System.out.println(PropertyReader.getValue("error.require", "loginId"));

		System.out.println("\nMultiple parameter replacement example:");
		String[] params = { "Roll No", "Student Name" };
		System.out.println(PropertyReader.getValue("error.multipleFields", params));
	}
}
