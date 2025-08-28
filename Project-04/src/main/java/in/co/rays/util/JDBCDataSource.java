package in.co.rays.util;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * JDBCDataSource is a utility class that manages JDBC connections using 
 * C3P0 connection pooling. It provides methods to obtain and close database 
 * connections in a safe and efficient way.
 * <p>
 * This class follows the Singleton pattern to ensure only one instance 
 * of the connection pool is created during the application lifecycle.
 * </p>
 * 
 * <p><b>Configuration:</b> Connection parameters are loaded from a 
 * resource bundle {@code in.co.rays.resourceBundle.system}.</p>
 * 
 * Example usage:
 * <pre>
 *     Connection conn = JDBCDataSource.getConnection();
 *     // use connection
 *     JDBCDataSource.closeConnection(conn);
 * </pre>
 * 
 * @author 
 * @version 1.0
 */
public final class JDBCDataSource {

	private static JDBCDataSource jds = null;

	private ComboPooledDataSource cpds = null;

	private static ResourceBundle rb = ResourceBundle.getBundle("in.co.rays.resourceBundle.system");

	/**
	 * Private constructor to initialize the C3P0 ComboPooledDataSource.
	 * Loads database configuration from the resource bundle.
	 */
	private JDBCDataSource() {
		try {
			cpds = new ComboPooledDataSource();
			cpds.setDriverClass(rb.getString("driver"));
			cpds.setJdbcUrl(rb.getString("url"));
			cpds.setUser(rb.getString("username"));
			cpds.setPassword(rb.getString("password"));
			cpds.setInitialPoolSize(Integer.parseInt(rb.getString("initialpoolsize")));
			cpds.setAcquireIncrement(Integer.parseInt(rb.getString("acquireincrement")));
			cpds.setMaxPoolSize(Integer.parseInt(rb.getString("maxpoolsize")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the singleton instance of {@code JDBCDataSource}.
	 *
	 * @return JDBCDataSource instance
	 */
	public static JDBCDataSource getInstance() {
		if (jds == null) {
			jds = new JDBCDataSource();
		}
		return jds;
	}

	/**
	 * Provides a database connection from the connection pool.
	 *
	 * @return a {@link Connection} object, or {@code null} if an error occurs
	 */
	public static Connection getConnection() {
		try {
			return getInstance().cpds.getConnection();
		} catch (SQLException e) {
			return null;
		}
	}

	/**
	 * Closes the given database resources: ResultSet, Statement, and Connection.
	 *
	 * @param conn the database connection to close
	 * @param stmt the SQL statement to close
	 * @param rs   the result set to close
	 */
	public static void closeConnection(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the given database resources: Statement and Connection.
	 *
	 * @param conn the database connection to close
	 * @param stmt the SQL statement to close
	 */
	public static void closeConnection(Connection conn, Statement stmt) {
		closeConnection(conn, stmt, null);
	}

	/**
	 * Closes only the database connection.
	 *
	 * @param conn the database connection to close
	 */
	public static void closeConnection(Connection conn) {
		closeConnection(conn, null);
	}
}
