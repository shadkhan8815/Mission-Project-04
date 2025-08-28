package in.co.rays.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import in.co.rays.bean.CollegeBean;
import in.co.rays.exception.ApplicationException;
import in.co.rays.exception.DatabaseException;
import in.co.rays.exception.DuplicateRecordException;
import in.co.rays.util.JDBCDataSource;

/**
 * CollegeModel class handles CRUD operations for college records.
 * It interacts with the database to perform Create, Read, Update, 
 * and Delete operations on the "st_college" table.
 * 
 * Responsibilities:
 * <ul>
 *   <li>Generate primary keys for college records.</li>
 *   <li>Add, update, and delete college details.</li>
 *   <li>Search and list college records based on criteria.</li>
 *   <li>Fetch details by primary key or name.</li>
 * </ul>
 * 
 * @author Shad Khan
 * @version 1.0
 * @since 2025
 */
public class CollegeModel {

    private static Logger log = Logger.getLogger(CollegeModel.class);

    /**
     * Gets the next primary key for the college table.
     *
     * @return the next primary key value
     * @throws DatabaseException if a database access error occurs
     */
    public Integer nextPk() throws DatabaseException {
        log.debug("CollegeModel nextPk() method started");
        Connection conn = null;
        int pk = 0;

        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("select max(id) from st_college");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pk = rs.getInt(1);
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            log.error("Database Exception in nextPk", e);
            throw new DatabaseException("Exception : Exception in getting PK");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CollegeModel nextPk() method End, PK=" + pk);
        return pk + 1;
    }

    /**
     * Adds a new college record to the database.
     *
     * @param bean the college bean containing college data
     * @return the generated primary key
     * @throws ApplicationException if a generic application exception occurs
     * @throws DuplicateRecordException if the college name already exists
     */
    public long add(CollegeBean bean) throws ApplicationException, DuplicateRecordException {
        log.debug("CollegeModel add() method started");
        Connection conn = null;
        int pk = 0;

        CollegeBean duplicateCollegeName = findByName(bean.getName());

        if (duplicateCollegeName != null) {
            log.error("Duplicate College Name : " + bean.getName());
            throw new DuplicateRecordException("College Name already exists");
        }

        try {
            conn = JDBCDataSource.getConnection();
            pk = nextPk();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn
                    .prepareStatement("insert into st_college values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, pk);
            pstmt.setString(2, bean.getName());
            pstmt.setString(3, bean.getAddress());
            pstmt.setString(4, bean.getState());
            pstmt.setString(5, bean.getCity());
            pstmt.setString(6, bean.getPhoneNo());
            pstmt.setString(7, bean.getCreatedBy());
            pstmt.setString(8, bean.getModifiedBy());
            pstmt.setTimestamp(9, bean.getCreatedDatetime());
            pstmt.setTimestamp(10, bean.getModifiedDatetime());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
        } catch (Exception e) {
            log.error("Application Exception in add()", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback Exception in add()", ex);
                throw new ApplicationException("Exception : add rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception : Exception in add College");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CollegeModel add() method End, PK=" + pk);
        return pk;
    }

    /**
     * Updates an existing college record.
     *
     * @param bean the college bean with updated data
     * @throws ApplicationException if a generic application exception occurs
     * @throws DuplicateRecordException if the updated name already exists for another college
     */
    public void update(CollegeBean bean) throws ApplicationException, DuplicateRecordException {
        log.debug("CollegeModel update() method started");

        Connection conn = null;

        CollegeBean beanExist = findByName(bean.getName());

        if (beanExist != null && beanExist.getId() != bean.getId()) {
            log.error("Duplicate College Name Found in Update : " + bean.getName());
            throw new DuplicateRecordException("College is already exist");
        }

        try {
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement(
                    "update st_college set name = ?, address = ?, state = ?, city = ?, phone_no = ?, created_by = ?, modified_by = ?, created_datetime = ?, modified_datetime = ? where id = ?");
            pstmt.setString(1, bean.getName());
            pstmt.setString(2, bean.getAddress());
            pstmt.setString(3, bean.getState());
            pstmt.setString(4, bean.getCity());
            pstmt.setString(5, bean.getPhoneNo());
            pstmt.setString(6, bean.getCreatedBy());
            pstmt.setString(7, bean.getModifiedBy());
            pstmt.setTimestamp(8, bean.getCreatedDatetime());
            pstmt.setTimestamp(9, bean.getModifiedDatetime());
            pstmt.setLong(10, bean.getId());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
        } catch (Exception e) {
            log.error("Application Exception in update()", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback Exception in update()", ex);
                throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception in updating College ");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CollegeModel update() method End");
    }

    /**
     * Deletes a college record from the database.
     *
     * @param bean the college bean identifying the record to delete
     * @throws ApplicationException if an error occurs during deletion
     */
    public void delete(CollegeBean bean) throws ApplicationException {
        log.debug("CollegeModel delete() method started, ID=" + bean.getId());
        Connection conn = null;
        try {
            conn = JDBCDataSource.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement("delete from st_college where id = ?");
            pstmt.setLong(1, bean.getId());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
        } catch (Exception e) {
            log.error("Application Exception in delete()", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.error("Rollback Exception in delete()", ex);
                throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
            }
            throw new ApplicationException("Exception : Exception in delete college");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CollegeModel delete() method End");
    }

    /**
     * Finds a college by primary key.
     *
     * @param pk the primary key of the college
     * @return the CollegeBean object, or null if not found
     * @throws ApplicationException if an error occurs during retrieval
     */
    public CollegeBean findByPk(long pk) throws ApplicationException {
        log.debug("CollegeModel findByPk() method started, PK=" + pk);

        StringBuffer sql = new StringBuffer("select * from st_college where id = ?");

        CollegeBean bean = null;
        Connection conn = null;

        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, pk);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new CollegeBean();
                bean.setId(rs.getLong(1));
                bean.setName(rs.getString(2));
                bean.setAddress(rs.getString(3));
                bean.setState(rs.getString(4));
                bean.setCity(rs.getString(5));
                bean.setPhoneNo(rs.getString(6));
                bean.setCreatedBy(rs.getString(7));
                bean.setModifiedBy(rs.getString(8));
                bean.setCreatedDatetime(rs.getTimestamp(9));
                bean.setModifiedDatetime(rs.getTimestamp(10));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            log.error("Application Exception in findByPk()", e);
            throw new ApplicationException("Exception : Exception in getting College by pk");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CollegeModel findByPk() method End");
        return bean;
    }

    /**
     * Finds a college by name.
     *
     * @param name the name of the college
     * @return the CollegeBean object, or null if not found
     * @throws ApplicationException if an error occurs during retrieval
     */
    public CollegeBean findByName(String name) throws ApplicationException {
        log.debug("CollegeModel findByName() method started, Name=" + name);

        StringBuffer sql = new StringBuffer("select * from st_college where name = ?");

        CollegeBean bean = null;
        Connection conn = null;

        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new CollegeBean();
                bean.setId(rs.getLong(1));
                bean.setName(rs.getString(2));
                bean.setAddress(rs.getString(3));
                bean.setState(rs.getString(4));
                bean.setCity(rs.getString(5));
                bean.setPhoneNo(rs.getString(6));
                bean.setCreatedBy(rs.getString(7));
                bean.setModifiedBy(rs.getString(8));
                bean.setCreatedDatetime(rs.getTimestamp(9));
                bean.setModifiedDatetime(rs.getTimestamp(10));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            log.error("Application Exception in findByName()", e);
            throw new ApplicationException("Exception : Exception in getting College by Name");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CollegeModel findByName() method End");
        return bean;
    }

    /**
     * Returns a list of all colleges.
     *
     * @return list of CollegeBean
     * @throws ApplicationException if an error occurs during retrieval
     */
    public List<CollegeBean> list() throws ApplicationException {
        log.debug("CollegeModel list() method started");
        List<CollegeBean> list = search(null, 0, 0);
        log.debug("CollegeModel list() method End, Total Records=" + list.size());
        return list;
    }

    /**
     * Searches for colleges based on the given criteria.
     *
     * @param bean the search criteria
     * @param pageNo the page number
     * @param pageSize the size of each page
     * @return list of CollegeBean matching the criteria
     * @throws ApplicationException if an error occurs during search
     */
    public List<CollegeBean> search(CollegeBean bean, int pageNo, int pageSize) throws ApplicationException {
        log.debug("CollegeModel search() method started");

        StringBuffer sql = new StringBuffer("select * from st_college where 1 = 1");

        if (bean != null) {
            if (bean.getId() > 0) {
                sql.append(" and id = " + bean.getId());
            }
            if (bean.getName() != null && bean.getName().length() > 0) {
                sql.append(" and name like '" + bean.getName() + "%'");
            }
            if (bean.getAddress() != null && bean.getAddress().length() > 0) {
                sql.append(" and address like '" + bean.getAddress() + "%'");
            }
            if (bean.getState() != null && bean.getState().length() > 0) {
                sql.append(" and state like '" + bean.getState() + "%'");
            }
            if (bean.getCity() != null && bean.getCity().length() > 0) {
                sql.append(" and city like '" + bean.getCity() + "%'");
            }
            if (bean.getPhoneNo() != null && bean.getPhoneNo().length() > 0) {
                sql.append(" and phone_no = " + bean.getPhoneNo());
            }
        }

        if (pageSize > 0) {
            pageNo = (pageNo - 1) * pageSize;
            sql.append(" limit " + pageNo + ", " + pageSize);
        }

        ArrayList<CollegeBean> list = new ArrayList<CollegeBean>();
        Connection conn = null;

        try {
            conn = JDBCDataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bean = new CollegeBean();
                bean.setId(rs.getLong(1));
                bean.setName(rs.getString(2));
                bean.setAddress(rs.getString(3));
                bean.setState(rs.getString(4));
                bean.setCity(rs.getString(5));
                bean.setPhoneNo(rs.getString(6));
                bean.setCreatedBy(rs.getString(7));
                bean.setModifiedBy(rs.getString(8));
                bean.setCreatedDatetime(rs.getTimestamp(9));
                bean.setModifiedDatetime(rs.getTimestamp(10));
                list.add(bean);
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            log.error("Application Exception in search()", e);
            throw new ApplicationException("Exception : Exception in search college");
        } finally {
            JDBCDataSource.closeConnection(conn);
        }
        log.debug("CollegeModel search() method End, Records Found=" + list.size());
        return list;
    }
}
