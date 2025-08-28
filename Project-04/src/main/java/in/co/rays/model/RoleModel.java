package in.co.rays.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import in.co.rays.bean.RoleBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DatabaseException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.util.JDBCDataSource;

/**
 * RoleModel is a Model class that provides methods to perform CRUD operations 
 * on the st_role database table.
 * 
 * It includes methods to add, update, delete, search, and retrieve Role records.
 * 
 * @author Shad Khan
 * @version 1.0
 */
public class RoleModel {

    private static Logger log = Logger.getLogger(RoleModel.class);

    /**
     * Gets the next primary key value for the st_role table.
     *
     * @return the next primary key as Integer
     * @throws DatabaseException if any database error occurs
     */
    public Integer nextPk() throws DatabaseException {
        log.debug("RoleModel nextPk started");
        Connection conn = null;
        int pk = 0;
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select max(id) from st_role");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pk = rs.getInt(1);
            }
            rs.close();
            pstmt.close();
            log.debug("Next PK retrieved: " + (pk + 1));
        } catch (Exception e) {
            log.error("Database exception in nextPk", e);
            throw new DatabaseException("Exception : Exception in getting PK");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return pk + 1;
    }

    /**
     * Adds a new Role record into the database.
     *
     * @param bean RoleBean object containing role details
     * @return the primary key of the newly added role
     * @throws ApplicationException if any application level exception occurs
     * @throws DuplicateRecordException if the role name already exists
     */
    public long add(RoleBean bean) throws ApplicationException, DuplicateRecordException {
        log.debug("RoleModel add started for Role: " + bean.getName());
        Connection conn = null;
        int pk = 0;

        RoleBean duplicataRole = findByName(bean.getName());

        if (duplicataRole != null) {
            log.error("Duplicate role found: " + bean.getName());
            throw new DuplicateRecordException("Role already exists");
        }

        try {
            pk = nextPk();
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement("insert into st_role values(?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, pk);
            pstmt.setString(2, bean.getName());
            pstmt.setString(3, bean.getDescription());
            pstmt.setString(4, bean.getCreatedBy());
            pstmt.setString(5, bean.getModifiedBy());
            pstmt.setTimestamp(6, bean.getCreatedDatetime());
            pstmt.setTimestamp(7, bean.getModifiedDatetime());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
            log.debug("Role added successfully with id: " + pk);
        } catch (Exception e) {
            log.error("Exception in adding Role", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback exception in add Role", ex);
                throw new ApplicationException("Exception : add rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception : Exception in add Role");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return pk;
    }

    /**
     * Updates an existing Role record in the database.
     *
     * @param bean RoleBean object containing updated role details
     * @throws ApplicationException if any application level exception occurs
     * @throws DuplicateRecordException if the role name already exists for another role
     */
    public void update(RoleBean bean) throws ApplicationException, DuplicateRecordException {
        log.debug("RoleModel update started for id: " + bean.getId());
        Connection conn = null;

        RoleBean duplicateRole = findByName(bean.getName());

        if (duplicateRole != null && duplicateRole.getId() != bean.getId()) {
            log.error("Duplicate role found during update: " + bean.getName());
            throw new DuplicateRecordException("Role already exists");
        }

        try {
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement(
                    "update st_role set name = ?, description = ?, created_by = ?, modified_by = ?, created_datetime = ?, modified_datetime = ? where id = ?");
            pstmt.setString(1, bean.getName());
            pstmt.setString(2, bean.getDescription());
            pstmt.setString(3, bean.getCreatedBy());
            pstmt.setString(4, bean.getModifiedBy());
            pstmt.setTimestamp(5, bean.getCreatedDatetime());
            pstmt.setTimestamp(6, bean.getModifiedDatetime());
            pstmt.setLong(7, bean.getId());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
            log.debug("Role updated successfully for id: " + bean.getId());
        } catch (Exception e) {
            log.error("Exception in updating Role", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback exception in update Role", ex);
                throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception in updating Role");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
    }

    /**
     * Deletes a Role record from the database.
     *
     * @param bean RoleBean object containing role details
     * @throws ApplicationException if any exception occurs during deletion
     */
    public void delete(RoleBean bean) throws ApplicationException {
        log.debug("RoleModel delete started for id: " + bean.getId());
        Connection conn = null;

        try {
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement("delete from st_role where id = ?");
            pstmt.setLong(1, bean.getId());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
            log.debug("Role deleted successfully for id: " + bean.getId());
        } catch (Exception e) {
            log.error("Exception in deleting Role", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback exception in delete Role", ex);
                throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception : Exception in delete Role");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
    }

    /**
     * Finds a Role by its primary key.
     *
     * @param pk primary key of the Role
     * @return RoleBean object containing role details
     * @throws ApplicationException if any database error occurs
     */
    public RoleBean findByPk(long pk) throws ApplicationException {
        log.debug("RoleModel findByPk started for id: " + pk);
        RoleBean bean = null;
        Connection conn = null;
        StringBuffer sql = new StringBuffer("select * from st_role where id = ?");
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, pk);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new RoleBean();
                bean.setId(rs.getLong(1));
                bean.setName(rs.getString(2));
                bean.setDescription(rs.getString(3));
                bean.setCreatedBy(rs.getString(4));
                bean.setModifiedBy(rs.getString(5));
                bean.setCreatedDatetime(rs.getTimestamp(6));
                bean.setModifiedDatetime(rs.getTimestamp(7));
            }
            rs.close();
            pstmt.close();
            log.debug("RoleModel findByPk completed for id: " + pk);
        } catch (Exception e) {
            log.error("Exception in findByPk", e);
            throw new ApplicationException("Exception : Exception in getting User by pk");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return bean;
    }

    /**
     * Finds a Role by its name.
     *
     * @param name name of the role
     * @return RoleBean object if found, otherwise null
     * @throws ApplicationException if any database error occurs
     */
    public RoleBean findByName(String name) throws ApplicationException {
        log.debug("RoleModel findByName started for name: " + name);
        StringBuffer sql = new StringBuffer("select * from st_role where name = ?");
        RoleBean bean = null;
        Connection conn = null;
        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new RoleBean();
                bean.setId(rs.getLong(1));
                bean.setName(rs.getString(2));
                bean.setDescription(rs.getString(3));
                bean.setCreatedBy(rs.getString(4));
                bean.setModifiedBy(rs.getString(5));
                bean.setCreatedDatetime(rs.getTimestamp(6));
                bean.setModifiedDatetime(rs.getTimestamp(7));
            }
            rs.close();
            pstmt.close();
            log.debug("RoleModel findByName completed for name: " + name);
        } catch (Exception e) {
            log.error("Exception in findByName", e);
            throw new ApplicationException("Exception : Exception in getting User by Role");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return bean;
    }

    /**
     * Returns list of all roles.
     *
     * @return list of RoleBean
     * @throws ApplicationException if any database error occurs
     */
    public List<RoleBean> list() throws ApplicationException {
        log.debug("RoleModel list started");
        List<RoleBean> list = search(null, 0, 0);
        log.debug("RoleModel list completed with size: " + list.size());
        return list;
    }

    /**
     * Searches roles based on given criteria.
     *
     * @param bean RoleBean object containing search criteria
     * @param pageNo page number for pagination
     * @param pageSize number of records per page
     * @return list of RoleBean matching the criteria
     * @throws ApplicationException if any database error occurs
     */
    public List<RoleBean> search(RoleBean bean, int pageNo, int pageSize) throws ApplicationException {
        log.debug("RoleModel search started");
        StringBuffer sql = new StringBuffer("select * from st_role where 1=1");

        if (bean != null) {
            if (bean.getId() > 0) {
                sql.append(" and id = " + bean.getId());
            }
            if (bean.getName() != null && bean.getName().length() > 0) {
                sql.append(" and name like '" + bean.getName() + "%'");
            }
            if (bean.getDescription() != null && bean.getDescription().length() > 0) {
                sql.append(" and description like '" + bean.getDescription() + "%'");
            }
        }

        if (pageSize > 0) {
            pageNo = (pageNo - 1) * pageSize;
            sql.append(" limit " + pageNo + ", " + pageSize);
        }

        Connection conn = null;
        ArrayList<RoleBean> list = new ArrayList<RoleBean>();

        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new RoleBean();
                bean.setId(rs.getLong(1));
                bean.setName(rs.getString(2));
                bean.setDescription(rs.getString(3));
                bean.setCreatedBy(rs.getString(4));
                bean.setModifiedBy(rs.getString(5));
                bean.setCreatedDatetime(rs.getTimestamp(6));
                bean.setModifiedDatetime(rs.getTimestamp(7));
                list.add(bean);
            }
            rs.close();
            pstmt.close();
            log.debug("RoleModel search completed with size: " + list.size());
        } catch (Exception e) {
            log.error("Exception in search Role", e);
            throw new ApplicationException("Exception : Exception in search Role");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        return list;
    }
}
