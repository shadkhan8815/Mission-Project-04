package in.co.rays.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import in.co.rays.bean.UserBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DatabaseException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.exception.RecordNotFoundException;
import in.co.rays.util.EmailBuilder;
import in.co.rays.util.EmailMessage;
import in.co.rays.util.EmailUtility;
import in.co.rays.util.JDBCDataSource;

/**
 * UserModel is a Model class responsible for performing CRUD operations and 
 * user-related business logic for the st_user table in the database.
 * 
 * <p>This class provides methods for:
 * <ul>
 * <li>Adding, updating, deleting users</li>
 * <li>Finding users by primary key or login</li>
 * <li>Authenticating users</li>
 * <li>Searching users with pagination</li>
 * <li>Managing password: change, reset, forget</li>
 * <li>Registering users with email notifications</li>
 * </ul>
 * </p>
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class UserModel {

    private static Logger log = Logger.getLogger(UserModel.class);

    /**
     * Gets the next primary key value from st_user table.
     * 
     * @return next primary key value
     * @throws DatabaseException if any database error occurs
     */
    public Integer nextPk() throws DatabaseException {

        log.debug("Model nextPK Started");
        Connection conn = null;
        int pk = 0;

        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select max(id) from st_user");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pk = rs.getInt(1);
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            log.error("Exception in getting PK", e);
            throw new DatabaseException("Exception : Exception in getting PK");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }

        log.debug("Model nextPK End");
        return pk + 1;
    }

    /**
     * Adds a new User to the database.
     * 
     * @param bean UserBean containing user details
     * @return primary key of newly added user
     * @throws ApplicationException if any application-level error occurs
     * @throws DuplicateRecordException if login id already exists
     */
    public long add(UserBean bean) throws ApplicationException, DuplicateRecordException {
        log.debug("Add method started for User: " + bean.getLogin());
        Connection conn = null;
        int pk = 0;

        UserBean existbean = findByLogin(bean.getLogin());

        if (existbean != null) {
            log.error("Duplicate login ID: " + bean.getLogin());
            throw new DuplicateRecordException("Login Id already exists");
        }

        try {
            pk = nextPk();
          // factory desing pattern...  
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn
                    .prepareStatement("insert into st_user values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, pk);
            pstmt.setString(2, bean.getFirstName());
            pstmt.setString(3, bean.getLastName());
            pstmt.setString(4, bean.getLogin());
            pstmt.setString(5, bean.getPassword());
            pstmt.setDate(6, new java.sql.Date(bean.getDob().getTime()));
            pstmt.setString(7, bean.getMobileNo());
            pstmt.setLong(8, bean.getRoleId());
            pstmt.setString(9, bean.getGender());
            pstmt.setString(10, bean.getCreatedBy());
            pstmt.setString(11, bean.getModifiedBy());
            pstmt.setTimestamp(12, bean.getCreatedDatetime());
            pstmt.setTimestamp(13, bean.getModifiedDatetime());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
            log.debug("User added successfully with PK: " + pk);
        } catch (Exception e) {
            try {
                conn.rollback();
                log.error("Rollback in Add User", e);
            } catch (Exception ex) {
                log.error("Exception during rollback", ex);
                throw new ApplicationException("Exception : add rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception : Exception in add User");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return pk;
    }

    /**
     * Updates an existing User in the database.
     * 
     * @param bean UserBean containing updated details
     * @throws ApplicationException if any application-level error occurs
     * @throws DuplicateRecordException if login id already exists
     */
    public void update(UserBean bean) throws ApplicationException, DuplicateRecordException {
        log.debug("Update method started for User: " + bean.getLogin());
        Connection conn = null;

        UserBean beanExist = findByLogin(bean.getLogin());

        if (beanExist != null && !(beanExist.getId() == bean.getId())) {
            log.error("Duplicate login ID on update: " + bean.getLogin());
            throw new DuplicateRecordException("Login Id is already exist");
        }

        try {
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement(
                    "update st_user set first_name = ?, last_name = ?, login = ?, password = ?, dob = ?, mobile_no = ?, role_id = ?, gender = ?, created_by = ?, modified_by = ?, created_datetime = ?, modified_datetime = ? where id = ?");
            pstmt.setString(1, bean.getFirstName());
            pstmt.setString(2, bean.getLastName());
            pstmt.setString(3, bean.getLogin());
            pstmt.setString(4, bean.getPassword());
            pstmt.setDate(5, new java.sql.Date(bean.getDob().getTime()));
            pstmt.setString(6, bean.getMobileNo());
            pstmt.setLong(7, bean.getRoleId());
            pstmt.setString(8, bean.getGender());
            pstmt.setString(9, bean.getCreatedBy());
            pstmt.setString(10, bean.getModifiedBy());
            pstmt.setTimestamp(11, bean.getCreatedDatetime());
            pstmt.setTimestamp(12, bean.getModifiedDatetime());
            pstmt.setLong(13, bean.getId());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
            log.debug("User updated successfully: " + bean.getLogin());
        } catch (Exception e) {
            log.error("Exception in updating User", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback exception in update", ex);
                throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception in updating User ");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
    }

    /**
     * Deletes a User from the database.
     * 
     * @param bean UserBean containing user id to be deleted
     * @throws ApplicationException if any application-level error occurs
     */
    public void delete(UserBean bean) throws ApplicationException {
        log.debug("Delete method started for User ID: " + bean.getId());
        Connection conn = null;

        try {
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement("delete from st_user where id = ?");
            pstmt.setLong(1, bean.getId());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
            log.debug("User deleted successfully: " + bean.getId());
        } catch (Exception e) {
            log.error("Exception in deleting User", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback exception in delete", ex);
                throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception : Exception in delete User");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
    }

    /**
     * Finds a User by primary key.
     * 
     * @param pk primary key
     * @return UserBean if found, otherwise null
     * @throws ApplicationException if any application-level error occurs
     */
    public UserBean findByPk(long pk) throws ApplicationException {
        log.debug("FindByPK method started for ID: " + pk);
        UserBean bean = null;
        Connection conn = null;

        StringBuffer sql = new StringBuffer("select * from st_user where id = ?");

        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, pk);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new UserBean();
                bean.setId(rs.getLong(1));
                bean.setFirstName(rs.getString(2));
                bean.setLastName(rs.getString(3));
                bean.setLogin(rs.getString(4));
                bean.setPassword(rs.getString(5));
                bean.setDob(rs.getDate(6));
                bean.setMobileNo(rs.getString(7));
                bean.setRoleId(rs.getLong(8));
                bean.setGender(rs.getString(9));
                bean.setCreatedBy(rs.getString(10));
                bean.setModifiedBy(rs.getString(11));
                bean.setCreatedDatetime(rs.getTimestamp(12));
                bean.setModifiedDatetime(rs.getTimestamp(13));
            }
            rs.close();
            pstmt.close();
            log.debug("User fetched by PK successfully: " + pk);
        } catch (Exception e) {
            log.error("Exception in getting User by PK", e);
            throw new ApplicationException("Exception : Exception in getting User by pk");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return bean;
    }

    /**
     * Finds a User by login id.
     * 
     * @param login login id
     * @return UserBean if found, otherwise null
     * @throws ApplicationException if any application-level error occurs
     */
    public UserBean findByLogin(String login) throws ApplicationException {
        log.debug("FindByLogin method started for login: " + login);
        StringBuffer sql = new StringBuffer("select * from st_user where login = ?");

        UserBean bean = null;
        Connection conn = null;

        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new UserBean();
                bean.setId(rs.getLong(1));
                bean.setFirstName(rs.getString(2));
                bean.setLastName(rs.getString(3));
                bean.setLogin(rs.getString(4));
                bean.setPassword(rs.getString(5));
                bean.setDob(rs.getDate(6));
                bean.setMobileNo(rs.getString(7));
                bean.setRoleId(rs.getLong(8));
                bean.setGender(rs.getString(9));
                bean.setCreatedBy(rs.getString(10));
                bean.setModifiedBy(rs.getString(11));
                bean.setCreatedDatetime(rs.getTimestamp(12));
                bean.setModifiedDatetime(rs.getTimestamp(13));
            }
            rs.close();
            pstmt.close();
            log.debug("User fetched by login successfully: " + login);
        } catch (Exception e) {
            log.error("Exception in getting User by login", e);
            throw new ApplicationException("Exception : Exception in getting User by login");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return bean;
    }

    /**
     * Authenticates a User by login id and password.
     * 
     * @param login login id
     * @param password user password
     * @return UserBean if authentication is successful, otherwise null
     * @throws ApplicationException if any application-level error occurs
     */
    public UserBean authenticate(String login, String password) throws ApplicationException {
        log.debug("Authenticate method started for login: " + login);
        UserBean bean = null;
        Connection conn = null;

        StringBuffer sql = new StringBuffer("select * from st_user where login = ? and password = ?");

        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new UserBean();
                bean.setId(rs.getLong(1));
                bean.setFirstName(rs.getString(2));
                bean.setLastName(rs.getString(3));
                bean.setLogin(rs.getString(4));
                bean.setPassword(rs.getString(5));
                bean.setDob(rs.getDate(6));
                bean.setMobileNo(rs.getString(7));
                bean.setRoleId(rs.getLong(8));
                bean.setGender(rs.getString(9));
                bean.setCreatedBy(rs.getString(10));
                bean.setModifiedBy(rs.getString(11));
                bean.setCreatedDatetime(rs.getTimestamp(12));
                bean.setModifiedDatetime(rs.getTimestamp(13));
            }
            rs.close();
            pstmt.close();
            log.debug("Authentication successful for login: " + login);
        } catch (Exception e) {
            log.error("Exception in authentication", e);
            throw new ApplicationException("Exception : Exception in get roles");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return bean;
    }

    /**
     * Fetches all Users without pagination.
     * 
     * @return List of UserBean
     * @throws ApplicationException if any application-level error occurs
     */
    public List<UserBean> list() throws ApplicationException {
        log.debug("List method called");
        List<UserBean> list = search(null, 0, 0);
        log.debug("List method completed, total users: " + list.size());
        return list;
    }

    /**
     * Searches Users based on given criteria and supports pagination.
     * 
     * @param bean UserBean containing search criteria
     * @param pageNo page number
     * @param pageSize number of records per page
     * @return List of UserBean matching criteria
     * @throws ApplicationException if any application-level error occurs
     */
    public List<UserBean> search(UserBean bean, int pageNo, int pageSize) throws ApplicationException {
        log.debug("Search method called");
        Connection conn = null;
        ArrayList<UserBean> list = new ArrayList<UserBean>();

        StringBuffer sql = new StringBuffer("select * from st_user where 1=1");

        if (bean != null) {
            if (bean.getId() > 0) {
                sql.append(" and id = " + bean.getId());
            }
            if (bean.getFirstName() != null && bean.getFirstName().length() > 0) {
                sql.append(" and first_name like '" + bean.getFirstName() + "%'");
            }
            if (bean.getLastName() != null && bean.getLastName().length() > 0) {
                sql.append(" and last_name like '" + bean.getLastName() + "%'");
            }
            if (bean.getLogin() != null && bean.getLogin().length() > 0) {
                sql.append(" and login like '" + bean.getLogin() + "%'");
            }
            if (bean.getPassword() != null && bean.getPassword().length() > 0) {
                sql.append(" and password like '" + bean.getPassword() + "%'");
            }
            if (bean.getDob() != null && bean.getDob().getDate() > 0) {
                sql.append(" and dob = " + bean.getDob());
            }
            if (bean.getMobileNo() != null && bean.getMobileNo().length() > 0) {
                sql.append(" and mobile_no = " + bean.getMobileNo());
            }
            if (bean.getRoleId() > 0) {
                sql.append(" and role_id = " + bean.getRoleId());
            }
            if (bean.getGender() != null && bean.getGender().length() > 0) {
                sql.append(" and gender like '" + bean.getGender() + "%'");
            }
        }

        if (pageSize > 0) {
            pageNo = (pageNo - 1) * pageSize;
            sql.append(" limit " + pageNo + ", " + pageSize);
        }

        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new UserBean();
                bean.setId(rs.getLong(1));
                bean.setFirstName(rs.getString(2));
                bean.setLastName(rs.getString(3));
                bean.setLogin(rs.getString(4));
                bean.setPassword(rs.getString(5));
                bean.setDob(rs.getDate(6));
                bean.setMobileNo(rs.getString(7));
                bean.setRoleId(rs.getLong(8));
                bean.setGender(rs.getString(9));
                bean.setCreatedBy(rs.getString(10));
                bean.setModifiedBy(rs.getString(11));
                bean.setCreatedDatetime(rs.getTimestamp(12));
                bean.setModifiedDatetime(rs.getTimestamp(13));
                list.add(bean);
            }
            rs.close();
            pstmt.close();
            log.debug("Search completed, total results: " + list.size());
        } catch (Exception e) {
            log.error("Exception in search user", e);
            throw new ApplicationException("Exception : Exception in search user");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return list;
    }

    /**
     * Changes password of a User.
     * 
     * @param id user id
     * @param oldPassword current password
     * @param newPassword new password
     * @return true if password changed successfully
     * @throws RecordNotFoundException if user not found
     * @throws ApplicationException if any application-level error occurs
     */
    public boolean changePassword(Long id, String oldPassword, String newPassword)
            throws RecordNotFoundException, ApplicationException {
        log.debug("changePassword method started for user ID: " + id);
        boolean flag = false;
        UserBean beanExist = null;

        beanExist = findByPk(id);
        if (beanExist != null && beanExist.getPassword().equals(oldPassword)) {
            beanExist.setPassword(newPassword);
            try {
                update(beanExist);
                log.debug("Password updated successfully for user ID: " + id);
            } catch (DuplicateRecordException e) {
                log.error("Duplicate record exception while changing password for user ID: " + id, e);
                throw new ApplicationException("LoginId is already exist");
            }
            flag = true;
        } else {
            log.error("User not found or old password mismatch for ID: " + id);
            throw new RecordNotFoundException("Login not exist");
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("login", beanExist.getLogin());
        map.put("password", beanExist.getPassword());
        map.put("firstName", beanExist.getFirstName());
        map.put("lastName", beanExist.getLastName());

        String message = EmailBuilder.getChangePasswordMessage(map);

        EmailMessage msg = new EmailMessage();
        msg.setTo(beanExist.getLogin());
        msg.setSubject("Rays ORS Password has been changed Successfully.");
        msg.setMessage(message);
        msg.setMessageType(EmailMessage.HTML_MSG);

        EmailUtility.sendMail(msg);
        log.debug("Password change email sent to: " + beanExist.getLogin());

        return flag;
    }

    /**
     * Resets password for a User and sends an email with new password.
     * 
     * @param bean UserBean
     * @return true if reset successful
     * @throws ApplicationException if any application-level error occurs
     */
    public boolean resetPassword(UserBean bean) throws ApplicationException {
        log.debug("resetPassword method started for user ID: " + bean.getId());
        String newPassword = String.valueOf(new Date().getTime()).substring(0, 4);
        UserBean userData = findByPk(bean.getId());
        userData.setPassword(newPassword);

        try {
            update(userData);
            log.debug("Password reset successfully for user ID: " + bean.getId());
        } catch (DuplicateRecordException e) {
            log.error("Duplicate record exception while resetting password for user ID: " + bean.getId(), e);
            return false;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("login", bean.getLogin());
        map.put("password", bean.getPassword());
        map.put("firstName", bean.getFirstName());
        map.put("lastName", bean.getLastName());

        String message = EmailBuilder.getForgetPasswordMessage(map);

        EmailMessage msg = new EmailMessage();
        msg.setTo(bean.getLogin());
        msg.setSubject("Password has been reset");
        msg.setMessage(message);
        msg.setMessageType(EmailMessage.HTML_MSG);

        EmailUtility.sendMail(msg);
        log.debug("Password reset email sent to: " + bean.getLogin());

        return true;
    }

    /**
     * Sends a forget password email to the user.
     * 
     * @param login user login (email id)
     * @return true if email sent successfully
     * @throws ApplicationException if any application-level error occurs
     * @throws RecordNotFoundException if login not found
     */
    public boolean forgetPassword(String login) throws ApplicationException, RecordNotFoundException {
        log.debug("forgetPassword method started for login: " + login);
        UserBean userData = findByLogin(login);
        boolean flag = false;

        if (userData == null) {
            log.error("User with login " + login + " not found for forget password");
            throw new RecordNotFoundException("Email ID does not exists !");
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("login", userData.getLogin());
        map.put("password", userData.getPassword());
        map.put("firstName", userData.getFirstName());
        map.put("lastName", userData.getLastName());

        String message = EmailBuilder.getForgetPasswordMessage(map);

        EmailMessage msg = new EmailMessage();
        msg.setTo(login);
        msg.setSubject("Rays ORS Password Reset");
        msg.setMessage(message);
        msg.setMessageType(EmailMessage.HTML_MSG);

        EmailUtility.sendMail(msg);
        log.debug("Forget password email sent to: " + login);

        flag = true;
        return flag;
    }

    /**
     * Registers a new user and sends confirmation email.
     * 
     * @param bean UserBean
     * @return primary key of newly registered user
     * @throws ApplicationException if any application-level error occurs
     * @throws DuplicateRecordException if login id already exists
     */
    public long registerUser(UserBean bean) throws ApplicationException, DuplicateRecordException {
        log.debug("registerUser method started for login: " + bean.getLogin());
        long pk = add(bean);
        log.debug("User registered successfully with PK: " + pk);

        HashMap<String, String> map = new HashMap<>();
        map.put("login", bean.getLogin());
        map.put("password", bean.getPassword());

        String message = EmailBuilder.getUserRegistrationMessage(map);

        EmailMessage msg = new EmailMessage();
        msg.setTo(bean.getLogin());
        msg.setSubject("Registration is successful for ORS Project");
        msg.setMessage(message);
        msg.setMessageType(EmailMessage.HTML_MSG);

        EmailUtility.sendMail(msg);
        log.debug("Registration email sent to: " + bean.getLogin());

        return pk;
    }
}